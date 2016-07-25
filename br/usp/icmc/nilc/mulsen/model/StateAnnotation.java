package br.usp.icmc.nilc.mulsen.model;

public enum StateAnnotation {
	/**Palavra ainda não anotada*/
	NO_ANNOTED,
	
	/**Substantivo não anotado*/
	NOUN_NO_ANNOTED,
	
	/**Um Token já anotado*/
	ANNOTED,
	
	/**Um Token previamente anotado, esperando aprovação do anotador*/
	PREV_ANNOTED,
	
	/**Tokens que não devem ser anotados*/
	NO_ANNOTE; 
}
