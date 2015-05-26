package br.usp.mateml.steps.preprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.steps.preprocessing.snowball.SpanishStemmer;

public class DataRepresentation {

	ConfigurationLoader configuration;

	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<AttributeList> atributos = new ArrayList<AttributeList>(); // lista constendo os atributos de cada documento da coleção
	private HashMap<String,Integer> atrDF = new HashMap<String,Integer>(); //armazena as DF dos atributos
	private HashMap<String,String> stemPal = new HashMap<String,String>(); // dicionario do tipo palavra - stem
	private ArrayList<File> filesIn = new ArrayList<File>();
	//public StopWords sw = new StopWords(language); //Objeto para remocao das stopwords dos documentos

	public DataRepresentation (ConfigurationLoader configuration) {
		if (configuration != null) {
			this.configuration= configuration; 
		}

	}

	public void createDataRepresentationPretext(WordProcessor processadorDePalavras,
			SpanishStemmer spanishStemmer) {

		FileList fileList = new FileList();
		fileList.ListaArquivos(new File(configuration.getCaminhoCorpus()),
				filesIn); //Vetor para armazenar os documentos textuais

		//deletando documentos pre-existentes das pastas de saida:
		Treatment.deleteFolder(new File(configuration.getCaminhoPretextData()));
		Treatment.deleteFolder(new File(configuration.getCaminhoPretextMaid()));
		Treatment.deleteFolder(new File(configuration.getCaminhoPretextName()));
		Treatment.deleteFolder(new File(configuration.getCaminhoPretextStem()));
		Treatment.deleteFolder(new File(configuration.getCaminhoPretext1()));


		int numDocs = 0;
		for (int i = 0; i < filesIn.size(); i++) { // criando vetores contendo os atributos e suas frequencias em cada documento da coleção
			//System.out.println(filesIn.get(i).getAbsolutePath());
			AttributeList atribs = new AttributeList();

			if (filesIn.get(i).toString().endsWith(".txt")) {
				atribs.atributos = processadorDePalavras.processarPalavrasEN_PT_ES(filesIn.get(i),
						configuration.getLanguage(), configuration.isStopword(),
						configuration.isStemmer(), stemPal, names, atrDF);
				atributos.add(i, atribs);
				numDocs++;
				System.out.print(".");
			}

		}

		System.out.println("");

		//Util.imprimeHashStringHash(PreProcessamento.hashStemWdST);
		geraArquivoStemWdST_e_1Gram(processadorDePalavras);
		gerarArquivoLogDoStemWdST();

		HashMap<String,String> classes = new HashMap<String,String>();
		ArrayList<String> todasClasses = new ArrayList<String>();
		if(configuration.isUsarDiretorioComoClasse()){
			classes = ObterClasses(filesIn, todasClasses);
		}

		//Criando o Data
		String[][] data; //Matriz de dados
		int numAtr = 0;
		if(configuration.isUsarDiretorioComoClasse()){
			numAtr = names.size() + 2;
			data = new String[filesIn.size()][numAtr];
		}else{
			numAtr = names.size() + 1;
			data = new String[filesIn.size()][numAtr];
		}

		for(int i=0;i<filesIn.size();i++){ // Inicializando Matriz
			for(int j=0; j<=names.size();j++){
				data[i][j] = "0";
			}
		}

		if(configuration.isFreqTrueBinarioFalse()){ // Construir arquivos data e name usando frequencia das palavras
			for(int i=0;i<filesIn.size();i++){
				data[i][0] = "\"" + filesIn.get(i).getPath() + "\"";
				AttributeList atrbs = atributos.get(i);
				for(int j=0; j<atrbs.atributos.size();j++){
					AtrFreq item = atrbs.atributos.get(j);
					int pos = names.indexOf(item.atributo);
					//System.out.print(pos + " ");
					data[i][pos+1] = item.frequencia.toString();
				}
				if(configuration.isUsarDiretorioComoClasse()){
					//data[i][numAtr - 1] = "\"" + classes.get(filesIn.get(i).getAbsolutePath()) + "\"";
					data[i][numAtr - 1] = "\"" + classes.get(filesIn.get(i).getPath()) + "\"";
				}
			}
		}else{ // Construir arquivos data e name usando binario nas palavras
			for(int i=0;i<filesIn.size();i++){
				data[i][0] = "\"" + filesIn.get(i).getPath() + "\"";
				AttributeList atrbs = atributos.get(i);
				for(int j=0; j<atrbs.atributos.size();j++){
					AtrFreq item = atrbs.atributos.get(j);
					int pos = names.indexOf(item.atributo);
					//System.out.print(pos + " ");
					data[i][pos+1] = "1";
				}
				if(configuration.isUsarDiretorioComoClasse()){
					data[i][numAtr - 1] = "\"" + classes.get(filesIn.get(i).getPath()) + "\"";
				}
			}
		}


		/*		GerarArff(data, names, tArff.getText(), usarDiretorioComoClasse,
				todasClasses, filesIn.size(), numAtr);
		 */

		validateDataNameFiles(numAtr, data);
	}

	//Testando o data e o names
	private void validateDataNameFiles(int numAtr, String[][] data) {
		FileWriter arqData = null;
		try{

			File f = new File(configuration.getCaminhoPretextName());
			String path = f.getParent();
			Treatment.criarDiretorio(path);
			Treatment.criarArquivo(f);

			FileWriter arqNames = new FileWriter(f);

			arqNames.write("filenames:string" + "\n");
			for(int i=0;i<names.size();i++){
				arqNames.write(names.get(i) + "\n");
			}

			arqNames.flush();
			arqNames.close();

			f = new File(configuration.getCaminhoPretextData());
			path = f.getParent();
			Treatment.criarDiretorio(path);
			Treatment.criarArquivo(f);

			arqData = new FileWriter(f);

			for(int i=0;i<filesIn.size();i++){
				StringBuffer linha = new StringBuffer();
				for(int j=0;j<numAtr;j++){
					linha.append(data[i][j] + ",");
				}
				arqData.write(linha.toString().substring(0,linha.toString().length()-1) + "\n");
			}


		} catch (IOException e) {
			throw new RuntimeException("Error on saving the data and/or name file: " + arqData);
		} finally {
			if (arqData != null) {
				try {
					arqData.flush();
					arqData.close();
				} catch (IOException e) {
					System.out.println("Error on closing data and/or name file: " +arqData);
					e.printStackTrace();
				}

			}
		}


	}

	private void gerarArquivoLogDoStemWdST() {
		String dados = "",
				arquivoLogStemWdST = new File(configuration.getCaminhoPretext1()).getParent() + "/arquivoLogStemWdST.txt";

		if (Treatment.hashLog == null) {
			System.out.println("O hash eh nulo e nao pode ser lido.");
			return;
		}

		try {
			Treatment.criarArquivo(new File (arquivoLogStemWdST));
			Set<String> set = Treatment.hashLog.keySet();
			Object[] array = set.toArray();

			for (int i=0; i<array.length; i++) {
				dados += array[i] + ": ";

				Set<String> set2 = Treatment.hashLog.get(array[i]).keySet();
				Object[] array2 = set2.toArray();
				for (int j=0; j<array2.length; j++) {
					dados += array2[j];
					if (j > 0)
						dados += ", ";
				}
				dados += "\n";


				//dados += array[i] + ": " + Treatment.hashLog.get(array[i]) + "\n";
			}

			Treatment.gravarArquivoGeral(arquivoLogStemWdST, dados);

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}	
	}

	public HashMap<String,String> ObterClasses(ArrayList<File> filesIn, ArrayList<String> todasClasses){
		HashMap<String,String> classes = new HashMap<String,String>();

		for(int i=0;i<filesIn.size();i++){
			String arquivo = filesIn.get(i).getAbsolutePath();
			arquivo.replace("\\", "/");
			arquivo = arquivo.substring(0,arquivo.lastIndexOf("/"));
			arquivo = arquivo.substring(arquivo.lastIndexOf("/") + 1, arquivo.length());
			classes.put(filesIn.get(i).getAbsolutePath(), arquivo);
			if(!todasClasses.contains(arquivo)){
				todasClasses.add(arquivo);
			}
		}

		return classes;
	}

	/**
	 * Esse metodo gera o arquivo 'stemWdST.all'.
	 * Modelo do arquivo 'stemWdST.all' :
	 * 	abat : 4(2/390)
	 * 		abate:2
	 * 		abatidas:2
	 */
	public void geraArquivoStemWdST_e_1Gram(WordProcessor processadorDePalavras) {

		if (processadorDePalavras.hashStemWdST == null) {
			System.out.println("O hash eh nulo e nao pode ser lido.");
			return;
		}

		String linhaArqStem = "", linhaArqAll = "";

		Set<String> set = processadorDePalavras.hashStemWdST.keySet();
		Object[] array = set.toArray();
		for (int i=0; i<array.length; i++) {
			String linhaParte2 = "",
					stem = (String)array[i],
					palOriginal = "";
			int freqPalOriginal = 0,
					freqTotal = 0,
					df = 0;

			if (!stem.equals("") && !stem.trim().equals("@")) {
				Set<String> set2 = processadorDePalavras.hashStemWdST.get(array[i]).keySet();
				Object[] array2 = set2.toArray();
				for (int j=0; j<array2.length; j++) {
					palOriginal = (String)array2[j];
					freqPalOriginal = processadorDePalavras.hashStemWdST.get(array[i]).get(array2[j]);
					freqTotal += freqPalOriginal;
					linhaParte2 += "\n\t" + palOriginal + ":" + freqPalOriginal;
				}

				if (processadorDePalavras.hashStemFreq.containsKey(stem))
					df = processadorDePalavras.hashStemFreq.get(stem);

				linhaArqStem += "\n" + stem + " : " + freqTotal + "(" + df + "/" + processadorDePalavras.numDocs + ")" + linhaParte2;
				linhaArqAll += "\n" + freqTotal + ":(" + df + "/" + processadorDePalavras.numDocs + "):" + stem;
			}
		}

		Treatment.gravarArquivoGeral(configuration.getCaminhoPretextStem(), linhaArqStem);
		Treatment.gravarArquivoGeral(configuration.getCaminhoPretext1(), linhaArqAll);
	}

}