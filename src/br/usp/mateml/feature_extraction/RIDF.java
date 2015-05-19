package br.usp.mateml.feature_extraction;

/**
 * 
 * @author mel
 "Residual IDF (RIDF) is an alternative to IDF, which looks for terms whose
document frequency is larger than chance. More precisely, RIDF is defined as the
difference between logs of actual document frequency and document frequency
predicted by Poisson distribution [13].

	RIDF (i) = Idf (i) − log(1 − p(0; λ(i))),

where p is the Poisson distribution with parameter λ(i) = fD
(the average number of occurrences of word wi per document).
f (i) is the number of words i
in the collection. 1 − p(0; λ(i)) is the Poisson probability of a document with at
least one occurrence of i." (Knoth et. al, 2009)

Codigo original:

double tf = (double) tfidfFeatureStore.getTermFreq(s) / ((double) tfidfFeatureStore.getTotalTermFreq() + 1.0);
double df_i = (double) tfidfFeatureStore.getDocFreq(s) == 0 ? 1 : (double) tfidfFeatureStore.getDocFreq(s);

double D = (double) tfidfFeatureStore.getTotalDocs();
double ridf = -Math.log(df_i / D) + Math.log(1 - Math.exp(-tf / D));
 *
 */

public class RIDF {

	public double calculaRIDF (int termFreq, int totalTermFreq,
			int docFreq, int totalDocs) {

		double idf = Math.log( (double) totalDocs / (double) docFreq);
		double D = (double) totalDocs;
		double exp = Math.exp(-termFreq / D);
		double parte2 = -Math.log(1 - exp);
		
		double ridf = idf + parte2;
		
		return ridf;
	}

}