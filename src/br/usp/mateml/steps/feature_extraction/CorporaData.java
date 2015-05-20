package br.usp.mateml.steps.feature_extraction;

import java.util.HashMap;
import java.util.Set;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;

public class CorporaData {

	public int calculaSomatoriaTF_CorpusGeral(HashMap<String, NodeCandidate> hashCG) {
		int somatoriaTF = 0;

		if (hashCG.isEmpty()) {
			return -1;
		}

		Set<String> set = hashCG.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String ct = (String)array[i];
			somatoriaTF += hashCG.get(ct).freq_CorpusGeral;
		}

		return somatoriaTF;
	}

	public int calculaSomatoriaTF_CorpusEspec(Candidates candidates) {
		int somatoriaTF = 0;

		if (candidates.hashCandidatesIsEmpty()) {
			return -1;
		}

		Set<String> set = candidates.getHashCandidates().keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String ct = (String)array[i];
			if (candidates.getNode(ct).tamGram == 1) // se eh unigrama.
				somatoriaTF += candidates.getNode(ct).freq;
		}

		return somatoriaTF;
	}

}
