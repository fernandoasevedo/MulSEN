package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.build.edgeBuild;

import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Edge;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Graph;
import br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph.Vertice;

public class WeightEdge<Value, Tag, Relation, Mark> implements EdgeBuilder<Value, Tag, Relation, Mark> {

	@Override
	public void edgeBuilde(String u_id, String v_id,
			Graph<Value, Tag, Relation, Mark> graph) {
		
		Vertice<Value, Tag> u = graph.get( u_id );
		Vertice<Value, Tag> v = graph.get( v_id );
		boolean relation_plus = false;
		for( Edge<Relation, Mark> e : u.edges() ){
			
			if( e.getU().equals( v ) || e.getV().equals( v ) ){
				int r = (java.lang.Integer) e.getRelation();
				e.setRelation(  (Relation) new Integer( ++r ) );
				relation_plus = true;
				break;
			}
		}
		
		if ( !relation_plus )
			graph.addEdge(u_id, v_id, false, (Relation) new Integer( 1 ) );
	}
}

