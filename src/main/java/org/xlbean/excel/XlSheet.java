package org.xlbean.excel;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.data.ExcelDataLoader;

/**
 * Wrapper class of {@link Sheet}.
 * 
 * @author Kazuya Tanikawa
 *
 */
public class XlSheet {

	private static final Pattern ERROR_MESSAGE_PATTERN = Pattern.compile("Cannot get.*from a ERROR.*");

	private static Logger log = LoggerFactory.getLogger(ExcelDataLoader.class);

    public enum ValueType {
        def, string, date,
    };

    private Sheet sheet;

    /**
     * Cache for performance.
     */
    private FormulaEvaluator evaluator;

    public XlSheet(Sheet sheet) {
        this.sheet = sheet;
        if (sheet != null) {
            Workbook wb = sheet.getWorkbook();
            CreationHelper creationHelper = wb.getCreationHelper();
            this.evaluator = creationHelper.createFormulaEvaluator();
        }
    }

    public String getSheetName() {
        return sheet.getSheetName();
    }

    public String getCellValue(XlCellAddress address) {
        return getCellValue(address, null);
    }

    public String getCellValue(XlCellAddress address, ValueType type) {
        return getCellValue(address.getRow(), address.getColumn(), type);
    }

    public String getCellValue(int row, int col) {
        return getCellValue(row, col, null);
    }

    /**
     * Get value of the cell as it looks.
     * 
     * @param row
     * @param col
     * @return
     */
    public String getCellValue(int row, int col, ValueType type) {
        Cell cell = getCell(row, col);
        if (cell == null) {
            return null;
        } else {
            return getCellValue(cell, type);
        }
    }
    
    /**
     * <p>
     * {@link Cell#getCellTypeEnum} is used because this method is to be
     * replaced by existing method in the future version.
     * </p>
     * 
     * @param cell
     * @return
     */
    public String getCellValue(Cell cell, ValueType type) {
        CellType cellType = null;
        if (CellType.FORMULA == cell.getCellTypeEnum()) {
            // Do not use evaluateInCell, as this method updates a cell
            // when it evaluates the cell so that it is very slow.
            try {
                cellType = evaluator.evaluateFormulaCellEnum(cell);
            } catch (FormulaParseException e) {
                handleException(e, cell);
            }
        } else {
            cellType = cell.getCellTypeEnum();
        }
        if (type == null || ValueType.def == type) {
            return getCellValueAsData(cell, cellType, false);
        } else if (ValueType.string == type) {
            return getCellValueAsPresented(cell);
        } else if (ValueType.date == type) {
            return getCellValueAsData(cell, cellType, true);
        } else {
            return null;
        }
    }

    public String getCellComment(int row, int col) {
        Comment comment = sheet.getCellComment(new CellAddress(row, col));
        if (comment == null) {
            return null;
        } else {
            return comment.getString().toString();
        }
    }
    
    public void setCellValue(XlCellAddress address, String value) {
        setCellValue(address.getRow(), address.getColumn(), value);
    }

    public void setCellValue(int row, int col, String value) {
        setCellValue(row, col, value, null);
    }

    public void setCellValue(int row, int col, String value, ValueType type) {
        Cell cell = getCell(row, col, true);
        setCellValue(cell, value, type);
    }

    public void setCellValue(Cell cell, String value, ValueType type) {
        if (type == null || ValueType.def.equals(type)) {
            setCellValueAsData(cell, value, false);
        } else if (ValueType.date.equals(type)) {
            setCellValueAsData(cell, value, true);
        } else if (ValueType.string.equals(type)) {
            setCellValueAsString(cell, value);
        } else {
            setCellValueAsData(cell, value, false);
        }
    }
    
    protected void setCellValueAsString(Cell cell, String value) {
        if (cell == null || value == null) {
            return;
        }
        cell.setCellValue(value);
    }

    protected void setCellValueAsData(Cell cell, String value, boolean forceDate) {
        if (cell == null || value == null) {
            return;
        }
        if (forceDate) {
            cell.setCellValue(parseDateTimeValue(value));
        } else {
            String dataFormatString = cell.getCellStyle().getDataFormatString();
            if (isStringValue(dataFormatString)) {
                cell.setCellValue(value);
            } else if (isDateTimeValue(cell.getCellStyle().getDataFormatString())) {
                cell.setCellValue(parseDateTimeValue(value));
            } else {
                try {
                    cell.setCellValue(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    private static final String DATETIMEFORMAT_CHARS = "ymdhs";
    private boolean isDateTimeValue(String dataFormatString) {
        if (dataFormatString == null) {
            return false;
        }
        return dataFormatString.replaceAll("\\[.*\\]", "").chars().anyMatch(c -> DATETIMEFORMAT_CHARS.indexOf(c) >= 0);
    }

    private boolean isTimeValue(String dataFormatString) {
        if (dataFormatString == null) {
            return false;
        }
        if (dataFormatString.contains("y") || dataFormatString.contains("d")) {
            return false;
        }
        return dataFormatString.matches(".*h.*m*.*");
    }
    private boolean isStringValue(String dataFormatString) {
        if (dataFormatString == null) {
            return false;
        }
        return "@".equals(dataFormatString);
    }

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * Format excel date value which is a differential number from 1900/1/0, to
     * date-time string(yyyy-MM-dd'T'HH:mm:ss.SSS).
     * 
     * @param value
     * @return
     */
    protected String formatDateTimeValue(double value) {
        int days = (int) value;
        long time = (long) (Math.round((value - days) * 86400 * 1000) * 1000000);
        LocalDateTime dateTime = LocalDateTime.of(1899, 12, 30, 0, 0, 0, 0).plusDays((int) value).plusNanos(time);

        // returns yyyy-MM-dd'T'HH:mm:ss.SSS
        return DATETIME_FORMAT.format(dateTime);
    }

    /**
     * Format excel date value which is a differential number from 1900/1/0, to
     * time string(HH:mm:ss.SSS) depends on the {@code value}.
     * 
     * @param value
     * @return
     */
    protected String formatTimeValue(double value) {
        int days = (int) value;
        long time = (long) (Math.round((value - days) * 86400 * 1000) * 1000000);
        LocalDateTime dateTime = LocalDateTime.of(1899, 12, 30, 0, 0, 0, 0).plusDays((int) value).plusNanos(time);
        // returns HH:mm:ss.SSS
        return TIME_FORMAT.format(dateTime);
    }

    protected double parseDateTimeValue(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return 0;
        }
        if (dateTime.length() == 12) {
            LocalTime baseTime = LocalTime.of(0, 0, 0, 0);
            LocalTime time = LocalTime.parse(dateTime, TIME_FORMAT);
            Duration duration = Duration.between(baseTime, time);
            return duration.toNanos() / 1000000000d / 86400d;
        } else if (dateTime.length() == 23) {
            LocalDateTime baseDate = LocalDateTime.of(1899, 12, 30, 0, 0, 0, 0);
            LocalDateTime date = LocalDateTime.parse(dateTime, DATETIME_FORMAT);
            Duration duration = Duration.between(baseDate, date);
            return duration.toNanos() / 1000000000d / 86400d;
        } else {
            throw new IllegalArgumentException(String.format("Illegal date time format. [%s]", dateTime));
        }
    }

    protected String getCellValueAsData(Cell cell, boolean forceDate) {
    	return getCellValueAsData(cell, cell.getCellTypeEnum(), forceDate);
    }
    /**
     * 
     * 
     * @param cell
     * @param type
     * @param forceDate
     * @return
     */
    protected String getCellValueAsData(Cell cell, CellType type, boolean forceDate) {
        if (CellType.BLANK.equals(type)) {
            return null;
        }
        try {
            String dataFormatString = cell.getCellStyle().getDataFormatString();
            if (forceDate || isDateTimeValue(dataFormatString)) {
                if (isTimeValue(dataFormatString)) {
                    return formatTimeValue(cell.getNumericCellValue());
                } else {
                    return formatDateTimeValue(cell.getNumericCellValue());
                }
            }
            if (CellType.BOOLEAN.equals(type)) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (CellType.STRING.equals(type)) {
            	return cell.getRichStringCellValue().getString();
            } else {
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            }
        } catch (IllegalStateException e) {
            try {
                return cell.getRichStringCellValue().getString();
            } catch (IllegalStateException e2) {
                handleException(e2, cell);
            }
            return null;
        }
    }
    
    private void handleException(Exception e, Cell cell) {
        if (e instanceof FormulaParseException 
                || ERROR_MESSAGE_PATTERN.matcher(e.getMessage()).matches()) {
            // Handle exception with specific message
            log.warn("Excel error exists at (sheet:{}, row:{}, column:{})", 
                    cell.getSheet().getSheetName(),
                    cell.getRowIndex(), 
                    cell.getColumnIndex());
        } else {
            throw new RuntimeException(e);
        }
    }

    protected String getCellValueAsPresented(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell, evaluator);
    }

    private Cell getCell(int row, int col) {
        return getCell(row, col, false);
    }
    
    private Cell getCell(int row, int col, boolean create) {
        Row r = sheet.getRow(row);
        if (r == null) {
            if (create) {
                r = sheet.createRow(row);
            } else {
                return null;
            }
        }
        Cell cell = r.getCell(col);
        if (cell == null) {
            if (create) {
                return r.createCell(col);
            } else {
                return null;
            }
        }
        return cell;
    }

    public int getMaxRow() {
        return sheet.getLastRowNum();
    }

    public int getMaxColumn() {
        if (sheet.getRow(0) == null) {
            return 0;
        }
        return sheet.getRow(0).getLastCellNum();
    }
    
    public int getMaxColumn(int row) {
        if (sheet.getRow(row) == null) {
            return 0;
        }
        return sheet.getRow(row).getLastCellNum();
    }
}
