package org.xlbean.definition;

/**
 * Super class for all DefinitionLoaders.
 * 
 * @param <T>
 * 
 * @see ExcelR1C1DefinitionLoader
 * @see ExcelCommentDefinitionLoader
 * @see BeanDefinitionLoader
 * 
 * @author Kazuya Tanikawa
 *
 */
public abstract class DefinitionLoader<T> {

    abstract public DefinitionRepository load();

    abstract public void initialize(Object definitionSource);
    
	private T definitionSource;
	
	protected T getDefinitionSource() {
		return definitionSource;
	}
	
	protected void setDefinitionSource(T definitionSource) {
	    this.definitionSource = definitionSource;
	}
	

}