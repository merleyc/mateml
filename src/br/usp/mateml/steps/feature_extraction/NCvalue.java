package br.usp.mateml.steps.feature_extraction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;



public class NCvalue {

	private HashMap<String, HashMap<String, Integer>> hashPalavrasContexto = new HashMap<String, HashMap<String,Integer>>();
	private HashMap<String, Integer> hashPalavrasContextoComFreq = new HashMap<String, Integer>();
	private HashMap<String, Integer> hashstemTipoTextoComFreq = new HashMap<String, Integer>();
	private HashMap<String, Double> hashNCvalue = new HashMap<String, Double>();

	public HashMap<String, Double> calcularNCvalue(
			ConfigurationLoader configuration, int janela,
			PretextLoader pretextLoader, Candidates candidates){

		long inicial = new Date().getTime();
		File diretorioParser = new File(configuration.getCaminhoParserPalavras());
		if (diretorioParser.exists()) {
		//if (Util.arquivoOK(caminhoParserPalavras)) {
			armazenarPalavrasDoCorpus(configuration, janela, pretextLoader, candidates);

			//System.out.println(new Date().getTime() - inicial + " milisegundos");

			efetuarCalculoNCvalue(candidates);
		}
		return hashNCvalue;

	}


	private void efetuarCalculoNCvalue(Candidates candidates) {
		double peso = 0.0, cvalue = 0.0, ncvalue = 0.0, somatoria = 0.0;
		Set<String> set = candidates.getHashCandidates().keySet();
		Object[] array = set.toArray();
		int tamCT = array.length,
				freqContexto = 0;
		String palavraContexto = "";

		// percorrer os candidatos a termos:
		for (int iCT=0; iCT<tamCT; iCT++) {
			String chave = (String)array[iCT];

			cvalue = candidates.getNode(chave).cvalue;

			// percorrer as palavras de contexto:
			if (hashPalavrasContexto.get(array[iCT]) !=  null) {
				Set<String> set2 = hashPalavrasContexto.get(array[iCT]).keySet();
				Object[] array2 = set2.toArray();
				somatoria = 0; peso = 0.0;
				palavraContexto = "";
				for (int iContexto=0; iContexto<array2.length; iContexto++) {
					palavraContexto = (String) array2[iContexto];
					if (!palavraContexto.equals("")) {
						freqContexto = hashPalavrasContexto.get(array[iCT]).get(palavraContexto);
						peso = calcularPeso(palavraContexto, tamCT);
						somatoria += freqContexto * peso * freqContexto;
					}
				}

				ncvalue = 0.8 * cvalue + 0.2 * (somatoria);
				hashNCvalue.put(chave, ncvalue);
			}
			//else System.out.println("Termo sem valor de NCvalue: " + chave);
		}

	}

	private double calcularPeso(String palavra, int numCT) {
		double peso = 0.0;

		// peso = num de candidatos que cont�m o ct (chave) / num total de candidatos
		// Para unigramas: num de candidatos que cont�m o ct (chave) = frequencia total no corpus
		peso = hashstemTipoTextoComFreq.get(palavra)*1.0 / numCT*1.0; // c�lculo somente para unigramas

		return peso;
	}

	/**
	 * SUGESTAO PARA OTIMIZACAO: ter um vetor com as palavras de contexto para cada texto
	 * @param caminhoParserPalavras
	 * @param caminhoPretextStem
	 * @param caminhoPretextMaid
	 * @param janela
	 */
	private void armazenarPalavrasDoCorpus(ConfigurationLoader configuration, int janela,
			PretextLoader pretextLoader, Candidates candidates) {
		
		String caminhoParserPalavras = configuration.getCaminhoParserPalavras();
		String caminhoPretextStem = configuration.getCaminhoPretextStem();
		String caminhoPretextMaid = configuration.getCaminhoPretextMaid();
		String arquivo = "";
		//Vector<Pair> stemTipoTexto = new Vector<Pair>(); //são as palavras normalizadas do texto, mantendo-se sua ordem. 

		// Para cada texto normalizado sem stopwords do corpus de dominio:
		String[] lista_arq = (new File(caminhoPretextMaid)).list();
		for (int index = 0; index < lista_arq.length; index++) {
			Vector<Pair> stemTipoTexto = new Vector<Pair>(); 
			arquivo = caminhoPretextMaid + "/" + lista_arq[index];

			if (new File(arquivo).isFile()) {
				stemTipoTexto = armazenarPalavrasNormalizadasDoTexto(stemTipoTexto, arquivo);
				stemTipoTexto = obtemTipoMorfossintatico(stemTipoTexto, configuration,
						lista_arq[index], pretextLoader);

				// Para cada candidato a termo (ct):
				Set<String> set = candidates.getHashCandidates().keySet();
				Object[] array = set.toArray();

				for (int i=0; i<array.length; i++) {
					String chave = (String)array[i];
					obterPalavrasContexto(chave, stemTipoTexto, janela);
				}

			}

		}

	}

	/**
	 * Esse metodo obtem o tipo morfossintatico de cada stem do texto atual.
	 * @param stemTipoTexto contem as palavras normalizadas do texto atual.
	 * @param caminhoParserPalavras eh o diretorio dos arquivos processados externamente a esse programa pelo parser Palavras.
	 * @param caminhoPretextStem eh o diretorio dos arquivos da PreText ou equivalentes.
	 * @param nomeArquivo eh o nome do texto normalizado atual.
	 * @return um vetor "stemTipoTexto" contendo a palavra normalizada do texto atual e seu tipo morofssintatico. 
	 */
	private Vector<Pair> obtemTipoMorfossintatico(
			Vector<Pair> stemTipoTexto, ConfigurationLoader configuration,
			String nomeArquivo, PretextLoader pretextLoader) {

		String caminhoParserPalavras = configuration.getCaminhoParserPalavras();
		String caminhoPretextStem = configuration.getCaminhoPretextStem();
		String language = configuration.getLanguage();
		
		HashMap<String, String> palavraTipo = buscarTipoDaPalavraNoParser(caminhoParserPalavras, language, nomeArquivo); //<palavra, tipo>
		HashMap<String, Vector<String>> stemPalavra = pretextLoader.buscarStemDePalavra(caminhoPretextStem); //<stem, palavra>
		stemTipoTexto = buscarTipoDoStem(stemTipoTexto, palavraTipo, stemPalavra);

		return stemTipoTexto;
	}

	private HashMap<String, String> buscarTipoDaPalavraNoParser(
			String caminhoParserPalavras, String language, String nomeArquivo) {

		HashMap<String, String> palavraTipo = new HashMap<String, String>();
		String nomeArqTiger = caminhoParserPalavras + "/" + nomeArquivo + ".xml";

		if (new File(nomeArqTiger).isFile()) {

			POS padroesMorfossintaticos = new POS();
			Vector<NodePOS> vetor = padroesMorfossintaticos.grava_vector(new File(nomeArqTiger), language);
			if (vetor != null)
				palavraTipo = encontra_padroes_unigramas(vetor);
		}
		else {
			System.out.println("O arquivo " + nomeArquivo + " do parser Palavras eh nulo: Metodo NCValue.");
			return null;
		}



		return palavraTipo;
	}

	/**
	 * UNIGRAMA: encontra padroes nos unigramas.	
	 * @param vetor contendo a palavra original do texto e sua classe/tipo. 
	 */
	protected HashMap<String, String> encontra_padroes_unigramas(Vector<NodePOS> vetor){
		HashMap<String, String> palavraTipo = new HashMap<String, String>();
		NodePOS nodo;

		for (int i=0; i<vetor.size(); i++) {
			nodo = vetor.get(i);
			if (!nodo.palavra.equals("")) {
				String termo = Treatment.tratar_termo(nodo.palavra + "<>", true);
								
				int ss;
				if (termo.contains("tornam"))
					ss=1;
				
				if (!termo.equals(""))
					palavraTipo.put(termo, nodo.classe);
			}
		}
		return palavraTipo;
	}

	/**
	 * Esse metodo busca o tipo morfossintatico da palavra normalizada atual e o grava em um vetor "stemTipoTexto". 
	 * @param stemTipoTexto eh um vetor contendo as palavras normalizadas do texto atual, no caso, o stem.
	 * @param palavraTipo eh um hash contendo as palavras originais e seus respectivos tipos morfossintaticos.
	 * @param stemPalavra eh um hash contendo as formas normalizadas, nesse caso stemmizadas e suas respectivas palavras originais. 
	 * @return um vetor "stemTipoTexto" que contem as palavras normalizadas do texto atual e seus respectivos tipos morfossintaticos. 
	 */
	private Vector<Pair> buscarTipoDoStem(Vector<Pair> stemTipoTexto,
			HashMap<String, String> palavraTipo,
			HashMap<String, Vector<String>> stemPalavra) {

		if (stemTipoTexto == null) {
			System.out.println("O vetor stemTipoTexto eh nulo: Metodo NCValue.");
			return stemTipoTexto;
		}

		// Para cada palavra do texto, buscar seu tipo morfossintatico:
		int tam = stemTipoTexto.size();
		for (int i=0; i<tam; i++) {

			String palavraNormalizada = stemTipoTexto.get(i).palavra;
			Pair node = stemTipoTexto.get(i);
			String tipo = "na"; // quando nao encontrou o tipo da palavra normalizada, gravarah "na" (nenhuma das alternativas) como tipo da mesma.

			if (stemPalavra.containsKey(palavraNormalizada)) {
				Vector<String> vetorPal = stemPalavra.get(palavraNormalizada);
				for (int j=0; j<vetorPal.size(); j++) {
					String palavraOriginal = vetorPal.get(j);
					//System.out.println("palavraTipo: " + palavraTipo + " - palavraOriginal: " + palavraOriginal);

					if (palavraTipo != null) {
						if (palavraTipo.containsKey(palavraOriginal)) {
							if (j == 0) // primeira palavra
								tipo = palavraTipo.get(palavraOriginal);
							else {
								String tipoAux = palavraTipo.get(palavraOriginal);
								if (tipoAux.equals("adj") || tipoAux.equals("n") || tipoAux.equals("v-fin") || tipoAux.equals("v-inf"))
									tipo = tipoAux;
							}
						}
					}
				}
			}
			node.tipo = tipo;
		}


		return stemTipoTexto;
	}


	/**
	 * Esse m�todo obt�m as palavras de contexto de um ct dado. O m�todo grava em um hash,
	 *  como chave, o ct e grava, como valor, em um hash as palavras de contexto e suas
	 *  respectivas frequ�ncias. Ao executar esse m�todo n vezes (sendo n o n�mero de
	 *  textos do corpus) as frequ�ncias das palavras de contexto corresponder�o as
	 *  frequ�ncias totais dessas palavras no corpus.
	 * @param chave � o candidato a termo (ct).
	 * @param stemTipoTexto sao as palavras do texto armazenadas em ordem num vetor.
	 */
	private void obterPalavrasContexto(String chave,
			Vector<Pair> stemTipoTexto, int janela) {
		String palavra = "", palavraContexto = "";
		int freq = 0, freqContexto = 0;
		int tam = stemTipoTexto.size();

		for (int iAtual=0; iAtual<tam; iAtual++) {
			palavra = ""; freq = 0;
			palavra = stemTipoTexto.get(iAtual).palavra;

			if (palavra.trim().equals(chave.trim())) {
				palavraContexto = "";

				//obter palavras de contexto anteriores e posteriores ao ct:
				for (int iContexto=iAtual-janela; iContexto<=iAtual+janela; iContexto++) {

					if (iContexto>=0 && iContexto<tam) { // para ter indice valido no vetor stemTipoTexto: iContexto>=0 && iContexto<tam

						palavraContexto = stemTipoTexto.get(iContexto).palavra;
						String tipo = stemTipoTexto.get(iContexto).tipo;

						if (iContexto != iAtual) { // para nao pegar o proprio ct: iContexto != iAtual
							if (tipo.equals("adj") || tipo.equals("n") || tipo.equals("v-fin") || tipo.equals("v-inf")) { // filtra palavraContexto por tipo morfossintatico (adjetivo, substantivo e verbo)
								// armazena a palavra de contexto no hash hashPalavrasContexto
								HashMap<String, Integer> hashAuxPalavrasContexto = hashPalavrasContexto.get(palavra);
								if (hashAuxPalavrasContexto == null) {
									hashAuxPalavrasContexto = new HashMap<String, Integer>();
								}						
								if (hashAuxPalavrasContexto.containsKey(palavraContexto))
									freqContexto = hashAuxPalavrasContexto.get(palavraContexto) + 1;
								else freqContexto = 1;

								hashAuxPalavrasContexto.put(palavraContexto,freqContexto);
								hashPalavrasContexto.put(palavra, hashAuxPalavrasContexto);

								// atualiza hash com a palavra de contexto e sua frequencia no corpus:
								if (hashPalavrasContextoComFreq.containsKey(palavraContexto))
									freq = hashPalavrasContextoComFreq.get(palavraContexto) + 1;
								else freq = 1;
								hashPalavrasContextoComFreq.put(palavraContexto, freq);
							}
						}
					}
				}
			}
		}
	}

	public Vector<Pair> armazenarPalavrasNormalizadasDoTexto(Vector<Pair> palavrasDoTexto, String nomeArquivo) {
		if (palavrasDoTexto == null)
			palavrasDoTexto = new Vector<Pair>();
		try {
			//BufferedReader in = new BufferedReader(new FileReader(nomeArquivo));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(nomeArquivo);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo),encoding));
			else in = new BufferedReader(new FileReader(nomeArquivo));
			
			String linha = in.readLine(),
					palavra = "";
			StringTokenizer st = null;
			int freq = 0;

			while (linha != null) {
				if (!linha.trim().equals("")) {
					st = new StringTokenizer(linha, " ");
					while (st.hasMoreTokens()) {
						palavra = st.nextToken().trim().toLowerCase();
						palavra = Treatment.removerAcentos(palavra);
						palavra = Treatment.removerPontuacao(palavra);
						if (!palavra.equals("") & !palavra.equals("@")) {
							Pair node = new Pair();
							node.palavra = palavra;
							palavrasDoTexto.add(node);

							// obtendo frequencia total da palavra no corpus:
							if (hashstemTipoTextoComFreq.containsKey(palavra))
								freq = hashstemTipoTextoComFreq.get(palavra) + 1;
							else freq = 1;
							hashstemTipoTextoComFreq.put(palavra, freq);
						}
					}

				}
				linha = in. readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return palavrasDoTexto;
	}

}