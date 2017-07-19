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
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.util.Util;

/**
 * Reader to read data from excel file and return XlBean.
 * 
 * <p>
 * 
 * </p>
 * 
 * @author Kazuya Tanikawa
 *
 */
public class XlBeanReader {

    private DefinitionLoader<?> definitionLoader;
    private ExcelDataLoader dataLoader;
    
    public XlBeanReader() {
        this(null, null);
    }
    
    public XlBeanReader(DefinitionLoader<?> definitionLoader) {
        this(definitionLoader, null);
    }
    
    public XlBeanReader(DefinitionLoader<?> definitionLoader, ExcelDataLoader dataLoader) {
        if (definitionLoader == null) {
            this.definitionLoader = new ExcelR1C1DefinitionLoader();
        } else {
            this.definitionLoader = definitionLoader;
        }
        if (dataLoader == null) {
            this.dataLoader = new ExcelDataLoader();
        } else {
            this.dataLoader = dataLoader;
        }
    }
    /**
     * Read definition and data from given {@code in} which is a
     * {@link InputStream} of excel file.
     * 
     * <p>
     * {@code in} is copied to on-memory stream before opening the file, so that
     * the file can be read even if it is opened.
     * </p>
     * 
     * @param in
     * @return
     */
    public XlBean read(InputStream in) {

        try (Workbook wb = WorkbookFactory.create(Util.copyToInputStream(in))) {

            return read(wb, wb);

        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read definition and data from given {@code excelFile} which is a
     * {@link File} of excel file.
     * 
     * <p>
     * Excel file is copied to on-memory stream before opening the file, so that
     * the file can be read even if it is opened.
     * </p>
     * 
     * @param excelFile
     * @return
     */
    public XlBean read(File excelFile) {
        // Copy excel file to on-memory stream to make it readable even if the
        // file is opened.
        return read(Util.copyToInputStream(excelFile));
    }

    public XlBean read(Object definitionSource, Workbook dataSource) {

        definitionLoader.initialize(definitionSource);
        DefinitionRepository definitions = definitionLoader.load();

        XlWorkbook workbook = XlWorkbook.wrap(dataSource);
        return this.dataLoader.load(definitions, workbook);
    }

}
