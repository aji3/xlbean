package org.xlbean.definition;

import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.jparsec.error.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.definition.parser.DefinitionParser;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlSheet;
import org.xlbean.excel.XlWorkbook;

/**
 * Read definitions from row R1 and colum C1 of excel sheets.
 *
 * <p>
 *
 * @author Kazuya Tanikawa
 */
public class ExcelR1C1DefinitionLoader extends DefinitionLoader<XlWorkbook> {

    private static Logger log = LoggerFactory.getLogger(ExcelR1C1DefinitionLoader.class);

    private static final List<DefinitionBuilder> DEFAULT_DEFINITION_BUILDERS = Arrays.asList(
        new SingleDefinitionResolver(),
        new TableDefinitionResolver());

    @Override
    public void initialize(Object definitionSource) {
        if (definitionSource == null || !(definitionSource instanceof Workbook)) {
            throw new IllegalArgumentException(
                String.format("Definition source should be an instance of %s", Workbook.class.getName()));
        }
        setDefinitionSource(XlWorkbook.wrap((Workbook) definitionSource));
    }

    /**
     * Scan all the sheets and read definition from row 1 and column 1.
     *
     * @param context
     */
    @Override
    public DefinitionRepository load() {
        XlWorkbook workbook = getDefinitionSource();
        DefinitionRepository definitions = new DefinitionRepository();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XlSheet sheet = workbook.getSheetAt(i);
            if (!isProcessingTargetSheet(sheet)) {
                continue;
            }
            long now = System.currentTimeMillis();
            log.debug("Start loading table definition from sheet {}", sheet.getSheetName());
            definitions.merge(readDefinition(sheet));
            log.info(
                "Loaded table definition from sheet {} [{} msec]",
                sheet.getSheetName(),
                (System.currentTimeMillis() - now));
        }
        return definitions;
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

    protected DefinitionRepository readDefinition(XlSheet sheet) {
        DefinitionRepository definitions = new DefinitionRepository();
        int maxRow = sheet.getMaxRow();
        int maxCol = sheet.getMaxColumn();
        if (Math.min(maxRow, maxCol) == 0) {
            return definitions;
        }

        // read column definition
        for (int col = 1; col <= maxCol; col++) {
            resolveDefinition(definitions, sheet, col, true);
        }

        // read row definition
        for (int row = 1; row <= maxRow; row++) {
            resolveDefinition(definitions, sheet, row, false);
        }
        return definitions;
    }

    private void resolveDefinition(
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
