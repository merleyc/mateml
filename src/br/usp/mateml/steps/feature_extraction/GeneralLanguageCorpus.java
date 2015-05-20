package br.usp.mateml.steps.feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import weka.core.Stopwords;
import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.steps.preprocessing.StemmerEn;
import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.steps.preprocessing.snowball.SpanishStemmer;
import br.usp.mateml.util.TestDetector;
import br.usp.mateml.util.Util;



public class GeneralLanguageCorpus {

	SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	/**
	 * Esse metodo carrega os termos do corpus de lingua geral e suas respectivas
	 * frequencias em um hash.
	 * Exemplo de uma linha do corpus de lingua geral:
	 * 1;"aaaaaahhhhh";1  sendo: sequenciaDoTermo;"termo";frequenciaDoTermoNoCorpus
	 * @param arquivo_corpusGeral eh o corpus geral em formato de
	 *  lista de termos + suas frequencias
	 */
	public HashMap<String, NodeCandidate> carregarHash_corpusGeral(String arquivo_corpusGeral) {
		if (!Util.arquivoOK(arquivo_corpusGeral))
			return null;

		HashMap<String, NodeCandidate> hashCorpusGeral = new HashMap<String, NodeCandidate>();
		String grama = "";
		try {
			//BufferedReader in = new BufferedReader(new FileReader(arquivo_corpusGeral));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(arquivo_corpusGeral);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo_corpusGeral),encoding));
			else in = new BufferedReader(new FileReader(arquivo_corpusGeral));

			String linha = in.readLine();

			while (linha != null) {
				if (!linha.trim().equals("")) {
					StringTokenizer st = new StringTokenizer(linha,";");
					st.nextToken();
					String meio = st.nextToken();
					int freq_CorpusGeral = 0;
					if (st.hasMoreTokens()) {
						String freq = st.nextToken();
						freq_CorpusGeral = Integer.parseInt(freq);
					}

					st = new StringTokenizer(meio,"-");

					int ngramas = 0;
					String s = "";

					while (st.hasMoreTokens()){
						grama = st.nextToken();
						grama = grama.toLowerCase().replace("\"", "");
						grama = Treatment.tratar_termo(grama, true);
						if (grama.isEmpty()){
							continue;
						}
						if (s.isEmpty())
							s=grama;
						else s += "<>"+grama;
						ngramas++;

						NodeCandidate node = new NodeCandidate();
						node.corpusGeral = 1;
						node.freq_CorpusGeral = freq_CorpusGeral;
						hashCorpusGeral.put(s, node);
					}

					linha = in.readLine();
				}
			}

			in.close();

		} catch (Exception e) {
			System.out.println(grama);
			e.printStackTrace();
		}
		return hashCorpusGeral;
	}

	/**
	 * Percorre uma pasta com os arquivos de corpus de lingua geral (CG).
	 * Deve-se verificar a codificacao desses arquivos antes do processamento.
	 * Para remover as tags e seus conteudos, usar <(.*?)> no Notepad++.
	 * @param dirCorpusGeral diretorio com os arquivos do CG.
	 * @param removerStopwords serah 1 se deseja remover as stopwords.
	 * @param stemmizar serah 1 se deseja stemmizar as palavras do CG.
	 * Atencao: as stopwords do inicio e do fim do ct sempre serao removidas
	 * por default.
	 * @return hash contendo a palavra do CG como chave do hash e sua
	 * frequencia absoluta considerando todos os textos do CG como valor.
	 * @throws Throwable 
	 */
	public HashMap<String, Integer> textoNormalTOHash_corpusGeral(
			String dirCorpusGeral, boolean removerStopwords, boolean stemmizar, ConfigurationLoader configuration) {

		StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em inglês
		File arqCG = null;
		BufferedReader in;
		HashMap<String, Integer> hashPalFreq = new HashMap<String, Integer>();
		String[] lista_arq = (new File(dirCorpusGeral)).list();

		try {
			for (int index = 0; index < lista_arq.length; index++) {
				String arquivo = dirCorpusGeral + "/" + lista_arq[index];
				arqCG = new File(arquivo);

				if (arqCG.isFile()) {

					String nomeArqCG = arqCG.toString();
					if (!Util.arquivoOK(nomeArqCG)) {
						System.out.println("Nao encontrou o arquivo do CG: " + nomeArqCG);
						return null;
					}

					if (!arqCG.getName().toLowerCase().endsWith("~")) {

						//in = new BufferedReader(new FileReader(arqCG));
						String encoding = TestDetector.detectaEncodingImprimeArquivo(arquivo);
						if (encoding != null) 
							in = new BufferedReader(new InputStreamReader(new FileInputStream(arqCG),encoding));
						else in = new BufferedReader(new FileReader(arqCG));

						String linha = in.readLine();

						while (linha != null) {
							if (linha.contains(" ")) {

								StringTokenizer st = new StringTokenizer(linha," ");

								//se for o CG Brown em ingles, estará assim:
								//Research/nn projects/nns as/ql soon/rb as/cs possible/jj on/in the/at causes/nns
								//Terah que considerar: Research projects as soon as possible on the causes
								while (st.hasMoreTokens()) {
									String palavra = st.nextToken();

									palavra = processarPalavraCG(palavra, removerStopwords, stemmizar, configuration);

									if (!palavra.equals("")) {
										int freq_CorpusGeral = 1;
										if (hashPalFreq.containsKey(palavra))
											freq_CorpusGeral = hashPalFreq.get(palavra) + 1;

										hashPalFreq.put(palavra, freq_CorpusGeral);
									}

								}

							}
							else if (!linha.trim().equals("")) {
								String palavra = linha;
								palavra = processarPalavraCG(palavra, removerStopwords, stemmizar, configuration);

								if (!palavra.equals("")) {
									int freq_CorpusGeral = 1;
									if (hashPalFreq.containsKey(palavra))
										freq_CorpusGeral = hashPalFreq.get(palavra) + 1;

									hashPalFreq.put(palavra, freq_CorpusGeral);
								}
							}

							linha = in.readLine();
						}
						in.close();
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return hashPalFreq;
	}

	private String processarPalavraCG(String palavra, boolean removerStopwords, 
			boolean stemmizar, ConfigurationLoader configuration) {
		try {
			//descomentar se for executar para o CG Brown em ingles:
			if (palavra.contains("\t"))
				palavra = palavra.replace("\t", "");
			if (palavra.contains("/")) {
				StringTokenizer st2 = new StringTokenizer(palavra,"/");
				palavra = st2.nextToken();
			} //fim do codigo se for executar para o CG Brown em ingles.

			palavra = Treatment.tratar_termo(palavra, removerStopwords);

			if (!palavra.equals("")) {

				if (removerStopwords)
					if(Stopwords.isStopword(palavra))
						palavra = "";

				if (stemmizar) {
					if (configuration.getLanguage().trim().equals("esp"))
						palavra = new String(spanishStemmer.stemmizarPalavraES(palavra));

					else if (configuration.getLanguage().trim().equals("ingl"))
						palavra = new String(StemmerEn.get(palavra));
				}

			}
		} catch (Throwable e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return palavra;
	}

	/**
	 * Esse metodo grava em um arquivo saida os dados de um hash no formato
	 * <String,Integer>.
	 * @param hash contem como chave a palavra e como valor da frequencia absoluta da mesma.
	 * @param arqSaida arquivo saida no formato: IDPalavra;palavra;freqAbsolutaPalavra.
	 * Exemplo:		5;"ativ";10
	 * 				6;"conhec";2
	 */
	public void gravarHashTOArquivo_CG(HashMap<String, Integer> hash,
			String arqSaida) {

		if (hash == null) {
			System.out.println("O hash eh nulo. Metodo CorpusLinguaGeral.gravarHashTOArquivo_CG.");
			return;
		}

		try {
			Treatment.criarArquivo(new File(arqSaida));
			FileWriter writer = new FileWriter(arqSaida);
			PrintWriter out = new PrintWriter(writer);

			Set<String> set = hash.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				String chave = (String)array[i];
				Integer valor = hash.get(chave);
				out.println(i+1 + ";" + chave + ";" + valor);
			}

			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

}