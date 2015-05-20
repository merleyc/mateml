package br.usp.mateml.steps.feature_extraction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;



// Formato arquivo entrada: matriz atributo-valor transposta no formato esparso (discoveresparsaAtribDoc1Gram.txt)
public class TC {

	int numeroAtributos, numeroDocumentos;
	ArrayList<String> valoresLinha;

	public HashMap<String, Double> calculaTC(String matrizDataEsparsa, Vector<String> MatrizNames, String caminhoArqSaidaTfIdf){
		HashMap<String, Double> hashTc = new HashMap<String, Double>();
		try{
			//  Inicializa��o do n�mero de atributos
			numeroAtributos = 0;

			//  Calcula a TFIDF para todos os atributos
			calculaTfIdf(matrizDataEsparsa, caminhoArqSaidaTfIdf);

			//  Calcula o valor de Term Contribution para cada atributo
			hashTc = obtemTc(hashTc, caminhoArqSaidaTfIdf, MatrizNames);

		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return hashTc;

	}

	//  Calcula o valor de Term Contribution para cada atributo
	private HashMap<String, Double> obtemTc(HashMap<String, Double> hashTc, String caminhoArqSaidaTfIdf, Vector<String> MatrizNames) throws IOException{

		String linha;
		String [] vetorLinha;
		double valorTc;
		int numeroAtributo, ultimoAtributo = 1, index = 0;

		//BufferedReader entradaTfIdf = new BufferedReader(new FileReader(caminhoArqSaidaTfIdf));
		String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoArqSaidaTfIdf);
		BufferedReader entradaTfIdf;
		if (encoding != null) 
			entradaTfIdf = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoArqSaidaTfIdf),encoding));
		else entradaTfIdf = new BufferedReader(new FileReader(caminhoArqSaidaTfIdf));
		

		//  Calcula para cada atributo o valor de TC
		for (int i = 0; i < numeroAtributos; i++){

			//  Retorna o ponteiro do arquivo para a linha correspondente ao atributo
			// atual, lendo-a e armazenando seus elementos em um vetor
			linha      = entradaTfIdf.readLine();
			vetorLinha = linha.split(",");

			//  Zera o valor de TC para aquele atributo
			valorTc = 0;

			//  Obtem o n�mero do atributo a partir da primeira posi��o do vetor: (nAtrib)
			numeroAtributo = Integer.parseInt((vetorLinha[0].split("\\(")[1]).split("\\)")[0]);

			/*			//  Verifica se o �ltimo atributo lido n�o � o anterior ao atual 
			// (i.e., �ltimo != atual - 1)
			if (ultimoAtributo != numeroAtributo - 1){
				//  Se for, grava quantos 0's quantos os correspondentes ao n�mero de 
				// atributos que nao foram contabilizados
				for (int j = 0; j < ultimoAtributo - numeroAtributo; j++){
					//  Obtem o termo correspondente ao codigo da matriz atr-valor
					String termo = Principal.MatrizNames.get(index);
					atualizarHash(1, termo, valorTc);
					index++;
				}
			}*/

			//  Atribui o valor do atributo atual como �ltimo atributo lido
			ultimoAtributo = numeroAtributo;

			//  Percorre a linha toda (todos os documentos contabilizados)
			// Come�a de 1 devido ao fato de na primeira posi��o estar presente o valor
			// do n�mero do atributo
			for (int j = 1; j < vetorLinha.length; j++){
				for (int k = j + 1; k < vetorLinha.length; k++){
					valorTc += Double.parseDouble(vetorLinha[j].split("\\(")[0]) * Double.parseDouble(vetorLinha[k].split("\\(")[0]);
				}
			}

			//  Obtem o termo correspondente ao codigo da matriz atr-valor
			String chave = MatrizNames.get(index);
			chave = Treatment.tratar_termo(chave, true);
			if (!chave.equals(""))
				hashTc.put(chave, valorTc);
			index++;
			int tam = vetorLinha.length;

			//  L� a linha do pr�ximo atributo
			//linha = saidaTfIdf.readLine();

		}
		return hashTc;

	}

	//  Calcula o valor de TfIDF para todos os atributos
	//  Armazena a TfIDF de maneira esparsa, no formato:
	// (nAtrib),valorTfIdf(nDoc),...
	private void calculaTfIdf(String matrizDataEsparsa, String caminhoArqSaidaTfIdf) throws IOException{

		double medida;
		String linha, linhaSaida;
		String [] vetLinha, splitConteudo;
		int df;

		//BufferedReader entrada = new BufferedReader(new FileReader(matrizDataEsparsa));
		String encoding = TestDetector.detectaEncodingImprimeArquivo(matrizDataEsparsa);
		BufferedReader entrada;
		if (encoding != null) 
			entrada = new BufferedReader(new InputStreamReader(new FileInputStream(matrizDataEsparsa),encoding));
		else entrada = new BufferedReader(new FileReader(matrizDataEsparsa));
		

		//  Inicializa��o dos buffers de output			
		File arqArqSaidaTfIdf = new File(caminhoArqSaidaTfIdf);
		Treatment.criarArquivo(arqArqSaidaTfIdf);
		FileWriter writer = new FileWriter(arqArqSaidaTfIdf);
		BufferedWriter saidaTfIdf = new BufferedWriter(writer);

		//  Primeira linha: numero de atributos
		linha           = entrada.readLine();
		numeroAtributos = Integer.parseInt(linha);
		//System.out.println(numeroAtributos);

		//  Segunda linha: numero de documentos
		linha            = entrada.readLine();
		numeroDocumentos = Integer.parseInt(linha);

		//  Demais linhas: freq��ncia		
		linha = entrada.readLine();
		while(linha != null){
			if (!linha.trim().equals("")) {

				//  Split da linha de entrada
				vetLinha = linha.split(",");

				//  Primeiro elemento da linha: n�mero do atributo
				linhaSaida = vetLinha[0];

				//  Pega a frequencia e o numero do documento onde ocorreu
				splitConteudo = vetLinha[1].split(" ");

				//  N�mero de documentos onde ocorreu o atributo
				df = splitConteudo.length;

				//  Calcula a TFIDF do atributo para cada documento
				for(int i = 0; i < splitConteudo.length; i++){

					medida = Double.parseDouble(splitConteudo[i].split("\\(")[0]) * Math.log10((double)numeroDocumentos / (double)df);
					//if (medida != 0)
					linhaSaida += "," + medida + "(" + splitConteudo[i].split("\\(")[1].split("\\)")[0] + ")";

				}

				//  Grava a TFIDF do atributo para cada documento
				saidaTfIdf.write(linhaSaida);
				saidaTfIdf.newLine();
				saidaTfIdf.flush();

			}
			//  L� a linha do pr�ximo atributo
			linha = entrada.readLine();

		}

		saidaTfIdf.flush();
		saidaTfIdf.close();


	}

	//  Conta o n�mero de documentos em que o atributo ocorre
	private int obtemDf(String [] linhaAtributo){

		int contadorOcorrencias = 0;

		//  Verifica quantas ocorr�ncias tem pela contagem do n�mero de elementos
		// diferentes de 0 que o vetor de ocorr�ncias possui
		for (String i : linhaAtributo){

			if(!i.equals("0"))
				contadorOcorrencias++;

		}

		//  Retorna o n�mero de documentos que cont�m o termo
		return contadorOcorrencias;
	}

	private void retornaParaLinha(int numeroLinha, RandomAccessFile raf) throws IOException{

		raf.seek(0);
		for(int i = 0; i < numeroLinha; i++)
			raf.readLine();

	}

	/*	public void main (String [] args){

		new TC();

	}*/

}