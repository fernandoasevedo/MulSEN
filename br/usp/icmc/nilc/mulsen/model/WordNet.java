package br.usp.icmc.nilc.mulsen.model;

import java.io.File;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.SynsetFactory;
import edu.smu.tspell.wordnet.impl.file.SynsetPointer;

public class WordNet {

	public static Synset[] getSynonyms( String word ){
		
		word = word.replace(" ", "_");
		//necessário para encontrar o dicionário da wordnet
		System.setProperty("wordnet.database.dir", "lib"+File.separator+"WordNet-3.0"+File.separator+"dict"+File.separator);
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Synset[] synsets = database.getSynsets( word, SynsetType.NOUN );
		
		return synsets;
	}
	
	public static Synset getSynset(int id) {		
		System.setProperty("wordnet.database.dir", "lib"+File.separator+"WordNet-3.0"+File.separator+"dict"+File.separator);
		
		SynsetFactory factory = SynsetFactory.getInstance();
		return factory.getSynset( new SynsetPointer( SynsetType.NOUN, id ) );
	}
}
