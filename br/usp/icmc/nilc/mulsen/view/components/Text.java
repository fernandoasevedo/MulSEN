package br.usp.icmc.nilc.mulsen.view.components;

import java.io.File;
import java.util.LinkedList;

import br.usp.icmc.nilc.mulsen.model.Language;


public class Text {
	private String name_file;
	private File directory;
	private LinkedList<Tokens> tokens;
	
	//Indica o idioma do texto
	private Language text_language; 
	
	public Text( String name_file, File directory ){
		this.name_file = name_file;
		this.directory = directory;
		this.tokens = new LinkedList<Tokens>();		
		
		//Verificando o idioma pelo padrão do título do texto
		String split_name[] = name_file.split("_");
		if( split_name[ 0 ].compareToIgnoreCase("pt") == 0)
			text_language = Language.PORTUGUESE;
		else
			if( split_name[ 0 ].compareToIgnoreCase("es") == 0)
				text_language = Language.SPANISH;
			else
				text_language = Language.ENGLISH;
		}

	public Language getLanguage(){
		return this.text_language;
	}
	
	public String getNameFile() {
		return name_file;
	}

	public void setNameFile(String name_file) {
		this.name_file = name_file;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public LinkedList<Tokens> getTokens() {
		return tokens;
	}

	public void addToken( Tokens token) {
		this.tokens.add( token );
	}
}
