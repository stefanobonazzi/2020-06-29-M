package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private Graph<Director, DefaultWeightedEdge> graph;
	private List<Director> vertices;
	private List<Director> best;
	private int totAttori;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public String creaGrafo(Integer year) {
		this.graph = new SimpleWeightedGraph<Director, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		this.vertices = this.dao.listDirectors(year);
		Graphs.addAllVertices(this.graph, this.vertices);
		
		for(Director d1: this.vertices) {
			for(Director d2: this.vertices) {
				DefaultWeightedEdge edge = this.graph.getEdge(d1, d2);
				
				if(!d1.equals(d2) && edge == null) {
					int weight = this.dao.listActors(d1, d2, year);
					
					if(weight > 0) {
						edge = this.graph.addEdge(d1, d2);
						this.graph.setEdgeWeight(edge, weight);
					}
				}
			}
		}
		
		String s = "Grafo Creato\n#VERTICI: "+this.graph.vertexSet().size()+"\n#ARCHI: "+this.graph.edgeSet().size();
		return s;
	}

	public List<Director> getVertices() {
		return vertices;
	}

	public String registiAdiacenti(Director d) {
		Map<Integer, Director> res = new HashMap<Integer, Director>();
		for(Director dir: Graphs.neighborListOf(this.graph, d)) 
			res.put((int) this.graph.getEdgeWeight(this.graph.getEdge(d, dir)), dir);
		List<Integer> l = new LinkedList<>(res.keySet());
		Collections.sort(l);
		Collections.reverse(l);
		
		String s = "REGISTI ADIACENTI A: "+d+"\n";
		for(Integer i: l) 
			s += res.get(i)+" -  # attori condivisi: "+i+"\n";
		
		return s;
	}

	public String registiAffini(Director d, Integer c) {
		this.best = new ArrayList<Director>();
		this.totAttori = 0;
		
		List<Director> parziale = new ArrayList<Director>();
		parziale.add(d);
		this.ricorsiva(parziale, c, 0);
		
		String s = "Registi Affini:\nAttori condivisi totali: "+this.totAttori+"\n";
		for(Director dd: this.best) 
			s += dd+"\n";
	
		return s;
	}

	private void ricorsiva(List<Director> parziale, Integer c, int x) {
		if(parziale.size() > this.best.size()) {
			this.best = new ArrayList<Director>(parziale);
			this.totAttori = x;
		} 
			
		Director dir = parziale.get(parziale.size()-1);
		for(Director d: Graphs.neighborListOf(this.graph, dir)) {
			if(!parziale.contains(d) && x+this.graph.getEdgeWeight(this.graph.getEdge(dir, d)) <= c) {
				parziale.add(d);
				this.ricorsiva(parziale, c, (int) (x+this.graph.getEdgeWeight(this.graph.getEdge(dir, d))));
				parziale.remove(d);
			}
		}
		
	}

}
