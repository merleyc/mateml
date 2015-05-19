package br.usp.mateml.feature_extraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import br.usp.mateml.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;
import br.usp.mateml.util.Util;

public class ReferenceList {

	private final HashMap<String, Boolean> hash_referenceList = new HashMap<String, Boolean>();

	public ReferenceList(String fileReferenceList) {
		if (hash_referenceList.isEmpty() &&
				fileReferenceList != null && !fileReferenceList.equals("")) {
			loadReferenceList(fileReferenceList);
		}
	}

	public boolean isReferenceListEmpty() {
		if (hash_referenceList.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Esse metodo armazena os termos da lista de referencia em um hash e conta a quantidade dos mesmos.
	 * @param arquivo contendo os termos de referenica do dominio.
	 * @return hash com os termos de referencia e suas respectivas frequencias.
	 */
	private void loadReferenceList(String arquivo) {
		try {
			//int numTermos = 0;
			if (!Util.arquivoOK(arquivo)) {
				System.out.println("Nao encontrou o arquivo da lista de referencia");
				return;
			}

			String encoding = TestDetector.detectaEncodingImprimeArquivo(arquivo);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo),encoding));
			else in = new BufferedReader(new FileReader(arquivo));

			//BufferedReader in = new BufferedReader(new FileReader(arquivo));
			String line = in.readLine().trim();

			while (line != null) {
				if (!line.equals("")) {
					line = Treatment.removerAcentos(line.toLowerCase().trim());
					String[] vetor_termo = line.split(" ");
					int tam = vetor_termo.length;
					String linha_aux = "";
					for (int i=0; i < tam; i++) {
						//	if (!br.usp.extracao.util.Tratamento.ehStopword(vetor_termo[i]))
						linha_aux += " " + vetor_termo[i];
					}
					linha_aux = linha_aux.toLowerCase().trim();
					if (!hash_referenceList.containsKey(linha_aux)) {
						hash_referenceList.put(linha_aux.trim(),true);
						//numTermos++;
					}
				}
				line = in.readLine();
			}

			Control.controle_lr = true;
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean containsKey(String line) {
		if (hash_referenceList.containsKey(line)) {
			return true;
		}
		return false;
	}

}
