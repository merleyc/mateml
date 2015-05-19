package br.usp.mateml.feature_extraction;

import java.io.File;
import java.util.Random;

public class RandomATE {

	
	public void extracaoAleatoria(File arqSaida){
		Random rand = new Random(1);
		String data = "";
		for (int i=0; i < 4; i++){
			int numero = rand.nextInt(2);
			data = Integer.toString(numero) + "\n";
		}
		
		//TODO Tratamento.gravarArquivoGeral(arqSaida, data);
		
	}
	
}
