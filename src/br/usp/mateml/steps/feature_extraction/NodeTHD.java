package br.usp.mateml.steps.feature_extraction;

public class NodeTHD implements Comparable<NodeTHD>{
	
	public int freq;
	public String palavra;

	@Override
	public int compareTo(NodeTHD o) {
		int resultado = 0;
		
		if (this.freq > o.freq)
			resultado = 1;
		else if (this.freq < o.freq)
			resultado = -1;
			
		return resultado;
	}
	

	
}
