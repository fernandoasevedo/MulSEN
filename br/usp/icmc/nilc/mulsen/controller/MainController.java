package br.usp.icmc.nilc.mulsen.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import br.usp.icmc.nilc.mulsen.model.Language;
import br.usp.icmc.nilc.mulsen.model.MorphoTags;
import br.usp.icmc.nilc.mulsen.model.StateAnnotation;
import br.usp.icmc.nilc.mulsen.model.WordNet;

import nlputil.morphotagger.Mxpost;
import nlputil.morphotagger.TreeTagger;
import nlputil.Tokenizer;

import org.annolab.tt4j.TreeTaggerException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import br.usp.icmc.nilc.mulsen.view.AnnotersView;
import br.usp.icmc.nilc.mulsen.view.TextView;
import br.usp.icmc.nilc.mulsen.view.components.Text;
import br.usp.icmc.nilc.mulsen.view.components.Tokens;

import edu.smu.tspell.wordnet.Synset;

public class MainController {

	private boolean open;
	
	//Utilizado para ter um referêncial das janelas
	private JFrame frame;
	private JFileChooser chooser;
	
	
	//Filtros de arquivo
	private FileFilter xml_filter;
	private FileFilter txt_filter;
	
	/**Etiquetadores morfossintáticos para inglês e espanhol*/	
	private TreeTagger english_tagger;
	private TreeTagger spanish_tagger;
	private TreeTagger portuguese_tagger;
	
	/**Arquivos necessários para os etiquetadores*/
	private File mxpost_tag_set;

		
	public MainController( JFrame frame ) throws IOException{
		
		this.open = false;
		this.frame = frame;
		this.chooser = new JFileChooser();
		
		
		//Criando os taggers para português, inglês e espanhol
		this.mxpost_tag_set = new File("lib"+File.separator+"MXPOST"+File.separator+"port");
		File english_model = new File( "lib" +File.separator+ "treetagger" + File.separator + "model" +  File.separator + "english.par:utf8");
		File spanish_model = new File(  "lib" +File.separator+ "treetagger" + File.separator + "model" +  File.separator + "spanish.par:utf8");
		File portuguese_model = new File(  "lib" +File.separator+ "treetagger" + File.separator + "model" +  File.separator + "pt.par:utf8"); 
		
		this.english_tagger = new TreeTagger( english_model );
		this.spanish_tagger = new TreeTagger( spanish_model );				
		this.portuguese_tagger = new TreeTagger( portuguese_model );
		
		/*Filtro para capturar somente arquivos xml*/
		this.xml_filter = new FileFilter() {			
			public String getDescription() {
				return "xml";
			}
			public boolean accept(File f) {				
				if ( f.isDirectory() )
					return true;
				
				String name_spit[] = f.getName().split("\\.");
				if ( name_spit.length >= 2 )
					return ( name_spit[ 1 ].compareToIgnoreCase("xml") == 0 );
				
				return false;
			}
		};
		
		/*Filtro para capturar sometne arquivos txt*/
		this.txt_filter = new FileFilter() {			
			public String getDescription() {
				return "txt";
			}
			public boolean accept(File f) {				
				if ( f.isDirectory() )
					return true;
				
				String name_spit[] = f.getName().split("\\.");
				if ( name_spit.length >= 2 )
					return ( name_spit[ 1 ].compareToIgnoreCase("txt") == 0 ) ;
				
				return false;
			}
		};
		
	}
	
	
	/**
	 * Método para carregar um arquivo ou um cluster de textos que serão anotados.
	 * Os arquivos carregados serão tokenizados, etiquetados com o mxpost e, após esse
	 * processo, serão carregados para anotação
	 * 
	 * @param textView o objeto responsável por visualizar os textos
	 * @throws IllegalArgumentException caso algum erro de seleção dos arquivos ocorra
	 */
	public void openTXTFiles( TextView textView ) throws IllegalArgumentException{
		
		if ( chooser.isShowing() ) chooser.hide();
		
		chooser.setDialogTitle("Select an or more text");
		chooser.setFileFilter( txt_filter );
		chooser.setMultiSelectionEnabled( true );
		int option = chooser.showOpenDialog( frame );
				
		if ( option != JFileChooser.APPROVE_OPTION )
			return;
		
		File file[] = chooser.getSelectedFiles();
		if ( file == null )
			throw new IllegalArgumentException("No file selected!");

		if ( file.length == 0 )
			throw new IllegalArgumentException("No file selected!");
		
		try {

			//Diretório onde será salvo os arquivos
			//Trata-se de um diretório dentro da pasta text, cujo nome é o do mesmo diretório pai do arquivo principal
			//File out_dir = new File( "text" + File.separator + file[ 0 ].getAbsoluteFile().getParentFile().getName() );
			File out_dir = new File( file[ 0 ].getAbsoluteFile().getParentFile().getAbsolutePath() );
			if ( !out_dir.exists() )
				out_dir.mkdir();
			
			//Vetor onde será armazenados os arquivos anotados com o mxpost
			File pos_file[] = new File[ file.length ];
						
			//Para cada arquivo, é realizado a anotação com o mxpost de cada
			for( int i = 0; i < file.length; i++ ){
				
				//tokeniza o arquivo
				File input_tokenizada = new File("tokens_temp.txt");
				Tokenizer.tokenizer( file[ i ], input_tokenizada );

				//Nome do arquivo sem a extensão
				String name_file = file[ i ].getName().split("\\.")[ 0 ];
				Language language_file = Language.convert( name_file.split("_")[ 0 ]);
				if (language_file == null ) language_file = Language.ENGLISH;

				//Versão do arquivo etiquetado
				pos_file[ i ] = new File( out_dir.getAbsolutePath() + File.separator + name_file + ".tagged");

				//Fazer verificação de idioma
				switch ( language_file) {

					case PORTUGUESE:
						Mxpost.tagger_file( input_tokenizada, pos_file[ i ] , mxpost_tag_set );
						//portuguese_tagger.taggerFile( input_tokenizada, pos_file[ i ] );
					break;

					case ENGLISH:
						english_tagger.taggerFile( input_tokenizada, pos_file[ i ] );
					break;
					
					case SPANISH:
						spanish_tagger.taggerFile( input_tokenizada, pos_file[ i ] );
					break;
				}
				
			}
			
			textView.setText( pos_file );
			
			this.open = true;
			
		} catch (IOException e) {
						
			String erro = "";
			for( int i=0; i < file.length; i++)
				erro+= "<li>" + file[ i ].getName() + "</li>";

			
			System.out.println("Erro:\n" + erro + "\n\n" + e.getMessage() );
			
			throw new IllegalArgumentException("<html>Error in open file:<ul>" + erro + "</ul>");
			
		} catch (TreeTaggerException e) {		
			e.printStackTrace();
		}

	
		
		frame.paintAll( frame.getGraphics() );
	}


	/**
	 * Método que inicia o processo de abertura de uma anotação previamente salva.
	 * Neste procedimento, é empregado um filtro, que permite apenas a abertura de
	 * arquivos com extensão XML
	 * 
	 * @param text_view Componente onde será inserido os textos
	 * @param annoters_view Componente onde será inserido os anotadores
	 * 
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void openAnnotation( TextView text_view, AnnotersView annoters_view  ) throws IllegalArgumentException, IOException, JDOMException, IOException{
		
		if ( chooser.isShowing() ) chooser.hide();
		
		chooser.setDialogTitle("Selecione um arquivo XML");
		chooser.setFileFilter( xml_filter );
		chooser.setMultiSelectionEnabled( false );
		chooser.showOpenDialog( frame );
		
		File file = chooser.getSelectedFile();
		
		if ( file == null )
			throw new IllegalArgumentException("File not found. Please, for open a annoted file, you have select a xml file.");
		
		//Manipulando o arquivo xml para carregar a anotação
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build( new FileInputStream( file ) );

		Element root = document.getRootElement();

		//Pegar o nome dos anotadores;
		List<Element> list = root.getChild("Annotators").getChildren("Annotator");		
		for( int i=0; i < list.size(); i++ )
			annoters_view.addAnnotator( list.get( i ).getText() );
		
		
		//Pegando os arquivos		
		list = root.getChild("Files").getChildren("Text");
		
		//Vetor onde será armazenado os textos 
		Text array_text[] = new Text[ list.size() ];
		
		int index = 0;
		String translate = null;
		String translate_set[] = null;
		Synset synset = null;
		LinkedList<Synset> synset_set = null;
		Tokens t;
		boolean manual_translate;
		int synset_id;
		
		for( Element text : list ){
			
			array_text[ index ] = new Text(
					text.getAttributeValue("name") , 
					file.getParentFile() );
			
			Language text_language = Language.valueOf( text.getAttributeValue("language") );
			//Pegando os parágrafos
			for( Element paragraph : (List<Element>) text.getChildren("p") ){
				
				StateAnnotation annoted_state = StateAnnotation.NO_ANNOTED;
				for( Element token : (List<Element>) paragraph.getChildren("Token") ){
					
					annoted_state= StateAnnotation.valueOf( token.getChildText("Type") );
					
					t = new Tokens(
							token.getChildText("Word"),
							annoted_state,
							token.getChildText("Tag"),
							MorphoTags.strToTag( token.getChildText("MorphoTag") ),
							text_language
							);
					
					t.setLemma( token.getChildText("Lemma") );
					
					 
					Element translate_set_element = token.getChild("Translations");
					if( translate_set_element.getAttributeValue("manual_translation").compareTo("true") == 0)
						manual_translate = false;
					else
						manual_translate = true;
					
					List<Element> translate_element = translate_set_element.getChildren();
					if( !translate_element.isEmpty() ){
						
						translate_set = new String[ translate_element.size() ];
						for( int i=0; i < translate_element.size(); i++ ){
							if( i ==0 )
								translate = translate_element.get( i ).getText();
							translate_set[ i ] = translate_element.get( i ).getText();
						}
						
						Element synset_set_element = token.getChild("Synsets");
						List<Element> synset_element = synset_set_element.getChildren();
						synset_set = new LinkedList<Synset>();
						for( int i=0; i < synset_element.size(); i++ ){
							
							System.out.println(">"+ synset_element.get( i ).getText() +"<");
							
							if( i ==0 )
								synset = WordNet.getSynset( Integer.parseInt( synset_element.get( i ).getText() ) );
							
							synset_set.add( WordNet.getSynset( Integer.parseInt( synset_element.get( i ).getText() ) ) );
						}
					
						t.setAnnotation( manual_translate, translate, synset, translate_set, synset_set );
						t.setType( annoted_state );
					}
					
					array_text[ index ].addToken( t );
				}
				
				array_text[ index ].addToken( null );
			}
			
			index++;
		}
		
		this.open = true;
		
		text_view.setText( array_text );
	}

	/**
	 * Salva a anotação dos arquivos carregados em arquivos TXT
	 * Os arquivos salvos terão o mesno nome do arquivo fonte, porém,
	 * será adicionado a palavra ANOTADO no final do arquivo
	 */
	public void saveTXTFile( TextView text_view ) {
		
		int index = 0;
		for( Text t : text_view.getTexts() ){
			try {
								
				//Cria o diretório, caso não exita
				File out = t.getDirectory();
				
				if( !out.exists() )
					out.mkdir();
				
				out = new File( 
						t.getDirectory().getAbsolutePath() +
						File.separator +						 
						t.getNameFile().split("\\.")[0] + "_ANOTADO");
				
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter( new FileOutputStream( out ), "UTF-8" ));

				for( Tokens token : t.getTokens() ){
					if ( token == null )
						writer.write("\n\n");
					else
						writer.write( token.write() +" ");
				}

				index++;				
				writer.close();

				JOptionPane.showMessageDialog( frame, "Arquivos TXT salvos com sucesso!", "Anotação", JOptionPane.INFORMATION_MESSAGE);
				
			} catch (UnsupportedEncodingException e) {			
				e.printStackTrace();
				
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
				
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		
	}

	public boolean isOpen() {
		return this.open;
		
	}
}
