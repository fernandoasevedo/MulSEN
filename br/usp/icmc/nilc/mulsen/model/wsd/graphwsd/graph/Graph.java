package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph;

import java.util.Collection;
import java.util.HashMap;

public class Graph<Value, Tag,Relation, Mark> {
	private HashMap<String, Vertice<Value, Tag>> vertices;

	public Graph(){
		this.vertices = new HashMap<String,Vertice<Value, Tag>>();
	}
	
	public boolean contain( String vertice_id ){
		return this.vertices.containsKey( vertice_id );
	}
	
	public Collection<Vertice<Value, Tag>> getVertices(){
		return this.vertices.values();
	}
	
	public Vertice get( String vertice_id ){
		return this.vertices.get( vertice_id );	
	}
	
	public boolean addVertice( String vertice_id, Value value){
		
		if( this.contain( vertice_id ) )
			return false;
		
		Vertice<Value, Tag> new_vertice = new Vertice( value );
		this.vertices.put( vertice_id , new_vertice);
		
		return true;
	}
	
	public boolean addEdge( String u_id, String v_id, boolean directed, Relation relation){
		
		Vertice u;
		if( ( u = this.get( u_id ) ) == null  )
			return false;
		
		Vertice v;
		if( ( v = this.get( v_id ) ) == null  )
			return false;
		
		Edge<Relation, Mark> new_edge = new Edge<Relation, Mark>(u, v, directed, relation);
		u.addEdge( new_edge );		
		if( !directed )
			v.addEdge( new_edge );
		
		return true;
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		for( Vertice<Value, Tag> v : this.vertices.values() ){			
			builder.append( v.value );
			builder.append("[ ");			
			
			for( Vertice u : v.getNeighbors() ){
				builder.append( u.value );				
				builder.append( " ");
			}
			builder.append("] ");
		}
		
		return builder.toString();	
	}
	
	public String toEdges(){
		

		StringBuilder builder = new StringBuilder();
		for( Vertice<Value, Tag> v : this.vertices.values() ){			
			builder.append( v.value );
			builder.append("[ ");			
			
			for( Edge<Relation, Mark> e : v.edges ){
				if( e.u.equals( v ) )
					builder.append( e.v.value );
				else
					builder.append( e.u.value );
				builder.append("_");
				builder.append( e.relation );
			}
			
			builder.append("] ");
		}
		
		return builder.toString();	
	}
}
