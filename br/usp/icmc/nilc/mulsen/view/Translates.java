package br.usp.icmc.nilc.mulsen.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import br.usp.icmc.nilc.mulsen.model.Language;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.EnglishTranslator;
import nlputil.Security;
import nlputil.translate.wordreference.WordReferenceTranslate;

import br.usp.icmc.nilc.mulsen.view.components.TranslateCellRender;
import br.usp.icmc.nilc.mulsen.view.components.TranslateLabel;
import br.usp.icmc.nilc.mulsen.controller.Annotation;

/**
 * Classe que representa o componente destinado a tradução 
 * das palavras em Português-BR para o Inglês.
 * @author fernando
 *
 */
public class Translates extends JPanel{
	
	//Componentes
	private JList list;
	private DefaultListModel list_model;	
	private JTextField new_translate;
	private boolean manual_translate;
	private JButton add_translate;	
	private JLabel wait_label;
	

	//Tradutor Português-Inglês
	public static WordReferenceTranslate pten_translate;
	//Tradutor Espanho-Inglês
	public static  WordReferenceTranslate esen_translate;
	public static  EnglishTranslator enen_translate;
	
	private int automatic_translate;
	
	private LinkedList<TranslateLabel> translates_label;
	
	public Translates(){
		super();	
		setBorder( new TitledBorder("1 - Escolha a tradução")) ;
		
		translates_label = new LinkedList<TranslateLabel>();
		
		addComponents();
		
		pten_translate = new WordReferenceTranslate( Security.getInfo("wordreference_api_pten"), "pten");
		esen_translate = new WordReferenceTranslate( Security.getInfo("wordreference_api_esen"), "esen");
		enen_translate = new EnglishTranslator();
		
		changeActives( false );
	}

	/**
	 * Adiciona os componentes na tela:
	 * campos de textos, botões, labels, lista
	 */
	private void addComponents() {
		
		//Painel utilizado para organização do layout dos componentes
		JPanel p1 = new JPanel();
		p1.setLayout( new BoxLayout( p1, BoxLayout.PAGE_AXIS ));
		p1.setBorder( BorderFactory.createEmptyBorder(0, 5, 0, 5)); //Criando um espaçamento em torno do painel
		p1.setAlignmentX(RIGHT_ALIGNMENT);
		
		p1.add( new JLabel("Possíveis traduções:"));
		
		wait_label = new JLabel();
		wait_label.setVisible( false );
		p1.add( wait_label );
		
		p1.add( Box.createRigidArea( new Dimension(0, 5))); //Cria um espaçamento com 0 pixels de largura, e 5 de altura
		
		//lista que irá armazenar as possíveis traduções das palavras em Português
		list = new JList();
		list.setLayoutOrientation( JList.VERTICAL_WRAP );
		list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		list.setCellRenderer( new TranslateCellRender( translates_label ) );
		list.setVisibleRowCount( 5 );
		
		//modelo da lista
		list_model = new DefaultListModel();
		list.setModel( list_model );
		
		 //Criado para apresentar barras de rolagem na lista de traduções
		JScrollPane list_panel = new JScrollPane(list); 
		list_panel.setAlignmentX(LEFT_ALIGNMENT);
		p1.add( list_panel );
		p1.add( Box.createRigidArea( new Dimension(0, 5)));		
		
		//Outro painel utilizado para formatar o layout dos componentes
		JPanel p2 = new JPanel();
		p2.setBorder( BorderFactory.createEmptyBorder(10, 5, 10, 5)); //Criando um espaçamento em torno do painel
		p2.setLayout( new BoxLayout( p2, BoxLayout.X_AXIS ));
		p2.add( Box.createHorizontalGlue() );
		
		p2.add( new JLabel("Nova tradução:"));
		p2.add( Box.createRigidArea( new Dimension(5, 0)));
		
		//Campo para novas traduções
		new_translate = new JTextField(10);
		new_translate.setToolTipText("Indique uma tradução mais adequada");
		p2.add( new_translate );
		p2.add( Box.createRigidArea( new Dimension(5, 0)));
		
		//Botão de seleção de nova tradução
		p2.add( Box.createHorizontalGlue() );
		add_translate = new JButton("Adicionar nova tradução");
		add_translate.setToolTipText("Clique para usar a nova tradução sugerida");
		add_translate.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent ev) {	
				
				String word = new_translate.getText();
				if ( word.isEmpty() )
					return;
				
				manual_translate = true;
				automatic_translate++;
				translates_label.addFirst( new TranslateLabel( "",  -1 ) );
				list_model.add( 0 , word );				
				new_translate.setText("");
			}
		});
		p2.add( add_translate );
		
		
		setLayout( new BorderLayout() );		
		add( p1 , BorderLayout.CENTER );
		add( p2 , BorderLayout.PAGE_END );
	}

	/**
	 * Método responsável por adicionar os eventos aos componentes 
	 * de traduação.
	 * 
	 * @param annotation um objeto {@link Annotation} que encapsulo o controlador da aplicação
	 */
	public void setController(final Annotation annotation) {
		
		list.addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				if ( list.getSelectedIndex() < 0 )
					return;
							
				String word = (String) list.getSelectedValue();
				
				if ( annotation != null){
					annotation.searchSynset( word , manual_translate );
					
					TranslateLabel selected_label =  translates_label.get( list.getSelectedIndex());
					if( selected_label.index >= 0 )
						annotation.setSelectedSynset( selected_label.index );
				}
			}
		});
		
		
	}

	/**
	 * Método que recebe uma palavra e lista suas possíveis traduções no
	 * idioma Inglês
	 * 
	 * @param word uma {@link String} que representa a palavra a ser traduzida
	 */	
	public void translate( String word , Language language) {

		init();
		
		wait_label.setVisible( true );
		wait_label.setText("Traduzindo...");
		paintComponents( getGraphics() );		
		repaint();
		
		manual_translate = false;
		
		ArrayList<String> translates = null;
		
		try {
			
			//Verificando o idioma da palavra
			switch( language ){
				case PORTUGUESE:
					translates = pten_translate.translateWord( word );
				break;
				case SPANISH:
					translates = esen_translate.translateWord( word );
				break;
				case ENGLISH:
					translates = new ArrayList<String>(  );
					translates.add( word );
				break;
			}
			
			
			if( translates.isEmpty() ){
				wait_label.setForeground( Color.RED );
				wait_label.setText("Não foi encontrado nenhuma tradução para: " + word );
			}else{				
				//Adicionando as palavras na lista de seleção
				for(int i=0; i < translates.size(); i++ ){
					translates_label.add( new TranslateLabel("", -1));
					list_model.addElement( translates.get( i ) );					
				}
				
				automatic_translate = 0;
				
				wait_label.setVisible( false );
			}
			
		} catch (Exception e) {
			
			wait_label.setText("Erro durante a tradução da palavra: "+ word );
			wait_label.setForeground( Color.RED );
					
			e.printStackTrace();
		
		}

		//Ativa os comandos de adicionar nova tradução 
		changeActives( true );
		
		repaint();
	}

	/**
	 * Adiciona uma pré-traduação à lista de traduções. Este método 
	 * é usando para palavra pré-anotadas, assim, sua possível tradução 
	 * é listada
	 */
	public void addTranslate( String word ){
		changeActives( true );
				
		translates_label.add( 0,   new TranslateLabel("", -1));
		list_model.addElement( word );
		
		automatic_translate++;
		repaint();
	}
	
	public void setTranslate( String translates[]){
		init();
		changeActives( true );
		
		if ( translates.length <= 0 )
			return;
	
		for( int i=0; i < translates.length; i++ ){
			translates_label.add( new TranslateLabel("", i ));
			list_model.addElement( translates[ i ] );			
		}
	}
	
	public void setSelectedTranslate( String translate ){
	
		for( int i=0; i < list_model.size(); i++ )
			if( list_model.get( i ).toString().compareTo( translate) == 0){
				list.setSelectedIndex( i );
				break;
			}
				
	}
	
	public String[] getTranslates(){
		
		if ( list_model.size() ==0 )
			return new String[ 0 ];

		String array[];
		int index = 0;
		//Se realmente foi selecionado uma traducao manualmente
		if( list.getSelectedIndex() < automatic_translate ){
			array = new String[ list_model.size() -  automatic_translate + 1 ];
			
			array[ 0 ] = (String) list.getSelectedValue();
			index = 1;
			
		}else{
			array = new String[ list_model.size() -  automatic_translate ];
		}
		
		for(int i= automatic_translate ; i < list_model.size(); i++)
			array[ index++ ] = (String) list_model.get( i );		
		
		return array;
	}
	
	
	/**
	 * Método para mudar o estado de ativo ou desativos dos componentes 
	 * da interface 
	 * @param add_translate se true, é permito adicionar novas traduções
	 */
	private void changeActives( boolean add_translate ){		
		this.new_translate.setEditable( add_translate );
		this.add_translate.setEnabled( add_translate );
	}
	
	/**
	 * Método envodado toda vez que uma palavra foi anotada. Este método
	 * prepara a interface para receber uma nova anotação
	 */
	public void init( ){
		
		list_model.clear();
		translates_label.clear();
		
		changeActives( false );
		
		wait_label.setForeground( list.getForeground() );
		wait_label.setVisible( false );
		
		automatic_translate = 0;
	}
	
	public int getAutomaticTranslate(){
		return automatic_translate;
	}

	public boolean selectManual() {
		return list.getSelectedIndex() < automatic_translate;
	}

	public void setTranslateLabel(String label, int index ) {		
		this.translates_label.set( list.getSelectedIndex(),  new TranslateLabel(
				"<html>" + list.getSelectedValue() + "<br />" + label, index ) );		
	}

}
