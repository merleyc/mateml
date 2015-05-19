package br.usp.mateml.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;


public class IndicativePhraseLoader {

	public Vector<Vector<String>> carregarEI(String caminhoArqExpIndicativas) {

		try {
			Vector<Vector<String>> vetorEI = new Vector<Vector<String>>();
			//BufferedReader in = new BufferedReader(new FileReader(caminhoArqExpIndicativas));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoArqExpIndicativas);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoArqExpIndicativas),encoding));
			else in = new BufferedReader(new FileReader(caminhoArqExpIndicativas));
			
			String linha = in.readLine(),
			palavra = "";

			while (linha != null) {
				Vector<String> vetorPalavras = new Vector<String>();
				if (!linha.trim().equals("")) {

					StringTokenizer st = new StringTokenizer(linha, " ");

					while (st.hasMoreTokens()){
						palavra = st.nextToken().toLowerCase();
						palavra = Treatment.removerAcentos(palavra);
						palavra = Treatment.removerPontuacao(palavra);
						if (!palavra.equals(""))
							vetorPalavras.add(palavra);						
					}
					if (vetorPalavras != null && !vetorPalavras.isEmpty())
						vetorEI.add(vetorPalavras);
				}

				linha = in.readLine();
			}

			in.close();
			
			return vetorEI;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
