package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.build.edgeBuild;

import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Graph;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Vertice;

public interface EdgeBuilder<Value, Tag, Relation, Mark> {

	public void edgeBuilde( String u_id, String v_id, Graph<Value, Tag, Relation, Mark> graph); 
}
