package br.usp.icmc.nilc.mulsen.model.wsd.graphwsd.graph;

/**
 * 
 * @author fernando
 *
 * @param <Relation>
 * @param <Mark> 
 */
public class Edge<Relation, Mark> {
	
	//Vertice de origem e destino respectivamente, caso a aresta seja direcionada
	protected Vertice u,v;
	protected Relation relation;
	
	protected Mark mark;
	
	protected boolean visited;
	protected boolean directed;
	
	public Edge( Vertice u, Vertice v, boolean directed, Relation relation){
		this.u = u;
		this.v = v ;
		this.relation = relation;
		this.directed = directed;
		this.visited = false;
		mark = null;
	}
	
	/**
	 * @return Um {@link Vertice} u, que representa o vertice de origem quando a aresta é direcionada
	 */
	public Vertice getU(){ return this.u; }
	
	/**
	 * @return Um {@link Vertice} v, que representa o vertice de destino quando a aresta é direcionada
	 */
	public Vertice getV(){ return this.v; }
	
	/**
	 * @return Uma {@link Relation} que representa o tipo de relação que a aresta armazena entre os vértices u e v
	 */
	public Relation getRelation(){ return this.relation; }
	
	/**
	 * Altera a relação entre os vértices U e V da aresta
	 * @param relation A nova {@link Relation} que será armazenada pela aresta 
	 */
	public void setRelation( Relation relation ){
		this.relation = relation;
	}
	
	/**
	 * @return true, caso a aresta seja direcionada do {@link Vertice} U para o {@link Vertice} V
	 * @return false, caso a aresta não seja direcionada do {@link Vertice} U para o {@link Vertice} V
	 */
	public boolean isDirected(){ return this.directed; }
	
	/**
	 * @return Uma {@link Mark} que está armanzenado na aresta
	 */
	public Mark getMark(){ return this.mark; }
	
	/**
	 * Altera o valor da marcação armazenada na aresta
	 * @param new_mark Uma {@link Mark} representando a nova marcação da aresta
	 */
	public void setMark( Mark new_mark ){
		this.mark = mark;
	}

}
