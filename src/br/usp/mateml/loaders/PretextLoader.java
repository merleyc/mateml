package br.usp.mateml.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.feature_extraction.Pair;
import br.usp.mateml.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;

public class PretextLoader {

	/**
	 * Esse m�todo carrega os candidatos a termos que est�o no arquivo PreText .all para um hash.
	 * @param caminhoarq caminho do arquivo da Pretext.
	 * @return hash listact (lista de candidatos a termos) com suas respectivas frequencias, DF,
	 * tamanho do corpus em n�mero de textos e quantidade de gramas (unigrama, bigrama e trigrama).
	 */
	public HashMap<String, NodeCandidate> lerArqPretextGramas(int ngrama, String caminhoarq){

		if ((caminhoarq == null) || (caminhoarq.isEmpty()) || (!new File(caminhoarq).isFile()) ) {
			System.out.println("O arquivo " + ngrama + "-Gram.all da PreTexT nao existe ou estah vazio." + caminhoarq);
			return null;
		}

		HashMap<String, NodeCandidate> listact = new HashMap<String, NodeCandidate>();

		File arqPretext = new File(caminhoarq);

		try {
			if (arqPretext.exists() == false) {
				System.out.println("Nao encontrou o arquivo de entrada da PreTexT: " + caminhoarq);
				return null;
			}

			//BufferedReader in = new BufferedReader(new FileReader(arqPretext));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoarq);
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
						NodeCandidate nodo = new NodeCandidate();
						nodo.termo = string;
						nodo.freq = freq;
						nodo.compGram = compGram;
						if (ngramas>-1)
							nodo.tamGram = ngramas+1;
						nodo.tamCorpus = tamCorpus;
						nodo.df = DF;

						listact.put(string, nodo);
					}
				}
				line = in.readLine();
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listact;
	}

	/**
	 * Esse m�todo grava em um vetor os cts do arquivo no formato .name da
	 *  Pretext em suas mesmas ordens.
	 * @param matrizName � matriz no formato .name da Pretext.
	 * @return VetorMatrizNames � o vetor com os cts gravados.
	 */
	public Vector<String> obterTermoDaMatrizName(String matrizName) {
		if (matrizName == null) {
			System.out.println("Nao existe arquivo .name da Pretext: " + matrizName);
			return null;
		}

		Vector<String> VetorMatrizNames = new Vector<String>();
		BufferedReader entrada;

		try {
			//entrada = new BufferedReader(new FileReader(matrizName));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(matrizName);
			if (encoding != null) 
				entrada = new BufferedReader(new InputStreamReader(new FileInputStream(matrizName),encoding));
			else entrada = new BufferedReader(new FileReader(matrizName));

			String linha, termo;
			try {
				linha = entrada.readLine();
				termo = "";
				int i =0;
				StringTokenizer st;
				if (linha.trim().equals("filenames:string"))
					linha = entrada.readLine(); // Exemplo: "xerox":integer.

				while (linha != null) {
					st = new StringTokenizer(linha, "\"");
					if (st.hasMoreTokens()) {
						termo = st.nextToken();
						VetorMatrizNames.add(i, termo);
					}
					i++;
					linha = entrada.readLine();
				}

				entrada.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return VetorMatrizNames;
	}

	public HashMap<String, String> carregarStem(
			String caminhoPretextStem) {
		/* Exemplo de entrada:
		 * public : 1(1/2)
       				publicacao:1
	   				publicacoes:1
		 */

		HashMap<String, String> hashStem = new HashMap<String, String>();

		try {
			//BufferedReader in = new BufferedReader(new FileReader(caminhoPretextStem));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoPretextStem);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoPretextStem),encoding));
			else in = new BufferedReader(new FileReader(caminhoPretextStem));

			String linha = in.readLine(),
					stem = "", palavra = "";
			StringTokenizer st = null;

			while (linha != null) {
				if (!linha.trim().equals("")) {
					st = new StringTokenizer(linha,":");

					//public : 1(1/2)
					if (linha.contains("("))
						stem = st.nextToken().trim();

					/*	publicacao:1
   					publicacoes:1
					 */
					else {
						palavra = st.nextToken().trim().toLowerCase();
						palavra = Treatment.removerAcentos(palavra);
						palavra = Treatment.removerPontuacao(palavra);
						hashStem.put(palavra, stem);
					}

				}
				linha = in.readLine();
			}

			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hashStem;
	}

	/**
	 * Esse metodo grava em um hash o stem de um dado texto e sua(s) respectiva(s) palavra(s) original(is), assim: hash<stem, palavraOriginal>.
	 * @param caminhoPretextStem eh o diretorio onde está os arquivos que contem as palavras e os stems.
	 * @param nomeArquivo eh o nome do arquivo atual.
	 * @return um hash com a palavra original e seu respectivo stem.
	 */
	public HashMap<String, Vector<String>> buscarStemDePalavra(
			String caminhoPretextStem) {

		HashMap<String, Vector<String>> stemPalavra = new HashMap<String, Vector<String>>();
		HashMap<String, String> palavraStem = new HashMap<String, String>();

		if (new File(caminhoPretextStem).isFile())
			palavraStem = carregarStem(caminhoPretextStem);

		//invertendo o hash de "palavra,stem" para "stem,palavra":
		Set<String> set = palavraStem.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			String palavra = array[i].toString();
			String stem = palavraStem.get(array[i]).toString();

			// obtendo palavras originais correspondentes ao stem atual, caso haja.
			Vector<String> vetorPalavras = new Vector<String>();
			if (stemPalavra.containsKey(stem))
				vetorPalavras = stemPalavra.get(stem);
			vetorPalavras.add(palavra);

			stemPalavra.put(stem, vetorPalavras);
		}

		return stemPalavra;
	}

	/**
	 * Esse metodo grava em um hash o stem de um dado texto e sua(s) respectiva(s) palavra(s)
	 * original(is), deixando para uma cada das palavras um espaço para colocar seu respectivo
	 * tipo morfossintatio. Assim: hash<stem; palavraOriginal,tipo>.
	 * @param caminhoPretextStem eh o diretorio onde está os arquivos que contem as palavras e os stems.
	 * @param nomeArquivo eh o nome do arquivo atual.
	 * @return um hash com a palavra original e tipo, e seu respectivo stem.
	 */
	public HashMap<String, Vector<Pair>> buscarStemDePalavraTipo(
			String caminhoPretextStem) {

		HashMap<String, Vector<Pair>> stemPalavra = new HashMap<String, Vector<Pair>>();
		HashMap<String, String> palavraStem = new HashMap<String, String>();

		if (new File(caminhoPretextStem).isFile())
			palavraStem = carregarStem(caminhoPretextStem);

		//invertendo o hash de "palavra,stem" para "stem,palavra":
		Set<String> set = palavraStem.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			String palavra = array[i].toString();
			String stem = palavraStem.get(array[i]).toString();

			// obtendo palavras originais correspondentes ao stem atual, caso haja.
			Vector<Pair> vetorPalavras = new Vector<Pair>();
			if (stemPalavra.containsKey(stem))
				vetorPalavras = stemPalavra.get(stem);
			Pair par = new Pair();
			par.palavra = palavra;
			par.tipo = "";
			vetorPalavras.add(par);

			stemPalavra.put(stem, vetorPalavras);
		}

		return stemPalavra;
	}

}