package br.usp.icmc.nilc.mulsen.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import br.usp.icmc.nilc.mulsen.view.components.SynsetCellRender;

import br.usp.icmc.nilc.mulsen.model.WordNet;
import br.usp.icmc.nilc.mulsen.controller.Annotation;
import edu.smu.tspell.wordnet.Synset;

public class SynsetView extends JPanel{
	private JList list;
	private JButton select_synset;
	private JButton remove_synset;
	private JCheckBox view_hype;
	private JCheckBox view_hypo;
	private JLabel error_msg;
	private DefaultListModel list_model;
	private SynsetCellRender cell_render;
	
	public SynsetView(){
		super();	
		setBorder( new TitledBorder("2 - Escolha o Synset")) ;
		
		addComponents();
	}

	private void addComponents() {
	
		setLayout( new BorderLayout() );
		
		/*Adicionando comandos de visualização*/
		JPanel view_buttons = new JPanel( new FlowLayout( FlowLayout.LEFT));
		
		view_hype = new JCheckBox("Ver Hiperônimos");
		view_hype.setToolTipText("Clique para ativar ou desativar a visualização dos Hiperônimos dos synsets");
		view_hype.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {				
				cell_render.setViewHypernyms( view_hype.isSelected() );
				
				for( int i=0; i < list_model.size(); i++)
					list_model.set( i, list_model.get( i ) );
			}
		});
		view_buttons.add( view_hype );

		view_hypo = new JCheckBox("Ver Hipônimos");
		view_hypo.setToolTipText("Clique para ativar ou desativar a visualização dos Hipônimos dos synsets");
		view_hypo.addActionListener( new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				cell_render.setViewtHyponyms( view_hypo.isSelected() );
				//TODO talvez há um jeito mais eficaz
				for( int i=0; i < list_model.size(); i++)
					list_model.set( i, list_model.get( i ) );
			}
		});
		view_buttons.add( view_hypo );
		
		add( view_buttons, BorderLayout.NORTH );
		
		/*Adicionando labels informativos*/
		JPanel main_panel = new JPanel();
		main_panel.setLayout( new BoxLayout( main_panel, BoxLayout.Y_AXIS ));
		main_panel.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		main_panel.add( new JLabel("Synsents:"));
		main_panel.add(  Box.createRigidArea( new Dimension(0, 5)));
		
		error_msg = new JLabel("Não foi encontrado nenhum synset");
		error_msg.setForeground( Color.RED );
		
		main_panel.add( error_msg );
		error_msg.setVisible( false );		
		main_panel.add(  Box.createRigidArea( new Dimension(0, 5)));
		
		/*Adicionando a lista de visualização de synsets*/
		list = new JList();
		list.setLayoutOrientation( JList.VERTICAL );
		list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		//modelo da lista
		list_model = new DefaultListModel();
		list.setModel( list_model );
		cell_render = new SynsetCellRender();
		list.setCellRenderer(cell_render );
		
		//adicionano a lista no painel
		JScrollPane list_panel = new JScrollPane(list);		
		list_panel.setAlignmentX(LEFT_ALIGNMENT);
		main_panel.add( list_panel );
		main_panel.add( Box.createRigidArea( new Dimension(0, 5)));
			
		add( main_panel, BorderLayout.CENTER );
		
		//Painel para adicionar comandos de anotação
		JPanel annotation_buttons = new JPanel( new FlowLayout( FlowLayout.LEFT ));
		
		//botão de selecionar anotação
		select_synset = new JButton("Selecionar Synset");
		annotation_buttons.add( select_synset );
				
		remove_synset = new JButton("Remover anotação");
		annotation_buttons.add( remove_synset );
		
		add( annotation_buttons, BorderLayout.SOUTH );
		
		changeActives( false, false);
	}

	public void setController(final Annotation annotation) {
		
		select_synset.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				
				if ( annotation == null )
					return;
				
				if( list.getSelectedIndex() == -1)
					return;
				
				Synset synsets[] = new Synset[ list_model.size() ];
				for( int i=0; i < synsets.length; i++)
					synsets[ i ] = (Synset) list_model.get( i );
			
				annotation.note( list.getSelectedIndex(), synsets ) ;
			}
		});
		
		remove_synset.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				annotation.removeAnnotation();			
			}
		});
		
		list.addListSelectionListener( new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if ( list.getSelectedIndex() < 0 )
					return;		
				
				Synset s = (Synset) list_model.get( list.getSelectedIndex() );
				
				annotation.setTranslateLabel("<html>" +						
						( list.getSelectedIndex() + 1) + "--" + 
						SynsetCellRender.formatSynonyms( s ) + "<br />" + 
						s.getDefinition() + "</html> ",
						list.getSelectedIndex() );
			}
		});
	}

	public void search(String word) {
		
		clear();
		
		Synset synsets[] = WordNet.getSynonyms( word );
		
		if( synsets.length == 0 )
			error_msg.setVisible( true );
		else
			changeActives( true,  remove_synset.isEnabled() );//Permite a seleção de synsets sem alterar o estado de remover synset
		
		for (int i = 0; i < synsets.length; i++)
			list_model.addElement( synsets[ i ] );
	}

	/**
	 * Limpa os valores da lista
	 */
	public void clear() {
		error_msg.setVisible( false );
		list_model.clear();				
	}

	/**
	 * Seleciona um determinado synset. Este método é aplicado quando
	 * se deseja re-anotar uma palavra ou quando esta palavra foi previamente
	 * anotada
	 * 
	 * @param synset o synset que será selecioando
	 */
	public void setSelectedSynset(Synset synset) {
		
		for( int i=0; i < list_model.size(); i++ ){
			if ( synset.equals( (Synset) list_model.get( i ) )){
				list.setSelectedIndex( i );
				break;
			}
		}
		
		changeActives( true, true);
	}
	
	/**
	 * Seleciona um determinado synset. Este método é aplicado quando
	 * se deseja re-anotar uma palavra ou quando esta palavra foi previamente
	 * anotada
	 * 
	 * @param synset o synset que será selecioando
	 */
	public void setSelectedSynset(int index) {
		
		list.setSelectedIndex( index );
	
		changeActives( true, true);
	}
	
	/**
	 * Método para mudar o estado de ativo ou desativos dos componentes 
	 * da interface 
	 * @param annote se true, o botão de selecionar synset fica ativado
	 * @param remove se true, o botão de remover synset fica ativado
	 */
	private void changeActives( boolean annote, boolean remove ){		
		this.select_synset.setEnabled( annote );
		this.remove_synset.setEnabled( remove );
	}
	
	/**
	 * Método envodado toda vez que uma palavra foi anotada. Este método
	 * prepara a interface para receber uma nova anotação
	 */
	public void init( ){
		list_model.clear();
		changeActives(false, false);
	}
}
