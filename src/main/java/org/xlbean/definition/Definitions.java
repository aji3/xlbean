package org.xlbean.definition;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlSheet;
import org.xlbean.excel.XlWorkbook;

/**
 * Repository to handle {@link Definition} instances.
 *
 * @author Kazuya Tanikawa
 */
public class Definitions {

    private static Logger log = LoggerFactory.getLogger(BeanDefinitionLoader.class);

    private List<Definition> definitions = new ArrayList<>();

    private XlWorkbook workbook;

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
        Definition duplicatedDefinition = definitions
            .stream()
            .filter(d -> d.getClass().equals(definition.getClass()))
            .filter(d -> d.getDefinitionId().equals(definition.getDefinitionId()))
            .findFirst()
            .orElse(null);
        if (duplicatedDefinition == null) {
            definitions.add(definition);
        } else {
            duplicatedDefinition.merge(definition);
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
                log.warn(
                    "Invalid definition [{}] ({})",
                    definition.getName(),
                    definition.getClass().getName());
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
        return definitions
            .stream()
            .collect(
                Collectors.groupingBy(
                    Definition::getName,
                    Collectors.reducing(null, (i, t) -> t)));

    }

    /**
     * Add all the instances in the {@code anotherRepository} to this repository.
     *
     * @param anotherRepository
     */
    public void merge(Definitions anotherRepository) {
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

    public Stream<Definition> stream() {
        return definitions.stream();
    }

    public void activate(XlWorkbook workbook) {
        this.workbook = workbook;
        definitions.forEach(this::activateOne);
    }

    private void activateOne(Definition definition) {
        XlSheet sheet = workbook.getSheet(definition.getSheetName());
        if (sheet == null) {
            log.warn(
                "No sheet named \"{}\" was found for definition \"{}\". ({})",
                definition.getSheetName(),
                definition.getName(),
                definition.getClass().getName());
            return;
        }
        definition.setSheet(sheet);
    }

    public void write(OutputStream os) throws IOException {
        workbook.write(os);
    }

    @Override
    public String toString() {
        return "DefinitionRepository [definitions=" + definitions + "]";
    }
}
