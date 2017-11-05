package org.xlbean.data;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.DefinitionRepository.DefinitionValidationContext;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.util.XlBeanFactory;

/**
 * Load data from {@code workbook} by {@code definitions}.
 *
 * <p>First validate all the {@link Definition}
 *
 * @author Kazuya Tanikawa
 */
public class ExcelDataLoader {

  private static Logger log = LoggerFactory.getLogger(ExcelDataLoader.class);

  public XlBean load(DefinitionRepository definitions, XlWorkbook workbook) {
    definitions.validate(workbook);

    DefinitionValidationContext validationContext = definitions.validateAll();
    if (validationContext.isError()) {
      log.warn(
          "Skip loading due to definition error: {}",
          String.join(
              ", ",
              validationContext
                  .getErrorDefinitions()
                  .stream()
                  .map(def -> def.getName())
                  .collect(Collectors.toList())));
      return null;
    }

    XlBean retBean = XlBeanFactory.getInstance().createBean();
    definitions.forEach(
        definition -> {
          long now = System.currentTimeMillis();
          log.debug("Start loading data: {}", definition.getName());
          getValueLoader(definition).load(retBean);
          log.info(
              "Loaded data: {} [{} msec]",
              definition.getName(),
              (System.currentTimeMillis() - now));
        });
    return retBean;
  }

  protected ValueLoader<?> getValueLoader(Definition definition) {
    if (definition instanceof SingleDefinition) {
      return new SingleValueLoader((SingleDefinition) definition);
    } else {
      return new TableValueLoader((TableDefinition) definition);
    }
  }
}
