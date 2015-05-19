package br.usp.mateml.candidates;


public class NodeCandidate {

	public String termo;
	public int compGram; // tamanho da palavra em num de caracteres
	public int tamGram; // se eh unigrama, bigrama, trigrama...
	public int tamCorpus; // numero de documentos no corpus
	public int freq; // frequencia absoluta da palavra
	public double atf;
	public int df;
	public double tfidf;
	public double tv;
	public double tvq;
	public double tc;
	public int pos; // posicao da palavra
	public int corpusGeral; // 1 se parece no corpus de lingua geral e 0 se nao aparece
	public int freq_CorpusGeral; // frequencia do termo no corpus geral
	public double weirdness;
	public double ridf;
	public double thd; // indice termhood
	public int rankTHD_CorpusEspec; // usado no calculo do indice termhood
	public int rankTHD_CorpusGeral; // usado no calculo do indice termhood
	public double tds; // Especificidade de Dominio de Termo / term domain specificity
	public double glossex;
	
	public int expr_indicativas; // se estah perto de expressoes indicativas
	public int listaRef; // se estah na lista de referencia do dominio
	public int padrao; // se pertence a algum padrao morfossintatico pre-definido
	public double cvalue;
	public double ncvalue;
	public int sintagmas;
	public int nucleo_sintagmas;
	public int substantivo;
	public int adjetivo;
	public int verbo;
	public int numPalOriginais;
	// Caracteristicas identificadas na taxonomia do mesmo dominio do corpus:
	public String listaTermosTax;
	public int parent;
	public int numIrmaos;
	public String listaDocumentos;
	public int nivel;
	public int numDocs;
	public int numNohsFilhos;
	public int numDescendentes;
	public float valorTermo; //eh o valor dado para cada termo de cada noh no momento de criar a taxonomia com o problema do Ricardinho
	
	public NodeCandidate(){	
		this.termo = null;
		this.compGram = -1;
		this.tamGram = -1; // tamanho da palavra
		this.tamCorpus = -1;
		this.freq = -1; // frequencia da palavra
		this.atf = -1.0; // Average Term Frequency
		this.df = -1;
		this.tfidf = -1.0;
		this.tv = -1.0;
		this.tvq = -1.0;
		this.tc = -1.0;
		this.pos = -1;
		this.corpusGeral = 0;
		this.freq_CorpusGeral = 0;
		this.weirdness = -1.0;
		this.ridf = -1.0;
		this.thd = -1.0;
		this.rankTHD_CorpusEspec = -1;
		this.rankTHD_CorpusGeral = 1; //ATENCAO: sempre setar como "1" para que os CTs que nao aparecam no CG tenha thd = 1!
		this.tds = -1; //ATENCAO: sempre setar como "-1" pois utiliza essa comparacao para atribuir aos CTs que nao aparecam no CG tds = max(tds)!
		this.glossex = -1.0;
		
		this.expr_indicativas = 0;
		this.listaRef = 0;
		this.padrao = 0;
		this.cvalue = -1.0;
		this.ncvalue = -1.0;
		this.sintagmas = 0;
		this.nucleo_sintagmas = 0;
		this.substantivo = -1;
		this.adjetivo = -1;
		this.verbo = -1;
		this.numPalOriginais = -1;
		this.listaTermosTax = null;
		this.parent = -1;
		this.numIrmaos = -1;
		this.listaDocumentos = null;
		this.nivel = -1;
		this.numDocs = -1;
		this.numNohsFilhos = -1;
		this.numDescendentes = -1;
		this.valorTermo = -1;
	}

	public NodeCandidate(String termo, int comprimentoGram, int tamanho_gram, int tamanho_corpus,
			int frequencia, double average_term_frequency,
			int document_frequency, double tfidf, double tv, double tvq, double tc,
			int posicao, int aparece_corpusGeral, int expressoes_indicativas, int frequencia_corpusGeral,
			double weirdness, double ridf, double indice_termhood, int rankTHD_CorpusEspec,
			int rankTHD_CorpusGeral, double term_domain_specificity, double glossex,
			
			double term_frequency_inverse_domain_frequency,
			int listaReferencia, int padraoMorf, double cvalue, double ncvalue, int sintagmas,
			int nucleo_sintagmas, int substantivo, int adjetivo, int verbo, int numeroPalalavraOriginais,
			String listaTermosTax, int parent, int numIrmaos, String listaDocumentos, int nivel,
			int numDocs, int numNohsFilhos,	int numDescendentes, float valorTermo){		
		this.termo = termo;
		this.compGram = comprimentoGram;
		this.tamGram = tamanho_gram;
		this.tamCorpus = tamanho_corpus;
		this.freq = frequencia;
		this.atf = average_term_frequency;
		this.df = document_frequency;
		this.tfidf = tfidf;
		this.tv = tv;
		this.tvq = tvq;
		this.tc = tc;
		this.pos = posicao;
		this.corpusGeral = aparece_corpusGeral;
		this.freq_CorpusGeral = frequencia_corpusGeral;
		this.weirdness = weirdness;
		this.ridf = ridf;
		this.thd = indice_termhood;
		this.rankTHD_CorpusEspec = rankTHD_CorpusEspec;
		this.rankTHD_CorpusGeral = rankTHD_CorpusGeral;
		this.tds = term_domain_specificity;
		this.glossex = glossex;
		
		this.expr_indicativas = expressoes_indicativas;
		this.listaRef = listaReferencia;
		this.padrao = padraoMorf;
		this.cvalue = cvalue;
		this.ncvalue = ncvalue;
		this.sintagmas = sintagmas;
		this.nucleo_sintagmas = nucleo_sintagmas;
		this.substantivo = substantivo;
		this.adjetivo = adjetivo;
		this.verbo = verbo;
		this.numPalOriginais = numeroPalalavraOriginais;
		this.listaTermosTax = listaTermosTax; 
		this.parent = parent;
		this.numIrmaos = numIrmaos;
		this.listaDocumentos = listaDocumentos;
		this.nivel = nivel;
		this.numDocs = numDocs;
		this.numNohsFilhos = numNohsFilhos;
		this.numDescendentes = numDescendentes;
		this.valorTermo = valorTermo;
	}
	
}
