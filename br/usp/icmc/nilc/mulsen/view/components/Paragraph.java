package br.usp.icmc.nilc.mulsen.view.components;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Paragraph extends JPanel{

	public Paragraph(){
		super();

		setSize(50, 100);		
		setOpaque( true );
		setBackground( Color.WHITE );
		setLayout( new FlowLayout( FlowLayout.LEFT ));
		setAutoscrolls( false );
		setBorder( BorderFactory.createEmptyBorder(0, 0, 5, 0));
	
	}
}
