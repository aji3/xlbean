package org.xlbean.definition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;
import org.xlbean.XlList;
import org.xlbean.converter.BeanConverter;
import org.xlbean.converter.BeanConverterFactory;
import org.xlbean.data.ExcelDataSaver;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.FileUtil;

/**
 * Generates {@link DefinitionRepository} from a bean given as definitionSource.
 *
 * <p>
 * Basically this class is needed only when you want to write out a bean into
 * blank excel sheet.
 *
 * @author Kazuya Tanikawa
 */
public class BeanDefinitionLoader implements DefinitionLoader {

    private static Logger log = LoggerFactory.getLogger(BeanDefinitionLoader.class);

    private BeanConverter converter = BeanConverterFactory.getInstance().createBeanConverter();

    private String newSheetName = "data";

    /**
     * Number of iterations for list in list. (e.g. if value of this field is 2,
     * then list#anotherList[0].aaa, list#anotherList[1] will be defined.)
     */
    private int numberOfIterations;

    public BeanDefinitionLoader() {
        this(1);
    }

    public BeanDefinitionLoader(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public DefinitionRepository load(Object definitionSource) {
        if (definitionSource == null) {
            throw new IllegalArgumentException(String.format("Definition source should not be null"));
        }
        Object xlBeanOrXlList = null;
        if (definitionSource instanceof XlBean || definitionSource instanceof XlList) {
            xlBeanOrXlList = definitionSource;
        } else {
            xlBeanOrXlList = converter.toMap(definitionSource);
        }
        DefinitionRepository definitions = loadInternal(xlBeanOrXlList, new BeanDefinitionLoaderContext());
        definitions.sort(Comparator.comparing(Definition::getName));

        setCellInfo(definitions);

        XlWorkbook workbook = createNewWorkbookWithDefinitions(definitions);
        validate(definitions, workbook);
        definitions.activate(workbook);

        return definitions;
    }

    /**
     * Based on given {@code definitions}, create an excel sheet with the
     * definitions.
     * 
     * @param definitions
     * @return
     */
    private XlWorkbook createNewWorkbookWithDefinitions(DefinitionRepository definitions) {

        XlBean bean = new XlBeanImpl();
        DefinitionRepository definitionDefinitions = createDefinitionForDefinitions(bean, definitions);

        initNewWorkbokToBeXlBeanTarget(bean, definitionDefinitions);

        File tempWorkbookExcelFile = new File("_" + BeanDefinitionLoader.class.getName() + "_temp.xlsx");
        tempWorkbookExcelFile.deleteOnExit();
        try (OutputStream os = new FileOutputStream(tempWorkbookExcelFile)) {

            Workbook rawWorkbook = new XSSFWorkbook();
            rawWorkbook.createSheet(newSheetName);

            XlWorkbook workbook = XlWorkbook.wrap(rawWorkbook);
            definitionDefinitions.activate(workbook);

            new ExcelDataSaver().save(
                bean,
                definitionDefinitions,
                os);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }

        try (InputStream in = new FileInputStream(tempWorkbookExcelFile)) {
            Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in));
            return XlWorkbook.wrap(wb);
        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new XlBeanException(e);
        }

    }

    private void initNewWorkbokToBeXlBeanTarget(XlBean bean, DefinitionRepository definitions) {
        SingleDefinition mark = new SingleDefinition();
        mark.setName(DefinitionConstants.TARGET_SHEET_MARK);
        mark.setCell(new XlCellAddress.Builder().row(0).column(0).build());
        bean.put(DefinitionConstants.TARGET_SHEET_MARK, DefinitionConstants.TARGET_SHEET_MARK);
        mark.setSheetName(newSheetName);
        definitions.addDefinition(mark);
    }

    /**
     * This method creates definitions for writing out definitions to row 1 and
     * column 1 of excel sheet. This step is required because when templateWorkbook
     * is null, new sheet is created blank so that definitions also written to excel
     * sheet.
     * 
     * @param bean
     * @param definitions
     * @return
     */
    private DefinitionRepository createDefinitionForDefinitions(XlBean bean, DefinitionRepository definitions) {
        DefinitionRepository definitionDefinitions = new DefinitionRepository();
        definitions
            .stream()
            .flatMap(definition -> createDefinitionForDefinition(definition, bean))
            .forEach(definitionDefinitions::addDefinition);
        return definitionDefinitions;
    }

    private Stream<Definition> createDefinitionForDefinition(Definition definition, XlBean bean) {
        List<Definition> retList = new ArrayList<>();
        if (definition instanceof TableDefinition) {
            TableDefinition table = (TableDefinition) definition;
            for (SingleDefinition attr : table.getAttributes().values()) {
                int row, column;
                if (attr.getName().startsWith("~")) {
                    row = attr.getCell().getRow();
                    column = 0;
                } else {
                    row = 0;
                    column = attr.getCell().getColumn();
                }
                retList.add(
                    createDefinition(
                        row,
                        column,
                        table.getName() + "#" + attr.getName(),
                        bean));
            }
        } else {
            // it instanceof SingleDefinition
            SingleDefinition single = (SingleDefinition) definition;

            retList.add(createDefinition(single.getCell().getRow(), 0, single.getName(), bean));
            retList.add(createDefinition(0, single.getCell().getColumn(), single.getName(), bean));
        }
        return retList.stream();
    }

    private Definition createDefinition(int row, int column, String name, XlBean bean) {
        String key = String.format("$definition_%d_%d", row, column);
        SingleDefinition definitionDefinition = new SingleDefinition();
        definitionDefinition.setName(key);
        definitionDefinition.setCell(new XlCellAddress.Builder().row(row).column(column).build());
        definitionDefinition.setSheetName(newSheetName);

        setValueForDefinition(key, name, bean);
        return definitionDefinition;
    }

    private void setValueForDefinition(String key, String value, XlBean bean) {
        Object obj = bean.get(key);
        if (obj == null) {
            bean.put(key, value);
        } else {
            if (obj instanceof String) {
                bean.put(key, ((String) obj) + ", " + value);
            } else {
                // skip
            }
        }
    }

    /**
     * Calls {@link Definition#validate()} of all the {@link Definition} instances
     * in this repository, then set the corresponding excel sheet instance to each
     * definitions.
     *
     * @param workbook
     */
    private void validate(DefinitionRepository definitions, XlWorkbook workbook) {
        for (Definition definition : definitions.getDefinitions()) {
            // Check if the definition is valid
            if (!definition.validate()) {
                log.warn(
                    "Invalid definition [{}] ({})",
                    definition.getName(),
                    definition.getClass().getName());
                continue;
            }
        }
    }

    /**
     * Set cell information to Definition object in given {@code definitions}.
     * 
     * <p>
     * Since definitions created by this class derives from some object and not
     * Excel sheet, there is no cell information set by default. Cell information is
     * in ExcelR1C1Definition format.
     * </p>
     * 
     * 
     * @param definitions
     */
    private void setCellInfo(DefinitionRepository definitions) {
        CellInfoGenerator generator = new CellInfoGenerator();
        definitions.forEach(
            it ->
            {
                if (it instanceof TableDefinition) {
                    TableDefinition table = (TableDefinition) it;
                    for (SingleDefinition attr : table
                        .getAttributes()
                        .values()
                        .stream()
                        .sorted(Comparator.comparing(SingleDefinition::getName))
                        .collect(Collectors.toList())) {
                        attr.setCell(generator.generateCellForTableColumn());
                    }
                    SingleDefinition start = new SingleDefinition();
                    start.setName("~");
                    start.setCell(new XlCellAddress.Builder().row(1).build());
                    table.addAttribute(start);
                } else {
                    // it instanceof SingleDefinition
                    SingleDefinition single = (SingleDefinition) it;
                    single.setCell(generator.generateCellForSingle());
                }
                it.setSheetName("data");
            });
    }

    private class CellInfoGenerator {
        private int columnForTables = 2;
        private int rowForSingles = 1;

        public XlCellAddress generateCellForTableColumn() {
            return new XlCellAddress.Builder().row(1).column(columnForTables++).build();
        }

        public XlCellAddress generateCellForSingle() {
            return new XlCellAddress.Builder().row(rowForSingles++).column(1).build();
        }
    }

    /**
     * Recursively scan {@code obj} and generate Definition.
     * 
     * @param obj
     * @param context
     * @return
     */
    private DefinitionRepository loadInternal(Object obj, BeanDefinitionLoaderContext context) {
        DefinitionRepository definitions = new DefinitionRepository();
        if (obj instanceof XlBean) {
            ((Map<?, ?>) obj)
                .forEach(
                    (key, value) ->
                    {
                        if (key == null) {
                            return;
                        }
                        context.push(key.toString());
                        definitions.merge(loadInternal(value, context));
                    });
        } else if (obj instanceof List) {
            loadTableDefinition((List<?>) obj, context, definitions);
        } else {
            // This path is for the "obj" which is not XlBean nor XlList.
            // It should be String.
            loadSingleDefinition(context, definitions);
        }
        context.pop();
        return definitions;
    }

    private void loadSingleDefinition(
            BeanDefinitionLoaderContext context, DefinitionRepository definitions) {
        if (context.getDepth() == 0) {
            context.push("value");
        }
        SingleDefinition single = new SingleDefinition();
        single.setName(context.getCurrentName());
        single.getOptions().put("type", "string");
        definitions.addDefinition(single);
    }

    private void loadTableDefinition(
            List<?> list, BeanDefinitionLoaderContext context, DefinitionRepository definitions) {
        if (context.getDepth() == 0) {
            context.push("values");
        }
        TableDefinitionForBeanLoader table = new TableDefinitionForBeanLoader(list);
        table.setName(context.getCurrentName());
        definitions.addDefinition(table);
        Map<String, Definition> attributesMap = new HashMap<>();
        for (Object bean : list) {
            DefinitionRepository attributes = loadInternal(bean, new BeanDefinitionLoaderContext());
            attributesMap.putAll(attributes.toMap());
        }
        attributesMap.values().forEach(it -> {
            if (it instanceof SingleDefinition) {
                table.addAttribute((SingleDefinition) it);
            } else if (it instanceof TableDefinitionForBeanLoader) {
                convertInternalTableDefinitionToNestedSingleDefinition((TableDefinitionForBeanLoader) it, table);
            } else {
                throw new IllegalArgumentException("Illegal definition type");
            }
        });
    }

    /**
     * 
     * 
     * @param internalTableDefinition
     * @param table
     */
    private void convertInternalTableDefinitionToNestedSingleDefinition(
            TableDefinitionForBeanLoader internalTableDefinition,
            TableDefinitionForBeanLoader table) {
        internalTableDefinition.getAttributes().values().forEach(
            item ->
            {
                for (int i = 0; i < Math.min(numberOfIterations, internalTableDefinition.getSourceList().size()); i++) {
                    SingleDefinition single = new SingleDefinition();
                    String newName = String.format(
                        "%s[%d].%s",
                        internalTableDefinition.getName(),
                        i,
                        item.getName());
                    single.setName(newName);
                    single.getOptions().put("type", "string");
                    table.addAttribute(single);
                }
            });
    }

    /**
     * Context class used to create name of definitions in {@link #loadInternal}.
     *
     * @author Kazuya Tanikawa
     */
    private class BeanDefinitionLoaderContext {
        private List<String> keys = new ArrayList<>();

        public int getDepth() {
            return keys.size();
        }

        public void push(String key) {
            keys.add(key);
        }

        public String pop() {
            if (keys.size() == 0) {
                return null;
            }
            return keys.remove(keys.size() - 1);
        }

        public String getCurrentName() {
            return String.join(".", keys);
        }
    }

    private class TableDefinitionForBeanLoader extends TableDefinition {
        private List<?> sourceList;

        public TableDefinitionForBeanLoader(List<?> sourceList) {
            this.sourceList = sourceList;
        }

        public List<?> getSourceList() {
            return sourceList;
        }

    }
}
