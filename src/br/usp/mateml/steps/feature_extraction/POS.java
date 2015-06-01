package br.usp.mateml.steps.feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.security.auth.callback.LanguageCallback;

import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;

public class POS {

	private HashMap<String, Boolean> listaPadroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> listaStemPadroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> unigramas_padroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> bigramas_padroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> trigramas_padroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> tetragramas_padroes = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> maiorgramas_padroes = new HashMap<String, Boolean>();

	public HashMap<String, Boolean> percorrerArqParser_PM(
			ConfigurationLoader configuration, PretextLoader pretextLoader) {

		String caminhoParserPalavras = configuration.getPathParserPalavras();
		String caminhoPretextStem = configuration.getPathPretextStem();
		String language = configuration.getLanguage();
		String nomeArqTiger = "";
		String[] lista_arq = (new File(caminhoParserPalavras)).list();

		for (int index = 0; index < lista_arq.length; index++) {
			nomeArqTiger = caminhoParserPalavras + "/" + lista_arq[index];
			if (new File(nomeArqTiger).isFile()) {

				//encontrar_padroes(new File(nomeArqTiger));

				Vector<NodePOS> vetor = grava_vector(new File(nomeArqTiger), language);
				if (vetor != null) {
					encontra_padroes_unigramas(vetor);
					encontra_padroes_bigramas(vetor);
					encontra_padroes_trigramas(vetor);
					encontra_padroes_tetragramas(vetor);
				}
			}
		}

		listaStemPadroes = buscarStemPadroes(caminhoPretextStem, pretextLoader);

		return listaStemPadroes;
	}

	private HashMap<String, Boolean> buscarStemPadroes(
			String caminhoPretextStem, PretextLoader pretextLoader) {

		HashMap<String, String> listaStem = pretextLoader.carregarStem(caminhoPretextStem);

		// percorrendo a lista de padroes obtidos:
		Set<String> set = listaPadroes.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			// obtendo padrao:
			String padrao = (String)array[i];

			// obtendo o stem do padrao atual:
			String stemPadrao = padrao;
			if (listaStem.containsKey(padrao)) {
				stemPadrao = listaStem.get(padrao);
				listaStemPadroes.put(stemPadrao, true);
			}
			else if (padrao.contains("<>")) {
				StringTokenizer st = new StringTokenizer(padrao.trim(), "<>");
				while (st.hasMoreTokens()) {
					String palavraPadrao = st.nextToken("<>");
					if (listaStem.containsKey(palavraPadrao))
						palavraPadrao = listaStem.get(palavraPadrao);
					listaStemPadroes.put(palavraPadrao, true);
				}				
			}

		}

		return listaStemPadroes;
	}

	protected void encontrar_padroes(File arqTiger) {
		try {
			if (arqTiger.exists() == false) {
				System.out.println("Nao encontrou o arquivo Tiger.txt");
				return;
			}
			//BufferedReader in = new BufferedReader(new FileReader(arqTiger));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(arqTiger.toString());
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),encoding));
			else in = new BufferedReader(new FileReader(arqTiger));

			String line = in.readLine().trim();
			int tam = 0, index = 0;

			while (line != null) {
				StringTokenizer st = new StringTokenizer(line.trim(), " ");
				if (st.hasMoreTokens()) {
					String pal = st.nextToken(),
							palavra = "", classe = "",
							palavra2 = "", classe2 = "";

					// encontra palavra e sua respectiva classe gramatical: 
					while (st.hasMoreTokens()) {
						pal = st.nextToken();
						if (pal.contains("word=")){
							tam = pal.length();
							index = pal.indexOf("=");
							palavra = pal.substring(index+2, tam-1);
							palavra = palavra.replaceAll(" ", "").trim(); // remove todos os espa�os das palavras
							palavra = Treatment.removerAcentos(palavra);
						}						
						if (pal.contains("pos=")) {
							tam = pal.length();
							index = pal.indexOf("=");
							classe = pal.substring(index+2, tam-1);
							break;
						}
					}

					if (classe.equals("v-fin") || classe.equals("v-pcp") 
							|| classe.equals("v-inf") || classe.equals("v-ger")){
						String termo = Treatment.tratar_termo(palavra + "<>", true);					
						/*						if (!termo.equals(""))
							atualiza_hash(termo);*/
					}

					line = in.readLine();

					// UNIGRAMA: encontrando padr�es nos unigramas:
					if (classe.equals("prop") || classe.equals("n") || classe.equals("adj")) {
						String termo = Treatment.tratar_termo(palavra + "<>", true);					
						if (!termo.equals(""))
							atualiza_hash(termo);

						// BIGRAMA: encontra pr�xima palavra e sua respectiva classe gramatical:
						palavra2 = ""; classe2 = "";
						st = new StringTokenizer(line.trim(), " ");
						pal = st.nextToken();

						while (st.hasMoreTokens()) {
							pal = st.nextToken();
							if (pal.contains("word=")) {
								tam = pal.length();
								index = pal.indexOf("=");
								palavra2 = pal.substring(index+2, tam-1);
								palavra2 = palavra2.replaceAll(" ", "").trim(); // remove todos os espa�os das palavras
								palavra2 = Treatment.removerAcentos(palavra2);
							}
							if (pal.contains("pos=")) {
								tam = pal.length();
								index = pal.indexOf("=");
								classe2 = pal.substring(index+2, tam-1);
								break;
							}
						}
						if (classe2.equals("prop") || classe2.equals("n") ||
								classe2.equals("adj")  || classe2.equals("adv")){

							int cont = 0;
							termo="";
							if(!palavra.equals("")) {termo+=palavra + "<>";cont++;}
							if(!palavra2.equals("")) {termo+=palavra2 + "<>";cont++;}
							if (!termo.equals("")) {
								termo = Treatment.tratar_termo(termo, true);
								atualiza_hash(termo);
							}
						}

						// TRIGRAMA: encontrando padr�es nos trigramas: 
						if (classe2.equals("prop") || classe2.equals("n") ||
								classe2.equals("adj") || classe2.equals("prp")) {

							//encontra pr�xima palavra e sua respectiva classe gramatical:
							line = in.readLine();
							if (line != null) {

								String palavra3 = "", classe3 = "";
								st = new StringTokenizer(line.trim(), " ");
								pal = st.nextToken();

								while (st.hasMoreTokens()) {
									pal = st.nextToken();
									if (pal.contains("word=")) {
										tam = pal.length();
										index = pal.indexOf("=");
										palavra3 = pal.substring(index+2, tam-1);
										palavra3 = palavra3.replaceAll(" ", "").trim(); // remove todos os espa�os das palavras
										palavra3 = Treatment.removerAcentos(palavra3);
									}
									if (pal.contains("pos=")) {
										tam = pal.length();
										index = pal.indexOf("=");
										classe3 = pal.substring(index+2, tam-1);
										break;
									}
								}
								if (classe3.equals("prop") || classe3.equals("n") ||
										classe3.equals("adj")) {
									int cont = 0;
									termo="";
									if(!palavra.equals("")) {termo+=palavra + "<>";cont++;}
									if(!palavra2.equals("")) {termo+=palavra2 + "<>";cont++;}
									if(!palavra3.equals("")) {termo+=palavra3 + "<>";cont++;}
									if (!termo.equals("")) {
										termo = Treatment.tratar_termo(termo, true);
										atualiza_hash(termo);
									}
								}
							}
						}
					}

				}
				else line = in.readLine();
			}

			in.close();
			/*			if (bigramas_padroes != null) 
				SintagmasNominais.grava_arq_saida2(arq_bigramas, bigramas_padroes);*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Vector<NodePOS> grava_vector(File arqTiger, String language) {
		Vector<NodePOS> vetor = null;

		if (language.equals("port"))
			vetor = grava_vectorPT(arqTiger, vetor);
		else if (language.equals("ingl"))
			vetor = grava_vectorEN(arqTiger, vetor);
		else System.out.println("Classe PadroesMorfossintaticos: defina a lingua.");

		return vetor;
	}

	protected Vector<NodePOS> grava_vectorPT(File arqTiger, Vector<NodePOS> vetor) {

		try {
			if (arqTiger.exists() == false) {
				System.out.println("Nao encontrou o arquivo Tiger.xml");
				return vetor;
			}
			vetor = new Vector<NodePOS>();

			//RandomAccessFile in = new RandomAccessFile(arqTiger,"r");
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),"UTF-8"));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(arqTiger.toString());
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),encoding));
			else in = new BufferedReader(new FileReader(arqTiger));

			//System.out.println(arqTiger.getPath());

			String line = in.readLine(),
					palavra = "", classe = "";
			int tam = 0, index = 0;
			boolean achouPal = false, achouClas = false;

			// gravando palavras no vetor
			while (line != null) {
				line = line.trim();

				StringTokenizer st = new StringTokenizer(line.trim(), " ");
				if (st.hasMoreTokens()) {
					String pal = st.nextToken();
					palavra = ""; classe = "";
					achouPal = false; achouClas = false;

					// encontra palavra e sua respectiva classe gramatical: 
					while (st.hasMoreTokens()) {
						pal = st.nextToken();
						if (pal.contains("word=")){
							tam = pal.length();
							index = pal.indexOf("=");
							palavra = pal.substring(index+2, tam-1);
							palavra = palavra.replaceAll(" ", "").trim().toLowerCase(); // remove todos os espa�os das palavras
							palavra = Treatment.removerAcentos(palavra);
							achouPal = true;
						}
						if (!palavra.equals("")) {
							if (pal.contains("pos=")) {
								tam = pal.length();
								index = pal.indexOf("=");
								classe = pal.substring(index+2, tam-1);
								achouClas = true;
							}
							if (achouPal && achouClas) {
								NodePOS nodo = new NodePOS();
								nodo.palavra = palavra;
								nodo.classe = classe;
								vetor.add(nodo);
								achouPal = false;
								achouClas = false;
							}
						}
					}
				}

				line = in.readLine();
			}
			in.close();
			return vetor;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vetor;
	}

	protected Vector<NodePOS> grava_vectorEN (File arqTiger, Vector<NodePOS> vetor) {

		try {
			if (arqTiger.exists() == false) {
				System.out.println("Nao encontrou o arquivo Tiger.xml");
				return vetor;
			}
			vetor = new Vector<NodePOS>();

			//RandomAccessFile in = new RandomAccessFile(arqTiger,"r");
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),"UTF-8"));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(arqTiger.toString());
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),encoding));
			else in = new BufferedReader(new FileReader(arqTiger));

			//System.out.println(arqTiger.getPath());

			String line = in.readLine(),
					palavra = "", classe = "";
			int tam = 0, index = 0;
			boolean achouPal = false, achouClas = false;

			// gravando palavras no vetor
			while (line != null) {
				line = line.trim();

				// encontra palavra e sua respectiva classe gramatical: 
				if (line.contains("<word>")){ //<word>A</word>
					tam = line.length();
					index = line.indexOf(">");
					palavra = line.substring(index+1, tam-7);
					palavra = palavra.replaceAll(" ", "").trim().toLowerCase(); // remove todos os espacos das palavras
					palavra = Treatment.removerAcentos(palavra);
					if (achouPal == true && achouClas == false)
						System.out.println("Nao encontrou o POS da palavra: " + palavra);
					achouPal = true;
				}
				if (!palavra.equals("")) {
					if (line.contains("<POS>")) {
						tam = line.length();
						index = line.indexOf(">");
						classe = line.substring(index+1, tam-6);
						classe = classe.toLowerCase();

						classe = converterPadraoParserPALAVRAS (classe);

						achouClas = true;
					}
					if (achouPal && achouClas) {
						NodePOS nodo = new NodePOS();
						nodo.palavra = palavra;
						nodo.classe = classe;
						vetor.add(nodo);
						achouPal = false;
						achouClas = false;
						palavra = ""; classe = "";
					}
				}



				line = in.readLine();
			}
			in.close();
			return vetor;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vetor;
	}


	private String converterPadraoParserPALAVRAS(String classe) {

		if (classe.equals("JJ")) // padrao do parser PALAVRAS em portugues
			classe = "adj";
		else if (classe.equals("nn")) // padrao do parser da Stanford em ingles
			classe = "n";
		else if (classe.startsWith("vb"))
			classe = "v-fin";

		return classe;
	}

	// UNIGRAMA: encontrando padroes nos unigramas:
	protected void encontra_padroes_unigramas(Vector<NodePOS> vetor){
		NodePOS nodo;



		for (int i=0; i<vetor.size(); i++) {
			nodo = vetor.get(i);

			if (/*nodo.classe.equals("v-fin") || nodo.classe.equals("v-pcp") 
					|| nodo.classe.equals("v-inf")	|| nodo.classe.equals("v-ger")
					|| nodo.classe.equals("adj")	|| nodo.classe.equals("prop") || */nodo.classe.equals("n") ) {

				if (!nodo.palavra.equals("")) {
					String termo = Treatment.tratar_termo(nodo.palavra + "<>", true);					
					if (!termo.equals("")) {
						atualiza_hash(termo);
					}
				}
			}
		}
	}

	// BIGRAMA: encontrando padr�es nos bigramas:
	protected void encontra_padroes_bigramas(Vector<NodePOS> vetor){
		NodePOS nodo,nodo2;
		String classe1, classe2,
		pal1, pal2;

		for (int i=0; i<vetor.size(); i++) {
			nodo = vetor.get(i);

			// primeiro token:
			classe1 = nodo.classe;
			pal1 = nodo.palavra;
			if (classe1.equals("prop") || classe1.equals("n") || classe1.equals("adj")) {

				// segundo token:
				if ((i+1) < vetor.size()) {
					nodo2 = vetor.get(i+1);
					classe2 = nodo2.classe;
					pal2 = nodo2.palavra;
					if (classe2.equals("prop") || classe2.equals("n") ||
							classe2.equals("adj")  || classe2.equals("adv")){

						if (!pal1.equals("") && !pal2.equals("")) {
							String termo = Treatment.tratar_termo(pal1 + "<>" + pal2 + "<>", true);					
							if (!termo.equals(""))
								atualiza_hash(termo);
						}
					}
				}
			}
		}
	}

	// TRIGRAMA: encontrando padr�es nos trigramas:
	protected void encontra_padroes_trigramas(Vector<NodePOS> vetor){
		NodePOS nodo;
		String classe1, classe2, classe3,
		pal1, pal2, pal3;

		for (int i=0; i<vetor.size(); i++) {
			nodo = vetor.get(i);

			// primeiro token:
			classe1 = nodo.classe;
			pal1 = nodo.palavra;
			if (classe1.equals("prop") || classe1.equals("n")) {

				// segundo token:
				if ((i+2) < vetor.size()) {
					nodo = vetor.get(i+1);
					classe2 = nodo.classe;
					pal2 = nodo.palavra;
					if (classe2.equals("prop") || classe2.equals("n")
							|| classe2.equals("adj") || classe2.equals("prp")){

						// terceiro token:
						nodo = vetor.get(i+2);
						classe3 = nodo.classe;
						pal3 = nodo.palavra;
						if (classe3.equals("prop") || classe3.equals("n") || classe3.equals("adj")){

							if (!pal1.equals("") && !pal2.equals("") && !pal3.equals("")) {
								String termo = Treatment.tratar_termo(pal1 + "<>" + pal2 + "<>"  + pal3 + "<>", true);					
								if (!termo.equals(""))
									atualiza_hash(termo);
							}
						}
					}
				}
			}
		}
	}

	// TETRAGRAMA: encontrando padr�es nos tetragramas:
	protected void encontra_padroes_tetragramas(Vector<NodePOS> vetor){
		NodePOS nodo;
		String classe1, classe2, classe3, classe4,
		pal1, pal2, pal3, pal4;

		for (int i=0; i<vetor.size(); i++) {
			nodo = vetor.get(i);

			// primeiro token:
			classe1 = nodo.classe;
			pal1 = nodo.palavra;
			if (classe1.equals("prop") || classe1.equals("n")) {

				// segundo token:
				if ((i+3) < vetor.size()) {
					nodo = vetor.get(i+1);
					classe2 = nodo.classe;
					pal2 = nodo.palavra;
					if (classe2.equals("prop") || classe2.equals("n")
							|| classe2.equals("adj") || classe2.equals("prp")){

						// terceiro token:
						nodo = vetor.get(i+2);
						classe3 = nodo.classe;
						pal3 = nodo.palavra;
						if (classe3.equals("prop") || classe3.equals("n")
								|| classe3.equals("adj") || classe3.equals("prp")) {

							// quarto token:
							try{
								nodo = vetor.get(i+3);
							}catch (Exception e){
								e.printStackTrace();
							}
							classe4 = nodo.classe;
							pal4 = nodo.palavra;
							if (classe4.equals("prop") || classe4.equals("n") || classe4.equals("adj")) {

								if (!pal1.equals("") && !pal2.equals("") && !pal3.equals("") && !pal4.equals("")) {
									String termo = pal1 + "<>" + pal2 + "<>"  + pal3 + "<>"  + pal4 + "<>";
									termo = Treatment.tratar_termo(termo, true);					
									if (!termo.equals(""))
										atualiza_hash(termo);
								}
							}
						}
					}
				}
			}
		}
	}

	public void atualiza_hash(String termo) {
		listaPadroes.put(termo, true);

		/*		PARA GRAVAR CADA TERMO EM SEU RESPECTIVO HASH DE UNIGRAMA, BIGRAMA, ETC.:
 		int grama = 0;
 		StringTokenizer st = new StringTokenizer(termo.trim(), "<>"); 
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!token.isEmpty())
				grama = grama + 1;
		}
		if (grama == 1)
			unigramas_padroes.put(termo, true); //unigrama
		else if (grama == 2)
			bigramas_padroes.put(termo, true);
		else if (grama == 3)
			trigramas_padroes.put(termo, true);
		else if (grama == 4)
			tetragramas_padroes.put(termo, true);
		else maiorgramas_padroes.put(termo, true);*/
	}

	public HashMap<String, Vector<Integer>> obterTiposPalavrasOriginais(
			ConfigurationLoader configuration, PretextLoader pretextLoader) {

		String caminhoParserPalavras = configuration.getPathParserPalavras();
		String caminhoPretextStem = configuration.getPathPretextStem();
		String language = configuration.getLanguage();

		HashMap<String, Vector<Pair>> stemPalavra = pretextLoader.buscarStemDePalavraTipo(
				caminhoPretextStem); //<stem, palavra>

		// para cada texto do parser/Tiger, armazenar num hash todas as palavras e seus respectivos tipos:
		HashMap<String, String> palTipo = new HashMap<String, String>(); // <palavra,tipo>
		String nomeArqTiger = "";
		String[] lista_arq = (new File(caminhoParserPalavras)).list();

		for (int index = 0; index < lista_arq.length; index++) {
			nomeArqTiger = caminhoParserPalavras + "/" + lista_arq[index];
			if (new File(nomeArqTiger).isFile()) {
				palTipo = grava_hash(new File(nomeArqTiger), palTipo);
			}
		}

		// para cada stem, gravar os tipos das palavras originais correspondentes:
		Set<String> set = stemPalavra.keySet();
		Object[] array = set.toArray();

		// percorrendo cada stem:
		for (int i=0; i<array.length; i++) {
			String stem = array[i].toString();
			Vector<Pair> vetorPalavras = stemPalavra.get(stem);

			// percorrendo as palavras correspondentes ao stem atual:
			for (int j=0; j<vetorPalavras.size(); j++) {
				String palavraOriginal = vetorPalavras.get(j).palavra;
				if (palTipo.containsKey(palavraOriginal)) {
					String tipo = palTipo.get(palavraOriginal);
					Pair par = vetorPalavras.get(j);
					par.tipo = tipo;
				}

			}
		}

		HashMap<String, Vector<Integer>> stemNumTipos = contarTipos(stemPalavra);

		return stemNumTipos;
	}

	/**
	 * Esse metodo grava em um hash os stems e as razoes dos numeros de palavras originais, separadamente, dos tipos substantivos,
	 * verbo e adjetivo pelo numero total de palavras originais desse stem.
	 * @param stemPalavra tem, como chave, um stem e, como valor, um vetor com as respectivas palavras originais e seus tipos morofossintaticos.
	 * @return stemNumTipos contem, como chave, o stem e, como valor, um vetor com tres posicoes.
	 * A primeira posicao (0) corresponde ao numero de palavras originais desse stem do tipo SUBSTANTIVO / numero total de palavras originais desse stem;
	 * A segunda posicao (1) corresponde ao numero de palavras originais desse stem do tipo ADJETIVO / numero total de palavras originais desse stem;
	 * A terceira posicao (2) corresponde ao numero de palavras originais desse stem do tipo VERBO / numero total de palavras originais desse stem. 
	 */
	private HashMap<String, Vector<Integer>> contarTipos(
			HashMap<String, Vector<Pair>> stemPalavra) {

		HashMap<String, Vector<Integer>> stemNumTipos = new HashMap<String, Vector<Integer>>();

		Set<String> set = stemPalavra.keySet();
		Object[] array = set.toArray();

		// percorrendo cada stem:
		for (int i=0; i<array.length; i++) {
			String stem = array[i].toString();
			Vector<Pair> vetorPalavras = stemPalavra.get(stem);
			int substantivo = 0, adjetivo = 0, verbo = 0;
			int tamVetorPalavras = vetorPalavras.size();

			// percorrendo as palavras correspondentes ao stem atual:
			for (int j=0; j<tamVetorPalavras; j++) {
				String tipo = stemPalavra.get(stem).get(j).tipo;
				if (tipo.equals("n")) substantivo++;
				else if (tipo.equals("adj")) adjetivo++;
				else if (tipo.equals("v-inf") || tipo.equals("v-fin")) verbo++;
			}

			Vector<Integer> vetorRazao = new Vector<Integer>();
			vetorRazao.add(substantivo);
			vetorRazao.add(adjetivo);
			vetorRazao.add(verbo);
			vetorRazao.add(tamVetorPalavras);
			stemNumTipos.put(stem, vetorRazao);
		}

		return stemNumTipos;
	}

	protected HashMap<String, String> grava_hash(File arqTiger, HashMap<String, String> hashPalTipo) {

		try {
			if (arqTiger.exists() == false) {
				System.out.println("Nao encontrou o arquivo Tiger.txt");
				return hashPalTipo;
			}

			RandomAccessFile in = new RandomAccessFile(arqTiger,"r");
			String line = in.readLine(),
					palavra = "", classe = "";
			int tam = 0, index = 0;
			boolean achouPal = false, achouClas = false;

			// gravando palavras no vetor
			while (line != null) {
				line = line.trim();

				StringTokenizer st = new StringTokenizer(line.trim(), " ");
				if (st.hasMoreTokens()) {
					String pal = st.nextToken();
					palavra = ""; classe = "";
					achouPal = false; achouClas = false;

					// encontra palavra e sua respectiva classe gramatical: 
					while (st.hasMoreTokens()) {
						pal = st.nextToken();
						if (pal.contains("word=")){
							tam = pal.length();
							index = pal.indexOf("=");
							palavra = pal.substring(index+2, tam-1);
							palavra = palavra.replaceAll(" ", "").trim().toLowerCase(); // remove todos os espa�os das palavras
							palavra = Treatment.removerAcentos(palavra);
							achouPal = true;
						}
						if (!palavra.equals("")) {
							if (pal.contains("pos=")) {
								tam = pal.length();
								index = pal.indexOf("=");
								classe = pal.substring(index+2, tam-1);
								achouClas = true;
							}
							if (achouPal && achouClas) {
								hashPalTipo.put(palavra, classe);
								achouPal = false;
								achouClas = false;
							}
						}
					}
				}

				line = in.readLine();
			}

			in.close();
			return hashPalTipo;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashPalTipo;
	}


}