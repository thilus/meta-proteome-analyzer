package de.mpa.db.neo4j.properties;

public enum PathwayProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description"),
	PATHWAYID("PathwayID");
	
	PathwayProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
