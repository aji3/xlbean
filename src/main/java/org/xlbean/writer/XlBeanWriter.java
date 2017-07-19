package org.xlbean.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xlbean.XlBean;
import org.xlbean.data.ExcelDataSaver;
import org.xlbean.definition.Definition;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.util.Util;

/**
 * 
 * 
 * @author Kazuya Tanikawa
 *
 */
public class XlBeanWriter {

    private DefinitionLoader<?> definitionLoader;
    private ExcelDataSaver dataSaver;
    
    public XlBeanWriter() {
        this(null, null);
    }

    public XlBeanWriter(DefinitionLoader<?> definitionLoader) {
        this(definitionLoader, null);
    }

    public XlBeanWriter(ExcelDataSaver dataSaver) {
        this(null, dataSaver);
    }

    public XlBeanWriter(DefinitionLoader<?> definitionLoader, ExcelDataSaver dataSaver) {
        if (definitionLoader == null) {
            this.definitionLoader = new ExcelR1C1DefinitionLoader(); 
        } else {
            this.definitionLoader = definitionLoader;
        }
        if (dataSaver == null) {
            this.dataSaver = new ExcelDataSaver();
        } else {
            this.dataSaver = dataSaver;    
        }
    }

    public void write(File excelTemplate, XlBean data, File outputExcel) {
        try (OutputStream out = new FileOutputStream(outputExcel);) {
            write(Util.copyToInputStream(excelTemplate), data, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(InputStream excelTemplate, XlBean data, OutputStream outputExcel) {
        try (Workbook wb = WorkbookFactory.create(Util.copyToInputStream(excelTemplate));) {

            write(wb, wb, data, outputExcel);

        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
   
    public void write(Object definitionSource, Workbook templateWorkbook, XlBean bean, OutputStream outputExcel)
            throws IOException {

        definitionLoader.initialize(definitionSource);
        DefinitionRepository definitions = definitionLoader.load();

        if (templateWorkbook == null) {
            templateWorkbook = new XSSFWorkbook();
            templateWorkbook.createSheet("data");
            
            DefinitionRepository definitionDefinitions = createDefinitionDefinitions(bean, definitions);
            definitions.merge(definitionDefinitions);
        }
        XlWorkbook workbook = XlWorkbook.wrap(templateWorkbook);
        dataSaver.save(bean, definitions, workbook, outputExcel);
    }

    private DefinitionRepository createDefinitionDefinitions(XlBean bean, DefinitionRepository definitions) {
        DefinitionRepository definitionDefinitions = new DefinitionRepository();
        definitions.forEach(it -> {
            if (it instanceof TableDefinition) {
                TableDefinition table = (TableDefinition)it;
                for (SingleDefinition attr : table.getAttributes().values()) {
                    int row, column;
                    if (attr.getName().startsWith("~")) {
                        row = attr.getCell().getRow();
                        column = 0;
                    } else {
                        row = 0;
                        column = attr.getCell().getColumn();
                    }
                    definitionDefinitions.addDefinition(createDefinitionDefinition(row, column, table.getName() + "#" + attr.getName(), bean));
                }
            } else {
                // it instanceof SingleDefinition
                SingleDefinition single = (SingleDefinition)it;
                
                definitionDefinitions.addDefinition(createDefinitionDefinition(single.getCell().getRow(), 0, single.getName(), bean));
                definitionDefinitions.addDefinition(createDefinitionDefinition(0, single.getCell().getColumn(), single.getName(), bean));
            }
            it.setSheetName("data");
        });
        return definitionDefinitions;
    }
    
    private Definition createDefinitionDefinition(int row, int column, String name, XlBean bean) {
        String key = String.format("$definition_%d_%d", row, column);
        SingleDefinition definitionDefinition = new SingleDefinition();
        definitionDefinition.setName(key);
        definitionDefinition.setOriginalKeyString(key);
        definitionDefinition.setCell(new XlCellAddress.Builder().row(row).column(column).build());
        definitionDefinition.setSheetName("data");

        mergeDefinitionDefinition(key, name, bean);
        return definitionDefinition;
    }
    
    private void mergeDefinitionDefinition(String key, String value, XlBean bean) {
        Object obj = bean.get(key);
        if (obj == null) {
            bean.put(key, value);
        } else {
            if (obj instanceof String) {
                bean.put(key, ((String)obj) + ", " + value);
            } else {
                // skip
            }
        }
    }

}
