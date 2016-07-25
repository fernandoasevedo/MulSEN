package br.usp.icmc.nilc.mulsen.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import br.usp.icmc.nilc.mulsen.model.Language;
import br.usp.icmc.nilc.mulsen.model.MorphoTags;
import br.usp.icmc.nilc.mulsen.model.StateAnnotation;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.GraphWSD;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.Result;
import nlputil.Lemmar;
import nlputil.translate.Translator;
import br.usp.icmc.nilc.mulsen.view.components.Paragraph;
import br.usp.icmc.nilc.mulsen.view.components.Text;
import br.usp.icmc.nilc.mulsen.view.components.Tokens;
import br.usp.icmc.nilc.mulsen.controller.Annotation;
import edu.smu.tspell.wordnet.Synset;

/**
 * Classe que representa o componente responsável por apresentar o texto que
 * será anotado. Neste componente, são listadas abas que correspondem aos 
 * textos carregados.
 * 
 * @author fernando
 * 
 */
public class TextView extends JPanel {
	private Annotation annotation;

	private JPanel file_panel[  ];
	private JTabbedPane tabbed_panel;
	private JLabel wait_label;
	
	//Conjunto de componentes para visualização dos textos carregados
	private Text[] texts;
	//Arquivos carregados
	private File file[];

	// usado para verificar as palavras iguais
	private HashMap<String, Node> list_words; 
	
	// usado para contar a quantidade de palavras com significado
	private int meaning_words; 

	private GraphWSD wsd;
	
	/**
	 * Construtor da classe TextView. É criado e adicionado os componentes da
	 * GUI e inicializado algumas variáveis de instância
	 */
	public TextView() {
		super();
		
		/*
		 * Configurações de layout
		 */
		
		setBorder(new TitledBorder("Visualizador de texto"));
		setLayout(new BorderLayout());
		
		wait_label = new JLabel(
				"<html>Carregando os textos<br>Aguarde...</html>");
		wait_label.setIcon(new ImageIcon("img" + File.separator + "wait.gif"));			
		wait_label.setVisible(false);
		add(wait_label, BorderLayout.NORTH);

		tabbed_panel = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.WRAP_TAB_LAYOUT);
		tabbed_panel.add(new JPanel());
		add(tabbed_panel, BorderLayout.CENTER);

		// Um hash utilizado para verificar as palavras iguais
		list_words = new HashMap<String, Node>();

		texts = null;
		
		this.wsd = new GraphWSD( );
	}

	private void clear() {
		meaning_words = 0;
		list_words.clear();

		texts = null;
		file_panel = null;

		tabbed_panel.removeAll();
	}

	/**
	 * Método para carregar o texto, ou conjunto destes, que será anotado. Neste
	 * método é carregado um arquivo fos() ); rmatado segundo o padrão
	 * estipulado pelo MXPOST, ou seja, os tokens são separados por espaço.
	 * 
	 * @param file
	 *            Um {@link File} que indica o arquivo quer será carregado
	 * @throws IOException
	 *             É lançado uma exceção caso algum problema ocorra ao abrir o
	 *             aquivo passado como parâmetro
	 */
	public void setText(File file[]) throws IOException {

		clear();

		this.file = file;
		wait_label.setVisible( true );	
		
		openFile();
		
		wait_label.setVisible( false );
		
	}

	/**
	 * Método empregado para inciar o processo de abertura de novos
	 * arquivos para serem anotados.
	 * 
	 * Neste método, é empregado um filtro de arquivos, que permite
	 * apenas a abetura de arquivos com extensão txt
	 * 
	 * @throws IOException Caso haja algum problema na abertura dos arquivos selecionados
	 */
	private void openFile() throws IOException {

		file_panel = new JPanel[ file.length ];
		texts = new Text[ file.length ];

		//Usado apenas para log do sistema, que é impresso em linha de comando
		long total_time = 0;
		long time0 = System.nanoTime();
		System.out.println("Loading...");
		
		
		for (int i = 0; i < file.length; i++) {

			// Carregado o arquivo explicitiando que sua formatação em UTF-8
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file[ i ]), "UTF-8"));

			texts[ i ] = new Text(file[ i ].getName(), file[ i ].getParentFile());
			
			//Layout
			file_panel[ i ] = new JPanel();
			file_panel[ i ].setOpaque(true);
			file_panel[ i ].setBackground(Color.white);
			file_panel[ i ].setLayout(new BoxLayout(file_panel[ i ],
					BoxLayout.Y_AXIS));

			//Adicionando um novo texto (aba) ao painel
			tabbed_panel.addTab("Texto " + (i + 1), new JScrollPane(
					file_panel[ i ], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			tabbed_panel.setToolTipTextAt( i, file[ i ].getName().split(".mxpost")[ 0 ] );

			HashMap<String, Tokens> linked_words = new HashMap<String, Tokens>();
			
			String line = "";
			while ((line = reader.readLine()) != null) {

				System.out.println( line );
				
				tabbed_panel.repaint();

				//Essas verificações de \n e \r\n são necessárias para não restringir o uso de Windows ou Unix
				if (line.compareTo("") == 0 || line.compareTo("\r\n") == 0 || line.compareTo("\n") == 0)
					continue;

				//Capturando os tokens tokenizados na linha atual do arquivo
				String tokens[  ] = line.split(" ");
				Paragraph p = new Paragraph();

				for (int j = 0; j < tokens.length; j++) {

					System.out.println( "\t" + tokens[  j  ] ) ;
					
					if (tokens[ j ].compareTo("") == 0
							|| tokens[ j ].compareTo("\r\n") == 0 
							|| line.compareTo("\n") == 0)
						continue;

					/*
					 * Separando a palavra de sua etiqueta no seguinte formato: 
					 * treetagger = [ palavra, etiqueta, lema ]
					 * mxpost = [palavra, etiqueta]
					 */
					String w[  ] = tokens[ j ].split("_");
					
					//Se a tag utilizada é do mxpot
					MorphoTags tag;
					if ( w.length == 2 )
						tag =  MorphoTags.mxpostMap( w[ 1 ] );
					else						
						tag = MorphoTags.treetaggerMap( w[  1  ] );
					
					// Verificando o estágio de anotação da palavra por meio de sua etiqueta
					StateAnnotation state_annotation;
					if( tag == MorphoTags.NOUN ){
						state_annotation = StateAnnotation.NOUN_NO_ANNOTED;
					}else{						
						if( tag != MorphoTags.OTHER ) {
							state_annotation = StateAnnotation.NO_ANNOTED;
						}
						else {
							state_annotation = StateAnnotation.NO_ANNOTE;
						}
					}
					
					// Criando os tokes que serão apresentados na tela
					Tokens n = new Tokens(w[ 0 ], state_annotation, w[ 1 ], tag, texts[  i  ].getLanguage() );
					n.setMorphoTag( tag );
					
					// Adicionando o token n na lista de tokens
					texts[ i ].addToken(n);

					//Lematizando o token
					if (n != null) {
						if (n.getAnnotedState() != StateAnnotation.NO_ANNOTE) {

							if( !w[  0  ].isEmpty() &&  w[  0  ].length() > 1 ){
								String lemma = n.getText();
								
								if( n.getLanguage() == Language.PORTUGUESE ){
									
									if( lemma.length() > 2 && Character.isLetter( lemma.charAt(  0 ) ) ){
										long l_time0 = System.nanoTime();
										n.setLemma( Lemmar.lemma(lemma, n.getTag()) );
										long l_time1 = System.nanoTime();
										total_time += (l_time1 - l_time0);
									}
									
								}
								else{
									//Usando o treetager, o terceiro argumento retornado é o lema da palavra
									n.setLemma( w[  2  ] );
									
								}
							}	
							
							if( linked_words.containsKey( n.getLemma() ) ){
								Tokens t = linked_words.get( n.getLemma() );
								t.addLink( n );
								n.addLink( t );
							}else{
								linked_words.put( n.getLemma(), n);
							}								
						}
					}

					p.add(n);
				}

				texts[ i ].addToken(null);
				file_panel[ i ].add(p);
								
			}
			
			Translator translator = null;
			switch ( texts[ i ].getLanguage() ) {
				case PORTUGUESE:
					translator = Translates.pten_translate;
				break;
				
				case SPANISH:
					translator = Translates.esen_translate;
				break;
					
				default:
					translator = Translates.enen_translate;
				break;
			}
			

			try {
				ArrayList<Result> results = wsd.disambiguateWithWindow( texts[  i  ].getTokens(), 5, translator);
				for( Result result : results ){
					
					if ( result.synset() != null ){
						
						String selected_translate = result.synset().getWordForms()[ 0 ];
						for( String s : result.synset().getWordForms() ){
							if( result.word().translates().contains( s.toLowerCase() )){
								selected_translate = s;
								break;
							}
						}						
						
						result.word().token().setPreAnnotation(
								false,  //manualmente traduzida?
								selected_translate, //tradução selecionada								 
								result.synset(), //synset selecionado
								result.word().translates().toArray( new String[  result.word().translates().size()  ]),
								new LinkedList<Synset>( result.word().synsets() ),
								true);
					}
				}
			} catch (Exception e) {						
				e.printStackTrace();
			}
				
			
			reader.close();
		}
		
		//Adicionando eventos de mouse aos tokens selecionáveis (que podem ser anotados)
		Tokens n;
		for (int i = 0; i < texts.length; i++) {
			LinkedList<Tokens> list = texts[ i ].getTokens();
			for (int j = 0; j < list.size(); j++) {
				n = list.get(j);
				if (n != null)
					if (n.getAnnotedState() != StateAnnotation.NO_ANNOTE)
						addEvent( n );
			}
		}

		/*
		 * Impressão de log de tempo de pré-processamento do sistema
		 */
		long time1 = System.nanoTime();
		System.out.println("Tempo(s): " + (time1 - time0) / 1000000000);
		System.out.println("Tempo(ns): " + (time1 - time0));

		System.out.println("\nLematizacao");
		System.out.println("Tempo(s): " + (total_time / 1000000000));
		System.out.println("Tempo(ns): " + total_time);

		System.out.println("\nFrequencia");
		time0 = System.nanoTime();
		makeFrequency();
		time1 = System.nanoTime();
		System.out.println("Tempo(s): " + (time1 - time0) / 1000000000);
		System.out.println("Tempo(ns): " + (time1 - time0));

		wait_label.setVisible(false);
		paintComponents(getGraphics());
		repaint();
		
	}

	/**
	 * Gera um arquivo com as frequências de cada palavra no cluster
	 */
	private void makeFrequency() {

		Iterator<Entry<String, Node>> iterator = list_words.entrySet()
				.iterator();
		ArrayList<Node> frequency_list = new ArrayList<Node>();

		Node n;
		while (iterator.hasNext()) {
			n = iterator.next().getValue();
			frequency_list.add(n);
		}

		Collections.sort(frequency_list, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.frequency > n2.frequency)
					return -1;
				if (n1.frequency < n2.frequency)
					return 1;
				return 0;
			}
		});

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(texts[ 0 ].getDirectory()
									.getAbsolutePath()
									+ File.separator
									+ texts[ 0 ].getDirectory().getName()
									+ ".frequencia"), "UTF-8"));

			writer.write("Nro de substantivos = " + meaning_words );
			writer.newLine();writer.newLine();
			writer.write("Palavra\tFrequência");
			writer.newLine();
			
			int i = 0;
			for (; i < frequency_list.size(); i++) {

				n = frequency_list.get(i);
				writer.write(n.t.getLemma() + "\t" + n.frequency );
				writer.newLine();
			}

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addEvent(Tokens n ) {

		// Verifica se a palavra já existia na lista de palavras
		
		Node equal_world = list_words.get( n.getLemma() );

		// Se já existe, adiciona na palavra atual um link para a palavra ja
		// existente
		if (equal_world != null) {
			
			equal_world.t.addLink( n );
			n.addLink( equal_world.t );

			if (n.getMorphoTag() == MorphoTags.NOUN) {
				equal_world.frequency++;
				meaning_words++;
			}

		} else {
			list_words.put(n.getLemma(), new Node(n));
		}

		// Adicionando o evento de click
		n.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				annotation.translate((Tokens) e.getSource() );
			}
		});
	}

	public void setText(Text text[  ]) {

		clear();

		this.texts = text;
		file_panel = new JPanel[ texts.length ];

		// Adicionando os componentes visuais
		for (int i = 0; i < texts.length; i++) {

			file_panel[ i ] = new JPanel();
			file_panel[ i ].setLayout(new BoxLayout(file_panel[ i ],
					BoxLayout.PAGE_AXIS));

			Paragraph p = new Paragraph();
			for (Tokens token : texts[ i ].getTokens()) {

				if (token != null)
					if (token.getAnnotedState() != StateAnnotation.NO_ANNOTE )
						addEvent(token);

				if (token == null) {
					file_panel[ i ].add(p);
					p = new Paragraph();
				} else
					p.add(token);
			}

			tabbed_panel.addTab(			
					"Texto " + (i + 1), new JScrollPane(file_panel[ i ],
							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			tabbed_panel.setToolTipTextAt(i, texts[ i ].getNameFile());
		}
	}

	public void setController(Annotation annotation) {
		this.annotation = annotation;
	}

	public Text[ ] getTexts() {
		return texts;
	}

	private class Node {
		protected Tokens t;
		protected int frequency;

		public Node(Tokens t) {
			this.t = t;
			this.frequency = 1;
		}
	}
}
