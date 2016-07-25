package br.usp.icmc.nilc.mulsen.model;

//TODO talvez possamos utilizar os SynsetType 

public enum MorphoTags {
	//Uma palavra com tag morfo-sintática diferente
	NONE("Nenhuma etiquetada"), 
	
	//Subtantivo
	NOUN("Substantivos"),
	
	 //Verbo
	VERB("Verbos"),
	
	 //Advérbio
	ADVERB("Advérbio"),
	
	 //Adjetivo
	ADJECTIVE("Adjetivo"),
	
	//Símbolos, números, etc.
	OTHER("Outra");
	
	private String description;	
	
	
	MorphoTags( String description ){
		this.description = description;
	}
	
	/**
	 * Converte o valor apresentado ao usuário para um valor de tag
	 * 
	 * @param str Uma string passado pelo usuário
	 */
	public static MorphoTags strToTag( String str ){
		
		if( str.compareToIgnoreCase( "Nenhuma etiquetada") == 0 )
			return NONE;
		
		if( str.compareToIgnoreCase( "Substantivos") == 0 )
			return NOUN;
		
		if( str.compareToIgnoreCase( "Verbos") == 0 )
			return VERB;
		
		if( str.compareToIgnoreCase( "Advérbio") == 0 )
			return ADVERB;
		
		if( str.compareToIgnoreCase( "Adjetivo") == 0 )
			return ADJECTIVE;
		
		if( str.compareToIgnoreCase( "Outra") == 0 )
			return OTHER;
		
		return null;
	}
	
	/**
	 * Mapeia uma tag do mxpost para e enumeração {@link MorphoTags}
	 * 
	 * @param tag uma String representando a tag etiquetada por meio do mxpost
	 * 
	 * @return Uma enumeração correspondente a tag passada ou NONE caso não encontrada
	 */
	public static MorphoTags mxpostMap( String tag ){
	
		if ( tag.compareTo("N") == 0) return MorphoTags.NOUN;
	
		if ( tag.compareTo("VERB") == 0 ) return	MorphoTags.VERB;
	
		if ( tag.compareTo("ADJ") == 0 ) return MorphoTags.ADJECTIVE;
	
		if ( tag.compareTo("ADV") == 0 ) return MorphoTags.ADVERB;		
	
		//Sinais de pontuação ou números não devem ser anotados
		if( !Character.isLetterOrDigit( tag.charAt( 0 ) ) || tag.compareTo("NUM") == 0)
			return MorphoTags.OTHER;
	
		return NONE;
	}
	
	/**
	 * Mapeia uma tag do treetagger para e enumeração {@link MorphoTags}
	 * 
	 * @param tag uma String representando a tag etiquetada por meio do mxpost
	 * 
	 * @return Uma enumeração correspondente a tag passada ou NONE caso não encontrada
	 */
	public static MorphoTags treetaggerMap( String tag ){
		
		/**
		 * TAGS do inglês encontradas em 
		 * :<a href='http://www.ims.uni-stuttgart.de/projekte/corplex/TreeTagger/Penn-Treebank-Tagset.pdf'>http://www.ims.uni-stuttgart.de/projekte/corplex/TreeTagger/Penn-Treebank-Tagset.pdf</a>
		 * */		
		
		if( tag.startsWith("NN") ) return NOUN;
		
		if( tag.startsWith("VP") ) return VERB;
		
		if ( tag.startsWith("JJ") ) return ADJECTIVE;
		
		if ( tag.startsWith("RB")) return ADVERB;
		
		//Símbolos
		if ( tag.compareTo("SYM") == 0 || tag.compareTo("QT")==0 ||
				tag.compareTo("DOTS") == 0 || tag.compareTo("COLON") == 0 || 
				tag.compareTo("CM") == 0 || tag.compareTo("BACKSLASH")==0 ) return OTHER;
		
		/**
		 * TAGS do espanhol encontradas em 
		 * :<a href='ftp://ftp.ims.uni-stuttgart.de/pub/corpora/spanish-tagset.txt'>ftp://ftp.ims.uni-stuttgart.de/pub/corpora/spanish-tagset.txt</a>
		 * 
		 * Obs.: Nenhuma das tags do espanhol respeitam as restriçõs anteriores do inglês
		 */
		if( tag.startsWith("NM") ) return NOUN;
		
		if( tag.startsWith("V") ) return VERB;
		
		if ( tag.compareTo("ADJ") == 0) return ADJECTIVE;
		
		if ( tag.compareTo("ADV") == 0 ) return ADVERB;
		
		return NONE;
	}
	
	
	@Override
	public String toString() {
		return this.description;
	}
}
