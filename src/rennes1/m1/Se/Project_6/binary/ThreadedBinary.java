package rennes1.m1.Se.Project_6.binary;

import java.security.InvalidParameterException;
import java.util.LinkedList;

import rennes1.m1.Se.Project_6.graph.Graph;
import rennes1.m1.Se.Project_6.graph.WeightedGraph;


public class ThreadedBinary{
	
	public static Byte[] getBinary(Graph graph) throws InvalidParameterException, InterruptedException{
		
		int headerSize = 16;																//can be increased up to 64
		int myGraphSize = graph.getSize();
		int binaryStringSize = (int) Math.ceil((headerSize + myGraphSize*myGraphSize)/8.0);
		
		if(Math.pow(2, headerSize-1)-1 < myGraphSize) throw new InvalidParameterException("The input graph is too big");
		
		Byte[] binary = new Byte[binaryStringSize];
	
		Byte[] mySize = getBinary(myGraphSize);
		
		for(int i = 0; i < headerSize/8 ; i++) { //loads the size of the graph into the header
			
			binary[i] = (byte) mySize[i+(4-headerSize/8)];
			
		}
		
		if(graph.isDirected()) { //sets the first bit to 0 -> not directed, 1 -> directed
		
			binary[0] = (byte) (binary[0] | 0x80);
		
		}else {
		
			binary[0] = (byte) (binary[0] & 0x7f);
		
		}
		
		for(int i = 0; i < binary.length; i++) {
			if(binary[i] == null) binary[i] = (byte) 0x00;
		}
		int arrLength;
		arrLength = (int)(Math.ceil((myGraphSize/8.0))+1);
		int threadCount = 6;
		Thread[] threadPool = new Thread[threadCount];
		for(int n = 0; n < threadCount; n++) {
			threadPool[n] = new Thread() {
				
				public void run() {
					extracted(graph, headerSize, myGraphSize, binary, arrLength, threadCount);
				}
			};
			threadPool[n].setName(Integer.toString(n));
			threadPool[n].start();
		}
		
		for(int y = 0; y < threadCount; y++) {
			threadPool[y].join();
			//threadPool[y].interrupt();
		}

		return binary;
	}

private static void extracted(Graph graph, int headerSize, int myGraphSize, Byte[] binary, int arrLength, int threadCount) {
	int threadId = Integer.parseInt(Thread.currentThread().getName());
	int offset;
	int chunk = myGraphSize/(threadCount);
	chunk = (chunk == 0)? 1: chunk;
	int scheduler = (myGraphSize%threadCount == 0)? 0: 1;
	for(int s = 0; s < scheduler+1; s++) {
		int ceiling = (chunk*(threadId+1))+ chunk*s*threadCount;
		int start = (chunk*threadId) + chunk*s*threadCount;
		for(int i = start; i < ceiling ; i++) { //runs through each vertix, encodes it into binary and adds it to the Byte array
			if(i < myGraphSize) {
				offset = (i*myGraphSize);
				Byte[] myCodedInfo = getByteFormatted(arrLength, graph.getVertixList(i), offset%8);
				
				for(int j = 0; j < myCodedInfo.length; j++) {
					int index = j + offset/8 + (headerSize/8);
					if(index < binary.length) {
						binary[index] = (byte) (binary[index] | myCodedInfo[j]);	
					}
				}	
			}else {
				return;
			}
		}
	}
	return;
		
}
	
	
	public static Graph getGraph(Byte[] binary) throws InterruptedException {
		
		int headerSize = 16;
		int byteHeaderSize = headerSize/8;
		
		Byte[] header = new Byte[4];
		
		for(int i = 0; i < byteHeaderSize; i++) {
			header[i + (4-byteHeaderSize)] = binary[i]; 
		}
		for(int i = 0; i< header.length; i++) {
			if(header[i] == null) header[i] = 0x00;
		}
		
		boolean isDirected = false;
		
		if( (byte)(header[0] & 0x80) > 0) {
			isDirected = true;
		}
		int graphSize = getInt(header);
		Graph g = new Graph(true);
		for(int i = 0; i < graphSize; i++) {
			g.addVertix();
		}
		int threadCount = 6;
		Thread[] threadPool = new Thread[threadCount];
		for(int n = 0; n < threadCount; n++) {
			threadPool[n] = new Thread() {
				public void run() {
					extractedGraph(binary, byteHeaderSize, graphSize, g, threadCount);
				}
			};
			threadPool[n].setName(Integer.toString(n));
			threadPool[n].start();
		}
		
		for(int n = 0; n < threadCount; n++) {
			threadPool[n].join();
		}
		g.setDirected(isDirected);

		return g;
	}

	private static void extractedGraph(Byte[] binary, int byteHeaderSize, int graphSize, Graph g, int threadCount) {
		Byte[] data = new Byte[(int)(Math.ceil((graphSize/8.0))+1)];
		int threadId = Integer.parseInt(Thread.currentThread().getName());
		int chunk = graphSize/(threadCount);
		chunk = (chunk == 0)? 1: chunk;
		int scheduler = (graphSize%threadCount == 0)? 0: 1;
		
		for(int s = 0; s < scheduler+1; s++) {
			int start = chunk*threadId + chunk*s*threadCount;
			int ceiling = chunk*(threadId+1) + chunk*s*threadCount;
			for(int i = start; i < ceiling; i++) {
				int offset = graphSize*i/8;
				for(int j = 0; j < data.length; j++) {
					int index = j + offset + byteHeaderSize;
					if(index < binary.length) {
						data[j] = binary[index];	
					}
					else {
						data[j] = (byte)0x00;
					}
				}
				setEdgeInformation(data, g, graphSize, i);
			}
		}
		return;
	}
	
	public static Byte[] getByteFormatted(int ArrayLength, LinkedList<Integer> data, int offset) {
		
		Byte[] vertixData = new Byte[ArrayLength];
		Byte[] arrOfValues = new Byte[8];
		arrOfValues[0] = (byte) 0x80;
		arrOfValues[1] = (byte) 0x40;
		arrOfValues[2] = (byte) 0x20;
		arrOfValues[3] = (byte) 0x10;
		arrOfValues[4] = (byte) 0x08;
		arrOfValues[5] = (byte) 0x04;
		arrOfValues[6] = (byte) 0x02;
		arrOfValues[7] = (byte) 0x01;
		
		for(int i = 0; i < vertixData.length; i++) {
			vertixData[i] = 0x00;
		}
		
		for(int v : data) {
			vertixData[(int)((v+offset)/8)] = (byte) (vertixData[(int)((v+offset)/8)] | arrOfValues[(v+offset)%8]);
		}		
		return vertixData;
	}
		
	public static void setEdgeInformation(Byte[] data, Graph g, int length, int iteration) {
		
		Byte[] arrayOfValues = new Byte[8];
		arrayOfValues[0] = (byte)0x80;
		arrayOfValues[1] = (byte)0x40;
		arrayOfValues[2] = (byte)0x20;
		arrayOfValues[3] = (byte)0x10;
		arrayOfValues[4] = (byte)0x08;
		arrayOfValues[5] = (byte)0x04;
		arrayOfValues[6] = (byte)0x02;
		arrayOfValues[7] = (byte)0x01;
		
		int offset = (length*iteration)%8;
		for(int i = 0; i <length; i++) {
			if((byte)(data[((i + offset)/8)] & arrayOfValues[(i + offset)%8]) != 0 ) {
				g.addEdge(iteration, i);
			}	
		}
		
	}
	
	public static Byte[] getBinary(int value) {
		
		Byte[] result = new Byte[4];
		
		result[0] = (byte) (value >> 24);
		result[1] = (byte) (value >> 16);
		result[2] = (byte) (value >> 8);
		result[3] = (byte) (value >> 0);
		
		return result;
	}
	
	public static int getInt(Byte[] bytes) throws InvalidParameterException{
		int result;
		
		if(bytes.length > 4) throw new InvalidParameterException("the array is too long");
		result = ((bytes[0] & 0x7f) << 24) | ((bytes[1] & 0xff)<< 16) | ((bytes[2] & 0xff) << 8) | ((bytes[3] & 0xff) << 0);
		
		return result;
		
	}
	
	public static String binaryToString(Byte[] binary) {
		
		String bitString = "";
		
		for(int i = 0; i < binary.length ; i++) {
			String bSolution = Integer.toBinaryString(binary[i] & 0x000000ff);
			
				while(bSolution.length() < 8) {
					bSolution = "0" + bSolution;
				}
		
			bitString += " " + bSolution;
			
		}
		
		return bitString;
	}
	
}
