package br.usp.icmc.nilc.mulsen.view.components;

import java.awt.Component;
import java.util.LinkedList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class TranslateCellRender implements ListCellRenderer{
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private LinkedList<TranslateLabel> translates_label;
	private JLabel renderer;
	
	public TranslateCellRender( LinkedList<TranslateLabel> translate_label ){
		this.translates_label = translate_label;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		renderer = (JLabel) defaultRenderer
		        .getListCellRendererComponent(list, value, index, isSelected,
		                cellHasFocus);
		
		if(  index <= translates_label.size() && !translates_label.isEmpty() )
			renderer.setToolTipText( translates_label.get( index ).label );
		
		return renderer;
	}
	

}
