/* � necess�rio mudar no c�digo o caminho de sa�da e, ap�s executar o programa, deve digitar o caminho o entrada.
 */

package br.usp.mateml.output;

/**
 * Modificado do programa do Bruno M. Nogueira.
 * Entrada: discover_sem_classe.data -> MatrizEsparsaAtribDoc -> Sa�da: discoveresparsaAtribDoc1Gram.txt
 * (transforma a matriz atrib-valor da pretext para matriz atrib-valor esparsa).
 * 
 * 
 * 
 * Universidade de S�o Paulo - USP - S�o Carlos
 * Instituto de Ci�ncias Matem�ticas e de Computa��o - ICMC
 * Laborat�rio de Intelig�ncia Computacional - LABIC
 * 
 * Classe: MatrizEsparsaAtribDoc
 * 
 * - Objetivo: Transpor uma matriz atributo-valor e armazen�-la em arquivo na forma 
 * 			   esparsa.
 * 
 * - Entrada necess�ria: Matriz atributo-valor no formato de sa�da gerado pela ferra-
 *                       menta PreText (arquivo discover.data)
 *                       
 * - Sa�da: Matriz esparsa atributos x documentos em arquivi, o qual seguir� o se-
 * 			guinte formato:
 * 			
 * 			- Primeira linha do arquivo: n�mero total de atributos;
 * 			- Segunda linha do arquivo: n�mero total de documentos;
 * 			- Demais linhas: (numAtrib1),numOcor11(numLin11) numOcor21(numLin21) ... numOcorn1(numLinn1)
 * 							 (numAtrib2),numOcor12(numLin12) numOcor22(numLin22) ... numOcorn2(numLinn2)
 * 														...
 * 							 (numAtribm),numOcor11(numLin1m) numOcor21(numLin2m) ... numOcorn1(numLinnm)
 * 				- No qual: numAtrib = n�mero do atributo;
 * 						   numOcor  = n�mero de ocorr�ncias do atributo em um documento;
 * 						   numLin   = n�mero da linha onde aquele atributo teve aquele n�medo de ocorr�ncias;
 * 
 * @author Bruno Magalh�es Nogueira (brunomn@icmc.usp.br)
 * @version 0.1 - Mar�o/2008
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import br.usp.mateml.steps.preprocessing.Treatment;


public class SparseMatrix {

	private ArrayList<ArrayList<NodeMatrix>> elementos;
	private BufferedReader arquivoEntrada, teclado;
	private BufferedWriter arquivoSaida;

	private String linha;         // Armazena a linha lida no arquivo atualmente
	private String[] vetorLinha;  // Vetor com cada elemento da linha lida (separados por ,)
	private int numeroColunas;    // N�mero de colunas do documento (inclui o nome do documento) 
	private int contadorLinhas;   // Contador do n�mero de linhas contidas no documento


	public SparseMatrix(String nomeArquivoEntrada, String nomearquivoSaida){
		try{
			arquivoEntrada = new BufferedReader(new FileReader(nomeArquivoEntrada));

			File arqSaida = new File(nomearquivoSaida);
			Treatment.criarArquivo(arqSaida);
			FileWriter writer = new FileWriter(arqSaida);
			arquivoSaida = new BufferedWriter(writer); 

			criaMatriz();
			preencheMatriz();
			gravaMatriz();

			arquivoSaida.flush();
			arquivoSaida.close();

		}

		catch (IOException e){
			e.printStackTrace();
		}

	}

	// para chamar de outra classe
	//public File geraMatrizEsparsa (Vector)

	private void criaMatriz() throws IOException{

		//System.out.println("Criando posi��es da matriz...");

		linha          = arquivoEntrada.readLine();
		vetorLinha     = linha.split(",");
		numeroColunas  = vetorLinha.length;
		contadorLinhas = 1;

		// Cada posi��o desta lista corresponde a um atributo. Em cada uma destas 
		// posi��es encontra-se uma lista de nodos contendo a linha de ocorr�ncia
		// daquele atributo, bem como o seu valor
		elementos = new ArrayList<ArrayList<NodeMatrix>>();

		// Cria as listas para todas as colunas
		// In�cio em 1: elemento da primeira coluna � o nome da linha
		for(int i = 1; i < numeroColunas; i++){

			ArrayList<NodeMatrix> novo = new ArrayList<NodeMatrix>();
			elementos.add(novo);

		}

		//System.out.println("Posi��es Criadas!\n\n");
	}

	private void preencheMatriz () throws IOException{

		//System.out.println("Preenchendo posi��es da matriz...");

		while(linha != null) {
			if (!linha.trim().equals("")) {

				vetorLinha = linha.split(",");

				/*			if(contadorLinhas %100 == 0)
				System.out.println("Linha: " + contadorLinhas);*/

				//  Verifica o valor de todos os atributos do documento representado
				// na linha atual
				for(int i = 1; i < numeroColunas; i++){

					//  Se o atributo tiver ocorrido no documento
					if(!vetorLinha[i].equals("0")){
						// Constr�i um novo nodo com as informa��es coletadas
						NodeMatrix novoNodo = new NodeMatrix();
						novoNodo.numeroLinha = contadorLinhas;
						novoNodo.numeroOcorrencias = Integer.parseInt(vetorLinha[i]);

						// Adiciona o novo nodo na lista (posi��o = numero do atributo)
						// (i-1): foram criadas exatamente n listas (retirando a coluna
						// do nome do documento) sendo que estas v�o da posi��o 0 a n-1. 
						// Como aqui i vai de 1 a n, faz-se necess�rio o i-1.
						elementos.get(i-1).add(novoNodo);
					}
				}	
				contadorLinhas++;
			}
			// L� a pr�xima linha
			linha      = arquivoEntrada.readLine();

		}

		//System.out.println("Posi��es Preenchidas!\n\n");
	}

	/**
	 * 
	 * Grava a matriz esparsa em arquivo
	 * 
	 * - Cada linha tem o formato:
	 * (numeroAtributo),numeroOcorrenciasLinha(numeroLinha) numeroOcorrenciasLinha(numeroLinha)...
	 * 
	 * @throws IOException
	 */
	private void gravaMatriz() throws IOException{

		//System.out.println("Gravando matriz em arquivo!");

		ArrayList<NodeMatrix> atributoAtual;
		StringBuffer gravar;

		//  Primeira linha do arquivo: n�mero total de atributos
		arquivoSaida.write(String.valueOf(numeroColunas - 1));
		arquivoSaida.newLine();

		//  Segunda linha do arquivo: numero total de documentos
		arquivoSaida.write(String.valueOf(contadorLinhas - 1));
		arquivoSaida.newLine();
		arquivoSaida.flush();


		// Percorre todos os atributos
		for(int i = 0; i < elementos.size(); i++){

			atributoAtual = elementos.get(i);

			// Primeira marca��o da linha: n�mero do atributo entre parenteses
			// i + 1: atributos s�o numerados de 1 a n
			gravar = new StringBuffer();
			gravar.append("(" + (i + 1) + "),");

			// Percorre as marca��es correspondentes �s linhas (documentos)
			for(int j = 0; j < atributoAtual.size(); j++){
				gravar.append(atributoAtual.get(j).numeroOcorrencias);
				gravar.append("(" + atributoAtual.get(j).numeroLinha + ")");

				//  Evita gravar o ' ' no �ltimo elemento
				if(j != atributoAtual.size() - 1)
					gravar.append(" "); 
			}

			arquivoSaida.write(gravar.toString());
			arquivoSaida.newLine();
			arquivoSaida.flush();
		}

		//System.out.println("Matriz gravada!");
	}

}