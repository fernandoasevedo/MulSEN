package br.usp.icmc.nilc.mulsen.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AnnotersView extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultListModel list_model;
	private JList list;
	
	public AnnotersView(final JFrame frame ){
		super( frame, "Anotadores" );
		
		setSize(400, 200);
		
		setLayout( new BorderLayout() );
		
		//Adicionando a lista
		list = new JList();
		list.setBorder( BorderFactory.createTitledBorder("Anotadores") ); 
		list_model = new DefaultListModel();
		list.setModel( list_model );		
		add( list, BorderLayout.CENTER );
		
		JPanel buttons_panel = new JPanel();
		buttons_panel.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GroupLayout layout = new GroupLayout( buttons_panel );
		buttons_panel.setLayout( layout  );
		
		JButton add = new JButton("Adicionar anotador");
		add.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				String name = JOptionPane.showInputDialog(
						frame,
						"Digite o nome do anator",
						"Anotadores",
						JOptionPane.QUESTION_MESSAGE );

				if ( name == null )
					return;
				
				if( name.isEmpty() )
					return;
				
				list_model.addElement( name );

			}
		});
		
		JButton edit = new JButton("Editar anotador");
		edit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				String name = (String) list.getSelectedValue();
				if( name == null ){
					JOptionPane.showMessageDialog(
						frame,
						"Para editar um anotador, deve-se selecionar um nome na lista!",
						"Editar anotador",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				name = JOptionPane.showInputDialog(
						frame, 
						"Digite o novo nome para o anotador: " + name ,
						"Edição",
						JOptionPane.QUESTION_MESSAGE );

				if ( name == null )
					return;
				
				if( name.isEmpty() )
					return;
				
				list_model.set( list.getSelectedIndex(), name);
			}
		});
		
		JButton delete = new JButton("Excluir anotador");
		delete.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
			
				if( list.getSelectedIndex()  == -1 ){
					JOptionPane.showMessageDialog(
						frame,
						"Para excluir um anotador, deve-se selecionar um nome na lista!",
						"Excluir anotador",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int option = JOptionPane.showConfirmDialog(
						frame,
						"Deseja realmente excluir o anotador: " + list.getSelectedValue(),
						"Excluir anotador", 
						JOptionPane.YES_NO_OPTION );
			
				if ( option == JOptionPane.YES_OPTION )
					list_model.remove( list.getSelectedIndex() );
									
			}
		});
		
		layout.setHorizontalGroup(			
			layout.createParallelGroup()
				.addGap( 5 )
				.addComponent( add , 170, 170, 170)
				.addGap( 5 )
				.addComponent( edit , 170, 170, 170)
				.addGap( 5 )
				.addComponent( delete, 170, 170, 170)
		);
	
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGap( 5 )
					.addComponent( add )
					.addGap( 5 )
					.addComponent( edit )
					.addGap( 5 )
					.addComponent( delete )
			);

		add( buttons_panel , BorderLayout.EAST );
		
		dispose();
	}
	
	public static void main(String[] args) {
		
		AnnotersView a = new AnnotersView( null );
		a.setVisible( true );
	}
	
	public ArrayList<String> getAnnoters(){
		
		ArrayList<String> array = new ArrayList<String>( list_model.size() );
		for (int i = 0; i < list_model.size(); i++) 
			array.add( (String) list_model.get( i ) );
		
		return array;
	}

	public void addAnnotator(String annoter_name) {
		list_model.addElement( annoter_name );
	}
}
