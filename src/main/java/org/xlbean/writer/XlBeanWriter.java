package org.xlbean.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xlbean.XlBean;
import org.xlbean.data.ExcelDataSaver;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.FileUtil;

/**
 * Writer to write data into Excel file.
 * 
 * @author Kazuya Tanikawa
 */
public class XlBeanWriter {

    private DefinitionLoader definitionLoader;
    private ExcelDataSaver dataSaver;

    public XlBeanWriter() {
        this(null, null);
    }

    public XlBeanWriter(DefinitionLoader definitionLoader) {
        this(definitionLoader, null);
    }

    public XlBeanWriter(ExcelDataSaver dataSaver) {
        this(null, dataSaver);
    }

    public XlBeanWriter(DefinitionLoader definitionLoader, ExcelDataSaver dataSaver) {
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
        DefinitionRepository definitions = readDefinition(excelTemplate);

        try (OutputStream out = new FileOutputStream(outputExcel);) {
            write(definitions, data, out);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }
    }

    /**
     * Write {@code data} to {@code outputExcel} using {@code excelTemplate} as a
     * template.
     * 
     * <p>
     * If you want to write {@code data} to format excel file, use
     * {@link write(File, XlBean, File)}.
     * </p>
     * 
     * @param excelTemplate
     * @param data
     * @param outputExcel
     */
    public void write(InputStream excelTemplate, XlBean data, OutputStream outputExcel) {
        DefinitionRepository definitions = readDefinition(excelTemplate);

        write(definitions, data, outputExcel);
    }

    /**
     * Write {@code bean} to OutputStream {@code outputExcel} using
     * {@code definitionSource} as source of template information.
     * 
     * @param definitionSource
     *            Object to be passed to {@code definitionLoader} for source of
     *            definition. Type of object depends on implementation of
     *            {@code definitionLoader}.
     * @param templateWorkbook
     *            Excel workbook which has template information. If
     *            {@code templateWorkbook} is null, then blank excel book with
     *            {@code newSheetName} will be created and definitions created by
     *            {@code definitionLoader} will be populated to the excel book.
     * @param bean
     *            Data bean to write out to the outputExcel using templateWorkbook.
     * @param outputExcel
     *            OutputStream to write out excel data.
     * @throws IOException
     */
    public void write(
            Object definitionSource, XlBean bean, OutputStream outputExcel)
            throws IOException {

        DefinitionRepository definitions = definitionLoader.load(definitionSource);
        write(definitions, bean, outputExcel);
    }

    public void write(DefinitionRepository definitions, XlBean bean, OutputStream outputExcel) {
        dataSaver.save(bean, definitions, outputExcel);
    }

    protected DefinitionRepository readDefinition(Object definitionSource) {
        return definitionLoader.load(definitionSource);
    }

    protected DefinitionRepository readDefinition(File excelTemplateFile) {
        try (InputStream is = new FileInputStream(excelTemplateFile)) {
            return readDefinition(is);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }
    }

    protected DefinitionRepository readDefinition(InputStream excelTemplateInputStream) {
        try (InputStream copiedStream = FileUtil.copyToInputStream(excelTemplateInputStream);) {
            Workbook wb = WorkbookFactory.create(copiedStream);
            return readDefinition(wb);
        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
