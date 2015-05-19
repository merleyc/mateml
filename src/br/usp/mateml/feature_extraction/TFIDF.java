package br.usp.mateml.feature_extraction;

import java.util.HashMap;
import java.util.Set;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;


public class TFIDF {

	/**
	 * Esse m�todo calcula o valor de TFIF para cada n� (ct).
	 * @param nodo � o n� (ct) da lista de cts.
	 * @return o valor de TFIDF para do n� (ct) passado por par�metro.
	 */
	public HashMap<String, Double> calculate_TFIDF(Candidates candidates){
		Double TFIDF = 0.0, IDF = 0.0;
		HashMap<String, Double> hashTFIDF = new HashMap<String, Double>();
		
		Set<String> set = candidates.getHashCandidates().keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			NodeCandidate node = candidates.getNode(array[i].toString());
			String ct = node.termo;
			
			IDF = Math.log10((double)node.tamCorpus / (double)node.df);
			TFIDF = node.freq * IDF;
			
			hashTFIDF.put(ct, TFIDF);
			
		}

		return hashTFIDF;
	}

}