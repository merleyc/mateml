package br.usp.mateml.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.feature_extraction.taxonomy.NodeFeatureOfTax;
import br.usp.mateml.preprocessing.Treatment;


public class Util {

	public static boolean arquivoOK (String arquivo) {
		File fileArqPretextData = new File(arquivo);

		if (fileArqPretextData == null || !fileArqPretextData.isFile() || fileArqPretextData.length() == 0) {
			System.out.println("O arquivo \"" + arquivo + "\" nao existe ou eh vazio.");
			return false;
		}
		return true;
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por parametro.
	 * @param hash eh o hash a ser impresso.
	 */
	public static void imprimeHashBoolean(HashMap<String, Boolean> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			System.out.println(array[i]);
		}
	}

	/**
	 * Esse método imprime as chaves de um hash passado por parâmetro e seu valor String.
	 * @param hash eh o hash a ser impresso.
	 */
	public static void imprimeHashDeString(HashMap<String, String> hash ){
		if (hash == null) {
			System.out.println("O hash é nulo e não pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			System.out.println(array[i] + ": " + hash.get(array[i]));
		}
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por parametro.
	 * @param hash eh o hash a ser impresso.
	 */
	public static void imprimeHashStringHash(HashMap<String, HashMap<String, Integer>> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			System.out.print(array[i]+": ");

			Set<String> set2 = hash.get(array[i]).keySet();
			Object[] array2 = set2.toArray();
			for (int j=0; j<array2.length; j++) {
				System.out.print(array2[j]+", ");

			}
		}
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por par�metro e seus respectivos conte�dos string.
	 * @param hash � o hash a ser impresso.
	 */
	public static void imprimeHashString(HashMap<String, String> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String chave = (String)array[i];
			String valor = hash.get(chave);
			System.out.println(chave + " " + valor);
		}
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por par�metro e seus respectivos conte�dos string.
	 * @param hash � o hash a ser impresso.
	 */
	public static void imprimeHashChar(HashMap<String, Character> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String chave = (String)array[i];
			Character valor = hash.get(chave);
			System.out.println(chave + " " + valor);
		}
	}

	/**
	 * Esse metodo imprime as chaves STRING de um hash passado
	 * por parametro e seus respectivos valores INTEGER.
	 * @param hash eh o hash a ser impresso.
	 */
	public static void imprimeHashStringInteger(HashMap<String, Integer> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();

		for (int i=0; i<array.length; i++) {
			String chave = (String)array[i];
			Integer valor = hash.get(chave);
			System.out.println(chave + " " + valor);
		}
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por par�metro e seus respectivos conte�dos.
	 * @param hash � o hash a ser impresso.
	 */
	public static void imprimeHash(HashMap<String, NodeCandidate> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<String> set = hash.keySet();
		Object[] array = set.toArray();
		System.out.println("Tamanho do hash impresso: " + array.length);
		for (int i=0; i<array.length; i++) {
			NodeCandidate nodo = hash.get(array[i]);
			System.out.println("Nodo: " + nodo.termo + " - " +	nodo.freq + " - " +	nodo.freq_CorpusGeral + " - " + nodo.rankTHD_CorpusEspec + " - " + nodo.rankTHD_CorpusGeral);
		}
	}

	/**
	 * Esse metodo imprime os valores de um vetor.
	 * @param vetor � o vetor a ser impresso.
	 */
	public static void imprimeVetor(Vector<String> vetor ){
		if (vetor == null) {
			System.out.println("O vetor eh nulo e nao pode ser impresso.");
			return;
		}

		int tam = vetor.size();
		for (int i=0; i<tam; i++) {
			System.out.println(vetor.get(i));
		}
	}

	/**
	 * Esse metodo conta quantos gramas o candidato a termo contem, ou seja,
	 * se � unigrama tem um grama; se � bigrama tem dois gramas, etc.
	 * @param ct candidato a termo.
	 * @return quantidade de gramas do candidato a termo (ct)
	 */
	public static int contarGramas(String ct){
		StringTokenizer tokenizer = new StringTokenizer(ct,"<>");
		int gramas = 0;
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken().trim();
			if(!token.equals(""))gramas++;
		}
		return gramas;
	}

	public static Vector<String> armazenarPalavrasDoTexto(String nomeArquivo) {
		Vector<String> palavrasDoTexto = new Vector<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(nomeArquivo));
			String linha = in.readLine(),
					palavra = "";
			StringTokenizer st = null;

			while (linha != null) {
				if (!linha.trim().equals("")) {
					st = new StringTokenizer(linha, " ");
					while (st.hasMoreTokens()) {
						palavra = st.nextToken().trim().toLowerCase();
						palavra = Treatment.removerAcentos(palavra);
						palavra = Treatment.removerPontuacao(palavra);
						palavrasDoTexto.add(palavra);
					}
				}
				linha = in. readLine();
			}

			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return palavrasDoTexto;
	}

	public static HashMap<String, Boolean> armazenarPalavrasDoTextoEmHash(String nomeArquivo) {
		HashMap<String, Boolean> palavrasDoTexto = new HashMap<String, Boolean>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(nomeArquivo));
			String linha = in.readLine(),
					palavra = "";
			StringTokenizer st = null;

			while (linha != null) {
				if (!linha.trim().equals("")) {
					st = new StringTokenizer(linha, " ");
					while (st.hasMoreTokens()) {
						palavra = st.nextToken().trim().toLowerCase();
						palavra = Treatment.removerAcentos(palavra);
						palavra = Treatment.removerPontuacao(palavra);
						if (!palavra.equals("")) {
							palavrasDoTexto.put(palavra, true);
						}
					}

				}
				linha = in. readLine();
			}

			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return palavrasDoTexto;
	}

	/**
	 * Esse metodo verifica se um ct (candidato a termo) contem em um noh de um hash.
	 * @param nodo noh do hash.
	 * @param ct candidato a termo.
	 * @return verdadeiro se o ct esta no hash e falso caso contrario.
	 */
	//TODO está errado!! Ver o metodo contemGramaPara1Gram.
	public static boolean contemGrama(NodeCandidate nodo, String ct){
		StringTokenizer tokenizer = new StringTokenizer(nodo.termo,"<>");
		ArrayList<String> gramas_nodo_termo = new ArrayList<String>(); 
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken().trim();
			if(!token.equals(""))gramas_nodo_termo.add(token);
		}

		tokenizer = new StringTokenizer(ct,"<>");
		ArrayList<String> gramas_termo = new ArrayList<String>(); 
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken().trim();
			if(!token.equals(""))gramas_termo.add(token);
		}

		for(int i = 0; i < gramas_nodo_termo.size();i++){
			int j = 0;
			while(j < gramas_termo.size() && i+j < gramas_nodo_termo.size()&& 
					gramas_nodo_termo.get(i+j).equals(gramas_termo.get(j))){
				j++;
			}
			if (j==gramas_termo.size()) {
				return true;
			}
		}

		return false;
	}

	public static boolean compararNomesArquivos (String dir1, String dir2) {
		String arqDir1 = "", arqDir2 = "";
		File nomeArqDir1 = null, nomeArqDir2 = null;
		boolean achou = false, faltaArquivo = false;
		File f1 = new File(dir1);
		String[] lista_arqDir1 = f1.list();

		if (lista_arqDir1 == null) {
			System.out.println("Nao ha arquivos no diretorio: " + dir1);
			return false;
		}

		// Para cada texto do diretorio 1:
		for (int index = 0; index < lista_arqDir1.length; index++) {
			arqDir1 = dir1 + "/" + lista_arqDir1[index];
			if (arqDir1.endsWith(".txt")) {
				nomeArqDir1 = new File(arqDir1 + ".xml");

				// Para cada texto do diretorio 2:
				achou = false;
				String[] lista_arqDir2 = (new File(dir2)).list();
				if (lista_arqDir2 == null) {
					System.out.println("Nao ha arquivos no diretorio do parser PALAVRAS: " + dir2);
					return false;
				}

				for (int index2 = 0; index2 < lista_arqDir2.length && !achou; index2++) {
					arqDir2 = dir2 + "/" + lista_arqDir2[index2];

					if (!arqDir2.endsWith("~") && (arqDir2.endsWith(".xml"))) { // para nao pegar arquivos temporarios
						nomeArqDir2 = new File(arqDir2);
						if (nomeArqDir1.getName().equals(nomeArqDir2.getName()))
							achou = true;
					}
				}

				if (achou ==  false) {
					faltaArquivo = true;
					System.out.println(arqDir1);
				}
			}

		}
		if (faltaArquivo)
			System.out.println("Falta processar pelo parser PALAVRAS os arquivos listados acima.");
		return faltaArquivo;
	}

	/**
	 * Esse metodo imprime as chaves de um hash passado por parametro.
	 * @param hash eh o hash a ser impresso.
	 */
	public static void imprimeHashNodo(HashMap<Integer, NodeFeatureOfTax> hash ){
		if (hash == null) {
			System.out.println("O hash eh nulo e nao pode ser impresso.");
			return;
		}
		Set<Integer> set = hash.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			System.out.println("Noh: " + array[i]);
			NodeFeatureOfTax nodo = hash.get(array[i]);

			// para o termo "potenc":	if (nodo.id == 137 || nodo.id == 285 || nodo.id == 289 || nodo.id == 297)

			System.out.println(nodo.termos + " : " + nodo.parent + " : " +
					nodo.listaDocumentos + " : " + nodo.nivel + " : " + nodo.numDocs + 
					" : " + nodo.numDescendentes + " : " + nodo.numNohsFilhos + " : " + nodo.numIrmaos);
		}
	}

}