package de.mpa.graphdb.properties;

public enum EnzymeProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description");
	
	EnzymeProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}