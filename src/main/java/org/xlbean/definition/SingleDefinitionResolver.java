package org.xlbean.definition;

import java.util.regex.Pattern;

import org.xlbean.excel.XlCellAddress;

public class SingleDefinitionResolver extends DefinitionResolver {

  @Override
  public boolean isResolvable(String cellValue) {
    if (cellValue == null) {
      return false;
    }
    return Pattern.matches(
        "[a-zA-Z_]\\w*(\\.[a-zA-Z_]\\w*)*(\\?[a-zA-Z_]\\w*=\\w*(&[a-zA-Z_]\\w*=\\w*)*)?",
        cellValue);
  }

  @Override
  public Definition resolve(String key, XlCellAddress cell) {
    SingleDefinition definition = new SingleDefinition();
    String name = key;
    if (key.contains("?")) {
      name = key.substring(0, key.indexOf('?'));
    }
    definition.setName(name);
    definition.setOriginalKeyString(key);
    definition.setCell(cell);
    definition.addOptions(parseOptions(key));
    return definition;
  }
}
