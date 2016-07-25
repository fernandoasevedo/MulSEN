package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.Word;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.build.edgeBuild.WeightEdge;
import br.usp.icmc.nilc.mulsen.model.MorphoTags;
import br.usp.icmc.nilc.mulsen.model.StateAnnotation;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.build.Build;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.build.edgeBuild.EdgeBuilder;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Edge;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Graph;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import br.usp.icmc.nilc.mulsen.view.components.Tokens;


import nlputil.Tokenizer;
import nlputil.stoplist.StopListEn;
import nlputil.translate.Translator;
import nlputil.translate.wordreference.WordReferenceTranslate;
import nlputil.wordnet.WordNet;

public class GraphWSD {
	private Graph<Word, Integer, Integer, Integer> graph;
	//private Translator translate;
	private StopListEn en_stoplist;
	

	public GraphWSD( ){
		this.en_stoplist = new StopListEn();
	}
	
	
	/**
	 * Algoritmo de pré-processamento do texto, que consiste em:
	 * <ul>
	 * 	<li>Tokenização</li>
	 *  <li>Etiquetagem morfossintática</li>
	 *  <li>Remoção de stop-words e palavras cujo etiquetas não são aceitas na WordNet</li>
	 *  <li>Recuperação de informação para construção de rótulos das palavras (tradução, synsets)</li>
	 * </ul>
	 * 
	 * @param input Texto de entrada do algorimto
	 * @return Uma {@link ArrayList} contendo {@link Words} que encapsulam as palavras pré-processadas
	 * @throws Exception Este método faz uso de alguns recursos em arquivo. Assim, podem ser lançadas exceções na manipulação dos mesmos
	 */
	private ArrayList<Word> preProcessing( LinkedList<Tokens> input, Translator translate ) throws Exception{
		translate.loadSavedCache();		
		
		ArrayList<Word> words = new ArrayList<Word>();
		
		for( Tokens token : input ) {
			if( token != null ){
				if( token.getAnnotedState() != StateAnnotation.NO_ANNOTE ){
					
					Word new_word = new Word( token.getLemma().toLowerCase(), token.getMorphoTag() );
					new_word.token = token;
					
					//Inserindo informações para os rótulos
					new_word.translates = translate.translateWord( token.getLemma() );
					for( String t : new_word.translates )
						for ( Synset s : WordNet.getSynonyms( t , SynsetType.NOUN ) )
							if( !new_word.synsets.contains( s ) )
								new_word.synsets.add( s );
					
					words.add(  new_word );
				}
			}
		}

		translate.saveCache();
		
		return words;
	}
	
	public ArrayList<Result> disambiguateWithWindow( LinkedList<Tokens> input, int window_size, Translator translate ) throws Exception {
			
		WeightEdge<Word, Integer, Integer, Integer> edge_builder = new WeightEdge<Word, Integer, Integer, Integer>();
		this.graph = new Graph<Word, Integer, Integer, Integer>();
		
		ArrayList<Word> words = preProcessing( input , translate);
		
		Build.buildWindowGraph( graph ,words , window_size );
		
		ArrayList<Result> result = disambiguateWithWindow( words, 3 , edge_builder );
		
		return result;
	}
	
	/**
	 * Constrói um grafo cujo arestas são mapeadas conforme um construtor de arestas passado como parâmetro
	 * 
	 * @param input um {@link ArrayList}, onde cada posição há um texto de entrada. Estes testes são tratados 
	 * dentro de uma mesma coleção 
	 * @param window_size tamanho da janela que será usada no texto. No grafo, isso pode ser interpretado como k-vizinhos
	 * @param edge_builder um {@link EdgeBuilder} que representa como as arestas serão criadas
	 * @throws Exception Pode-se lançar exeções na etapa de pré-processamento do texto de entrada
	 */
	public ArrayList<Result> disambiguateWithWindow( ArrayList<Word> words, int window_size , EdgeBuilder edge_builder ) throws Exception{
		
		ArrayList<Result> result = new ArrayList<Result>();
		
		this.graph = new Graph<Word, Integer, Integer, Integer>();		
				
		Build.buildWindowGraph( graph, words , window_size , edge_builder);
		
		WeightEdgeComparator comparator = new WeightEdgeComparator();
		Word neighbors[] = new Word[ window_size ];
		ArrayList<String> vizinhos = new ArrayList<String>();
		
		for( Vertice<Word, Integer> v : graph.getVertices() ){
			
			if( v.getValue().tag == MorphoTags.NOUN ){
			
				vizinhos.clear();
				LinkedList<Edge> edges = v.edges();					
				Collections.sort( edges ,  comparator );
				
				int count = 0;
				for( Edge<Integer, Integer> e : edges ){

					if( count < window_size ){
						if( e.getV().equals( v ) ) neighbors[ count ] =  (Word) e.getU().getValue();
						else neighbors[ count ] =  (Word) e.getV().getValue();
					}					
					vizinhos.add( ((Word) e.getU().getValue()).word );
				}
				
				Synset s = leskWsd( v.getValue(), neighbors );
				if( s != null )
					result.add( new Result( v.getValue(), s));			
			}
		}
		
		return result;
		
	}
	
	
	private Synset leskWsd(Word target, Word[] context ) {
		
		if( target.synsets.isEmpty() )
			return null;
		
		int values[] = new int[ target.synsets.size() ];
		
		int max_value=0, synset_index = 0;
		
		ArrayList<String> synset_label; 
		for( int index=0; index < values.length; index++ ){

			//Rótulo do synset da palavra alvo
			synset_label = Tokenizer.tokenizer( target.synsets.get( index ).getDefinition() );
			this.en_stoplist.removeStopWord( synset_label );
			
			int sum = 0;
			for( Word context_word : context )
				if( context_word != null )
					sum+= LeskUtil.comumWords( synset_label.toString(), context_word.translates.toString() );
			
			if( sum > max_value ){
				max_value = sum;
				synset_index = index;
			}
		}
		
		return target.synsets.get( synset_index );
	}

	
	private class WeightEdgeComparator implements Comparator<Edge>{
		@Override
		public int compare(Edge e1, Edge e2) {
			return (Integer)e2.getRelation() - (Integer) e1.getRelation();
		}
		
	}
}
 