package br.usp.mateml.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import br.usp.mateml.steps.preprocessing.Treatment;


/**
 * Modificado da classe do Bruno M. Nogueira.
 * Entrada: dicover.data -> RetiraClasseDiscover.java -> Sa�da: discover_sem_classe.data
 */
public class DiscoverClass {

	BufferedReader entData, entNames, teclado;
	BufferedWriter saidaData, saidaNames;

	public DiscoverClass(String caminhoData, String caminhoDataSemClasse){

		try {
			entData = new BufferedReader(new FileReader(caminhoData));

			File arqSaida = new File(caminhoDataSemClasse);
			Treatment.criarArquivo(arqSaida);
			FileWriter writer = new FileWriter(arqSaida);
			saidaData = new BufferedWriter(writer); 

			trataData();
			//trataNames();
		} 

		catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void trataData() throws IOException{

		String linha = entData.readLine(), linhaSaida;
		String [] linhaSplit;
		StringBuffer strBuffer = new StringBuffer();

		while(linha != null){
			if (!linha.trim().equals("")) {
				linhaSplit = linha.split(",");
				//			linhaSaida = linhaSplit[0];
				strBuffer = null;
				strBuffer = new StringBuffer();
				strBuffer.append(linhaSplit[0]);
				for(int i = 1; i < linhaSplit.length; i++)
					strBuffer.append("," + linhaSplit[i]);


				saidaData.write(strBuffer.toString());
				saidaData.newLine();
			}	
			linha = entData.readLine();
		}

		saidaData.flush();
		saidaData.close();
	}

	private void trataNames() throws IOException{

		String linhaAtual = entNames.readLine(), ultLinha;

		ultLinha = linhaAtual;

		linhaAtual = entNames.readLine();		
		while(linhaAtual != null){

			saidaNames.write(ultLinha);
			saidaNames.newLine();

			ultLinha = linhaAtual;
			linhaAtual = entNames.readLine();			
		}

		saidaNames.flush();
		saidaNames.close();
	}

}