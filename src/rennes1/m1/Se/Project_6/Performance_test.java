package rennes1.m1.Se.Project_6;
import rennes1.m1.Se.Project_6.graph.Graph;
import rennes1.m1.Se.Project_6.binary.ThreadedBinary;

import java.security.InvalidParameterException;
import java.util.Scanner;

import rennes1.m1.Se.Project_6.binary.Binary;

public class Performance_test {

	public static void main(String[] args) throws InvalidParameterException, InterruptedException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Graph converter 1.0\\n ");
		loop: while(true) {
			System.out.print("graphConverter:localhost:~#");
			String cmd = sc.nextLine();
			switch(cmd) {
				case "s":
					simpleTest();
					break;
				case "b":
					System.out.print("please introduce the size of the graph\n");
					int p1 = sc.nextInt();
					System.out.print("please introduce the number of iterations for the test\n");
					int p2 = sc.nextInt();
					if(p1 > 0 && p2 > 0) {
						System.out.print("running tests with parameters:\nsize of Graph: " + p1 + "\niterations: " + p2);
						benchmarkTest(p1, p2);	
					}else {
						System.out.print("some or all parameters introduced are INVALID or INCOMPATIBLE");
					}
					break;
					
				case "e":
					System.out.print("please introduce the start size of the graph\n");
					int s1 = sc.nextInt();
					System.out.print("please introduce the end size of the graph\n");
					int s2 = sc.nextInt();
					System.out.print("please introduce the number of iterations for each test\n");
					int s3 = sc.nextInt();
					if(s1 < s2 && s1 > 0 && s2 > 0 && s3 > 0) {
						System.out.print("running bach of tests from size: " + s1 + " to size: " + s2 + " with " + s3 + " iterations per test...\n");
						extensiveTest(s1, s2, s3);
					} else {
						System.out.print("some or all parameters introduced are INVALID or INCOMPATIBLE");
					}
					break;
				case "q":
					System.out.println("quitting...");
					break loop;
				case "h":
					System.out.print("the following commands are available:\ns: executes a simple test\nb: executes a test with several iterations\ne: executes an extensive set of tests with different sized graphs\nq: quit the program");
				default:
					System.out.println(cmd + ": command not found. Use 'h' to get a list of valid commands");
				
			}
	
		}
				
		//simpleTest();
		//benchmarkTest(300, 100);
		//extensiveTest(600, 1050, 100);

	}
	
	public static void simpleTest() throws InvalidParameterException, InterruptedException {
		
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
		
		System.out.println("graph: " + mygraph.toString());
		
		Byte[] b = Binary.getBinary(mygraph);
		
		Byte[] c = ThreadedBinary.getBinary(mygraph);
		
		System.out.println("sequential encode:	" + Binary.binaryToString(b));
	
		System.out.println("threaded encode:	" + ThreadedBinary.binaryToString(c));
		
		Graph sg = Binary.getGraph(b);
		
		Graph tg = ThreadedBinary.getGraph(c);
		
		System.out.println("sequential result:	" + sg.toString());
		
		System.out.println("threaded result:	" + tg.toString());
	}
	
	public static void benchmarkTest(int size, int Size_of_tests) throws InvalidParameterException, InterruptedException {
		
		Graph mygraph	= new Graph(true);
		int vertixCount = size; //500 better performance, longer runtime
		for(int f = 0; f < vertixCount; f++) {
			mygraph.addVertix();
		}
		
		for(int w = 0; w < vertixCount; w++) {
			for(int x = 0; x < vertixCount; x++) { //vertixCount SUBSTITUTED HERE!!!!
				mygraph.addEdge(x, w);
			}
		}
		
		//String i = mygraph.toString();
		
		//System.out.println(i);
		Byte[] b;
		Byte[] c;
		
		int testSize = Size_of_tests;
		float averageToBinary = 0;
		float averageToGraph= 0;
		System.out.println("Graph encoding test starting...");
		
		for(int o = 0; o < testSize; o++) {
			long startTime = System.nanoTime();
			
			b = Binary.getBinary(mygraph);
			
			long sequentialTime = System.nanoTime() - startTime;
			
			startTime = System.nanoTime();
			
			c = ThreadedBinary.getBinary(mygraph);
			
			long threadedTime = System.nanoTime() - startTime;
			
			//System.out.println(Binary.binaryToString(b));
			//System.out.println(Binary.binaryToString(c));
			float percentage = (float)(sequentialTime-threadedTime)*100/(float)sequentialTime;
			boolean truth = Binary.binaryToString(b).equals(Binary.binaryToString(c));
			System.out.println("Sequential time:	" + sequentialTime + "ns	Threaded time:	" + threadedTime + "ns	threaded performance: " + percentage + "% better ");
			
			if(truth) {
				averageToBinary += percentage;
			}
			
		}
		System.out.println("done...");
		System.out.println("Graph retrieval test starting...");
		
		for(int o = 0; o < testSize; o++) {
			long startTime = System.nanoTime();
			
			b = Binary.getBinary(mygraph);
			
			long sequentialTime = System.nanoTime() - startTime;
			
			startTime = System.nanoTime();
			
			c = ThreadedBinary.getBinary(mygraph);
			
			long threadedTime = System.nanoTime() - startTime;
			
			//System.out.println(Binary.binaryToString(b));
			//System.out.println(Binary.binaryToString(c));
			float percentage = (float)(sequentialTime-threadedTime)*100/(float)sequentialTime;
			boolean truth = Binary.binaryToString(b).equals(Binary.binaryToString(c));
			System.out.println("Sequential time:	" + sequentialTime + "ns	Threaded time:	" + threadedTime + "ns	threaded performance: " + percentage + "% better ");
			
			if(truth) {
				averageToGraph += percentage;
			}else {
				System.out.println("!!!!");
			}
			
		}
		averageToBinary = averageToBinary / testSize;
		averageToGraph = averageToGraph/ testSize;
		System.out.println("binary conversion performance increase --> " + averageToBinary + "% graph retrieval performance Increase --> " + averageToGraph + "%");
		//Graph g = Binary.getGraph(b);
		
		//System.out.println(g.toString());
		
		System.out.println("done...");
	}
	
	
	public static void extensiveTest(int startNumber, int endNumber, int size_of_test) throws InvalidParameterException, InterruptedException {
		Graph mygraph;
		
		int maxTests = endNumber, startTests = startNumber;
		float[] averageToBinary = new float[maxTests/50];
		float[] averageToGraph = new float[maxTests/50];
		
		for(int testValue = startTests; testValue < maxTests; testValue = testValue + 50) {
			
			mygraph	= new Graph(true);
			int vertixCount = testValue; //500 better performance, longer runtime
			for(int f = 0; f < vertixCount; f++) {
				mygraph.addVertix();
			}
			
			for(int w = 0; w < vertixCount; w++) {
				for(int x = 0; x < vertixCount; x++) {
					mygraph.addEdge(w, x);
				}
			}
			
			//String i = mygraph.toString();
			
			//System.out.println(i);
			Byte[] b;
			Byte[] c;
			
			int testSize = size_of_test;
			int avgIndex = (testValue-startTests)/50;
			
			
			
			averageToBinary[avgIndex] = 0;
			averageToGraph[avgIndex] = 0;
			System.out.println("Graph encoding test starting...");
			
			for(int o = 0; o < testSize; o++) {
				long startTime = System.nanoTime();
				
				b = Binary.getBinary(mygraph);
				
				long sequentialTime = System.nanoTime() - startTime;
				
				startTime = System.nanoTime();
				
				c = ThreadedBinary.getBinary(mygraph);
				
				long threadedTime = System.nanoTime() - startTime;
				
				//System.out.println(Binary.binaryToString(b));
				//System.out.println(Binary.binaryToString(c));
				float percentage = (float)(sequentialTime-threadedTime)*100/(float)sequentialTime;
				boolean truth = Binary.binaryToString(b).equals(Binary.binaryToString(c));
				System.out.println("Sequential time:	" + sequentialTime + "ns	Threaded time:	" + threadedTime + "ns	threaded performance: " + percentage + "% better ");
				
				if(truth) {
					averageToBinary[avgIndex] += percentage;
				}
				
			}
			System.out.println("done...");
			System.out.println("Graph retrieval test starting...");
			
			for(int o = 0; o < testSize; o++) {
				long startTime = System.nanoTime();
				
				b = Binary.getBinary(mygraph);
				
				long sequentialTime = System.nanoTime() - startTime;
				
				startTime = System.nanoTime();
				
				c = ThreadedBinary.getBinary(mygraph);
				
				long threadedTime = System.nanoTime() - startTime;
				
				//System.out.println(Binary.binaryToString(b));
				//System.out.println(Binary.binaryToString(c));
				float percentage = (float)(sequentialTime-threadedTime)*100/(float)sequentialTime;
				boolean truth = Binary.binaryToString(b).equals(Binary.binaryToString(c));
				System.out.println("Sequential time:	" + sequentialTime + "ns	Threaded time:	" + threadedTime + "ns	threaded performance: " + percentage + "% better ");
				
				if(truth) {
					averageToGraph[avgIndex] += percentage;
				}else {
					System.out.println("!!!!");
				}
				
			}
			averageToBinary[avgIndex] = averageToBinary[avgIndex] / testSize;
			averageToGraph[avgIndex] = averageToGraph[avgIndex]/ testSize;
			System.out.println("binary conversion performance increase --> " + averageToBinary[avgIndex] + "% graph retrieval performance Increase --> " + averageToGraph[avgIndex] + "%");
			//Graph g = Binary.getGraph(b);
			
			//System.out.println(g.toString());
			
			System.out.println("done...");
		}
		
		System.out.println("Solutions:");
		for(int i = 0; i < averageToBinary.length; i++) {
			System.out.println(averageToBinary[i] + " " + averageToGraph[i]);
		}
	}

}
