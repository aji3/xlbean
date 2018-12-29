package org.xlbean.definition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.jparsec.error.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.definition.InTableOptionDefinition.OptionKey;
import org.xlbean.definition.parser.DefinitionParser;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlSheet;
import org.xlbean.excel.XlWorkbook;

/**
 * Read definitions from row R1 and colum C1 of excel sheets.
 *
 * @author Kazuya Tanikawa
 */
public class ExcelR1C1DefinitionLoader implements DefinitionLoader {

    private static Logger log = LoggerFactory.getLogger(ExcelR1C1DefinitionLoader.class);

    private static final List<DefinitionBuilder> DEFAULT_DEFINITION_BUILDERS = Arrays.asList(
        new SingleDefinitionBuilder(),
        new TableDefinitionBuilder(),
        new InTableOptionDefinitionBuilder());

    /**
     * Scan all the sheets and read definition from row 1 and column 1.
     *
     * @param context
     */
    @Override
    public DefinitionRepository load(Object definitionSource) {
        XlWorkbook workbook = initialize(definitionSource);
        DefinitionRepository definitions = new DefinitionRepository();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XlSheet sheet = workbook.getSheetAt(i);
            if (!isProcessingTargetSheet(sheet)) {
                continue;
            }
            long now = System.currentTimeMillis();
            log.debug("Start loading table definition from sheet {}", sheet.getSheetName());
            definitions.merge(readAllSheetDefinition(sheet));
            log.info(
                "Loaded table definition from sheet {} [{} msec]",
                sheet.getSheetName(),
                (System.currentTimeMillis() - now));
        }

        definitions.validateAll();
        definitions.activate(workbook);
        return definitions;
    }

    private XlWorkbook initialize(Object definitionSource) {
        if (definitionSource == null || !(definitionSource instanceof Workbook)) {
            throw new IllegalArgumentException(
                String.format("Definition source should be an instance of %s", Workbook.class.getName()));
        }
        return XlWorkbook.wrap((Workbook) definitionSource);
    }

    /**
     * Returns true if the value of cell(0, 0) is "####".
     *
     * @param sheet
     * @return
     */
    protected boolean isProcessingTargetSheet(XlSheet sheet) {
        return DefinitionConstants.TARGET_SHEET_MARK.equals(sheet.getCellValue(0, 0));
    }

    protected List<DefinitionBuilder> getDefinitionBuilders() {
        return DEFAULT_DEFINITION_BUILDERS;
    }

    /**
     * Read all definitions in given {@code sheet}.
     * 
     * <p>
     * This method is intended to be overridden by inheriting classes to realize
     * variation of definitions.
     * </p>
     * 
     * @param sheet
     * @return
     */
    protected DefinitionRepository readAllSheetDefinition(XlSheet sheet) {
        DefinitionRepository definitions = new DefinitionRepository();
        int maxRow = sheet.getMaxRow();
        int maxCol = sheet.getMaxColumn();
        if (Math.min(maxRow, maxCol) == 0) {
            return definitions;
        }

        // read column definition
        for (int col = 1; col <= maxCol; col++) {
            readCellDefinition(definitions, sheet, col, true);
        }

        // read row definition
        for (int row = 1; row <= maxRow; row++) {
            readCellDefinition(definitions, sheet, row, false);
        }

        processInTableOptionAndRemoveFromDefinitions(definitions, sheet);

        return definitions;
    }

    private void processInTableOptionAndRemoveFromDefinitions(DefinitionRepository definitions, XlSheet sheet) {
        List<Definition> intableOptions = definitions
            .stream()
            .filter(def -> InTableOptionDefinition.class.equals(def.getClass()))
            .collect(Collectors.toList());

        if (intableOptions.size() > 0) {
            Map<String, List<Definition>> defKeyToDefinitionsMap = definitions
                .stream()
                .filter(def -> !InTableOptionDefinition.class.equals(def.getClass()))
                .collect(Collectors.groupingBy(Definition::getName));

            intableOptions.forEach(intableOptionDefinition -> {
                processInTableOption(
                    (InTableOptionDefinition) intableOptionDefinition,
                    defKeyToDefinitionsMap.get(intableOptionDefinition.getName()),
                    sheet);
            });

            definitions.getDefinitions().removeAll(intableOptions);
        }
    }

    private void processInTableOption(
            InTableOptionDefinition optionDef,
            List<Definition> definitionsToReflectInTableOption,
            XlSheet sheet) {
        if (definitionsToReflectInTableOption == null) {
            return;
        }
        optionDef.getOptionKeys().forEach(optionKey -> {
            definitionsToReflectInTableOption.forEach(targetDefinition -> {
                if (targetDefinition instanceof TableDefinition) {
                    TableDefinition tableDefinition = (TableDefinition) targetDefinition;
                    tableDefinition.getAttributes().values().forEach(attr -> {
                        loadInTableOptionAndAddToDefinition(optionKey, attr, sheet);
                    });
                } else if (targetDefinition instanceof SingleDefinition) {
                    SingleDefinition singleDefinition = (SingleDefinition) targetDefinition;
                    loadInTableOptionAndAddToDefinition(optionKey, singleDefinition, sheet);
                } else {
                    throw new UnsupportedOperationException("Unsupported Definition class");
                }
            });
        });
    }

    private void loadInTableOptionAndAddToDefinition(
            OptionKey optionKey,
            SingleDefinition singleDefinition,
            XlSheet sheet) {
        String optionValue = loadOptionCell(optionKey.getCell(), singleDefinition.getCell(), sheet);
        singleDefinition.addOption(optionKey.getOptionKey(), optionValue);
    }

    private String loadOptionCell(XlCellAddress inTableOptionCell, XlCellAddress definitionCell, XlSheet sheet) {
        if (inTableOptionCell.getRow() != null && definitionCell.getColumn() != null) {
            return sheet.getCellValue(inTableOptionCell.getRow(), definitionCell.getColumn());
        } else if (inTableOptionCell.getColumn() != null && definitionCell.getRow() != null) {
            return sheet.getCellValue(definitionCell.getRow(), inTableOptionCell.getColumn());
        }
        return null;
    }

    private void readCellDefinition(
            DefinitionRepository definitions, XlSheet sheet, int num, boolean isColumn) {
        String value = null;
        if (isColumn) {
            value = sheet.getCellValue(0, num);
        } else {
            value = sheet.getCellValue(num, 0);
        }
        if (value == null) {
            return;
        }
        Arrays
            .stream(value.split(","))
            .map(str -> str.trim())
            .map(this::parse)
            .filter(elem -> elem != null)
            .map(elem -> build(elem, isColumn, num, sheet.getSheetName()))
            .forEach(definitions::addDefinition);
    }

    private Definition build(Object parsedDefinition, boolean isColumn, int num, String sheetName) {
        DefinitionBuilder definitionBuilder = getDefinitionBuilder(parsedDefinition);
        Definition newDefinition = null;
        if (isColumn) {
            newDefinition = definitionBuilder.build(
                parsedDefinition,
                new XlCellAddress.Builder().column(num).build());
        } else {
            newDefinition = definitionBuilder.build(
                parsedDefinition,
                new XlCellAddress.Builder().row(num).build());
        }
        newDefinition.setSheetName(sheetName);
        return newDefinition;
    }

    protected Object parse(String definitionStr) {
        try {
            return DefinitionParser.parse(definitionStr);
        } catch (ParserException e) {
            log.warn("Invalid definition: {}", definitionStr);
        }
        return null;
    }

    protected DefinitionBuilder getDefinitionBuilder(Object parsedObject) {
        for (DefinitionBuilder resolver : getDefinitionBuilders()) {
            if (resolver.isBuildable(parsedObject)) {
                return resolver;
            }
        }
        return null;
    }

}
