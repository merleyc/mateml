package br.usp.mateml.feature_extraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import br.usp.mateml.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;


public class TV {

	int numAtrib, numDocs;

	public HashMap<String, Double> calculaTV(String matrizDataEsparsa, Vector<String> MatrizNames) throws IOException{

		int i = 0; 
		//BufferedReader entrada = new BufferedReader(new FileReader(matrizDataEsparsa));
		String encoding = TestDetector.detectaEncodingImprimeArquivo(matrizDataEsparsa);
		BufferedReader entrada;
		if (encoding != null) 
			entrada = new BufferedReader(new InputStreamReader(new FileInputStream(matrizDataEsparsa),encoding));
		else entrada = new BufferedReader(new FileReader(matrizDataEsparsa));
		
		String linha = entrada.readLine();
		HashMap<String, Double> hashTv = new HashMap<String, Double>();

		//  Primeira linha do arquivo: numero de atributos da tabela
		numAtrib = Integer.parseInt(linha);

		//  Segunda linha: numero de documentos
		linha    = entrada.readLine();
		numDocs   = Integer.parseInt(linha);

		//  Demais linhas: conteudo
		linha = entrada.readLine();
		while (linha != null) {
			if (!linha.trim().equals("")) {

				// Calculo de tv
				double valorTv = tv(linha);

				// Obtem o termo correspondente ao codigo da matriz atr-valor
				String chave = MatrizNames.get(i);
				chave = Treatment.tratar_termo(chave, true);
				if (!chave.equals(""))
					hashTv.put(chave.trim(), valorTv);
				i++;
			}
			
			linha = entrada.readLine();
		}
		
		return hashTv;
	}

	/* Entrada: cada linha do arquivo contendo matriz esparsa inversa (atributos x documentos)
	 by Bruno */
	public double tv(String linha){

		double tv = 0, fMed;
		int freqTotal = 0, freqAtual, docAtual;
		int [] freq;

		String [] vetSplit = linha.split(",")[1].split(" ");
		freq = new int[numDocs]; //  Calcula para todas as possiveis, inclusive
		// aquelas que nao houve frequencia 

		//  Calcula a frequencia total e obtem cada uma das frequencias
		for (int i = 0; i < vetSplit.length; i++){
			freqAtual = Integer.parseInt(vetSplit[i].split("\\(")[0]);
			docAtual  = Integer.parseInt(vetSplit[i].split("\\(")[1].split("\\)")[0]);
			freq [docAtual - 1] = freqAtual;  //  -1: documentos estao numerados de 1 a n
			freqTotal += freqAtual;
		}
		
		//  Calcula a frequencia media
		fMed = (double)freqTotal / (double)numDocs;

		//  Calcula o valor de tvq
		for (int i = 0; i < freq.length; i++){

			tv += Math.pow((double)freq[i] - fMed, 2);

		}

		return tv;
	}
}