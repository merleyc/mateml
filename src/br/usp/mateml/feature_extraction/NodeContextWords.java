package br.usp.mateml.feature_extraction;

public class NodeContextWords {

		public String palavra;
		public int freq; // frequencia da palavra
		
		public NodeContextWords(){	
			this.palavra = null;
			this.freq = -1; // frequencia da palavra
		}
		
		public NodeContextWords(String palavra, int frequencia){		
			this.palavra = palavra;
			this.freq = frequencia;
		}
		
	}

