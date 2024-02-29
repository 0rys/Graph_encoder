package rennes1.m1.Se.Project_6.graph;

import java.util.LinkedList;

public interface Igraphene {

	public void addVertix();
	
	public boolean checkVertix(int key);
	
	public void removeEdge(int key1, int key2);
	
	public int getSize();

	public boolean isDirected();
	
	public void setDirected(boolean directed);
	
}
