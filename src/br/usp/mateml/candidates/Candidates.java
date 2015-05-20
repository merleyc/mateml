package br.usp.mateml.candidates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.main.GlobalVariables;
import br.usp.mateml.steps.feature_extraction.ATF;
import br.usp.mateml.steps.feature_extraction.Control;
import br.usp.mateml.steps.feature_extraction.CorporaData;
import br.usp.mateml.steps.feature_extraction.GlossEx;
import br.usp.mateml.steps.feature_extraction.NodeFeatureOfTax;
import br.usp.mateml.steps.feature_extraction.NodeTHD;
import br.usp.mateml.steps.feature_extraction.RIDF;
import br.usp.mateml.steps.feature_extraction.TDS;
import br.usp.mateml.steps.feature_extraction.THD;
import br.usp.mateml.steps.feature_extraction.Weirdness;
import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;

public class Candidates {

	private HashMap<String, NodeCandidate> hash_ct1gram = new HashMap<String, NodeCandidate>();
	private CorporaData corporaData = new CorporaData();
	private GlossEx glossex = new GlossEx();

	public Candidates(int gram, String pathPretextFile) {
		loadCandidates(gram, pathPretextFile);
	}

	public boolean hashCandidatesIsEmpty() {
		if (hash_ct1gram == null || hash_ct1gram.isEmpty()) {
			return true;
		}
		return false;
	}

	public HashMap<String, NodeCandidate> getHashCandidates() {
		return this.hash_ct1gram;
	}

	public NodeCandidate getNode(String st) {
		if (hash_ct1gram != null && containsKey(st)) {
			return hash_ct1gram.get(st);
		}
		return null;
	}

	public void put(String st, NodeCandidate node) {
		if (hash_ct1gram == null) {
			hash_ct1gram = new HashMap<String, NodeCandidate>();
		}
		hash_ct1gram.put(st, node);
	}

	public boolean containsKey(String st) {
		if (hash_ct1gram != null && hash_ct1gram.containsKey(st)) {
			return true;
		}
		return false;
	}


	/**
	 * Esse metodo carrega os candidatos a termos que estao no arquivo PreText .all para um hash.
	 * @param pathPretextFile caminho do arquivo da Pretext.
	 * @return hash listact (lista de candidatos a termos) com suas respectivas frequencias, DF,
	 * tamanho do corpus em numero de textos e quantidade de gramas (unigrama, bigrama e trigrama).
	 */

	private void loadCandidates(int gram, String pathPretextFile){

		if ((pathPretextFile == null) || (pathPretextFile.isEmpty()) || (!new File(pathPretextFile).isFile()) ) {
			System.out.println("O arquivo " + gram + "-Gram.all da PreTexT nao existe ou estah vazio." + pathPretextFile);
			return;
		}

		File arqPretext = new File(pathPretextFile);

		try {
			if (arqPretext.exists() == false) {
				System.out.println("Nao encontrou o arquivo de entrada da PreTexT: " + pathPretextFile);
				return;
			}

			//BufferedReader in = new BufferedReader(new FileReader(arqPretext));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(pathPretextFile);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arqPretext),encoding));
			else in = new BufferedReader(new FileReader(arqPretext));

			String line = in.readLine();

			// exemplo da pretext: 1:(1/2):ativ
			while (line != null) {
				if (!line.equals("")) {

					int ngramas = -1; 		// quantos gramas o ct tem, ie, se � composto de uma palavra (unigrama), duas (bigrama), etc.
					String string = "", 	// variavel para obter o ct.
							grama = "",				// vari�vel para obter o ct.
							stringFreq = "", 		//frequencia do ct no corpus.
							stringDF = "",	 		// document frequency, ie, em quantos docs o ct aparece. 
							stringTamCorpus = ""; 	// quantidade de textos do corpus.
					int freq = -1,			//frequencia do ct no corpus.
							DF = -1,				// document frequency, ie, em quantos docs o ct aparece.
							tamCorpus = -1,			// quantidade de textos do corpus.	
							compGram = -1; 			// tamanho da palavra em num de caracteres
					StringTokenizer st = new StringTokenizer(line,":");

					if (st.hasMoreTokens()) {
						stringFreq = st.nextToken();
						if (Treatment.ehNumero(stringFreq))
							freq = Integer.parseInt(stringFreq);

						if (st.hasMoreTokens()) {
							String aux = st.nextToken(); // (2/100)
							StringTokenizer st_aux = new StringTokenizer(aux,"/");
							if (st_aux.hasMoreTokens()) {
								aux = st_aux.nextToken(); // (2
								stringDF = Treatment.replaceAll('(', ' ', aux);							
								DF = Integer.parseInt(stringDF.trim());
							}
							if (st_aux.hasMoreTokens()) {
								aux = st_aux.nextToken(); // 100)
								stringTamCorpus = Treatment.replaceAll(')', ' ', aux);
								tamCorpus = Integer.parseInt(stringTamCorpus.trim());
							}
						}

						if (st.hasMoreTokens()) { // ativ
							grama = st.nextToken();
							grama = Treatment.tratar_termo(grama, true);
							if (!grama.isEmpty()) {
								if (string.isEmpty()) {
									string = grama;
									compGram = string.length();
								}
								else {
									string += "<>"+grama;
									compGram = string.length() - 2;
								}
								ngramas++;
							} 
						}
					}

					if (!string.equals("")) {
						NodeCandidate node = new NodeCandidate();
						node.termo = string;
						node.freq = freq;
						node.compGram = compGram;
						if (ngramas>-1)
							node.tamGram = ngramas+1;
						node.tamCorpus = tamCorpus;
						node.df = DF;

						GlobalVariables.candidates.hash_ct1gram.put(string, node);
					}
				}
				line = in.readLine();
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Esse metodo calcula o weirdness de cada CT utilizando, para isso, um corpus de lingua geral.
	 * Coloca weirdness = 1 para os candidatos que nao aparecem no corpus de lingua geral.
	 * @param hashCG
	 */
	public void atualizar_hashWeirdness(HashMap<String, NodeCandidate> hashCG,
			Candidates candidates) {

		if (hashCG == null) {
			System.out.println("Error: Weirdness feature: hash of general language corpus is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: Weirdness feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();
			int numPalCG = hashCG.size(),
					numCTs = candidates.getHashCandidates().size();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				// verifica se o ct estah presente no corpus de lingua geral:
				double weirdness = 1;
				if (hashCG.containsKey(ct)) {
					int freqCT_CorpusGeral = hashCG.get(ct).freq_CorpusGeral;
					int freqCT = nodeCT.freq;
					weirdness = Weirdness.calculaWeirdness(freqCT, numCTs, freqCT_CorpusGeral, numPalCG);
				}

				// atualiza hash1gram:
				NodeCandidate node =  candidates.getHashCandidates().get(ct);
				node.weirdness = weirdness;
				candidates.getHashCandidates().put(ct, node);

			}

			Control.controle_weirdness = true;
			System.out.println("Caracteristica Weirdness armazenada.");
		}
	}

	public void atualizar_hashRIDF(Candidates candidates, RIDF ridf) {

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: RIDF feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();
			int numCTs = candidates.getHashCandidates().size();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				// verifica se o ct estah presente no corpus de lingua geral:
				double value_ridf = -1;
				int freqCT = nodeCT.freq,
						docFreq = nodeCT.df,
						totalDocs = nodeCT.tamCorpus;

				value_ridf = ridf.calculaRIDF(freqCT, numCTs, docFreq, totalDocs);

				// atualiza hash1gram:
				NodeCandidate node =  candidates.getHashCandidates().get(ct);
				node.ridf = value_ridf;
				candidates.getHashCandidates().put(ct, node);

			}

			Control.controle_ridf = true;
			System.out.println("Caracteristica RIDF armazenada.");
		}
	}

	/**
	 * Average Term Frequency in Corpus - calculated by dividing the total frequency
	 *  of a term in a corpus by its document frequency.
	 *  ( http://code.google.com/p/jatetoolkit/wiki/JATEIntro )
	 * @param hash_candidates 
	 */
	public void atualizar_hashATF(Candidates candidates, ATF atf) {

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: ATF feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;


				// verifica se o ct estah presente no corpus de lingua geral:
				double value_atf = -1;
				int freqCT = nodeCT.freq,
						docFreqCT = nodeCT.df;

				value_atf = atf.calculaATF(freqCT, docFreqCT);

				// atualiza hash1gram:
				NodeCandidate node =  candidates.getHashCandidates().get(ct);
				node.atf = value_atf;
				candidates.getHashCandidates().put(ct, node);

			}

			Control.controle_atf = true;
			System.out.println("Caracteristica ATF armazenada.");
		}
	}

	public void atualizar_hashTHD(HashMap<String, NodeCandidate> hashCG,
			Candidates candidates, THD thd) {

		if (hashCG == null) {
			System.out.println("Error: THD feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: THD feature: candidate hash is null or empty.");
		} else {

			// obter valores de ordenacao (rank) dos CTs:
			Vector<NodeTHD> vetorRankEspec_THD = thd.ordenaPalPorFreq_CorpusEspec_THD(candidates);
			Vector<NodeTHD> vetorRankGeral_THD = thd.ordenaPalPorFreq_CorpusGeral_THD(hashCG);

			// armazenar valores de ordenacao (rank) dos CTs:
			thd.calcula_e_armazena_RankEspec_THD(vetorRankEspec_THD, candidates);
			thd.calcula_e_armazena_RankGeral_THD(vetorRankGeral_THD, candidates);

			//Util.imprimeHash(candidates.getHashCandidates());

			int qtdePal_CorpusEspec = candidates.getHashCandidates().size(),
					qtdePal_CorpusGeral = hashCG.size();

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;
				int rankEspec_THD = nodeCT.rankTHD_CorpusEspec;
				int rankGeral_THD = nodeCT.rankTHD_CorpusGeral;

				// verifica se o ct estah presente no corpus de lingua geral:
				double valueThd = -1;

				valueThd = thd.calculaTHD(ct, rankEspec_THD, rankGeral_THD, qtdePal_CorpusEspec, qtdePal_CorpusGeral);

				// atualiza hash1gram:
				NodeCandidate node = candidates.getHashCandidates().get(ct);
				node.thd = valueThd;
				candidates.getHashCandidates().put(ct, node);

			}

			Control.controle_thd = true;
			System.out.println("Caracteristica THD armazenada.");
		}
	}

	public void atualizar_hashTDS (HashMap<String, NodeCandidate> hashCG,
			Candidates candidates, TDS tds) {

		if (hashCG == null) {
			System.out.println("Error: hash of general language corpus is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TDS feature: candidate hash is null or empty.");
		} else {

			// obter somatorias de frequencias dos corpora:
			int tfTotalPals_CorpusEspec = corporaData.calculaSomatoriaTF_CorpusEspec(candidates);
			int tfTotalPals_CorpusGeral = corporaData.calculaSomatoriaTF_CorpusGeral(hashCG);

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;
				int freqPal_CorpusEspec = nodeCT.freq;
				int freqPal_CorpusGeral = nodeCT.freq_CorpusGeral;

				double value_tds = -1;

				value_tds = tds.calculaTDS(ct, freqPal_CorpusEspec, tfTotalPals_CorpusEspec,
						freqPal_CorpusGeral, tfTotalPals_CorpusGeral);

				// atualiza hash1gram:
				if (value_tds != -1) {
					NodeCandidate node =  candidates.getHashCandidates().get(ct);
					node.tds = value_tds;
					candidates.getHashCandidates().put(ct, node);
				}

			}

			// atribui o valor maximo de TDS para os CTs que nao aparecem no corpus de lingua geral:
			atualizar_hashTDS_CTs_naoApareceNoCG(candidates, tds);

			Control.controle_tds = true;
			System.out.println("Caracteristica TDS armazenada.");
		}
	}

	/**
	 * Esse metodo atribui aos CTs que nao aparecem no corpus de lingua geral
	 * TDS = valor maximo de TDS obtido considerando todos os valores de TDS.
	 * @param hash_candidates 
	 */
	public void atualizar_hashTDS_CTs_naoApareceNoCG(Candidates candidates, TDS tds) {

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TDS 2 feature: candidate hash is null or empty.");
		} else {

			Vector<String> vetor_ct_comCG_zero = tds.get_vetor_ct_comCG_zero();
			double maxTDS = tds.get_maxTDS();

			if (vetor_ct_comCG_zero != null && !vetor_ct_comCG_zero.isEmpty()) {

				for (int i=0; i < vetor_ct_comCG_zero.size(); i++) {

					String ct = vetor_ct_comCG_zero.get(i).trim();
					if (candidates.getHashCandidates().containsKey(ct)) {
						NodeCandidate node =  candidates.getHashCandidates().get(ct);
						node.tds = maxTDS;
						candidates.getHashCandidates().put(ct, node);
					}

				}
			}

			vetor_ct_comCG_zero = null;
		}
	}

	public void atualizar_hashGlossEx (double alpha, double beta,
			HashMap<String, NodeCandidate> hashCG,
			Candidates candidates) {

		if (hashCG == null) {
			System.out.println("Error: GlosseEx feature: hash of general language corpus is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: GlossEx feature: candidate hash is null or empty.");
		} else {

			// obter somatorias de frequencias dos corpora:
			int tfTotalPals_CorpusEspec = corporaData.calculaSomatoriaTF_CorpusEspec(candidates);
			int tfTotalPals_CorpusGeral = corporaData.calculaSomatoriaTF_CorpusGeral(hashCG);

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				double valueGlossex = -1;

				valueGlossex = glossex.calculaGlossEx(ct, candidates, hashCG, alpha, beta,
						tfTotalPals_CorpusEspec, tfTotalPals_CorpusGeral);

				// atualiza hash1gram:
				if (valueGlossex != -1) {
					NodeCandidate node =  candidates.getHashCandidates().get(ct);
					node.glossex = valueGlossex;
					candidates.getHashCandidates().put(ct, node);
				}

			}

			// atribui o valor maximo de TDS para os CTs que nao aparecem no corpus de lingua geral:
			atualizar_hashGlossEx_CTs_naoApareceNoCG(candidates);

			Control.controle_glossex = true;
			System.out.println("Caracteristica GlossEx armazenada.");
		}
	}

	/**
	 * Esse metodo atribui aos CTs que nao aparecem no corpus de lingua geral
	 * GlossEx = valor maximo de GlossEx obtido considerando todos os valores de GlossEx.
	 */
	public void atualizar_hashGlossEx_CTs_naoApareceNoCG(
			Candidates candidates) {

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: GlossEx 2 feature: candidate hash is null or empty.");
		} else {
			Vector<String> vetor_ct_comCG_zero = glossex.get_vetor_ct_comCG_zero();
			double maxGlossEx = glossex.get_maxGlossEx();

			if (vetor_ct_comCG_zero != null && !vetor_ct_comCG_zero.isEmpty()) {

				for (int i=0; i < vetor_ct_comCG_zero.size(); i++) {

					String ct = vetor_ct_comCG_zero.get(i).trim();
					if (candidates.getHashCandidates().containsKey(ct)) {
						NodeCandidate node =  candidates.getHashCandidates().get(ct);
						node.glossex = maxGlossEx;
						candidates.getHashCandidates().put(ct, node);
					}

				}
			}

			vetor_ct_comCG_zero = null;
		}
	}

	public void atualizar_hashCG(HashMap<String, NodeCandidate> hashCG,
			Candidates candidates) {

		if (hashCG == null) {
			System.out.println("Error: TFIDF feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TFIDF feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();
			String termoCG = "";

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				// percorre as palavras do corpus de língua geral:
				Set<String> setCG = hashCG.keySet();
				Object[] arrayCG = setCG.toArray();

				NodeCandidate node =  candidates.getHashCandidates().get(ct);
				for (int iCG=0; iCG<arrayCG.length; iCG++) {
					termoCG = (String)arrayCG[iCG];

					if (termoCG.startsWith(ct)) { // se o ct eh um prefixo da palavra do corpus geral:
						node.corpusGeral = 1;

						// considera a soma das frequencias onde o ct eh um prefixo da palavra do corpus geral:
						NodeCandidate nodeCG =  hashCG.get(termoCG);
						int freq_CorpusGeral = nodeCG.freq_CorpusGeral;
						if (node.freq_CorpusGeral > 0)
							freq_CorpusGeral += node.freq_CorpusGeral;
						node.freq_CorpusGeral = freq_CorpusGeral;					
					}

				}
				candidates.getHashCandidates().put(ct, node);
			}

			/*		Set<String> set = hash_candidates.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String chave = (String)array[i];
			NodeCandidate node =  hash_candidates.get(chave);
			if (CG.containsKey(chave)) {
				NodeCandidate nodeCG = CG.get(chave);
				node.corpusGeral = nodeCG.corpusGeral;
				node.freq_CorpusGeral = nodeCG.freq_CorpusGeral;
				hash_candidates.put(chave, node);
			}
			else {
				node.corpusGeral = 0;
				node.freq_CorpusGeral = 0;
				hash_candidates.put(chave, node);
			}
		}*/

			Control.controle_cg = true;
			System.out.println("Caracteristica Frequencia do Corpus de Lingua Geral armazenada.");
		}
	}

	public void atualizar_hashEI(HashMap<String, Boolean> hashPalavrasPertoEI,
			Candidates candidates) {

		if (hashPalavrasPertoEI == null) {
			System.out.println("Error: Indicative phrase feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: Indicative phrase feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();
			String palavraPertoEI = "";

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				// percorre os candidatos a termos:
				Set<String> setEI = hashPalavrasPertoEI.keySet();
				Object[] arrayEI = setEI.toArray();
				NodeCandidate node =  candidates.getHashCandidates().get(ct);

				for (int iEI=0; iEI<arrayEI.length; iEI++) {
					palavraPertoEI = (String)arrayEI[iEI];
					if (palavraPertoEI.indexOf(ct) == 0) // se o ct � uma substring da palavra que abrange um dado padrao morfossintatico
						node.expr_indicativas = 1;
				}
				candidates.getHashCandidates().put(ct, node);
			}


			/*		Set<String> set = hashPalavrasPertoEI.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String palavraPertoEI = (String)array[i];
			if (hash_candidates.containsKey(palavraPertoEI)) {
				NodeCandidate node =  hash_candidates.get(palavraPertoEI);
				node.expr_indicativas = 1;
				hash_candidates.put(palavraPertoEI, node);
			}
		}*/

			Control.controle_ei = true;
			System.out.println("Caracteristica de Expressoes Indicativas armazenada.");
		}
	}

	public void atualizar_hashCvalue(HashMap<String, Double> hashCvalue,
			Candidates candidates) {

		if (hashCvalue == null) {
			System.out.println("Error: C-value feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: C-value feature: candidate hash is null or empty.");
		} else {

			Set<String> set = hashCvalue.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String ct = (String)array[i];
				if (candidates.getHashCandidates().containsKey(ct)) {
					double cvalue = hashCvalue.get(ct);
					NodeCandidate node =  candidates.getHashCandidates().get(ct);
					node.cvalue = cvalue;
					candidates.getHashCandidates().put(ct, node);
				}
			}

			Control.controle_cvalue = true;
			System.out.println("Caracteristica C-Value armazenada.");
		}
	}

	public void atualizar_hashNCvalue(HashMap<String, Double> hashNCvalue,
			Candidates candidates) {

		if (hashNCvalue == null) {
			System.out.println("Error: NC-value feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: NC-value feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();
			String termoNC = "";

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;

				// percorre os candidatos a termos e seus valores de NCValue:
				Set<String> setNC = hashNCvalue.keySet();
				Object[] arrayNC = setNC.toArray();
				NodeCandidate node =  candidates.getHashCandidates().get(ct);

				for (int iNC=0; iNC<arrayNC.length; iNC++) {
					termoNC = (String)arrayNC[iNC];
					if (termoNC.indexOf(ct) == 0) { // se o ct � uma substring da palavra que abrange um dado padrao morfossintatico
						node.ncvalue = hashNCvalue.get(termoNC);
					}
				}
				candidates.getHashCandidates().put(ct, node);
			}

			/*		Set<String> set = hashNCvalue.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String ct = (String)array[i];
			if (hash_candidates.containsKey(ct)) {
				//if (termoPM.indexOf(ct) == 0)
				//
				double ncvalue = hashNCvalue.get(ct);
				NodeCandidate node =  hash_candidates.get(ct);
				node.ncvalue = ncvalue;
				hash_candidates.put(ct, node);
			}
		}*/

			Control.controle_ncvalue= true;
			System.out.println("Caracteristica NC-Value armazenada.");
		}
	}

	public void atualizar_hashSintagmas(HashMap<String, Character> hashSintagmas,
			Candidates candidates) {

		if (hashSintagmas == null) {
			System.out.println("Error: NP feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: NP feature: candidate hash is null or empty.");
		} else {

			Set<String> set = hashSintagmas.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String palavraSintagma = (String)array[i];
				int nucleo = 0, sintagma = 0;

				if (candidates.getHashCandidates().containsKey(palavraSintagma)) {
					char caracteristicaSintagma = hashSintagmas.get(palavraSintagma);
					if (caracteristicaSintagma == 's') 
						sintagma = 1;
					else if (caracteristicaSintagma == 'n') {
						nucleo = 1;
						sintagma = 1;
					}
					NodeCandidate node =  candidates.getHashCandidates().get(palavraSintagma);
					if (candidates.getHashCandidates().get(palavraSintagma).nucleo_sintagmas != 1) {
						node.nucleo_sintagmas = nucleo;
						node.sintagmas = sintagma;
						candidates.getHashCandidates().put(palavraSintagma, node);
					}
				}
			}

			/*		if (hashSintagmas == null) {
			System.out.println("O hash com os sintagmas eh nulo.");
			return;
		}

		String palavraS = "", ct = "";
		char tipo;		

		HashMap<String, Character> hashSintagmasAux = Sintagmas.separarPalavrasSintagmas(hashSintagmas);

		// percorre cada ct:
		Set<String> set = hash_candidates.keySet();
		Object[] array = set.toArray();

		for (int iCT=0; iCT<array.length; iCT++) {
			ct = (String)array[iCT];

			// percorre cada palavra dos sintagmas e nucleos:
			Set<String> setS = hashSintagmasAux.keySet();
			Object[] arrayS = setS.toArray();

			for (int iS=0; iS<arrayS.length; iS++) {
				palavraS = (String)arrayS[iS];
				tipo = hashSintagmasAux.get(palavraS);
				int nucleo = 0,	sintagma = 0;

				if (palavraS.indexOf(ct) == 0) { // se o ct � uma substring do nucleo do sintagma ou de uma parte do sintagma
					if (tipo == 's') 
						sintagma = 1;
					else if (tipo == 'n') {
						nucleo = 1;
						sintagma = 1;
					}
					NodeCandidate node =  hash_candidates.get(ct);
					if (hash_candidates.get(ct).nucleo_sintagmas != 1) {
						node.nucleo_sintagmas = nucleo;
						node.sintagmas = sintagma;
						hash_candidates.put(ct, node);
					}
				}
			}

		}*/
			Control.controle_sintagmas = true;
			System.out.println("Caracteristicas Sintagmas e Nucleos armazenadas.");
		}
	}

	/**
	 * Esse metodo verifica se o CT *unigrama* faz parte de algum padrao morfossintatico (PM) considerado e atuliaz
	 * @param hashUnigramasPadroes eh um hash contendo as palavras que foram consideradas pelo parser Palavras como padrao morofssintatico.
	 */
	public void atualizar_hashPadroesMorfossintaticos(HashMap<String, Boolean> hashUnigramasPadroes,
			Candidates candidates) {

		if (hashUnigramasPadroes == null) {
			System.out.println("Error: POS feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: POS feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> set = hashUnigramasPadroes.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String palavraPadrao = (String)array[i];
				int padrao = 0;

				if (candidates.getHashCandidates().containsKey(palavraPadrao)) {
					padrao = 1;

					NodeCandidate node =  candidates.getHashCandidates().get(palavraPadrao);
					node.padrao = padrao;
					candidates.getHashCandidates().put(palavraPadrao, node);
				}
			}

			Control.controle_pm = true;
			System.out.println("Caracteristica de Padroes Morfossintaticos armazenada.");
		}
	}

	/**
	 * Esse metodo verifica se o CT *unigrama* contem na lista de termos de referencia do dominio.
	 * @param hashLR eh um hash contendo as os termos de referencia *stemizados*.
	 */
	public void atualizar_hashLR(HashMap<String, Boolean> hashLR,
			Candidates candidates) {

		if (hashLR == null || hashLR.isEmpty()) {
			Control.controle_lr = false;
			System.out.println("Error: Reference list feature hash is null or empty.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			Control.controle_lr = false;
			System.out.println("Error: Reference list feature: candidate hash is null or empty.");
		} else {

			String termo = "";		
			Set<String> set = hashLR.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				termo = array[i].toString();
				if (candidates.getHashCandidates().containsKey(termo)) {
					NodeCandidate node =  candidates.getHashCandidates().get(termo);
					node.listaRef = 1;
				}
				else System.out.println(termo);
			}

			Control.controle_lr = true;
			System.out.println("Lista de termos de referencia armazenada.");
		}
	}


	public void atualizar_hashTFIDF(HashMap<String, Double> hashTFIDF,
			Candidates candidates) {

		if (hashTFIDF == null) {
			System.out.println("Error: TFIDF feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TFIDF feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> setCT = candidates.getHashCandidates().keySet();
			Object[] arrayCT = setCT.toArray();

			for (int iCT=0; iCT<arrayCT.length; iCT++) {
				NodeCandidate nodeCT = candidates.getHashCandidates().get(arrayCT[iCT]);
				String ct = nodeCT.termo;
				double TFIDF = 0.0;

				if (hashTFIDF.containsKey(ct)) {
					TFIDF = hashTFIDF.get(ct);
				}

				nodeCT.tfidf = TFIDF;
				candidates.getHashCandidates().put(ct, nodeCT);
			}

			Control.controle_tfidf = true;
			System.out.println("Caracteristica TFIDF armazenada.");
		}
	}

	public void atualizar_hashTv(HashMap<String, Double> hashTv,
			Candidates candidates) {

		if (hashTv == null) {
			System.out.println("Error: TV feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TV feature: candidate hash is null or empty.");
		} else {

			Set<String> set = hashTv.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String chave = (String)array[i];
				chave = Treatment.tratar_termo(chave, true);
				double valueTv = hashTv.get(chave);
				if (candidates.getHashCandidates().containsKey(chave)) {
					NodeCandidate node =  candidates.getHashCandidates().get(chave);
					node.tv = valueTv;
					candidates.getHashCandidates().put(chave, node);
				}
				else System.out.println(chave);
			}

			Control.controle_tv = true;
			System.out.println("Caracteristica TV armazenada.");
		}
	}

	public void atualizar_hashTvq(HashMap<String, Double> hashTvq,
			Candidates candidates) {

		if (hashTvq == null) {
			System.out.println("Error: TVQ feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TVQ feature: candidate hash is null or empty.");
		} else {

			Set<String> set = hashTvq.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String chave = (String)array[i];
				chave = Treatment.tratar_termo(chave, true);
				double valueTvq = hashTvq.get(chave);
				if (candidates.getHashCandidates().containsKey(chave)) {
					NodeCandidate node =  candidates.getHashCandidates().get(chave);
					node.tvq = valueTvq;
					candidates.getHashCandidates().put(chave, node);
				}
			}

			Control.controle_tvq = true;
			System.out.println("Caracteristica TVQ armazenada.");
		}
	}

	public void atualizar_hashTc(HashMap<String, Double> hashTc,
			Candidates candidates) {

		if (hashTc == null) {
			System.out.println("Error: TC feature hash is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: TC feature: candidate hash is null or empty.");
		} else {

			Set<String> set = hashTc.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String chave = (String)array[i];
				chave = Treatment.tratar_termo(chave, true);
				double valueTc = hashTc.get(chave);
				if (candidates.getHashCandidates().containsKey(chave)) {
					NodeCandidate node =  candidates.getHashCandidates().get(chave);
					node.tc = valueTc;
					candidates.getHashCandidates().put(chave, node);
				}
			}

			Control.controle_tc = true;
			System.out.println("Caracteristica TC armazenada.");
		}
	}

	public void atualizar_hashTiposPalavrasOriginais(
			HashMap<String, Vector<Integer>> hashTipoPalOriginais,
			Candidates candidates) {

		if (hashTipoPalOriginais == null) {
			System.out.println("Error: POS feature: ratio of POS is null.");
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: POS feature: candidate hash is null or empty.");
		} else {

			// percorre os candidatos a termos:
			Set<String> set = hashTipoPalOriginais.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String stem = (String)array[i];

				if (candidates.containsKey(stem)) {
					int substantivo = hashTipoPalOriginais.get(stem).get(0);
					int adjetivo = hashTipoPalOriginais.get(stem).get(1);
					int verbo = hashTipoPalOriginais.get(stem).get(2);
					int numPalOriginais = hashTipoPalOriginais.get(stem).get(3);

					NodeCandidate nodo = candidates.getNode(stem);
					nodo.substantivo = substantivo;
					nodo.adjetivo = adjetivo;
					nodo.verbo = verbo;
					nodo.numPalOriginais = numPalOriginais;
					candidates.put(stem, nodo);
				}
			}

			Control.controle_TipoPalOriginais = true;
			System.out.println("Caracteristica de Classe gramatical das palavras origens.");
		}
	}


	public static void atualizar_hashCaracteristicasTaxonomia(
			HashMap<Integer, NodeFeatureOfTax> hashTax, Candidates candidates) {
		if (hashTax == null) {
			System.out.println("O hash com características da taxonomia eh nulo.");
			return;
		} else if (candidates.getHashCandidates() != null) {
			// percorre os termos da taxonomia:
			Set<Integer> setNohTax = hashTax.keySet();
			Object[] arrayNohTax = setNohTax.toArray();

			for (int iNohTax=0; iNohTax<arrayNohTax.length; iNohTax++) {
				NodeFeatureOfTax nohTax = hashTax.get(arrayNohTax[iNohTax]);
				String listaTermos = nohTax.termos;
				if (listaTermos != null) {
					listaTermos = listaTermos.trim();

					// Percorre a lista de termos do noh atual da taxonomia:
					if (listaTermos.contains(",")) {
						StringTokenizer tokenizer = new StringTokenizer(listaTermos,",");
						while (tokenizer.hasMoreTokens()) {
							String termoTax = tokenizer.nextToken().trim();
							colocaDadosHashCaracteristicasTaxonomia (nohTax, termoTax, candidates);
						}
					}

					else if (!listaTermos.equals("")) {
						colocaDadosHashCaracteristicasTaxonomia(nohTax, listaTermos, candidates);
					}

				}
			}
			System.out.println("Caracteristicas da taxonomia armazenadas.");
		}
	}


	// atualiza, no hash de candidatos a termos, as caracteristicas do termo atual provindas da taxonomia:
	public static void colocaDadosHashCaracteristicasTaxonomia(
			NodeFeatureOfTax nohTax, String termoValor, Candidates candidates) {

		if (candidates.getHashCandidates() != null) {

			String termo = "", valor = "";
			// Separa o termo do seu valor:
			/* ex. de termoTax: [energ [9.20459803246039], se for o primeiro. Se for o termo do meio serah:
				energ [9.20459803246039]. Se for o ultimo, serah energ [9.20459803246039]].
			 */
			termoValor = termoValor.replace('[',' ');
			termoValor = termoValor.replace(']',' ');
			if (termoValor.contains(" ")) {
				StringTokenizer tokenizerTermo = new StringTokenizer(termoValor, " ");
				if (tokenizerTermo.hasMoreElements())
					termo = tokenizerTermo.nextToken().trim();

				if (tokenizerTermo.hasMoreElements())
					valor = tokenizerTermo.nextToken().trim();
			}
			if (!candidates.containsKey(termo))
				System.out.println("MatrizSaida: - Termo: " + termo);

			if ((!termo.equals("")) &&  (candidates.containsKey(termo))) {

				NodeCandidate nodo = candidates.getNode(termo);

				nodo.parent = nohTax.parent;
				nodo.numIrmaos = nohTax.numIrmaos;
				nodo.listaDocumentos = nohTax.listaDocumentos;
				nodo.nivel = nohTax.nivel;
				nodo.numDocs = nohTax.numDocs;
				nodo.numNohsFilhos = nohTax.numNohsFilhos;
				nodo.numDescendentes = nohTax.numDescendentes;
				nodo.valorTermo = Float.parseFloat(valor);

				candidates.put(termo, nodo);

			} else System.out.println("Nao encontrou termo em um específico noh da taxonomia.");
		}
	}

}