package rennes1.m1.Se.Project_6;

import rennes1.m1.Se.Project_6.binary.Binary;
import rennes1.m1.Se.Project_6.graph.Graph;
import rennes1.m1.Se.Project_6.graph.Igraphene;
import rennes1.m1.Se.Project_6.graph.WeightedGraph;

public class mainW {

	
	public static void main(String[] args) {
		WeightedGraph mygraph = new WeightedGraph(false);
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addEdge(0, 0, 511);
		
		mygraph.addEdge(0, 1, 15);
		
		mygraph.addEdge(0, 2, 15);		
		
		mygraph.addEdge(0, 3, 15);
		
		mygraph.addEdge(0, 4, 15);
		
		mygraph.addEdge(0, 5, 15);

		mygraph.addEdge(1, 1, 15);
		
		mygraph.addEdge(1, 2, 15);
		
		mygraph.addEdge(1, 3, 15);		
		
		mygraph.addEdge(1, 4, 15);
		
		mygraph.addEdge(2, 2, 15);
		
		mygraph.addEdge(2, 3, 15);		
		
		mygraph.addEdge(2, 4, 15);
		
		mygraph.addEdge(3, 3, 15);		
		
		mygraph.addEdge(3, 4, 15);
		
		mygraph.addEdge(4, 4, 15);
		
		mygraph.addEdge(5, 1, 15);
		
		mygraph.addEdge(5, 2, 15);
		
		mygraph.addEdge(5, 3, 15);
		
		mygraph.addEdge(5, 4, 15);
		
		mygraph.addEdge(5, 5, -15);
		
		String i = mygraph.toString();
		
		System.out.println(i);
		
		Byte[] b = Binary.getBinary(mygraph);
		
		System.out.println(Binary.binaryToString(b));
		
		WeightedGraph g = Binary.getWeightedGraph(b);
		
		System.out.println(g.toString());
	}
}
