package br.usp.mateml.steps.feature_extraction;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.util.Util;

public class Cvalue {

	public HashMap<String, Double> calcularCvalue(String caminhoPretextStem, Candidates candidates,
			PretextLoader pretextLoader){
		HashMap<String, Double> hashCvalue = new HashMap<String, Double>();
		int gramas = 0, outros_ct = 0, freqTotal_subcadeias, freq_ct_no_corpus;
		double cvalue = -1.0;
		String ct = "", ct_aux = ""; // candidato a termos

		HashMap<String, Vector<String>> stemPalavra = pretextLoader.buscarStemDePalavra(caminhoPretextStem); //<stem, palavra>
		
		Set<String> set = candidates.getHashCandidates().keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			freqTotal_subcadeias = 0;
			outros_ct = 0;
			NodeCandidate node = candidates.getNode(array[i].toString());
			ct = node.termo;
			freq_ct_no_corpus = node.freq; /* freq do ct atual no corpus */

			/* contar a frequencia total do ct atual como subcadeia dos
			outros ct, exceto a frequencia do proprio */
			for (int j=0; j<array.length; j++) {
				if (i == j) {
					outros_ct++;
					continue;
				}

				NodeCandidate nodo2 = candidates.getNode(array[j].toString());
				ct_aux = nodo2.termo;
				if (Util.contemGrama(nodo2, ct)) {
					freqTotal_subcadeias ++;
					outros_ct++; /* contar em qtos outros cts o ct atual
					aparece como subcadeia,	incluindo o proprio. */
				}
			}
		
			// obtendo a quantidade de palavras originais diferentes nas quais o stem (ct) foi derivado:
			int numPalOrig = numPalavrasOriginais_para1Gram(ct, stemPalavra);
			
			// Se candidato a termo (ct) NAO aparece em outro candidato do hash, exceto nele proprio
			gramas = Util.contarGramas(ct); /* contar qtos gramas o ct atual possui */
			double c = (1 + Math.log((double) gramas) / Math.log((double) 2 ));
			if (outros_ct <= 1) // se o ct atual aparece em um ct mais longo que ele.
				cvalue = c * freq_ct_no_corpus;
			//else cvalue = c * ( freq_ct_no_corpus - ((1 / (double)outros_ct) * freqTotal_subcadeias) );
			// abaixo eu considero outros_ct como a quantidade de palavras originais diferentes nas quais o stem (ct) foi derivado:
			else
				cvalue = c * ( freq_ct_no_corpus - ((1 / (double)numPalOrig) * freqTotal_subcadeias) );
			
			// atualizar o hash do C-Value:
			hashCvalue.put(ct, cvalue);

/*			if (ct.equals("monitor")){
				System.out.println(ct + " - cvalue: "+cvalue + " - gramas: "+gramas + " - freq_ct_no_corpus: "+freq_ct_no_corpus +" - outros_ct: "+outros_ct+
						"- freqTotal_subcadeias: "+freqTotal_subcadeias);
			}*/

		}
		return hashCvalue;
	}

	/**
	 * Esse metodo retorna a quantidade de palavras originais (palavras com variacoes diferentes) no qual um ct (candidato a termo), que é unigrama e normalizado, foi derivado.
	 * @param stemPalavra um hash que contem, como chave, o stem e, como valor, um vetor com as respectivas palavras originais.
	 * @param ct candidato a termo (unigrama) normalizado.
	 * @return nulo, quando o hash utilizado estah vazio; zero, quando nao existem palavras originais para o ct normalizado; numGramas, eh o numero palavras originais no qual o ct foi derivado.
	 */
	public Integer numPalavrasOriginais_para1Gram(String ct, HashMap<String, Vector<String>> stemPalavra){
		if (stemPalavra == null) {
			System.out.println("Hash nulo. - Caracteristica Cvalue");
			return null;
		}
		
		int numGramas = 0;
		Set<String> set = stemPalavra.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			String chave = array[i].toString();
			if (chave.equals(ct)) {
				Vector<String> palavrasOriginais = stemPalavra.get(chave);
				numGramas = palavrasOriginais.size();
			}
		}
				
		return numGramas;
	}
	
}