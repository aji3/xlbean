package org.xlbean.definition;

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
