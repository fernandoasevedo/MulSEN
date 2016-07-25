package br.usp.icmc.nilc.mulsen.view.components;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import br.usp.icmc.nilc.mulsen.model.Language;
import br.usp.icmc.nilc.mulsen.model.MorphoTags;
import br.usp.icmc.nilc.mulsen.model.StateAnnotation;

import edu.smu.tspell.wordnet.Synset;

/**
 * Classe que representa os tokens que serão apresentandos ao usuário.
 * Esses tokens podem ter possíveis anotações ou não, dependendo do seu tipo.
 * 
 * @author fernando
 *
 */
public class Tokens extends JLabel implements Serializable{
	
	/* ***************************
	***** Atributos do token  ****
	*****************************/
	
	private String lemma;
	
	/*Etiqueta atribuída pelo tagger durante a etapa de pré-processamento*/
	private String tag; 
	
	/*Etiqueta mapeada para o padrão adotado para todos os idiomas*/
	private MorphoTags morpho_tag;
	
	private int annoted_state; //Tipo do token
	private StateAnnotation state_annoted;
	
	/*Indicia se esta palavra foi traduzida manualmente*/
	private boolean manual_translate; 	
	
	private LinkedList<Synset> synsets_set; //Posíveis synsets
	private String[] translates_set; //Lista de possíveis traduções
	private String translate; //Tradução utilizado para a palavra
	private Synset annoted_synset; //Synset anotado para esta palavra
	
	//Faz um link com tokens que possuem a mesma grafia, onde provavelmente possuiram a mesma anotação
	private ArrayList<Tokens> linked_tokens;
	
	private Language language;
	
	/**
	 * Construtor da classe Token
	 * @param word {@link String} que armazena o item lexical a ser armazenado
	 * @param annoted_state {@link StateAnnotation} que representa o estado inicial de anotação do token
	 * @param tag {@link String} contendo e etiqueta morfossintática atribuída ao token durante a etapa de pré-processamento
	 * @param tag {@link MorphoTags} uma tag, que corresponde a etiqueta anterior, porém, já mapeada para o padrão de todos os idiomas
	 * @param language {@link Language} que indica o idioma do texto que o item lexical pertence
	 */
	public Tokens( String word,
					StateAnnotation annoted_state,
					String tag,
					MorphoTags morpho_tag,
					Language language){
		super( word );		
		setOpaque( true );
		
		this.state_annoted = annoted_state;
		this.tag = tag;
		this.translate = null;
		this.annoted_synset = null;
		this.language = language;
		this.morpho_tag = morpho_tag;
		lemma = word;
		
		changeType( this.state_annoted );
		
		if ( state_annoted != StateAnnotation.NO_ANNOTE )
			linked_tokens = new ArrayList<Tokens>( );
	}

	public void setType( StateAnnotation annotted_state ){
		changeType(annotted_state);
	}
	
	
	/**
	 * Método envocado para alterar o tipo do token
	 * @param annoted_state um valor {@link int} que representa o novo tipo do token
	 */
	private void changeType( StateAnnotation annoted_state ) {
		
		this.state_annoted = annoted_state;
		
		setBackground( Color.WHITE );
		
		if ( annoted_state == StateAnnotation.NO_ANNOTED ){ 
			setBorder( null );
			setToolTipText("Palavra ainda não anotada!");
			return;
		}
		
		if ( annoted_state == StateAnnotation.NOUN_NO_ANNOTED ){
			setBorder( new LineBorder( Color.red, 2 ) );
			setToolTipText("Substantivo ainda não anotado!");
			return;
		}
		
		if ( annoted_state == StateAnnotation.ANNOTED ){
			setBorder( new LineBorder( Color.green ,2 ) );
			setToolTipText("Substantivo anotado!");
			return;
		}
		
		if ( annoted_state == StateAnnotation.PREV_ANNOTED ){
			setBackground( Color.YELLOW );
			setBorder( new LineBorder( Color.yellow, 2) );
			setToolTipText("Palavra previamente anotada!");
			return;
		}
				
	}
	
	public StateAnnotation getAnnotedState(){
		return this.state_annoted;
	}

	/**
	 * Adiciona a anotação à palavra
	 * 
	 * @param manual_translate um valor {@link boolean} que se true, indica que a palavra foi manualmente traduzida
	 * @param translate uma {@link String} que representa a tradução para inglês aplicada
	 * @param selected_synset um {@link int} que representa qual o synset selecionado dentro de {@link synset[]}
	 * @param synset um vetor contendo todo os possíveis synsets encontrados para a palavra
	 */
	public void setAnnotation( boolean manual_translate, String translate, Synset s, String[] translate_set, LinkedList<Synset> synsets_set ){
		
		this.manual_translate = manual_translate;
		this.translate = translate;
		this.translates_set = translate_set.clone();
		this.annoted_synset = s;		
		this.synsets_set = (LinkedList<Synset>) synsets_set.clone();
		
		//Altera-se o tipo do token
		changeType( StateAnnotation.ANNOTED );
		
		//Coloca anotação para os possíveis toknes linkados
		if ( linked_tokens != null )
			for( Tokens t : linked_tokens )
				t.setPreAnnotation( manual_translate, translate,  s, translate_set, synsets_set, true );
	}
	
	
	/**
	 * Adiciona a anotação prévia à palavra, caso esta ainda não foi anotada
	 * 
	 * @param manual_translate um valor {@link boolean} que se true, indica que a palavra foi manualmente traduzida
	 * @param translate uma {@link String} que representa a tradução para inglês aplicada
	 * @param selected_synset um {@link int} que representa qual o synset selecionado dentro de {@link synset[]}
	 * @param synset um vetor contendo todo os possíveis synsets encontrados para a palavra
	 * @param first caso true, é verificado outras palavras linkadas, caso false, é verificado apenas para a palavra referenciada
	 */
	public void setPreAnnotation( boolean manual_translate, 
			String translate, Synset s, String[] translate_set, LinkedList<Synset> synsets_set, boolean first) {
		
		if (this.state_annoted != StateAnnotation.ANNOTED  ){		
			this.manual_translate = manual_translate;
			this.translate = translate;
			this.translates_set = translate_set.clone();
			this.annoted_synset = s;		
			this.synsets_set = (LinkedList<Synset>) synsets_set.clone();
			
			//Altera-se o tipo do token
			changeType( StateAnnotation.PREV_ANNOTED );
		}
		
		if ( first )
			if ( linked_tokens != null )
				for( int i=0; i < linked_tokens.size(); i++ )						
					linked_tokens.get( i ).setPreAnnotation( manual_translate, translate,  s, translate_set, synsets_set, false );
					
	}
	
	/**
	 * @return A palavra formatada para anotação. Caso não tenha anotação, a palvra é retornadada sozinha
	 * . Porém, caso há alguma anotação, retorna-se o padrão de anotação
	 */
	public String write(){
		
		if( annoted_synset == null )
			return getText();
		
		StringBuilder output = new StringBuilder();
		output.append( getText() );
		output.append( "<" );
		output.append( translate );
		output.append(",");
		output.append( annoted_synset.toString().split("]")[ 0 ] );
		output.append( "]>" );
				
		return output.toString();			
	}

	/**
	 * @return uma {@link String} que representa a tradução utilizada para esta palavra
	 */
	public String getTranslate() {
		return translate;
	}

	/**
	 * @return true, caso a palavra já tenha sido anotada. False, caso a palavra ainda não recebeu anotação
	 */
	public boolean isAnnoted() {
		return (annoted_synset != null );
	}

	
	public Synset getSynset(){
		
		if( annoted_synset == null )
			return null;
		
		return annoted_synset;
	}
	

	public void addLink(Tokens link_token ) {
		linked_tokens.add( link_token );
	}

	public void removeAnnotation() {
		changeType( StateAnnotation.NOUN_NO_ANNOTED );
		this.manual_translate = false;
		this.translate = null;
		this.translates_set = null;
		this.annoted_synset = null;
		this.synsets_set = null;
		
	}
	
	public boolean getmanual_translate(){
		return this.manual_translate;
	}

	public String[] getTtranslateSet() {
		return translates_set;
	}

	public LinkedList<Synset> getSynsetSet() {
		return synsets_set;
	}

	public String getTag(){
		return this.tag;
	}

	
	public MorphoTags getMorphoTag() {
		return this.morpho_tag;
	}
	
	public void setMorphoTag( MorphoTags morpho_tag ){
		this.morpho_tag = morpho_tag;
	}
	
	public String getLemma() {
		return this.lemma;
	}
	
	public void setLemma( String lemma ){
		this.lemma = lemma;
	}
	
	public ArrayList<Tokens> getLinkedTokens(){
		return this.linked_tokens;
	}
	
	public Language getLanguage(){
		return this.language;
	}
}
