package rennes1.m1.Se.Project_6.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;

public class WeightedGraph implements Igraphene{
	
	private boolean directed;
	
	private Map<Integer, Map<Integer, Integer>> vertixMap;
	
	private int nVertix;
	
	private int weightLimit;

	
	public WeightedGraph(boolean directed) {
		this.weightLimit = 0;
		this.nVertix = 0;
		this.directed = directed;
		this.vertixMap = new HashMap<>();
	}
	
	@Override
	public void addVertix() {
		this.vertixMap.put(this.nVertix, new HashMap<>());
		this.nVertix++;
	}

	@Override
	public boolean checkVertix(int key) {
		return this.vertixMap.get(key) != null;
	}


	public void addEdge(int origin, int objective, int weight) {
		if(!checkVertix(origin) || !checkVertix(objective)) return;
		if(Math.abs(this.weightLimit)< Math.abs(weight)) this.weightLimit = weight;
		this.vertixMap.get(origin).put(objective, weight);
		if(!this.directed && origin != objective) {
			this.vertixMap.get(objective).put(origin, weight);
		}
	}

	@Override
	public void removeEdge(int origin, int objective) {
		if(!checkVertix(origin) || !checkVertix(objective)) return;
		this.vertixMap.get(origin).remove(objective);
		if(!this.directed) {
			this.vertixMap.get(objective).remove(origin);
		}
	}

	public int checkEdge(int origin, int objective) {
		if(!checkVertix(origin)) return 0;
		return this.vertixMap.get(origin).get(objective);
	}
	
	public Map getEdges(int key) {
		if(!checkVertix(key)) return null;
		return this.vertixMap.get(key);
	}

	public String toString() {
		String full_graph = "[ ";
		boolean i = true;
		boolean j = true;
		for(Entry<Integer, Map<Integer, Integer>> entry : this.vertixMap.entrySet()) {
			if(!j) full_graph += ", ";
			full_graph +=  entry.getKey().toString() + " :( ";
			j = false;
			for(Entry<Integer, Integer> entro : entry.getValue().entrySet()) {
				if(!i) full_graph += ", ";
				full_graph += "(" + Integer.toString(entro.getKey()) + ", " + entro.getValue() + ")";
				i = false;
			}
			i = true;
			full_graph += " )";
		}
		full_graph += "]";
		return full_graph;
	}
	
	@Override
	public int getSize() {
		return this.nVertix;
	}
	
	public int weightLimit() {
		return this.weightLimit;
	}

	@Override
	public boolean isDirected() {
		return this.directed;
	}

	@Override
	public void setDirected(boolean directed) {
		this.directed = directed;
		
	}
	
}
