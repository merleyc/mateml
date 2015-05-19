package br.usp.mateml.feature_extraction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import br.usp.mateml.preprocessing.Treatment;



public class TVQ {

	int numAtrib, numDocs;

	public HashMap<String, Double> calculaTVQ(String matrizDataEsparsa, Vector<String> MatrizNames) throws IOException{

		//double valorTvq;
		int i = 0; 
		BufferedReader entrada = new BufferedReader(new FileReader(matrizDataEsparsa));
		String linha = entrada.readLine();
		HashMap<String, Double> hashTvq = new HashMap<String, Double>();

		//  Primeira linha do arquivo: numero de atributos da tabela
		numAtrib = Integer.parseInt(linha);

		//  Segunda linha: numero de documentos
		linha    = entrada.readLine();
		numDocs   = Integer.parseInt(linha);

		//  Demais linhas: conteudo
		linha = entrada.readLine();
		while(linha != null) {
			if (!linha.trim().equals("")) {
				// Calculo de tvq
				double valorTvq = tvq(linha);

				// Obtem o termo correspondente ao codigo da matriz atr-valor
				String chave = MatrizNames.get(i);
				chave = Treatment.tratar_termo(chave, true);
				if (!chave.equals(""))
					hashTvq.put(chave.trim(), valorTvq);
				i++;
			}
			linha = entrada.readLine();
		}
		return hashTvq;
	}

	/* Entrada: cada linha do arquivo contendo matriz esparsa inversa (atributos x documentos)
	 by Bruno */
	public double tvq(String linha) {
		double TVQ = 0.0,
				resp = 0.0;
		int [] freq;
		int freqTotal = 0;

		String [] vetSplit = linha.split(",")[1].split(" ");
		freq = new int[vetSplit.length];

		//  Calcula a frequencia total e obtem cada uma das frequencias
		for (int i = 0; i < vetSplit.length; i++){
			freq[i] = Integer.parseInt(vetSplit[i].split("\\(")[0]);
			freqTotal += freq[i];
			resp += Math.pow((double)freq[i], 2);
		}

		//  Calcula o valor de tvq
		TVQ = resp - (((double)1 / (double)freq.length) * Math.pow((double)freqTotal,2));

		return TVQ;
	}

}