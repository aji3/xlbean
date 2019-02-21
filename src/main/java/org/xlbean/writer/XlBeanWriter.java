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
import org.xlbean.definition.BeanDefinitionLoader;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.Definitions;
import org.xlbean.definition.ExcelCommentDefinitionLoader;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.FileUtil;

/**
 * Writer to write data into Excel file.
 * 
 * @author Kazuya Tanikawa
 */
public class XlBeanWriter {

    private DefinitionLoader definitionLoader = new ExcelR1C1DefinitionLoader();
    private ExcelDataSaver dataSaver = new ExcelDataSaver();

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
        Definitions definitions = readDefinition(excelTemplate);

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
        Definitions definitions = readDefinition(excelTemplate);

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
     * @param bean
     *            Data bean to write out to the outputExcel using templateWorkbook.
     * @param outputExcel
     *            OutputStream to write out excel data.
     * @throws IOException
     */
    public void write(
            Object definitionSource, XlBean bean, OutputStream outputExcel)
            throws IOException {

        Definitions definitions = definitionLoader.load(definitionSource);
        write(definitions, bean, outputExcel);
    }

    public void write(Definitions definitions, XlBean bean, OutputStream outputExcel) {
        dataSaver.save(bean, definitions, outputExcel);
    }

    protected Definitions readDefinition(Object definitionSource) {
        return definitionLoader.load(definitionSource);
    }

    protected Definitions readDefinition(File excelTemplateFile) {
        try (InputStream is = new FileInputStream(excelTemplateFile)) {
            return readDefinition(is);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }
    }

    protected Definitions readDefinition(InputStream excelTemplateInputStream) {
        try (InputStream copiedStream = FileUtil.copyToInputStream(excelTemplateInputStream);) {
            Workbook wb = WorkbookFactory.create(copiedStream);
            return readDefinition(wb);
        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class XlBeanWriterBuilder {

        private DefinitionLoader definitionLoader;
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
        public XlBeanWriterBuilder definitionLoader(DefinitionLoader definitionLoader) {
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
