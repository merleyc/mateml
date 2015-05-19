package br.usp.mateml.feature_extraction.taxonomy;


public class NodeFeatureOfTax {

	public int id;
	public String termos;
	public int parent;
	public int numIrmaos;
	public String listaDocumentos;
	public int nivel;
	public int numDocs;
	public int numNohsFilhos;
	public int numDescendentes;
	public float valorTermo; //eh o valor dado para cada termo de cada noh no momento de criar a taxonomia com o problema do Ricardinho	
	
	public NodeFeatureOfTax(){	
		this.id = -1;
		this.termos = null;
		this.parent = -1;
		this.numIrmaos = -1;
		this.listaDocumentos = null;
		this.nivel = -1;
		this.numDocs = -1;
		this.numNohsFilhos = -1;
		this.numDescendentes = -1;
		this.valorTermo = -1;
	}


	public NodeFeatureOfTax(int id, String termos, int parent, int numIrmaos, String listaDocumentos,
			int nivel, int numDocs, int numNohs, int numFilhos, int valorTermo) {
		super();
		this.id = id;
		this.termos = termos;
		this.parent = parent;
		this.numIrmaos = numIrmaos; 
		this.listaDocumentos = listaDocumentos;
		this.nivel = nivel;
		this.numDocs = numDocs;
		this.numNohsFilhos = numNohs;
		this.numDescendentes = numFilhos;
		this.valorTermo = valorTermo;
	}

	
	
	
}
