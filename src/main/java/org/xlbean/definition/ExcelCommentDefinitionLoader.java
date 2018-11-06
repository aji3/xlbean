package org.xlbean.definition;

import java.util.Arrays;
import java.util.List;

import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlSheet;

public class ExcelCommentDefinitionLoader extends ExcelR1C1DefinitionLoader {

    private static final List<DefinitionBuilder> EXCEL_COMMENT_DEFINITION_RESOLVERS = Arrays.asList(
        new SingleDefinitionResolver(),
        new TableDefinitionResolver() {
            @Override
            public Definition build(Object parsedObject, XlCellAddress cell) {
                TableDefinition definition = (TableDefinition) super.build(parsedObject, cell.clone());
                SingleDefinition columnDefinition = new SingleDefinition();
                columnDefinition.setName("~");
                columnDefinition.setCell(cell);
                definition.addAttribute(columnDefinition);
                return definition;
            }
        });

    @Override
    protected List<DefinitionBuilder> getDefinitionBuilders() {
        return EXCEL_COMMENT_DEFINITION_RESOLVERS;
    }

    protected boolean isProcessingTargetSheet(XlSheet sheet) {
        return DefinitionConstants.TARGET_SHEET_MARK.equals(sheet.getCellComment(0, 0));
    }

    @Override
    protected DefinitionRepository readAllSheetDefinition(XlSheet sheet) {
        DefinitionRepository definitions = new DefinitionRepository();
        int maxRow = sheet.getMaxRow();
        int maxCol = sheet.getMaxColumn();
        if (Math.min(maxRow, maxCol) == 0) {
            return definitions;
        }

        // read column definition
        for (int row = 0; row <= maxRow; row++) {
            for (int col = 0; col <= sheet.getMaxColumn(row); col++) {
                readCellDefinition(definitions, sheet, row, col);
            }
        }

        return definitions;
    }

    private void readCellDefinition(DefinitionRepository definitions, XlSheet sheet, int row, int col) {
        String value = sheet.getCellComment(row, col);
        if (value == null) {
            return;
        }

        Arrays
            .stream(value.split(","))
            .map(str -> str.trim())
            .map(this::parse)
            .filter(elem -> elem != null)
            .map(elem -> build(elem, row, col, sheet.getSheetName()))
            .forEach(definitions::addDefinition);

    }

    private Definition build(Object parsedDefinition, int row, int col, String sheetName) {
        DefinitionBuilder definitionBuilder = getDefinitionBuilder(parsedDefinition);
        Definition newDefinition = definitionBuilder.build(
            parsedDefinition,
            new XlCellAddress.Builder().row(row).column(col).build());

        newDefinition.setSheetName(sheetName);

        return newDefinition;
    }

}
