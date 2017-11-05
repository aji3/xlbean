package org.xlbean.definition;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import org.xlbean.excel.XlCellAddress;

public class TableDefinitionResolver extends DefinitionResolver {

  private static final String JAVA_VARIABLE_PATTERN = "[a-zA-Z_]\\w*";
  private static final String JAVA_LIST_VARIABLE_PATTERN =
      JAVA_VARIABLE_PATTERN + "(\\[[0-9]+\\])?";
  private static final String COMMA_CONNECTED_JAVA_VARIABLE_PATTERN =
      String.format(
          "(%s|%s\\.%s)(\\.%s|%s\\.%s)*",
          JAVA_VARIABLE_PATTERN,
          JAVA_LIST_VARIABLE_PATTERN,
          JAVA_VARIABLE_PATTERN,
          JAVA_VARIABLE_PATTERN,
          JAVA_LIST_VARIABLE_PATTERN,
          JAVA_VARIABLE_PATTERN);
  private static final String TABLE_OR_COLUMN_PATTERN =
      String.format(
          "%s(\\?%s=\\w*(&%s=\\w*)*)?",
          COMMA_CONNECTED_JAVA_VARIABLE_PATTERN, JAVA_VARIABLE_PATTERN, JAVA_VARIABLE_PATTERN);
  private static final Pattern RESOLVABLE_PATTERN =
      Pattern.compile(
          String.format(
              "%s#%s",
              TABLE_OR_COLUMN_PATTERN, TABLE_OR_COLUMN_PATTERN.replaceFirst("\\[a-z", "\\[~a-z")));

  @Override
  public boolean isResolvable(String definitionStr) {
    if (definitionStr == null) {
      return false;
    }
    return RESOLVABLE_PATTERN.matcher(definitionStr).matches();
  }

  @Override
  public Definition resolve(String key, XlCellAddress cell) {
    String[] tableNameAndColumnNameArray =
        Arrays.stream(key.split("#")).map(str -> str.trim()).toArray(size -> new String[size]);

    // Table
    String tableName = parseName(tableNameAndColumnNameArray[0]);
    Map<String, String> tableOptions = parseOptions(tableNameAndColumnNameArray[0]);
    TableDefinition definition = new TableDefinition();
    definition.setOriginalKeyString(tableNameAndColumnNameArray[0]);
    definition.setName(tableName);
    definition.addOptions(tableOptions);

    // Column
    String columnName = parseName(tableNameAndColumnNameArray[1]);
    Map<String, String> columnOptions = parseOptions(tableNameAndColumnNameArray[1]);
    SingleDefinition columnDefinition = new SingleDefinition();
    columnDefinition.setOriginalKeyString(tableNameAndColumnNameArray[1]);
    columnDefinition.setName(columnName);
    columnDefinition.addOptions(columnOptions);
    columnDefinition.setCell(cell);
    definition.addAttribute(columnDefinition);
    definition.addOptions(columnOptions); // Column options are also regarded as table options

    return definition;
  }
}
