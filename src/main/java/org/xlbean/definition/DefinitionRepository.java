package org.xlbean.definition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlSheet;
import org.xlbean.excel.XlWorkbook;

/**
 * Repository to handle {@link Definition} instances.
 *
 * @author Kazuya Tanikawa
 */
public class DefinitionRepository {

    private static Logger log = LoggerFactory.getLogger(DefinitionRepository.class);

    private List<Definition> definitions = new ArrayList<>();

    /**
     * Add {@link Definition} to this repository.
     *
     * <p>
     * It compares values of {@link Definition#getDefinitionId()} of existing
     * definition instances with the given {@code definition}. If the same
     * {@code definitionId} already exist, then it will merge them. Otherwise, it
     * will simply add the instance to this repository.
     *
     * @param definition
     */
    public void addDefinition(Definition definition) {
        Definition dupulicatedDefinition = null;
        for (Definition d : definitions) {
            if (d.getDefinitionId().equals(definition.getDefinitionId())) {
                dupulicatedDefinition = d;
                break;
            }
        }
        if (dupulicatedDefinition == null) {
            definitions.add(definition);
        } else {
            dupulicatedDefinition.merge(definition);
        }
    }

    /**
     * Calls {@link Definition#validate()} of all the {@link Definition} instances
     * in this repository, then set the corresponding excel sheet instance to each
     * definitions.
     *
     * @param workbook
     */
    public void validate(XlWorkbook workbook) {
        for (Definition definition : definitions) {
            // Check if the definition is valid
            if (!definition.validate()) {
                log.warn(
                    "Invalid definition [{}] ({})",
                    definition.getName(),
                    definition.getClass().getName());
                continue;
            }

            // Link the definition with excel sheet
            XlSheet sheet = workbook.getSheet(definition.getSheetName());
            if (sheet == null) {
                log.warn(
                    "No sheet named \"{}\" was found for definition \"{}\". ({})",
                    definition.getSheetName(),
                    definition.getName(),
                    definition.getClass().getName());
                continue;
            }
            definition.setSheet(sheet);
        }
    }

    /**
     * Returns true when any of the definitions in this repository is true.
     *
     * @return
     */
    public DefinitionValidationContext validateAll() {
        DefinitionValidationContext validationContext = new DefinitionValidationContext();
        for (Definition definition : definitions) {
            if (!definition.validate()) {
                validationContext.addErrorDefinition(definition);
            }
        }
        return validationContext;
    }

    public static class DefinitionValidationContext {
        private List<Definition> errorDefinitions = new ArrayList<>();

        public void addErrorDefinition(Definition definition) {
            errorDefinitions.add(definition);
        }

        public boolean isError() {
            return !errorDefinitions.isEmpty();
        }

        public List<Definition> getErrorDefinitions() {
            return errorDefinitions;
        }
    }

    public Map<String, Definition> toMap() {
        Map<String, Definition> retMap = new HashMap<>();
        definitions.forEach(
            it ->
            {
                Definition registered = retMap.get(it.getName());
                if (registered == null) {
                    retMap.put(it.getName(), it);
                }
            });
        return retMap;
    }

    /**
     * Add all the instances in the {@code anotherRepository} to this repository.
     *
     * @param anotherRepository
     */
    public void merge(DefinitionRepository anotherRepository) {
        anotherRepository.forEach(this::addDefinition);
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void sort(Comparator<? super Definition> c) {
        definitions.sort(c);
    }

    public void forEach(Consumer<? super Definition> c) {
        definitions.forEach(c);
    }

    @Override
    public String toString() {
        return "DefinitionRepository [definitions=" + definitions + "]";
    }
}
