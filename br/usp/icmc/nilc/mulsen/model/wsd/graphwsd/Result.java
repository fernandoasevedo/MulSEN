package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd;

import edu.smu.tspell.wordnet.Synset;

public class Result {
	protected Word word;
	protected Synset synset;
	
	public Result(Word word, Synset synset) {
		this.word = word;
		this.synset = synset;
	}
	
	public Word word(){ return this.word; }
	
	public Synset synset(){ return this.synset; }
}
