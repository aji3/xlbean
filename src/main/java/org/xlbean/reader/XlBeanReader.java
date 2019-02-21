package org.xlbean.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xlbean.XlBean;
import org.xlbean.data.ExcelDataLoader;
import org.xlbean.definition.BeanDefinitionLoader;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.Definitions;
import org.xlbean.definition.ExcelCommentDefinitionLoader;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.util.FileUtil;

/**
 * Reader to read data from excel file.
 *
 * <p>
 * This class is composite of {@link DefinitionLoader} and
 * {@link ExcelDataLoader}. To customize the way for definition, inherit
 * {@link DefinitionLoader} and replace default {@link DefinitionLoader} by
 * using {@link XlBeanReaderBuilder} and so as {@link ExcelDataLoader}.
 * </p>
 * 
 * 
 * @author Kazuya Tanikawa
 */
public class XlBeanReader {

    private DefinitionLoader definitionLoader = new ExcelR1C1DefinitionLoader();
    private ExcelDataLoader dataLoader = new ExcelDataLoader();

    /**
     * Read definition and data from given {@code in} which should be a
     * {@link InputStream} of excel file and return an instance of {@link XlBean}.
     *
     * <p>
     * {@code in} is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     * </p>
     *
     * @param in
     * @return
     */
    public XlBean read(InputStream in) {
        return readContext(in).getXlBean();
    }

    /**
     * Read definition and data from given {@code excelFile} which should be a
     * {@link File} of excel file and return an instance of {@link XlBean}.
     *
     * <p>
     * Excel file is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     * </p>
     * 
     * @param excelFile
     * @return
     */
    public XlBean read(File excelFile) {
        // Copy excel file to on-memory stream to make it readable even if the
        // file is opened.
        return read(FileUtil.copyToInputStream(excelFile));
    }

    /**
     * Read definition from {@code definitionSource} then read data from
     * {@code dataSource} and return an instance of {@link XlBean}.
     * 
     * @param definitionSource
     * @param dataSource
     * @return
     */
    public XlBean read(Object definitionSource, Workbook dataSource) {
        return readContext(definitionSource, dataSource).getXlBean();
    }

    /**
     * Read definition and data from given {@code in} which should be a
     * {@link InputStream} of excel file and return an instance of
     * {@link XlBeanReaderContext} filled with {@link XlBean} and
     * {@link Definitions}.
     * 
     * <p>
     * {@code in} is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     * </p>
     * 
     * @param in
     * @return
     */
    public XlBeanReaderContext readContext(InputStream in) {
        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {
            return readContext(wb, wb);
        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read definition and data from given {@code excelFile} which should be a
     * {@link File} of excel file and return an instance of
     * {@link XlBeanReaderContext} filled with {@link XlBean} and
     * {@link Definitions}.
     *
     * <p>
     * Excel file is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     * </p>
     * 
     * @param excelFile
     * @return
     */
    public XlBeanReaderContext readContext(File excelFile) {
        // Copy excel file to on-memory stream to make it readable even if the
        // file is opened.
        return readContext(FileUtil.copyToInputStream(excelFile));
    }

    /**
     * Read definition from {@code definitionSource} then read data from
     * {@code dataSource} and return an instance of {@link XlBeanReaderContext}
     * filled with {@link XlBean} and {@link Definitions}.
     * 
     * @param definitionSource
     * @param dataSource
     * @return
     */
    public XlBeanReaderContext readContext(Object definitionSource, Workbook dataSource) {
        Definitions definitions = definitionLoader.load(definitionSource);

        XlWorkbook workbook = XlWorkbook.wrap(dataSource);
        XlBean xlBean = this.dataLoader.load(definitions, workbook);

        XlBeanReaderContext context = new XlBeanReaderContext();
        context.setDefinitions(definitions);
        context.setXlBean(xlBean);
        return context;
    }

    /**
     * Builder for {@link XlBeanReader}.
     * 
     * @author tanikawa
     *
     */
    public static class XlBeanReaderBuilder {
        private DefinitionLoader definitionLoader;
        private ExcelDataLoader dataLoader;

        public XlBeanReader build() {
            if (definitionLoader == null) {
                definitionLoader = new ExcelR1C1DefinitionLoader();
            }
            if (dataLoader == null) {
                this.dataLoader = new ExcelDataLoader();
            }
            XlBeanReader reader = new XlBeanReader();
            reader.dataLoader = dataLoader;
            reader.definitionLoader = definitionLoader;
            return reader;
        }

        /**
         * Set DefinitionLoader of the XlBeanReader instance.
         * 
         * @see ExcelR1C1DefinitionLoader
         * @see ExcelCommentDefinitionLoader
         * @see BeanDefinitionLoader
         * 
         * @param definitionLoader
         * @return
         */
        public XlBeanReaderBuilder definitionLoader(DefinitionLoader definitionLoader) {
            this.definitionLoader = definitionLoader;
            return this;
        }

        /**
         * Set ExcelDataLoader of the XlBeanReader instance.
         * 
         * @see ExcelDataLoader
         * 
         * @param dataLoader
         * @return
         */
        public XlBeanReaderBuilder dataLoader(ExcelDataLoader dataLoader) {
            this.dataLoader = dataLoader;
            return this;
        }
    }

}
