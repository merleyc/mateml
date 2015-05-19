package br.usp.mateml.feature_extraction;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import weka.core.Stopwords;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.preprocessing.Treatment;

public class Phrases {

	/* PARA ATUALIZAR CADA GRAMA EM SEU HASH SEPARADAMENTE:
 	private HashMap<String, Boolean> unigramas_SN = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> bigramas_SN = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> trigramas_SN = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> tetragramas_SN = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> maiorgramas_SN = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> nucleos_SN = new HashMap<String, Boolean>();*/
	private HashMap<String, Character> listaSintagmas = new HashMap<String, Character>(); // Character: 'n'=nucleo do sintagma e 's' sintagma completo.
	private HashMap<String, Character> listaStemSintagmas = new HashMap<String, Character>();

	HashMap<String, Integer> mapnoterminals;
	HashMap<String, String> mapterminals;
	Vector<NodePhrases> v;

	public HashMap<String, Character> percorrerArqParser_Sintagmas(
			ConfigurationLoader configuration, PretextLoader pretextLoader) {

		HashMap<String, Character> listaStemSintagmas = new HashMap<String, Character>(); // Character: 'n'=nucleo do sintagma e 's' sintagma completo.
		String nomeArqTiger = "";
		String caminhoParserPalavras = configuration.getCaminhoParserPalavras();
		String caminhoPretextStem = configuration.getCaminhoPretextStem();
		String language = configuration.getLanguage(); 
		
		String[] lista_arq = (new File(caminhoParserPalavras)).list();

		for (int index = 0; index < lista_arq.length; index++) {
			nomeArqTiger = caminhoParserPalavras + "/" + lista_arq[index];
			if (new File(nomeArqTiger).isFile()) {

				// Encontrar Sintagmas Nominais:
				buscarSintagmas(new File(nomeArqTiger), language);

			}
		}

		listaStemSintagmas = buscarStemSintagmas(caminhoPretextStem, pretextLoader);

		return listaStemSintagmas;
	}

	private HashMap<String, Character> buscarStemSintagmas(
			String caminhoPretextStem, PretextLoader pretextLoader) {

		HashMap<String, String> listaStem = pretextLoader.carregarStem(caminhoPretextStem);

		// percorrendo a lista de sintagmas obtidos:
		Set<String> set = listaSintagmas.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			// obtendo sintagma e sua caracteristica (se eh nucleo ou nao do sintagma):
			String sintagma = (String)array[i];
			char caracteristicaSintagma = listaSintagmas.get(sintagma);

			// obtendo o stem do sintagma atual:
			String stemSintagma = sintagma;
			/*if (stemSintagma.contains("monitor"))
				System.out.println("");
			*/
			if (listaStem.containsKey(sintagma)) {
				stemSintagma = listaStem.get(sintagma);
				atualizaHashStem(caracteristicaSintagma, stemSintagma);
			}
			else if (sintagma.contains("<>")) {
				StringTokenizer st = new StringTokenizer(sintagma.trim(), "<>");
				while (st.hasMoreTokens()) {
					String palavraSintagma = st.nextToken("<>");
					if (listaStem.containsKey(palavraSintagma))
						palavraSintagma = listaStem.get(palavraSintagma);
					atualizaHashStem(caracteristicaSintagma, palavraSintagma);
				}				
			}

		}

		return listaStemSintagmas;
	}

	/**
	 * Esse metodo grava o stem do sintagma atual e sua caracteristica (dando prioridade a caracteristica de nucleo do sintagma)
	 * @param caracteristicaSintagma eh 'n'=nucleo do sintagma ou 's' sintagma completo.
	 * @param stemSintagma eh o stem do sintagma
	 */
	private void atualizaHashStem(char caracteristicaSintagma, String stemSintagma) {

		if (listaStemSintagmas.containsKey(stemSintagma)) {
			if (listaStemSintagmas.get(stemSintagma) != 'n')
				listaStemSintagmas.put(stemSintagma, caracteristicaSintagma);
			else listaStemSintagmas.put(stemSintagma, 'n');
		}
		else listaStemSintagmas.put(stemSintagma, caracteristicaSintagma);

	}

	public void buscarSintagmas(File arqTiger, String language) {
		try {
			if (arqTiger.exists() == false) {
				System.out.println("Nao encontrou o arquivo Tiger.xml");
				return;
			}
			//BufferedReader in = new BufferedReader(new FileReader(arqTiger));
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(arqTiger),"UTF-8"));
			RandomAccessFile in = new RandomAccessFile(arqTiger, "r");
			String line = in.readLine();

			while (line != null) {

				line = armazenar_palavras(in, line, language);

				//-encontrar_sintagmas("vp"); //Sintagmas verbais (vp)			-> ZERO de precis�o e recall
				encontrar_sintagmas("np"); //Sintagmas nominais (np)
				//-encontrar_sintagmas("ap"); //Sintagmas adjetivais (ap)		-> n�o encontrou nenhum "cu"
				//-encontrar_sintagmas("advp"); //Sintagmas adverbiais (advp)	-> ZERO de precis�o e recall
				//encontrar_sintagmas("pp"); //Sintagmas preposicionais (pp)		-> obteve valores somente para unigramas
				//-encontrar_sintagmas("cu"); //Sintagmas evidenciadores de rela��o de coordena��o (cu)		-> n�o encontrou nenhum "cu"

				if (v!=null)
					v.clear();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void encontrar_sintagmas(String tipo_sintagma) {
		//String[] vetor_tam;
		NodePhrases node;
		for (int i = 0; v!=null && i < v.size(); i++) {
			node = v.get(i);
			if (node.tipo.equals(tipo_sintagma)) {
				String sintagma = getString(node, new HashMap<String, Boolean>());
				//"codigo=" + node.cod

				if (!Stopwords.isStopword(sintagma)) { // verifica se termo eh stopword

					sintagma = Treatment.tratar_termo(sintagma, true);
					if (!sintagma.equals(""))
						atualiza_hash(sintagma);

				}

			}
		}
	}
	
	private String armazenar_palavras (RandomAccessFile in, String line, String language) throws IOException {
		Vector<NodePOS> vetor = null;

		if (language.equals("port"))
			line = armazenar_palavrasPT (in, line);
		else if (language.equals("ingl"))
			line = armazenar_palavrasEN (in, line);
		else System.out.println("Classe Sintagma: defina a lingua.");

		return line;
	}

	
	private String armazenar_palavrasEN(RandomAccessFile in, String line) {
		// TODO completar metodo!!!!!!
		return "";
	}

	private String armazenar_palavrasPT (RandomAccessFile in, String line)
			throws IOException {

		if (fim_sentenca(in, line))
			return in.readLine();

		while (line != null) {
			if (line.trim().equals("<terminals>")) {
				break;
			}
			if (fim_sentenca(in, line))
				return in.readLine();
			line = in.readLine();
		}
		line = in.readLine();
		mapterminals = new HashMap<String, String>();

		while (line != null) {
			if (fim_sentenca(in, line))
				return in.readLine();
			if (line.trim().equals("<nonterminals>")) {
				break;
			}
			StringTokenizer st = new StringTokenizer(line, " =<>\"");
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				if (s.trim().equals("id")) {
					String id = st.nextToken().trim();
					st.nextToken();
					String pal = st.nextToken().trim();
					mapterminals.put(id, pal);
				}
			}

			line = in.readLine();
		}
		line = in.readLine();
		v = new Vector<NodePhrases>();
		NodePhrases node = null;
		mapnoterminals = new HashMap<String, Integer>();
		Vector<String> heads = new Vector<String>();
		boolean ehhead = false;
		boolean ehnp  =false;
		while (line != null && !line.trim().equals("</nonterminals>")) {
			/*if (line.contains("s3_519"))
				System.out.println("");
			if (line.contains("s3_20"))
				System.out.println("");
			*/
			if (fim_sentenca(in, line))
				return in.readLine();
			StringTokenizer st = new StringTokenizer(line, " /=:<>\"");
			if (line.trim().equals("</nt>")) {
				v.add(node);
				mapnoterminals.put(node.cod, v.size() - 1);
				line = in.readLine();
				continue;
			}

			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s.trim().equals("id")) {
					String cod = st.nextToken().trim();
					node = new NodePhrases();
					node.cod = cod;
				} else if (s.trim().equals("cat")) {

					if(st.hasMoreTokens())node.tipo = st.nextToken().trim();
					else node.tipo="n";
//aqui
					if(node.tipo.equals("np"))
						ehnp=true;
					else ehnp=false;
				} else if (s.trim().equals("idref")) {
					if (node.filhos == null)
						node.filhos = new Vector<String>();
					String palavra = st.nextToken().replaceAll(" ", "").trim(); // remove todos os espa�os das palavras
					node.filhos.add(palavra);
					if(ehnp && ehhead) heads.add(mapterminals.get(palavra));
				}else if (s.trim().equals("label")) {
					String val=st.nextToken();
					if(val.equals("H")){
						ehhead = true;
					}else ehhead=false;

				}
			}
			line = in.readLine();
		}
		// Comentar esse "for" se nao deseja salvar os nucleos dos sintagmas:
		for (int i=0; i<heads.size(); i++) {
			//System.out.println("head " + i + " = " + heads.get(i));
			armazenar_nucleo_np(heads.get(i));
		}

		return line;
	}

	private void armazenar_nucleo_np(String nucleo_sintagma) {
		if (!Stopwords.isStopword(nucleo_sintagma)) { // verifica se termo � stopword

			nucleo_sintagma = Treatment.tratar_termo(nucleo_sintagma, true);
			if (!nucleo_sintagma.equals(""))
				listaSintagmas.put(nucleo_sintagma, 'n');
		}
	}

	private boolean fim_sentenca(RandomAccessFile in, String line)
			throws IOException {
		if (line != null && line.trim().equals("</s>"))
			return true;
		return false;
	}

	/**
	 * Esse metodo atualiza em um hash todos os sintagmas, dando preferencia por
	 * deixar o 'n' (nucleo) como valor, caso o sintagma jah exista no hash.	
	 * @param ct e o sintagma a ser atualizado no hash.
	 */
	private void atualiza_hash(String ct) {
				
		if (listaSintagmas.containsKey(ct)) {
			if (listaSintagmas.get(ct) != 'n')
				listaSintagmas.put(ct, 's');
		}
		else listaSintagmas.put(ct, 's');

		/*	PARA ATUALIZAR CADA GRAMA EM SEU HASH SEPARADAMENTE:
 		int grama = 0;
		StringTokenizer st = new StringTokenizer(ct.trim(), "<>"); 
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!token.isEmpty())
				grama = grama + 1;
		}
		if (grama == 1)
			unigramas_SN.put(ct, true); //unigrama
		else if (grama == 2)
			bigramas_SN.put(ct, true);
		else if (grama == 3)
			trigramas_SN.put(ct, true);
		else if (grama == 4)
			tetragramas_SN.put(ct, true);
		else maiorgramas_SN.put(ct, true);*/
	}

	String getString(NodePhrases node,HashMap<String, Boolean> visited) {
		String sol = "";
		if(visited.containsKey(node.cod)) return "";
		if (mapterminals.containsKey(node.cod))
			sol = mapterminals.get(node.cod) + " ";
		visited.put(node.cod, true);
		if (node.filhos != null)
			for (int i = 0; i < node.filhos.size(); i++) {
				String codfilho = node.filhos.get(i);

				if (mapnoterminals.containsKey(codfilho)) {
					int indfilho = mapnoterminals.get(codfilho);
					NodePhrases filho = v.get(indfilho);
					sol += getString(filho,visited) + " ";
				} else
					sol += mapterminals.get(codfilho) + " ";
			}
		return sol;
	}

	/**
	 * Esse m�todo percorre o hash dos sintagmas e nucleos de sintagmas e armazenar cada palavra dos sintagmas/nucleos
	 * separadamente na chave de um hashAux e, no valor desse hashAux, o m�todo armazena se essa palavra � parte de um
	 * sintagma ou � o nucleo dele.
	 * @param hashSintagmas
	 * @return hashAux contendo nas chaves as palavras dos sintagmas ou nucleos e nos valores do hashAux cont�m 's' se
	 * a palavra pertence a sintagma e 'n' se a palavra eh nucleo de sintagma.
	 */
	public HashMap<String, Character> separarPalavrasSintagmas(HashMap<String, Character> hashSintagmas) {
		String sintagma = "", palavra = "";
		HashMap<String, Character> hashSintagmasAux = new HashMap<String, Character>();

		// percorrer o hash dos sintagmas e nucleos de sintagmas e armazenar cada palavra dos sintagmas/nucleos separadamente em um hashAux:
		Set<String> set = hashSintagmas.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			sintagma = (String)array[i];
			char tipo = hashSintagmas.get(sintagma);

			// obtem cada palavra do sintagma:
			StringTokenizer st = new StringTokenizer(sintagma, "<>");
			while (st.hasMoreTokens()) {
				palavra = st.nextToken();
				if (!hashSintagmasAux.containsKey(palavra))
					hashSintagmasAux.put(palavra, tipo);
				else if (hashSintagmasAux.get(palavra) != 'n')
					hashSintagmasAux.put(palavra, tipo);				
			}
		}
		return hashSintagmasAux;
	}

}