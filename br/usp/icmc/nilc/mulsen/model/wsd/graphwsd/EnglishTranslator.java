package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd;

import java.util.ArrayList;

import nlputil.translate.TranslateMethodException;
import nlputil.translate.Translator;

public class EnglishTranslator extends Translator{

	public EnglishTranslator() {
		super("English - English");
		
	}

	@Override
	public ArrayList<String> translateWord(String word, String tag)
			throws TranslateMethodException {
		
		return translateWord( word );
	}

	@Override
	public ArrayList<String> translateWord(String word)
			throws TranslateMethodException {

		ArrayList<String> translate = new ArrayList<String>();
		translate.add( word );
		return translate;
	}

	@Override
	public String translateText(String text) throws TranslateMethodException {		
		return null;
	}

	@Override
	public void saveCache() throws TranslateMethodException {		
		
	}

	@Override
	public void loadSavedCache() throws TranslateMethodException {	
		
	}

}
