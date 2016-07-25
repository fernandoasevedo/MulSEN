package br.usp.icmc.nilc.mulsen.model;

public enum Language {
	PORTUGUESE("PORTUGUESE"), SPANISH ("SPANISH"), ENGLISH("ENGLISH");
	private String description;	
	
	
	Language( String language ){
		this.description = language;
	}
	
	public static Language convert( String sigla ){
	
		if( sigla.compareToIgnoreCase("pt") == 0)
			return PORTUGUESE;
		
		if( sigla.compareToIgnoreCase("en") == 0)
			return ENGLISH;
		
		if( sigla.compareToIgnoreCase("es") == 0)
			return SPANISH;
		
		return null;
	}
	
	@Override
	public String toString() {
		return this.description;
	}
}
