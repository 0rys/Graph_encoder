package rennes1.m1.Se.Project_6;

import rennes1.m1.Se.Project_6.binary.Binary;
import rennes1.m1.Se.Project_6.graph.Graph;
import rennes1.m1.Se.Project_6.binary.ThreadedBinary;

import java.security.InvalidParameterException;



public class mainPush {

	public static void main(String[] args) throws InvalidParameterException, InterruptedException {
		Graph mygraph = new Graph(false);
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addVertix();
		
		mygraph.addEdge(0, 0);
		
		mygraph.addEdge(0, 3);
		
		mygraph.addEdge(0, 1);		
		
		mygraph.addEdge(0, 2);
		
		mygraph.addEdge(0, 4);
		
		mygraph.addEdge(0, 5);
		
		mygraph.addEdge(5, 4);
		
		mygraph.addEdge(5, 1);
		
		mygraph.addEdge(5, 2);
		
		mygraph.addEdge(5, 3);
		
		mygraph.addEdge(5, 5);
		
		Byte[] a = Binary.getBinary(mygraph);
		
		Byte[] b = ThreadedBinary.getBinary(mygraph);
		
		System.out.println(Binary.binaryToString(a));
		
		System.out.println(Binary.binaryToString(b));
		
		Graph c = Binary.getGraph(a);
		
		Graph d = ThreadedBinary.getGraph(b);
		
		String sequentialString = c.toString(), threadedString = d.toString();
		
		System.out.println(sequentialString);
		
		System.out.println(threadedString);
		
		System.out.println(sequentialString.equals(threadedString));
		
		
		
	}

}
