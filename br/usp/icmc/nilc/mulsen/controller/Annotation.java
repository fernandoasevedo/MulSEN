package br.usp.icmc.nilc.mulsen.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import br.usp.icmc.nilc.mulsen.model.WordNet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import br.usp.icmc.nilc.mulsen.view.AnnotersView;
import br.usp.icmc.nilc.mulsen.view.SynsetView;
import br.usp.icmc.nilc.mulsen.view.TextView;
import br.usp.icmc.nilc.mulsen.view.Translates;
import br.usp.icmc.nilc.mulsen.view.components.SynsetCellRender;
import br.usp.icmc.nilc.mulsen.view.components.Text;
import br.usp.icmc.nilc.mulsen.view.components.Tokens;
import edu.smu.tspell.wordnet.Synset;

/**
 * Classe responsável por controlar a anotação
 * 
 * @author fernando
 *
 */
public class Annotation {
	private Translates t;
	private SynsetView sv;
	private TextView tv;
	private Tokens word;
	
	//processo de anotacao
	private String translate;
	private boolean manual_translate;
	
	//Utilizado para salvar o estado da anotação
	private int count;
	
	private AnnotersView av;

	private String tmp_file;
	private String save_file;
	
	private JFileChooser chooser;
	
	public Annotation(Translates t, SynsetView sv, TextView tv , AnnotersView annoters){
		this.t = t;
		this.sv = sv;
		this.tv = tv;
		this.count = 0;
		this.av = annoters;
		
		inicialize( );
	}
		
	private void inicialize(){
		this.word = null;
		this.translate = null;
		this.manual_translate = false;
		sv.clear();
		
		this.tmp_file = new Timestamp( System.currentTimeMillis() ).toString().split(" ")[ 0 ];
		save_file = "";
		
		chooser = new JFileChooser();
	}
	
	/**
	 * Método usado para traduzir uma palavra 
	 * e inserir suas possíveis traduções na lista de tradução
	 * 
	 * @param word uma {@link String} representando a palavra que será traduzida
	 */
	public void translate( Tokens word ){
		
		inicialize();				
		this.word = word;
		
		
		if( !word.isAnnoted() ){
			String lemma = word.getLemma();			
			t.translate( lemma , word.getLanguage() );
			
		}else{
			t.setTranslate( word.getTtranslateSet() );
			t.setSelectedTranslate( word.getTranslate() );
			translate = word.getTranslate();
			sv.search( word.getTranslate() );
			sv.setSelectedSynset( word.getSynset() );
		}
	}

	/**
	 * Método usado para buscar os synsets de uma palavra
	 * e inseri-los na lista de possíveis synsets
	 *  
	 * @param word uma {@link String} representando a palavra que será chave de busca dos synsets
	 * @param manual_translate se true, indica que a palavra foi traduzida manualmente. Se false, a palavra foi traduziada automaticamente 
	 */
	public void searchSynset( String word ,boolean manual_translate ) {
		this.manual_translate  = manual_translate;
		translate = word;		
		sv.search( word );
	}

	public void note(int index, Synset synsets[]) {

		String options[] = {"Sim", "Não"};
		
		int op = JOptionPane.showOptionDialog( tv,
				"Palavra: " + word.getText() +"\n" +
				"Tradução: " + translate+"\n" +
				"Synset:\n\t " + SynsetCellRender.formatSynonyms( synsets[  index ] )+
						"\n\t" + synsets[ index ].getDefinition(),
				"Deseja realizar esta anotação?",				
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, 
				options,
				options[ 0 ] );

		if ( op == JOptionPane.YES_OPTION ){			
			
			
			String translate_set[] = t.getTranslates();
			
			LinkedList<Synset> synset_set = new LinkedList<Synset>();
			for( int i=0; i < translate_set.length; i++ ){
				Synset aux[] = WordNet.getSynonyms( translate_set[ i ] );
				if ( aux.length == 0 )
					continue;
				
				for( int j=0; j < aux.length; j++ )
					if ( !synset_set.contains( aux[ j ] ) )
						synset_set.add( aux[ j ]);
			}
			word.setAnnotation( manual_translate, translate, synsets[ index ], translate_set, synset_set );
			
			//Faz com que os componentes fiquem esperando uma nova palavra
			t.init();
			sv.init();

			count = (count + 1) % 2;
			if ( count == 0 )
				saveXMLFile( false );
		}
	}

	/**
	 * Este método salva a anotação no formato xml
	 */
	public void saveXMLFile( boolean final_save ) {
		
		if( final_save ){
			
			save_file = tv.getTexts()[ 0 ].getDirectory().getAbsoluteFile() +
						File.separator +   
						tv.getTexts()[ 0 ].getDirectory().getName() + 
						".xml";
			
			int option = JOptionPane.showConfirmDialog( tv,
					"<html>Deseja alterar o nome do arquivo XML?<br />" +
					"Atualmente, o arquivo é <b>" + save_file + "</b></html>",
					"Salvar anotação",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE );
			
			if( option == JOptionPane.YES_OPTION ){
		
				chooser.setCurrentDirectory( tv.getTexts()[ 0 ].getDirectory().getAbsoluteFile() );
				chooser.setDialogTitle("Salvar anotação");
				chooser.showSaveDialog( tv );
				chooser.setMultiSelectionEnabled( false );
				File new_file = chooser.getSelectedFile();
				
				if( new_file == null )
					return;
				
				if ( new_file.getName().endsWith(".xml") || new_file.getName().endsWith(".XML"))
					save_file = new_file.getAbsolutePath();
				else
					save_file = new_file.getAbsolutePath() + ".xml";
			}
			
		}else{
			
			save_file = tv.getTexts()[ 0 ].getDirectory().getAbsoluteFile() +
						File.separator +   
						tv.getTexts()[ 0 ].getDirectory().getName() +
						tmp_file + 
						".xml";
		}
		
		//Cria uma thread para não parar a anotação
		new Thread("SaveXMLFile"){
			public void run() {
				try {
					
					Document document = new Document();
					Element root = new Element("save");
					document.setRootElement( root );

					//Salvando o nome dos anotadores
					Element annoters = new Element("Annotators");
					int count = 1;
					for( String name : av.getAnnoters() ){
						Element new_annoter = new Element("Annotator");
						new_annoter.setAttribute("id",  "" + count );
						new_annoter.setText( name );
						
						annoters.addContent( new_annoter );
						count++;
					}
					root.addContent( annoters );
				
					count = 1;
					
					Element annoters_files = new Element("Files");
					root.addContent( annoters_files );
					
					Text texts[] = tv.getTexts();
					
					int text_count = 0;
					
					for( Text text : texts ){
						
						Element element_file = new Element("Text");
						//Coloca o nome do arquivo extraindo a extensão do mxpost
						element_file.setAttribute("name", text.getNameFile().replaceFirst("_mxpost.", ".") );
						element_file.setAttribute("language", text.getLanguage().toString() );
						
						text_count++;
						annoters_files.addContent( element_file );
						
						count = 0;
						Element paragraph = new Element("p");
						paragraph.setAttribute("number", "" + count );
						element_file.addContent( paragraph );
						
						for( Tokens t : text.getTokens() ){
							
							if( t == null ){																
								count++;
								paragraph = new Element("p");
								paragraph.setAttribute("number", "" + count);
								element_file.addContent( paragraph );
								
							}else{
								
								//Criando o token
								Element element_token = new Element("Token");
								
								Element word = new Element("Word");
								word.setText( t.getText() );
								element_token.addContent( word );
												
								Element tag = new Element("Tag");
								tag.setText( t.getTag() );
								element_token.addContent( tag );
								
								Element morpho_tag = new Element("MorphoTag");
								morpho_tag.setText( t.getMorphoTag().toString() );
								element_token.addContent( morpho_tag );
								
								Element lemma = new Element("Lemma");
								lemma.setText( t.getLemma() );
								element_token.addContent( lemma );
															
								Element type = new Element("Type");
								type.setText(""+ t.getAnnotedState() );
								element_token.addContent( type );
								
								Element translates = new Element("Translations");																		
								translates.setAttribute("manual_translation",  (t.getmanual_translate()? "true": "false") );
								
								//Pegando as traduções
								if( t.getTranslate() != null ){

									Element t_element = new Element("Translate");
									t_element.setText( t.getTranslate() );
									t_element.setAttribute( "selected", "true" );									
									translates.addContent( t_element );
									
									String translate_set[] = t.getTtranslateSet();								
									for( int i=0; i < translate_set.length; i++){
										if( translate_set[ i ].compareToIgnoreCase( t.getTranslate()) != 0 ){
											t_element = new Element("Translate");
											t_element.setText( translate_set[ i ] );
											t_element.setAttribute( "selected", "false" );											
											translates.addContent( t_element );
										}
									}
																			
								}								
								element_token.addContent( translates );
								
								
								Element synsets= new Element("Synsets");								
								Synset s = t.getSynset();
								
								if ( s != null ){
									
									Element synset_element = new Element("Synset");
									synset_element.setText(""+ s.hashCode() );									
									synset_element.setAttribute("selected", "true" );									
									synsets.addContent( synset_element );
									
									LinkedList<Synset> synset_set = t.getSynsetSet();									
									for( int i=0; i < synset_set.size(); i++ ){										
										if( synset_set.get( i ) != s ){
											synset_element = new Element("Synset");
											synset_element.setText(""+ synset_set.get( i ).hashCode() );
											synset_element.setAttribute("selected","false");										
											synsets.addContent( synset_element );
										}
										
									}
								}																				
								element_token.addContent( synsets );
								
								paragraph.addContent( element_token );
							}
						}
					}

					//Pegando o nome do arquivo que será salvo					
					File p = new File( save_file );					
					XMLOutputter out = new XMLOutputter();					
					out.output( document, new FileOutputStream( p ));
					System.out.println("Save tmp successful!");
					
				} catch (IOException e) {			
					e.printStackTrace();
				}
			}
		}.start();
		
	}

	public void removeAnnotation() {		
		if ( word != null ){
			word.removeAnnotation();
			t.init();
			sv.init();
		}
	}
	
	public void setTranslateLabel( String label, int index  ){		
		t.setTranslateLabel( label , index );
	}

	public void setSelectedSynset(int index) {
		sv.setSelectedSynset( index );
	}
	
}