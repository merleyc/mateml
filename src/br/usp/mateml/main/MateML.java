package br.usp.mateml.main;

import java.util.Scanner;

import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.output.Output;
import br.usp.mateml.steps.feature_extraction.Features;
import br.usp.mateml.steps.preprocessing.DataRepresentation;
import br.usp.mateml.steps.preprocessing.WordProcessor;
import br.usp.mateml.steps.preprocessing.snowball.SpanishStemmer;

public class MateML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MateML mateml = new MateML();
		mateml.extractTerms();
	}

	public void extractTerms() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Method for Automatic Term Extraction based on Machine Learning (MATE-ML).");
		System.out.println("Enter the config file path or enter \"help\":\n");
		String cfgPath = scanner.next();

		if (cfgPath.trim().toLowerCase().equals("help")) {
			printHelp();
		} else {
			loadCfgFile(cfgPath);
			preprocessTexts();
			extractFeatures();
			createOutput();
		}		
	}

	private void printHelp() {
		System.out.println("Help is coming soon...");
	}

	private void loadCfgFile(String cfgPath) {
		GlobalVariables.loadConfiguration(cfgPath);
	}

	private void preprocessTexts() {
		WordProcessor processadorDePalavras = new WordProcessor();
		SpanishStemmer spanishStemmer = new SpanishStemmer();

		GlobalVariables.loadStoplist(GlobalVariables.configuration.getCaminhoStoplist());

		if (GlobalVariables.configuration.isCriarArqsPretext()) {
			//StopWords sw = new StopWords(CarregadorConfiguracoes.language); //Objeto para remocao das stopwords dos documentos
			DataRepresentation dataRepresentation = new DataRepresentation(GlobalVariables.configuration);
			dataRepresentation.createDataRepresentationPretext(processadorDePalavras, spanishStemmer);
		}

	}

	/**  Esse metodo grava os ct a partir do arquivo .all da pretext em um hash de unigramas caso
	 *  os cts sejam unigramas, em um hash de bigramas caso os cts sejam bigramas, e assim por diante.
	 */
	private void extractFeatures() {
		PretextLoader pretextLoader = new PretextLoader();

		GlobalVariables.loadCandidates(1, GlobalVariables.configuration.getCaminhoPretext1());

		if (!GlobalVariables.candidates.hashCandidatesIsEmpty()) {
			System.out.println("Candidate term loaded.");
			Features features = new Features();
			features.calculateFeatures(1, GlobalVariables.candidates,
					GlobalVariables.configuration, pretextLoader);
		}
	}

	private void createOutput() {
		Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);
	}

}