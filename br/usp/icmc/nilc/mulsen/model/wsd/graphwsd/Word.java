package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd;

import java.util.ArrayList;

import br.usp.icmc.nilc.mulsen.model.MorphoTags;

import br.usp.icmc.nilc.mulsen.view.components.Tokens;

import edu.smu.tspell.wordnet.Synset;

public class Word {
	protected String word;
	protected MorphoTags tag;
	protected ArrayList<Synset> synsets;
	protected ArrayList<String> translates;
	protected ArrayList<String> glosa_label;
	protected ArrayList<String> sample_label;
	protected Tokens token;
		
	public Word( String word, MorphoTags tag){		
		this.word = word;
		this.tag = tag;
		this.synsets = new ArrayList<Synset>();
		this.translates = new ArrayList<String>();
		this.glosa_label = new ArrayList<String>();
		this.sample_label = new ArrayList<String>();
		
	}

	public String getWord() {
		return this.word;
	}
	
	@Override
	public String toString() {
		return this.word+"_"+ this.tag;				
	}
	
	public String word(){ return this.word; }
	public ArrayList<Synset> synsets(){ return this.synsets; }
	public ArrayList<String> translates(){ return this.translates; }
	public Tokens token(){ return this.token; }
}

