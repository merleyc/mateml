package br.usp.mateml.steps.feature_extraction;

import java.util.Vector;

public class TDS {

	private double maxTDS = 0;
	private Vector<String> vetor_ct_comCG_zero = new Vector<String>();
	
	/**
	 * Esse metodo calcula o valor do indice TDS (Especificidade de Dominio de Termo
	 * (term domain specificity) para uma palavra.
	 * @param freqPal_CorpusEspec eh a frequencia absoluta da palavra no corpus de
	 * dominio especifico.
	 * @param tfTotalPals_CorpusEspec eh o total de termos no corpus de dominio
	 * espeficico, i.e, a somatoria de todas as frequencias.
	 * @param freqPal_CorpusGeral eh a frequencia absoluta da palavra no corpus de
	 * lingua geral.
	 * @param tfTotalPals_CorpusGeral eh o total de termos no corpus de lingua geral.
	 * @return o valor de TDS de uma palavra, caso a palavra aparesca no corpus de
	 * lingua geral. Caso contrario, retorna -1 e grava o CT num vetor.
	 */
	public double calculaTDS(String ct, int freqPal_CorpusEspec, int tfTotalPals_CorpusEspec,
			int freqPal_CorpusGeral, int tfTotalPals_CorpusGeral) {

		double tds = -1.0;

		
		double p_CGeral = (double)freqPal_CorpusGeral / (double)tfTotalPals_CorpusGeral;
		if (p_CGeral == 0) {
			tds = -1;
			vetor_ct_comCG_zero.add(ct);
		}
		else {
			double p_CEspec = (double)freqPal_CorpusEspec / (double)tfTotalPals_CorpusEspec;
			tds = p_CEspec / p_CGeral;
		}

		maxTDS(tds);
		
		return tds;
	}
	
	private void maxTDS(double tds) {
		if (maxTDS < tds)
			maxTDS = tds;		
	}
	
	public double get_maxTDS() {
		return maxTDS;
	}

	public Vector<String> get_vetor_ct_comCG_zero() {
		return vetor_ct_comCG_zero;
	}

}
