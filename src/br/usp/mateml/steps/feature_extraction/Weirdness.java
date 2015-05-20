package br.usp.mateml.steps.feature_extraction;

public class Weirdness {

	public static double calculaWeirdness (int freqPal_CorpusEspec, int qtdePal_CorpusEspec,
			int freqPal_CorpusGeral, int qtdePal_CorpusGeral) {
		
		double weirdness = ( (double) freqPal_CorpusEspec / (double) qtdePal_CorpusEspec ) /
				( (double) freqPal_CorpusGeral / (double) qtdePal_CorpusGeral ) ;
		
		return weirdness;
	}
}
