package br.usp.mateml.steps.feature_extraction;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import br.usp.mateml.loaders.IndicativePhraseLoader;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.util.Util;


// programa para pegar as palavras de v�rios textos em um diret�rio. As palavras obtidas s�o as que est�o na frente de expressoes indicativas.

public class IndicativePhrases {

	private HashMap<String, Boolean> palavrasPertoEI = new HashMap<String, Boolean>();
	IndicativePhraseLoader carregadorExpressoesIndicativas = new IndicativePhraseLoader();
	
	public HashMap<String, Boolean> obterExprIndicativas(
			ConfigurationLoader configuration, PretextLoader pretextLoader) {

		String caminhoCorpus = configuration.getCaminhoCorpus();
		String caminhoArqExpIndicativas = configuration.getCaminhoExpressoesIndicativas();
		String caminhoPretextStem = configuration.getCaminhoPretextStem();
		
		if (!Util.arquivoOK(caminhoArqExpIndicativas)) {
			System.out.println("Erro com o arquivo de entrada de expressoes indicativas.");
			return null;
		}

		String nomeArquivo = "";
		Vector<String> palavrasTexto = new Vector<String>();

		Vector<Vector<String>> vetorEI = carregadorExpressoesIndicativas.carregarEI(caminhoArqExpIndicativas);

		HashMap<String, String> hashStem = pretextLoader.carregarStem(caminhoPretextStem);

		//Para cada texto do corpus de dominio:
		String[] lista_arq = (new File(caminhoCorpus)).list();
		for (int index = 0; index < lista_arq.length; index++) {
			nomeArquivo = caminhoCorpus + "/" + lista_arq[index];

			if (new File(nomeArquivo).isFile() && (!nomeArquivo.endsWith("~"))) {
				palavrasTexto = Util.armazenarPalavrasDoTexto(nomeArquivo);
				verificarEI(palavrasTexto, vetorEI, hashStem);
			}

		}

		return palavrasPertoEI;
	}

	private void verificarEI(Vector<String> palavrasTexto,
			Vector<Vector<String>> vetorEI, HashMap<String, String> hashStem) {
		String palavraTexto = "",
				inicioEI = "",
				stem = "";

		int tamTexto = 0;
		if (palavrasTexto == null) { System.out.println("Vetor de palavras do textos eh nulo."); }
		else  {	tamTexto = palavrasTexto.size(); }

		int tamVetorEI = 0;
		if (vetorEI == null) { System.out.println("Vetor de expressoes indicativas eh nulo."); }
		else { tamVetorEI = vetorEI.size(); }

		// percorre as palavras do texto do corpus:		
		for (int iTexto=0; iTexto<tamTexto; iTexto++) {
			palavraTexto = palavrasTexto.get(iTexto);

			// percorre
			for (int iVetorEI=0; iVetorEI<tamVetorEI; iVetorEI++){
				inicioEI = "";
				if (vetorEI.get(iVetorEI) == null) { System.out.println("Indice " + iVetorEI + " do vetor de expressoes indicativas eh nulo."); }
				else { inicioEI = vetorEI.get(iVetorEI).get(0); }

				if (inicioEI.equals(palavraTexto)) {

					// percorre uma expressao indicativa:
					Vector<String> EI = vetorEI.get(iVetorEI);
					int tamEI = 0;
					if (EI == null) { System.out.println("Vetor EI de expressoes indicativas eh nulo."); }
					else { tamEI = EI.size(); }

					boolean encontrouEI  = true;
					for (int iEI=0; iEI<tamEI && encontrouEI; iEI++) {
						int iTextoAux = iTexto + iEI;
						if (iTextoAux<tamTexto) {

							//verificando palavrasTexto.get(iTextoAux)
							String palavra = "";
							if (iTextoAux < tamTexto) { //verifica se o indice eh valido.
								palavra = palavrasTexto.get(iTextoAux);
								if (palavra == null) { palavra = ""; }
							}
							if (!EI.get(iEI).equals(palavra)){
								encontrouEI = false;
							}

						}
					}
					if (encontrouEI) {
						int index = iTexto-1;
						stem = "";
						if ( index >= 0 && index < tamTexto) {
							stem = buscaStem(palavrasTexto.get(index), hashStem);
						} else { System.out.println("stem eh vazio."); }
						if (!stem.equals(""))
							palavrasPertoEI.put(stem, true);
					}

				}

			}

		}

	}

	/**
	 * Esse metodo busca o correspondente stem de uma palavra dada.
	 * @param palavrasTexto palavra dada para ser buscado o seu stem.
	 * @param hashStem hash que cont�m a palavra original do texto como chave e o
	 *  seu respectivo stem como n�.
	 * @return o stem da palavra dada.
	 */
	private String buscaStem(String palavraTexto,
			HashMap<String, String> hashStem) {
		String stem = "";

		if (palavraTexto != null && hashStem != null) {
			if (hashStem.containsKey(palavraTexto)) {
				stem = hashStem.get(palavraTexto);
			}
		}

		return stem;
	}

}