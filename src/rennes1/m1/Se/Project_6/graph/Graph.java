package rennes1.m1.Se.Project_6.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class Graph implements Igraphene{
	
	private boolean directed;
	
	private Map<Integer, LinkedList<Integer>> vertixMap;
	
	private int nVertix;
	
	
	public Graph(boolean directed) {
		this.directed = directed;
		this.nVertix = 0;
		this.vertixMap = new HashMap<>();	
	}



	@Override
	public void addVertix() {
		this.vertixMap.put(this.nVertix, new LinkedList<Integer>());
		this.nVertix++;
	}


	@Override
	public boolean checkVertix(int key) {
		return this.vertixMap.get(key) != null;
	}



	public void addEdge(int origin, int objective) {
		if(!checkVertix(origin) || !checkVertix(objective)) return;
		this.vertixMap.get(origin).add(objective);
		if(!this.directed && origin != objective) {
			this.vertixMap.get(objective).add(origin);
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



	public boolean checkEdge(int origin, int objective) {
		if(!checkVertix(origin)) return false;
		return this.vertixMap.get(origin).get(objective) != null;
	}
	
	public String toString() {
		String full_graph = "[ ";
		boolean i = true;
		boolean j = true;
		for(Entry<Integer, LinkedList<Integer>> entry : this.vertixMap.entrySet()) {
			if(!j) full_graph += ", ";
			full_graph +=  entry.getKey().toString() + " :( ";
			j = false;
			for(int v : entry.getValue()) {
				if(!i) full_graph += ", ";
				full_graph += Integer.toString(v);
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



	@Override
	public boolean isDirected() {
		return this.directed;
	}



	public LinkedList<Integer> getVertixList(int key) {
		return this.vertixMap.get(key);
	}



	@Override
	public void setDirected(boolean directed) {
		this.directed = directed;
	}

}
