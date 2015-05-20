package br.usp.mateml.steps.feature_extraction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;

/**
 * Indice Termhood (THD). Chunyu Kit e Xiaoyue Liu, Measuring mono-word termhood by
 *  rank difference via corpus comparison, Terminology, 14 (2008), pp. 204–229.
 * Implementacao baseada na explicacao da tese da Lucelene Lopes (2011).
 * O valor do indice THD ficarah dentro do intervalo [0.0, 1.0], com 1.0 para o mais alto.
 * @author mel
 */
public class THD {

	public double calculaTHD (String ct, int rankTHD_CorpusEspec, int rankTHD_CorpusGeral,
			int qtdePal_CorpusEspec, int qtdePal_CorpusGeral) {

		double thd = ( (double) rankTHD_CorpusEspec / (double) qtdePal_CorpusEspec ) -
				( (double) rankTHD_CorpusGeral / (double) qtdePal_CorpusGeral );

		return thd;
	}

	/**
	 * O valor de ordenacao de um candidato a termo (CT) eh definido como
	 * |V_c|, que eh o tamanho do vocabulario do corpus c, para o ct mais
	 * frequente do corpus c. Para o segundo ct mais frequente, o valor de 
	 * ordenacao eh igual a |V_c| - 1, e assim por diante. O valor de
	 * ordenacao para o termo menos frequente eh igual a 1.
	 * @return 
	 * @return Esse metodo armazena retorna um vetor dos CTs ordenados em
	 * ordem crescente por suas frequencias. Entao, o valor de ordenacao
	 * (THD) de cada CT no corpus de dominio especifico (rankTHD_CorpusEspec)
	 * corresponde ao indice do CT nesse vetor + 1.
	 */
	public Vector<NodeTHD> ordenaPalPorFreq_CorpusEspec_THD(Candidates candidates) {
		Vector<NodeTHD> vetorFreqCTs = new Vector<NodeTHD>();

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: THD feature: Sort specific rank: candidate hash is null or empty.");
		} else {

			Set<String> set = candidates.getHashCandidates().keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String ct = (String)array[i];
				int freqCT = candidates.getNode(ct).freq;
				NodeTHD nodo = new NodeTHD();
				nodo.freq = freqCT;
				nodo.palavra = ct;

				vetorFreqCTs.add(nodo);
			}

			// ordena as frequencias do CTs em ordem crescente: 
			Collections.sort(vetorFreqCTs);
		}

		return vetorFreqCTs;
	}

	/**
	 * O valor de ordenacao de um candidato a termo (CT) eh definido como
	 * |V_c|, que eh o tamanho do vocabulario do corpus c, para o ct mais
	 * frequente do corpus c. Para o segundo ct mais frequente, o valor de 
	 * ordenacao eh igual a |V_c| - 1, e assim por diante. O valor de
	 * ordenacao para o termo menos frequente eh igual a 1.
	 * @return Esse metodo armazena retorna um vetor dos CTs ordenados em
	 * ordem crescente por suas frequencias. Entao, o valor de ordenacao de
	 * cada CT no corpus de lingua geral (rankTHD_CorpusGeral) corresponde
	 * ao indice do CT nesse vetor + 1.
	 */
	public Vector<NodeTHD> ordenaPalPorFreq_CorpusGeral_THD (HashMap<String, NodeCandidate> hashCG) {

		Vector<NodeTHD> vetorFreqPals_CG = new Vector<NodeTHD>();

		if (hashCG == null) {
			System.out.println("Error: THD feature: THD sorting: hash of general language corpus is null.");
		} else {

			Set<String> set = hashCG.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String palavra = (String)array[i];
				int freqPal = hashCG.get(palavra).freq_CorpusGeral;
				NodeTHD node = new NodeTHD();
				node.freq = freqPal;
				node.palavra = palavra;

				vetorFreqPals_CG.add(node);

			}

			// ordena as frequencias do CTs em ordem crescente: 
			Collections.sort(vetorFreqPals_CG);
		}
		return vetorFreqPals_CG;

	}

	/**
	 * Armazena no MatrizSaida.hashct_1gram o valor de ordenacao (THD)
	 * de cada CT no corpus de dominio especifico (rankTHD_CorpusEspec)
	 * @param vetorOrdenado com as frequencias dos CTs em ordem crescente.
	 */
	public void calcula_e_armazena_RankEspec_THD(Vector<NodeTHD> vetorOrdenado,
			Candidates candidates) {

		if (vetorOrdenado == null) {
			System.out.println("Error: THD feature: Specific rank: THD sort vector is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: THD feature: Specific rank: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos no vetor:
			for (int iCT=0; iCT<vetorOrdenado.size(); iCT++) {
				NodeTHD nodoVetor = vetorOrdenado.get(iCT);
				String ct = nodoVetor.palavra;
				int rankCT = iCT + 1; 

				// verifica se o ct estah presente no hash de unigramas:
				if (candidates.containsKey(ct.trim())) {

					// atualiza hash1gram:
					NodeCandidate node = candidates.getNode(ct);
					node.rankTHD_CorpusEspec = rankCT;
					candidates.put(ct, node);
				}

				else System.out.println("Erro ao calcular o rank para a medida THD. Metodo: MatrizSaida.armazenaRankEspec_THD.");

			}
			vetorOrdenado = null;
		}
	}

	/**
	 * O rank (indice de ordenacao) da palavra no corpus de lingua geral foi
	 * inicialmente cetado como 1 na classe NodoLista para que as palavras
	 * que nao aparecem no corpus de lingua geral tenham rankGeral = 1. 
	 * @param vetorOrdenado
	 * @param hashCG
	 */
	public void calcula_e_armazena_RankGeral_THD(Vector<NodeTHD> vetorOrdenado,
			Candidates candidates) {

		if (vetorOrdenado == null) {
			System.out.println("Error: THD feature: General rank: THD sort vector is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: THD feature: General rank: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos no vetor:
			for (int iPal=0; iPal<vetorOrdenado.size(); iPal++) {
				NodeTHD nodeVector = vetorOrdenado.get(iPal);
				String palavra = nodeVector.palavra;
				int rankPal = iPal + 1; 

				// verifica se o ct estah presente no hash de corpus de lingua geral:
				if (candidates.containsKey(palavra.trim())) {

					// atualiza hash1gram:
					NodeCandidate node = candidates.getNode(palavra);
					node.rankTHD_CorpusGeral = rankPal;
					candidates.put(palavra, node);

				}			

			}
			vetorOrdenado = null;
		}
	}
}