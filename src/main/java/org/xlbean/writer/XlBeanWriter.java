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
import org.xlbean.definition.BeanDefinitionLoader;
import org.xlbean.definition.Definition;
import org.xlbean.definition.DefinitionConstants;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelCommentDefinitionLoader;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.FileUtil;

/** @author Kazuya Tanikawa */
public class XlBeanWriter {

    private DefinitionLoader<?> definitionLoader = new ExcelR1C1DefinitionLoader();
    private ExcelDataSaver dataSaver = new ExcelDataSaver();
    private String newSheetName = "data";

    /**
     * Write {@code data} to {@code outputExcel} using {@code excelTemplate} as a
     * template.
     * 
     * <p>
     * This method allows {@code excelTemplate} and {@code outputExcel} to point to
     * the same file, which means to write {@code data} directly to format excel
     * file.
     * </p>
     * 
     * @param excelTemplate
     * @param data
     * @param outputExcel
     */
    public void write(File excelTemplate, XlBean data, File outputExcel) {
        try (OutputStream out = new FileOutputStream(outputExcel);) {
            write(FileUtil.copyToInputStream(excelTemplate), data, out);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }
    }

    public void write(InputStream excelTemplate, XlBean data, OutputStream outputExcel) {
        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(excelTemplate));) {

            write(wb, wb, data, outputExcel);

        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            throw new XlBeanException(e);
        }
    }

    public void write(
            Object definitionSource, Workbook templateWorkbook, XlBean bean, OutputStream outputExcel)
            throws IOException {

        definitionLoader.initialize(definitionSource);
        DefinitionRepository definitions = definitionLoader.load();

        if (templateWorkbook == null) {
            templateWorkbook = new XSSFWorkbook();
            templateWorkbook.createSheet(newSheetName);

            DefinitionRepository definitionDefinitions = createDefinitionDefinitions(bean, definitions);
            definitions.merge(definitionDefinitions);

            initNewWorkbokToBeXlBeanTarget(bean, definitions);
        }
        XlWorkbook workbook = XlWorkbook.wrap(templateWorkbook);
        dataSaver.save(bean, definitions, workbook, outputExcel);
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
     * column 1 of excel sheet. This step is required because When templateWorkbook
     * is null, new sheet is created blank so that definitions also written to excel
     * sheet.
     * 
     * 
     * @param bean
     * @param definitions
     * @return
     */
    private DefinitionRepository createDefinitionDefinitions(
            XlBean bean, DefinitionRepository definitions) {
        DefinitionRepository definitionDefinitions = new DefinitionRepository();
        definitions.forEach(
            it ->
            {
                if (it instanceof TableDefinition) {
                    TableDefinition table = (TableDefinition) it;
                    for (SingleDefinition attr : table.getAttributes().values()) {
                        int row, column;
                        if (attr.getName().startsWith("~")) {
                            row = attr.getCell().getRow();
                            column = 0;
                        } else {
                            row = 0;
                            column = attr.getCell().getColumn();
                        }
                        definitionDefinitions.addDefinition(
                            createDefinitionDefinition(
                                row,
                                column,
                                table.getName() + "#" + attr.getName(),
                                bean));
                    }
                } else {
                    // it instanceof SingleDefinition
                    SingleDefinition single = (SingleDefinition) it;

                    definitionDefinitions.addDefinition(
                        createDefinitionDefinition(single.getCell().getRow(), 0, single.getName(), bean));
                    definitionDefinitions.addDefinition(
                        createDefinitionDefinition(
                            0,
                            single.getCell().getColumn(),
                            single.getName(),
                            bean));
                }
                it.setSheetName(newSheetName);
            });
        return definitionDefinitions;
    }

    private Definition createDefinitionDefinition(int row, int column, String name, XlBean bean) {
        String key = String.format("$definition_%d_%d", row, column);
        SingleDefinition definitionDefinition = new SingleDefinition();
        definitionDefinition.setName(key);
        definitionDefinition.setCell(new XlCellAddress.Builder().row(row).column(column).build());
        definitionDefinition.setSheetName(newSheetName);

        mergeDefinitionDefinition(key, name, bean);
        return definitionDefinition;
    }

    private void mergeDefinitionDefinition(String key, String value, XlBean bean) {
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

    public static class XlBeanWriterBuilder {

        private DefinitionLoader<?> definitionLoader;
        private ExcelDataSaver dataSaver;

        public XlBeanWriter build() {
            if (definitionLoader == null) {
                definitionLoader = new ExcelR1C1DefinitionLoader();
            }
            if (dataSaver == null) {
                dataSaver = new ExcelDataSaver();
            }
            XlBeanWriter writer = new XlBeanWriter();
            writer.definitionLoader = definitionLoader;
            writer.dataSaver = dataSaver;
            return writer;
        }

        /**
         * Set DefinitionLoader of the XlBeanWriter instance.
         * 
         * @see ExcelR1C1DefinitionLoader
         * @see ExcelCommentDefinitionLoader
         * @see BeanDefinitionLoader
         * 
         * @param definitionLoader
         * @return
         */
        public XlBeanWriterBuilder definitionLoader(DefinitionLoader<?> definitionLoader) {
            this.definitionLoader = definitionLoader;
            return this;
        }

        /**
         * Set ExcelDataSaver of the XlBeanWriter instance.
         * 
         * @see ExcelDataSaver
         * @param dataSaver
         * @return
         */
        public XlBeanWriterBuilder dataSaver(ExcelDataSaver dataSaver) {
            this.dataSaver = dataSaver;
            return this;
        }

    }

}
