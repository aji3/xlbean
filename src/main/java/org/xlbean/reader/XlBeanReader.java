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
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelCommentDefinitionLoader;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.util.FileUtil;

/**
 * Reader to read data from excel file and return XlBean.
 *
 * <p>
 *
 * @author Kazuya Tanikawa
 */
public class XlBeanReader {

    private DefinitionLoader<?> definitionLoader = new ExcelR1C1DefinitionLoader();
    private ExcelDataLoader dataLoader = new ExcelDataLoader();

    /**
     * Read definition and data from given {@code in} which is a {@link InputStream}
     * of excel file.
     *
     * <p>
     * {@code in} is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     *
     * @param in
     * @return
     */
    public XlBean read(InputStream in) {

        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {

            return read(wb, wb);

        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read definition and data from given {@code excelFile} which is a {@link File}
     * of excel file.
     *
     * <p>
     * Excel file is copied to on-memory stream before opening the file, so that the
     * file can be read even if it is opened.
     *
     * @param excelFile
     * @return
     */
    public XlBean read(File excelFile) {
        // Copy excel file to on-memory stream to make it readable even if the
        // file is opened.
        return read(FileUtil.copyToInputStream(excelFile));
    }

    public XlBean read(Object definitionSource, Workbook dataSource) {

        definitionLoader.initialize(definitionSource);
        DefinitionRepository definitions = definitionLoader.load();

        XlWorkbook workbook = XlWorkbook.wrap(dataSource);
        return this.dataLoader.load(definitions, workbook);
    }

    public static class XlBeanReaderBuilder {
        private DefinitionLoader<?> definitionLoader;
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
        public XlBeanReaderBuilder definitionLoader(DefinitionLoader<?> definitionLoader) {
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
