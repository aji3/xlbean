package org.xlbean.definition;

/**
 * Super class for all DefinitionLoaders.
 *
 * @param <T>
 * @see ExcelR1C1DefinitionLoader
 * @see ExcelCommentDefinitionLoader
 * @see BeanDefinitionLoader
 * @author Kazuya Tanikawa
 */
public interface DefinitionLoader {

    public Definitions load(Object definitionSource);

}
