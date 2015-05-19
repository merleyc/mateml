package br.usp.mateml.feature_extraction;

/*package uk.ac.shef.wit.textractor.core.algorithm;

import uk.ac.shef.wit.textractor.JATRException;
import uk.ac.shef.wit.textractor.model.Term;
 */
import java.util.HashMap;
import java.util.Vector;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;

/**
 * Adaptado de:
 * 
 * An implementation of the GlossEx term recognition algorithm. See Kozakov, et. al 2004, <i>
 * Glossary extraction and utilization in the information search and delivery system for IBM Technical Support</i>
 *. This is the implementation of the scoring formula <b>only</b>, and does not include the filtering algorithm as mentioned
 * in the paper.
 * <p>
 * In the equation C(t) = a* TD(t) + B*TC(t	), default a=0.2, B = 0.8.
 * </p>
 *
 * "Here TD is term domain specificity, TC is term cohesion, and a and a (a + B = 1) are
 * constant values that determine the relative contributions of TD and TC." (Kozakov, et. al, 2004)
 * 
 * You might need to modify the value of B by increasing it substaintially when the reference corpus is relatively
 * much bigger than the target corpus, such as the BNC corpus. For details, please refer to the paper.
 *
 * //todo:test this class, see TC and TD values
 * @author <a href="mailto:z.zhang@dcs.shef.ac.uk">Ziqi Zhang</a>
 */

/*
(c) Copyright 2004 Natural Language Processing Group, The University of Sheffield
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class GlossEx {

	private double maxGlossEx = 0;
	private Vector<String> vetor_ct_comCG_zero = new Vector<String>();


	/**
	 * 
	 * @param ct candidato a termo
	 * @param hashCT hash com todos os candidatos a termos
	 * @param hashCG hash com as palavras do corpus de lingua geral
	 * @param alpha variavel constante. Se CT eh unigrama, alpha = 0.9, 
	 * senao alpha = valor passado por parametro.
	 * @param beta variavel constante. Se CT eh unigrama, beta = 0.1, 
	 * senao beta = valor passado por parametro.
	 * @param tfTotalPals_CorpusEspec frequencia total das palavras do corpus
	 * de dominio especifico.
	 * @param tfTotalPals_CorpusGeral frequencia total das palavras do corpus
	 * de lingua geral.
	 * @return valor de especificidade do dominio = GlossEx.
	 */
	public double calculaGlossEx(String ct, Candidates candidates,
			HashMap<String, NodeCandidate> hashCG, double alpha, double beta,
			int tfTotalPals_CorpusEspec, int tfTotalPals_CorpusGeral) {

		double score = -1.0;
		String[] elements = ct.split(" ");
		double T = (double) elements.length; // numero de palavras no CT
		double SUMwi = 0.0;
		double SUMfwi = 0.0;

		for (int i = 0; i < T; i++) {
			String wi = elements[i];
			wi = wi.trim();
			if (!candidates.containsKey(wi)) {
				System.out.println("Erro na caracteristica GlossEx. Hash de CT nao contem a palavra: " + wi);
				return -1;
			}
			double pwi_CorpusEspec = (double) candidates.getNode(wi).freq / (double) tfTotalPals_CorpusEspec;
			double pwi_CorpusGeral = (double) candidates.getNode(wi).freq_CorpusGeral / (double) tfTotalPals_CorpusGeral;

			if (pwi_CorpusGeral == 0) { // se alguma palavra que compoe o CT nao existe no corpus de lingua geral;
				vetor_ct_comCG_zero.add(ct);
				return -1;
			}
			SUMwi += Math.log(pwi_CorpusEspec / pwi_CorpusGeral); // somatoria da probabilidade das palavras que compoem o CT nos corpora.
			SUMfwi += (double) candidates.getNode(wi).freq; // somatoria das frequencias das palavras que compoem o CT. 
		}

		double TD = SUMwi / T;
		double TC = (T * Math.log10(candidates.getNode(ct).freq) * candidates.getNode(ct).freq) / SUMfwi;

		if (T == 1) { // se CT eh unigrama
			score = 0.9 * TD + 0.1 * TC;
		} else {
			score = alpha * TD + beta * TC;
		}

		maxGlossEx(score);

		return (score);
	}

	private void maxGlossEx(double scoreGlossEx) {
		if (maxGlossEx < scoreGlossEx)
			maxGlossEx = scoreGlossEx;		
	}

	public double get_maxGlossEx() {
		return maxGlossEx;
	}

	public Vector<String> get_vetor_ct_comCG_zero() {
		return vetor_ct_comCG_zero;
	}


}