package org.xlbean.definition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlSheet;
import org.xlbean.excel.XlWorkbook;

public class ExcelCommentDefinitionLoader extends ExcelR1C1DefinitionLoader {

    private static Logger log = LoggerFactory.getLogger(ExcelR1C1DefinitionLoader.class);

    @Override
    public void initialize(Object definitionSource) {
        if (definitionSource == null || !(definitionSource instanceof Workbook)) {
            throw new IllegalArgumentException(
                String.format("Definition source should be an instance of %s", Workbook.class.getName()));
        }
        setDefinitionSource(XlWorkbook.wrap((Workbook) definitionSource));
    }

    private static final List<DefinitionResolver> EXCEL_COMMENT_DEFINITION_RESOLVERS = Arrays.asList(
        new SingleDefinitionResolver(),
        new TableDefinitionResolver() {
            @Override
            public Definition resolve(String key, XlCellAddress cell) {
                TableDefinition definition = (TableDefinition) super.resolve(key, cell.clone());
                SingleDefinition columnDefinition = new SingleDefinition();
                columnDefinition.setOriginalKeyString("~");
                columnDefinition.setName("~");
                columnDefinition.setCell(cell);
                definition.addAttribute(columnDefinition);
                return definition;
            }
        });

    @Override
    protected java.util.List<DefinitionResolver> getDefinitionResolvers() {
        return EXCEL_COMMENT_DEFINITION_RESOLVERS;
    }

    protected boolean isProcessingTargetSheet(XlSheet sheet) {
        return DefinitionConstants.TARGET_SHEET_MARK.equals(sheet.getCellComment(0, 0));
    }

    @Override
    protected DefinitionRepository readDefinition(XlSheet sheet) {
        DefinitionRepository definitions = new DefinitionRepository();
        int maxRow = sheet.getMaxRow();
        int maxCol = sheet.getMaxColumn();
        if (Math.min(maxRow, maxCol) == 0) {
            return definitions;
        }

        // read column definition
        for (int row = 0; row <= maxRow; row++) {
            for (int col = 0; col <= sheet.getMaxColumn(row); col++) {
                resolveDefinition(definitions, sheet, row, col);
            }
        }

        return definitions;
    }

    private void resolveDefinition(
            DefinitionRepository definitions, XlSheet sheet, int row, int col) {
        String value = sheet.getCellComment(row, col);
        if (value == null) {
            return;
        }
        List<String> splittedDefinitionStr = Arrays.stream(value.split(",")).map(str -> str.trim()).collect(
            Collectors.toList());
        for (String definitionStr : splittedDefinitionStr) {
            DefinitionResolver resolver = getDefinitionResolver(definitionStr);
            if (resolver == null) {
                log.warn("Invalid definition: {}", definitionStr);
            } else {
                Definition newDefinition = resolver.resolve(
                    definitionStr,
                    new XlCellAddress.Builder().row(row).column(col).build());
                newDefinition.setSheetName(sheet.getSheetName());

                definitions.addDefinition(newDefinition);
            }
        }
    }
}
