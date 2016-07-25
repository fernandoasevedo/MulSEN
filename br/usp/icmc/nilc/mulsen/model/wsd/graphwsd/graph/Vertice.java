package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph;

import java.util.LinkedList;


/**
 * 
 * @author fernando
 *
 * @param <Value> Um valor que é armazenado no vértice
 * @param <Tag> Uma possível marcação que pode ser utilizada em algumas implementações
 */
public class Vertice<Value, Tag> {
	
	protected Value value;
	protected Tag tag;
	protected LinkedList<Edge> edges;
	
	protected boolean visited;
	
	public Vertice( Value value ){
		this.value = value;
		this.tag = null;
		this.edges = new LinkedList<Edge>();
		
		this.visited = false;
	}
	
	public Value getValue(){ return this.value; }
	
	public Tag getTag(){ return this.tag; }
	
	public void setTag( Tag new_tag ){ this.tag = new_tag; }
	
	public boolean isVisited( ) { return this.visited; }
	
	public void setVisited( boolean new_visited ){ this.visited = new_visited; }
	
	public LinkedList<Edge> edges(){ return this.edges; }
	
	protected void addEdge( Edge new_edge ){ this.edges.add( new_edge ); }
	
	/**
	 * @return Um vetor contendo todos os vértices vizinhos
	 * 
	 * Este método não usa o Pametrics, pois vértices podem armazenar objetivos de diferentes classes
	 */
	public Vertice[] getNeighbors(){
		
		Vertice neighbors[] = new Vertice[ edges.size() ];
		int index = 0;
		for( Edge e : this.edges )
			if( !e.v.equals( this ) ) 
				neighbors[ index++ ] = e.v;
			else 
				neighbors[ index++ ] = e.u;
		
		return neighbors;
	}
	
}
