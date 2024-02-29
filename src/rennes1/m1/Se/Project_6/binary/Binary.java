package rennes1.m1.Se.Project_6.binary;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import rennes1.m1.Se.Project_6.graph.Graph;
import rennes1.m1.Se.Project_6.graph.WeightedGraph;

public class Binary {
	
	private final int nHeaderSize = 16;
	private final int myEdgeBitSize = 5;
	private final int wHeaderSize = 16 + myEdgeBitSize;
	

	public static Byte[] getBinary(Graph graph) throws InvalidParameterException{
		
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
		
		int offset;
		int arrLength = (int)Math.ceil((2*myGraphSize - 1)/8.0);
		arrLength = (int)(Math.ceil((myGraphSize/8.0))+1);
		for(int i = 0; i < myGraphSize ; i++) { //runs through each vertix, encodes it into binary and adds it to the Byte array
			offset = (i*myGraphSize);
			Byte[] myCodedInfo = getByteFormatted(arrLength, graph.getVertixList(i), offset%8);
			
			for(int j = 0; j < myCodedInfo.length; j++) {
				int index = j + offset/8 + (headerSize/8);
				if(index < binary.length) {
					binary[index] = (byte) (binary[index] | myCodedInfo[j]);		
				}
				
			}
			
		}
		
		return binary;
	}
	
	public static Byte[] getBinary(WeightedGraph graph) throws InvalidParameterException{
		
		int myEdgeBitSize = 5; //smaller than 8
		int headerSize = 16; //16 bits for the size of the graph and if it is directed, and 5 bits for the size of each number
		int myGraphSize = graph.getSize();
		int myJumpSize = getBitSize(graph.weightLimit());
		int binaryStringSize = 1 +(int) Math.ceil((headerSize + myGraphSize*myGraphSize*myJumpSize)/8.0);
		
		if(Math.pow(2, headerSize-1)-1 < myGraphSize) throw new InvalidParameterException("The input graph is too big");
		if(Math.pow(2, myEdgeBitSize)-1 < myJumpSize) throw new InvalidParameterException("The input graph is too heavy");
		
		Byte[] binary = new Byte[binaryStringSize];
		
		for(int i = 0; i < binary.length; i++) {
			binary[i] = (byte)0x00;
		}
		
		Byte[] mySize = getBinary(myGraphSize);
		
		for(int i = 0; i < headerSize/8 ; i++) { //loads the size of the graph into the header
			
			binary[i] = (byte) (mySize[i+(4-headerSize/8)]);
		}
		
		if(graph.isDirected()) {
			binary[0] = (byte)(binary[0] | 0x80);
		}else {
			binary[0] = (byte)(binary[0] & 0x7f);
		}
		
		Byte[] myJumper = getBinary(myJumpSize);
			
		binary[headerSize/8] = (byte) (binary[headerSize/8] |((myJumper[myJumper.length-1] & 0xff) << 8 - myEdgeBitSize));
		
		headerSize += myEdgeBitSize - (8 - (myJumpSize%8));
		int offset = headerSize;
		int arrLength = (int)Math.ceil(myJumpSize/8.0 + 1) + (int)Math.ceil(myGraphSize*myJumpSize/8.0);
		for(int i = 0; i < myGraphSize; i++) {
			@SuppressWarnings("unchecked")
			Byte[] myCodedBytes = getMyHeavyBytesFormatted(offset%8, graph.getEdges(i), arrLength, myJumpSize);
			
			for(int k = 0; k < myCodedBytes.length; k++) {
				 
				 if((k + offset/8)< binary.length) {
				 
				 binary[k + offset/8] = (byte)(binary[k + offset/8] | myCodedBytes[k]);
				 
				 }
			}
			
			offset += myJumpSize*myGraphSize;
			
		}
		
		return binary;
	}
	
	public static Graph getGraph(Byte[] binary) {
		
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
		
		if( (byte)(header[0] & 0x80) > 0) {//check the first bit of the header to tell if the graph is or not directed
			isDirected = true;
		}
		int graphSize = getInt(header);//
		Graph g = new Graph(true);
		for(int i = 0; i < graphSize; i++) {
			g.addVertix();
		}
		Byte[] data = new Byte[(int)(Math.ceil((graphSize/8.0))+1)];
		for(int i = 0; i < graphSize; i++) {
			
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
		
		g.setDirected(isDirected);

		return g;
	}
	
	public static WeightedGraph getWeightedGraph(Byte[] binary) {
		
		int myEdgeBitSize = 5; //smaller than 8
		int myHeaderStart = 16;
		int headerSize = myEdgeBitSize + myHeaderStart;
		int byteHeaderSize = (int)Math.ceil(myHeaderStart/8.0);
		int byteEdgeMaxSize = (int)Math.ceil(myEdgeBitSize/8.0);
		int byteFullHeaderSize = headerSize;
		
		Byte[] header = new Byte[4];
		
		for(int i = 0; i < byteHeaderSize; i++) {
			header[i + (4-byteHeaderSize)] = binary[i];
		}
		for(int i = 0; i< header.length; i++) {
			if(header[i] == null) header[i] = 0x00;
		}
		
		Byte[] edgeSize = new Byte[4];
		
		
		edgeSize[3] = (byte)(binary[byteHeaderSize] >> (8-myEdgeBitSize));
		
		
		
		
		for(int i = 0; i< edgeSize.length; i++) {
			if(edgeSize[i] == null) edgeSize[i] = 0x00;
		}
		
		boolean isDirected = false;
		
		if((byte)(header[0] & 0x80) > 0) {
			isDirected = true;
		}
		
		int graphSize = getInt(header);
		int graphWeight = getInt(edgeSize);
		
		WeightedGraph g = new WeightedGraph(true);
		
		for(int i = 0; i < graphSize; i++) {
			g.addVertix();
		}
		
		Byte[] data = new Byte[(int)Math.ceil((graphSize*graphWeight/8.0)+1)];
		for(int i = 0; i < graphSize; i++) {
			int offset = graphSize*i*graphWeight;
			
			for(int j = 0; j < data.length; j++) {
				int index = j + ((offset + byteFullHeaderSize)/8);
				//System.out.println("data --> " + j + " index --> " + index);
				if(index >= binary.length) {
					data[j] = 0x00;
				}else {
					data[j] = binary[index];
				}
			}
			//System.out.println("data array --> " + binaryToString(data));
			setHeavyEdgeInformation(data, g, graphSize, graphWeight, i);
		}
		return g;
		
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
	
	public static Byte[] getMyHeavyBytesFormatted(int offset, Map<Integer, Integer> map, int length, int jump) {
		int toggle = (jump > 7)? 0: 1;
		Byte[] arrOfValues = new Byte[8];
		arrOfValues[0] = (byte) 0x80;
		arrOfValues[1] = (byte) 0x40;
		arrOfValues[2] = (byte) 0x20;
		arrOfValues[3] = (byte) 0x10;
		arrOfValues[4] = (byte) 0x08;
		arrOfValues[5] = (byte) 0x04;
		arrOfValues[6] = (byte) 0x02;
		arrOfValues[7] = (byte) 0x01;
		
		Byte[] b = new Byte[length];
		
		int insiderOffset = 0;
		
		for(int i = 0; i < b.length; i++) {
			b[i] = 0x00;
		}
		
		for(Entry<Integer, Integer> entry : map.entrySet()) {
			
			insiderOffset = entry.getKey()*jump + offset;
			
			//System.out.println("insiderOffset --> " + insiderOffset%8);
			
			int value = entry.getValue();
			
			Byte[] bytes;
			if(value <= 0) { 
				value *=(-1);
				System.out.println("worky worky");
				bytes = getBinary(value);
				bytes[(bytes.length -toggle - (jump/8))] = (byte) (bytes[(bytes.length - toggle - (jump/8))] | arrOfValues[(8-(jump%8))%8]);
			}
			else{
				bytes = getBinary(value);
			}
			
			Byte[] offsetData = new Byte[bytes.length + 1];
			
			int offsot = insiderOffset%8;
			
			for(int j = 0; j < offsetData.length; j++) offsetData[j] = 0x00;
			
			for(int i = bytes.length - (jump/8+1); i < bytes.length; i++) {
				
				//offsetData[i] = (byte) (offsetData[i] | ((bytes[i] & 0xff)));
				
				offsetData[i] = (byte) (offsetData[i] |((bytes[i] & 0xff) >>> offsot));
				
				if(i > 0) {
					offsetData[i] = (byte) (offsetData[i] |((bytes[i-1] & 0xff) << 8-offsot));	 
				}
				
				if(i == bytes.length-1) {
					offsetData[i+1] = (byte)(offsetData[i+1]| ((bytes[i] & 0xff) << 8-offsot));
				}
				
				System.out.println(binaryToString(offsetData));
			}
			
			offsot = (offsot+3)%8;
			for(int m = 0; m < (jump/8 + 1) + 1; m++) { 
				if((m + insiderOffset/8) < b.length) {
					b[(m + insiderOffset/8)] = (byte)(b[(m + insiderOffset/8)] | (offsetData[ m + (offsetData.length-1-(jump/8+1))]));
				}
			}
		}
		return b;
	}
	
	public static void setHeavyEdgeInformation(Byte[] data, WeightedGraph g, int length, int width, int iteration) {
		
		Byte[] arrOfValues = new Byte[8];
		arrOfValues[0] = (byte) 0x80;
		arrOfValues[1] = (byte) 0x40;
		arrOfValues[2] = (byte) 0x20;
		arrOfValues[3] = (byte) 0x10;
		arrOfValues[4] = (byte) 0x08;
		arrOfValues[5] = (byte) 0x04;
		arrOfValues[6] = (byte) 0x02;
		arrOfValues[7] = (byte) 0x01;
		
		Byte[] arrOfContinuousValues = new Byte[8];
		arrOfContinuousValues[0] = (byte)0xff;
		arrOfContinuousValues[1] = (byte)0x7f;
		arrOfContinuousValues[2] = (byte)0x3f;
		arrOfContinuousValues[3] = (byte)0x1f;
		arrOfContinuousValues[4] = (byte)0x0f;
		arrOfContinuousValues[5] = (byte)0x07;
		arrOfContinuousValues[6] = (byte)0x03;
		arrOfContinuousValues[7] = (byte)0x01;
		
		Byte[] arrOfInvertedValues = new Byte[9];
		arrOfInvertedValues[0] = (byte)0xff;
		arrOfInvertedValues[1] = (byte)0xfe;
		arrOfInvertedValues[2] = (byte)0xfc;
		arrOfInvertedValues[3] = (byte)0xf8;
		arrOfInvertedValues[4] = (byte)0xf0;
		arrOfInvertedValues[5] = (byte)0xe0;
		arrOfInvertedValues[6] = (byte)0xc0;
		arrOfInvertedValues[7] = (byte)0x80;
		arrOfInvertedValues[8] = (byte)0xff;
		
		
		int toggle = (width/8>0)? 0: 1;
		int offset = (width*length*iteration) + 5; //change 5 for the header offset reference
		int offsetLoop = offset;
		if(offset > width*length) offsetLoop = offsetLoop%8;
		int top = (int) Math.ceil((width/8.0)) + 1;
		Byte[] bit = new Byte[4];
		//System.out.println("----------> " + iteration + " <--------------");
		//System.out.println("oofset " + offsetLoop);
		int p1 = 0;
		for(int i = 0; i <length; i++) {
			//System.out.println("new edge " + i);
			for(int j = 0; j < top; j++) {
				p1 = (j*width + width*i + offsetLoop);
				int p2 = p1/8;
				
				//System.out.println("data index --> " + p1 + " now over eight --> " + p2 + " equals -->>> " + (p2%data.length));
				if(p2 < data.length) {
					bit[j + (4-top)] = data[p2];	
					//bit[j + (4-top)] = data[((j + (i*width) + (offset/8))%data.length)];	
				}
				
			}
			for(int n = 0; n < bit.length; n++) {
				if(bit[n] == null) bit[n] = 0x00;
				if(n < bit.length-1 && bit[n] == bit[n+1]) bit[n] = 0x00;
			}
			
			int offsetEnd = ((top-1)*width + width*i + offset);
			int offsetStart = offsetEnd - width;
			
			
			
			//System.out.println("Start --> " + offsetStart%8);
			//System.out.println("End --> " + offsetEnd%8);
			
			System.out.println("bffset " + binaryToString(bit));
			
			Byte[] dummy = new Byte[2];
			
			dummy[0] = arrOfContinuousValues[offsetStart%8];
			
			dummy[1] = arrOfInvertedValues[8 - (offsetEnd%8)];
			
			System.out.println("arr =>> 		 " + binaryToString(dummy));
			
			int bitsize = (offsetEnd/8) - (offsetStart/8);
			
			if(offsetEnd%8 == 0) bitsize--;		
			
			if(offsetEnd%8==0) {
				bit[3] = bit[2];
				bit[2] = bit[1];
				bit[1] = bit[0];
				bit[0] = 0x00;
			}
			
			int index = bit.length - (width/8 + toggle + bitsize);
			System.out.println("index -> " + index);
			bit[index] = (byte)(bit[index] & arrOfContinuousValues[offsetStart%8]);
			
			index = bit.length-1;
			bit[index] = (byte)(bit[index] & arrOfInvertedValues[8 - (offsetEnd%8)]);
			System.out.println("affset " + binaryToString(bit));
			Byte[] crux = new Byte[bit.length];
			
			for(int t = 0; t < crux.length; t++) crux[t] = 0x00;
			
			
			if(offsetEnd%8 != 0) {
				for(int l = bit.length - index; l < bit.length; l++) {
					crux[l] = (byte)(crux[l] | ((bit[l] & 0xff) >>>  8 -offsetEnd%8));
					if(l > 0) {
						crux[l] = (byte)(crux[l] | ((bit[l-1] & 0xff) << offsetEnd%8));
					}
				}
			
			}else {
				crux = bit;
			}
			
			//System.out.println("width --> " + (width/8));
			System.out.println("offset " + binaryToString(crux));
			int a = 1, flipper = 0;
			flipper = (width%8 == 0)? 1 : 0;
			index = (crux.length-1) -  (width/8 - flipper);
			int adderIndex = (8-(width%8)+1)%8;
			int signIndex = (8-(width%8))%8;
			Byte[] t = new Byte[1];
			t[0] = arrOfContinuousValues[adderIndex];
			Byte[] s = new Byte[1];
			s[0] = arrOfValues[signIndex];
			System.out.println("index: " + index +  " arr Index: " + binaryToString(t) + " arrValues: " + binaryToString(s));
			
			if((crux[index] & arrOfValues[signIndex]) != 0) a = -1;
			
			crux[index] = (byte)(crux[index] & arrOfContinuousValues[adderIndex]);
			
			int number = a * getInt(crux);
			
			System.out.println("finish " + binaryToString(crux));
			System.out.println("");
			
			g.addEdge(iteration, i, number);
			
		}
	}
	
	public static int logicGraphSize(int value) { 
		return (int) Math.ceil(Math.log(value)/Math.log(2));
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
	
	public static int getBitSize(int size) {
		int c = 2;
		while(Math.abs(size) != 1) {
			size /= 2;
			c++;
		}
		
		return c;
	}
	
}
