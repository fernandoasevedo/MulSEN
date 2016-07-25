package br.usp.icmc.nilc.mulsen.view.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;

public class SynsetCellRender  implements ListCellRenderer{
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private Synset synset;
	
	private boolean viewHypernyms;
	private boolean viewtHyponyms;
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel renderer = (JLabel) defaultRenderer
		        .getListCellRendererComponent(list, value, index, isSelected,
		                true );
		
		renderer.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5), 
				BorderFactory.createLineBorder( Color.GRAY, 1) )
				);
		
		this.synset = ((Synset) value);
		Synset hypers[] = ((NounSynset) synset).getHypernyms();
		Synset hypo[] = ((NounSynset) synset).getHyponyms();
		
		//Exemplos de uso do sinônimo
		String samples="";
		int count = 1;
		for(String aux : synset.getUsageExamples() ){
			samples+="<li>Ex. " + count + ": " + aux +"\n";
			count++;
		}
		if ( count > 1)
			samples = "<ul>" + samples + "</ul>";
		
		//Hiperônimos
		String hypeText = "";
		if ( viewHypernyms ){
			count = 1;
			hypeText = "<ul>";
			for( Synset h : hypers ){
				hypeText+="<li>Hiperônimo " + count + ":" + formatSynonyms( h ) + " " + h.getDefinition();
				count++;
			}
			hypeText += "</ul>";
		}
		
		//Hiperônimos
		String hypoText = "";
		if ( viewtHyponyms ){
			count = 1;
			hypoText = "<ul>";
			for( Synset h : hypo ){
				hypoText+="<li>Hipônimo " + count + ":" + formatSynonyms( h ) + " " + h.getDefinition();
				count++;
			}
			hypoText += "</ul>";
		}
		
		renderer.setText("<html>" +
				(index + 1) + " -- " +
				formatSynonyms( synset ) + "<br />" +
				synset.getDefinition() + 
				samples+
				hypeText+
				hypoText+
				"</html>");
		
		return renderer;
	}
	
	
	public static String formatSynonyms( Synset noun ){
		
		String text = "";
		boolean first = true;
		for(String aux : noun.getWordForms() ){
			if ( first ){
				text = aux;
				first = false;
				continue;
			}
			
			text+= ", " + aux;
		}
		
		return "{" + text + "}"; 
	}
	
	public Synset getSynset(){
		return synset;
	}

	public void setViewHypernyms( boolean flag ){
		this.viewHypernyms = flag;
	}
	
	public void setViewtHyponyms( boolean flag ){
		this.viewtHyponyms = flag;
	}
}
