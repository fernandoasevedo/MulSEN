package br.usp.icmc.nilc.mulsen.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nlputil.Security;

import org.jdom.JDOMException;

import br.usp.icmc.nilc.mulsen.controller.Annotation;
import br.usp.icmc.nilc.mulsen.controller.MainController;

/**
 * Classe principal, que se trata de um {@link JFrame} que recebe todos 
 * os componentes visuais.
 * 
 * @author fernando
 *
 */
public class Gui extends JFrame{
	
	//Componentes visuais
	private TextView text_view;
	private Translates translates;
	private SynsetView synset_view;

	//Controladores da aplicação
	private Annotation annotation;
	private MainController controller;

	private AnnotersView annoters_view;
	
	public Gui(){
		super("MulSEN -- Multilingual Sense Estimator from NILC");
		
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setSize(900, 500);		
		
		setContentPane( new JPanel() );
		getContentPane().setLayout( new GridLayout( 0, 1 ) );
		
		JSplitPane left_panel = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		
		//Criando e adicionando componentes
		text_view = new TextView();

		left_panel.add( text_view );
		left_panel.add( createTranslateView() );		
	
		JSplitPane central_panel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		
		central_panel.add( left_panel );
		central_panel.add(createSynsetView() );
		add( central_panel );
		createMenu();		
		
		annoters_view = new AnnotersView( this );
		
		//Criando e adicionando os controladores
		annotation = new Annotation( translates, synset_view , text_view, annoters_view );
		text_view.setController( annotation );
		translates.setController( annotation );
		synset_view.setController( annotation );
		
		try {
			controller = new MainController( this );
			
		} catch (IOException e) {		
			e.printStackTrace();
		}

		addFrameEvents();
		
		show();
		annoters_view.show();
		left_panel.setDividerLocation( 0.6 );
		central_panel.setDividerLocation( 0.5 );
		
	}
	
	@Override
	public void resize(int width, int height) {	
		super.resize(width, height);		
		System.out.println("MulSeN...");
	}
	
	
	private void addFrameEvents() {
		
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent e ) {
				
				if( !controller.isOpen() ){
					dispose();
					return;
				}
							
				String options[] = {"Sim", "Não" ,"Cancelar"};
				int op = JOptionPane.showOptionDialog(
						Gui.this, 
						"Deseja salvar a anotação em formato txt?\nCancelar para voltar à anotação!",
						"Salvar/Sair",
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE,
						null, 
						options,
						options[ 0 ]);
				
				if ( op != JOptionPane.CANCEL_OPTION ){
					
					annotation.saveXMLFile( true );
					
					if ( op == JOptionPane.YES_OPTION ){
						controller.saveTXTFile( text_view );
						dispose();
					}
					
					if( op == JOptionPane.NO_OPTION )
						dispose();
				}	
			
			}
			
		});
	}

	/**
	 * Criação do menu da aplicação
	 */
private void createMenu() {
		
		final JMenuBar menu_bar = new JMenuBar();
		
		//Criando os menus
		final JMenu file = new JMenu("Arquivo");
		final JMenu annotation = new JMenu("Anotação");
		
		JMenuItem open = new JMenuItem("Abrir Arquivo(s)");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker(){
					@Override
					protected Object doInBackground() throws Exception {
						try{			
							file.setEnabled( false );
							annotation.setEnabled( false );
							translates.init();
							synset_view.init();
							controller.openTXTFiles( text_view );						
						}catch( IllegalArgumentException ex ){							
							errorPrint( ex.getMessage() );
							file.setEnabled( true );
							annotation.setEnabled( true );
						}						
						return null;
					}
					
					
					protected void done() {					
						file.setEnabled( true );
						annotation.setEnabled( true );
					};
				}.execute();
				
			}
		});
		file.add( open );
		
		JMenuItem open_annotation = new JMenuItem("Abrir Anotação");
		open_annotation.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
									
					controller.openAnnotation( text_view , annoters_view);
					
					translates.init();
					synset_view.init();
					
				}catch( IllegalArgumentException ex ){
					errorPrint( ex.getMessage() );
					
				} catch (IOException e) {				
					e.printStackTrace();
					
				} catch (JDOMException e) {				
					e.printStackTrace();
				}
				
				menu_bar.setEnabled( true );
			}
		});
		file.add( open_annotation );
		
		JMenuItem save = new JMenuItem("Salvar Anotação em Texto");
		save.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveTXTFile( text_view );
			}
		});
		annotation.add( save );
		
		JMenuItem annoters = new JMenuItem("Anotadores");
		annoters.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				annoters_view.show();
			}
		});
		annotation.add( annoters );
		
		//Criando o menu Ajuda
		JMenu help = new JMenu("Ajuda");
		
		JMenuItem about = new JMenuItem("Sobre");
		about.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				
				String msg = "<html>" +
						"Ferramenta desenvolvida no <b>Núcleo Interinstitucional de Lingüística Computacional (NILC)</b>,<br>" +
						"no intuito de auxiliar anotação de córpus na tarefa de Desambiguação Lexical de Sentido<br />" +
						"para o Português do Brasil.<br /><br />" +
						"Desenvolvido por:<br />" +
						"	(Mestrando) Fernando Antônio Asevedo Nóbrega<br />" +
						"	(Orientador) Prof. Dr. Thiago Alexandre Salgueiro Pardo" +
						"<br /><br />ICMC -- USP<br />2012";
				
				JOptionPane.showMessageDialog( null, msg, "Sobre", JOptionPane.INFORMATION_MESSAGE );
			}
		});
		
		help.add( about );
		
		JMenuItem help_view = new JMenuItem("Ajuda");
		help.add( help_view );
		
		//Adicionando os menus da barra
		menu_bar.add( file );	
		menu_bar.add( annotation );
		menu_bar.add( help );
		
		//Adicionando a barra no JFrame
		setJMenuBar( menu_bar );
	}

	private void errorPrint(String message) {
		
		JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	private JPanel createTranslateView(){
		
		JPanel painel = new JPanel();
		painel.setLayout( new BorderLayout() );	
		
		translates = new Translates();
		translates.setController( annotation );
		
		painel.add( translates, BorderLayout.CENTER );
		
		return painel;		
	}
	
	/**
	 * Criando os componentes para anotação dos sentidos
	 */
	private JPanel createSynsetView(){
		JPanel painel = new JPanel();
		painel.setLayout( new BorderLayout() );		

		synset_view = new SynsetView();	
		painel.add( synset_view , BorderLayout.CENTER );
		painel.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return painel;
	}
	
	public static void main( String a[] ){

		System.setProperty("file.encoding", "UTF-8");		

		try {
			//Carregando as configurações no arquivo
			Security.read();
			System.setProperty("wordnet.database.dir", Security.getInfo("wordnet_dir") );
			System.setProperty("treetagger.home", Security.getInfo("wordnet_dir") );
			System.setProperty("treetagger.home", "lib"+ File.separator + "treetagger"  );

			SwingUtilities.invokeLater(new Runnable() {
			      public void run() {
			        new Gui().setVisible(true);
			      }
			 });

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

