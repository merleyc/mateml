package br.usp.mateml.steps.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ptstemmer.Stemmer;
import ptstemmer.implementations.OrengoStemmer;
import br.usp.mateml.main.GlobalVariables;
import br.usp.mateml.steps.preprocessing.snowball.SpanishStemmer;
import br.usp.mateml.util.TestDetector;


public class WordProcessor {

	public HashMap<String, HashMap<String, Integer>> hashStemWdST = new HashMap<String, HashMap<String,Integer>>();
	public HashMap<String, Integer> hashStemFreq = new HashMap<String, Integer>();
	private HashMap<String, Integer> hashChaveFreq = new HashMap<String, Integer>();
	public int numDocs = 0;


	public ArrayList<AtrFreq> processarPalavras(File arq, String lang,
			boolean remStopWords, boolean radicalizar, HashMap<String,String> stemPal,
			ArrayList<String> names, HashMap<String,Integer> atrDF){

		ArrayList<AtrFreq> atributos = new ArrayList<AtrFreq>();

		//StopWords sw = new StopWords(lang); //Objeto para remoção das stopwords dos documentos
		Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalização em portugues
		StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em ingles
		int numDocsStem = 1;
		String textoAtual = "";

		StringBuffer txt = new StringBuffer();

		try{
			if (!arq.exists()) {
				System.out.println("O arquivo nao foi encontrado: " +arq.getAbsolutePath());
			}
			//ATENCAO: o arquivo deve estar no formato ISO8859-15 e deve-se remover os arquivo *.*~ da pasta de entrada.
			//RandomAccessFile arqTexto = new RandomAccessFile(arq, "r");
			//BufferedReader in = new BufferedReader(new FileReader(arq)); // para arquivos UTF-8
			String encoding = TestDetector.detectaEncodingImprimeArquivo(arq.toString());
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arq),encoding));
			else in = new BufferedReader(new FileReader(arq));

			String linha;

			//while( (linha = arqTexto.readLine()) != null ) {
			while( (linha = in.readLine()) != null ) {
				txt.append(linha + " ");
			} // Leitura do arquivo texto e armazenamento na variável txt

			//arqTexto.close();
			in.close();

		}catch(Exception e){
			System.out.println("Ocorreu um erro ao ler o arquivo " + arq.getAbsolutePath() + ".");
		}

		numDocs++;
		// OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
		String textoLimpo = Treatment.clean(txt.toString().toLowerCase()); //Limpeza do arquivo texto

		if(remStopWords==true){
			textoLimpo = GlobalVariables.stoplist.removeStopWords(textoLimpo); //Remoção de StopWords do arquivo texto
		}

		ArrayList<String> palavras = new ArrayList<String>();
		HashMap<String, Integer> hashAtrFreq = new HashMap<String, Integer>();

		String[] todas_palavras = textoLimpo.split(" "); //Armazena as palavras do texto em um vetor\
		if(radicalizar == true){
			if(lang.equals("port")){ //Radicalizando as palavras dos documentos
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];

					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(stemPt.wordStemming(chave));
						stemPal.put(chave, stem);
					}
					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}
					}

					/*    Modelo do arquivo 'stemWdST.all' :
						abat : 4(2/390)
				        	abate:2
				        	abatidas:2*/
					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					// obtendo a frequencia da chave (palavra original) no corpus:
					int freqChave = 1;
					if (hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
					/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
					textoAtual = textoAtual + " " + stem;
				}

			}else{
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(StemmerEn.get(chave));
						stemPal.put(chave, stem);
					}

					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}

					}

					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					int freqChave = 1;
					if(hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
					/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
					textoAtual = textoAtual + " " + stem;

				}
			}
		}else{
			for(int i = 0;i<todas_palavras.length;i++){
				String chave = todas_palavras[i];
				chave = chave.trim();
				if(!stemPal.containsKey(chave)){
					stemPal.put(chave, chave);
				}
				if(hashAtrFreq.containsKey(chave)){
					Integer freq = hashAtrFreq.get(chave);
					hashAtrFreq.put(chave, freq + 1);
				}else{
					if(chave.length()>1){
						hashAtrFreq.put(chave, 1);
						if(!names.contains(chave)){
							names.add(chave);
						}
						if(!palavras.contains(chave)){
							palavras.add(chave);
							if(atrDF.containsKey(chave)){
								int valor = atrDF.get(chave);
								valor++;
								atrDF.put(chave, valor);
							}else{
								atrDF.put(chave, 1);
							}
						}
					}
				}

				// gravar dados para o arquivo stemWdST.all
				HashMap<String, Integer> hashChaveFreq_atual = null;
				if (hashStemWdST.containsKey(chave))
					hashChaveFreq_atual = hashStemWdST.get(chave);
				else
					hashChaveFreq_atual = new HashMap<String, Integer>();
				int freqChave = 1;
				if(hashChaveFreq.containsKey(chave)){
					freqChave = hashChaveFreq.get(chave);
					freqChave += 1;
				}
				hashChaveFreq.put(chave, freqChave);
				hashChaveFreq_atual.put(chave, freqChave);
				hashStemWdST.put(chave, hashChaveFreq_atual);
				/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
				textoAtual = textoAtual + " " + chave;

			}
		}

		Set<String> listaAtr =  hashAtrFreq.keySet();
		Object[] listaAtributos = listaAtr.toArray();
		for(int i=0;i<listaAtributos.length;i++){
			String chave = listaAtributos[i].toString();
			atributos.add(new AtrFreq(chave,hashAtrFreq.get(chave)));
		}

		hashStemFreq = atrDF;
		Treatment.gravarArquivo2(arq, textoAtual);

		return atributos;
	}

	public ArrayList<AtrFreq> processarPalavrasEN_PT_ES(File arq, String lang,
			boolean remStopWords, boolean radicalizar, HashMap<String,String> stemPal,
			ArrayList<String> names, HashMap<String,Integer> atrDF, SpanishStemmer spanishStemmer){

		ArrayList<AtrFreq> atributos = new ArrayList<AtrFreq>();

		//StopWords sw = new StopWords(lang); //Objeto para remocao das stopwords dos documentos
		Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalizacao em portugues
		StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalizacao em ingles
		int numDocsStem = 1;
		String textoAtual = "";

		StringBuffer txt = new StringBuffer();

		try{
			if (!arq.exists()) {
				System.out.println("O arquivo nao foi encontrado: " +arq.getAbsolutePath());
			}
			else {
				//ATENCAO: o arquivo deve estar no formato ISO8859-15 e deve-se remover os arquivo *.*~ da pasta de entrada.
				//RandomAccessFile arqTexto = new RandomAccessFile(arq, "r");
				//BufferedReader in = new BufferedReader(new FileReader(arq)); // para arquivos UTF-8
				//RandomAccessFile in = new RandomAccessFile(arq, "r");

				String encoding = TestDetector.detectaEncodingImprimeArquivo(arq.toString());
				BufferedReader in;
				if (encoding != null) 
					in = new BufferedReader(new InputStreamReader(new FileInputStream(arq),encoding));
				else in = new BufferedReader(new FileReader(arq));

				String linha;

				//while( (linha = arqTexto.readLine()) != null ) {
				while( (linha = in.readLine()) != null ) {
					txt.append(linha + " ");
				} // Leitura do arquivo texto e armazenamento na variavel txt

				//arqTexto.close();
				in.close();
			}
		}catch(Exception e){
			System.out.println("Ocorreu um erro ao ler o arquivo " + arq.getAbsolutePath() + ".");
		}

		numDocs++;
		// OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
		String textoLimpo = Treatment.clean(txt.toString().toLowerCase()); //Limpeza do arquivo texto

		if(remStopWords==true){
			textoLimpo = GlobalVariables.stoplist.removeStopWords(textoLimpo); //Remocao de StopWords do arquivo texto
		}

		ArrayList<String> palavras = new ArrayList<String>();
		HashMap<String, Integer> hashAtrFreq = new HashMap<String, Integer>();

		String[] todas_palavras = textoLimpo.split(" "); //Armazena as palavras do texto em um vetor\
		if(radicalizar == true){
			if(lang.equals("port")){ //Radicalizando as palavras dos documentos
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];

					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(stemPt.wordStemming(chave));
						stemPal.put(chave, stem);
					}
					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}
					}

					/*    Modelo do arquivo 'stemWdST.all' :
					abat : 4(2/390)
			        	abate:2
			        	abatidas:2*/
					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					// obtendo a frequencia da chave (palavra original) no corpus:
					int freqChave = 1;
					if (hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
					/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
					textoAtual = textoAtual + " " + stem;

				}

			}else if(lang.equals("ingl")) {
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(StemmerEn.get(chave));
						stemPal.put(chave, stem);
					}

					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}

					}

					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					int freqChave = 1;
					if(hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
					/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
					textoAtual = textoAtual + " " + stem;

				}
			} else if(lang.equals("esp")) {
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(spanishStemmer.stemmizarPalavraES(chave));
						stemPal.put(chave, stem);
					}

					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}

					}

					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					int freqChave = 1;
					if(hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
					/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
					textoAtual = textoAtual + " " + stem;

				}
			}

		}

		else{ // se nao for radicalizar
			for(int i = 0;i<todas_palavras.length;i++){
				String chave = todas_palavras[i];
				chave = chave.trim();
				if(!stemPal.containsKey(chave)){
					stemPal.put(chave, chave);
				}
				if(hashAtrFreq.containsKey(chave)){
					Integer freq = hashAtrFreq.get(chave);
					hashAtrFreq.put(chave, freq + 1);
				}else{
					if(chave.length()>1){
						hashAtrFreq.put(chave, 1);
						if(!names.contains(chave)){
							names.add(chave);
						}
						if(!palavras.contains(chave)){
							palavras.add(chave);
							if(atrDF.containsKey(chave)){
								int valor = atrDF.get(chave);
								valor++;
								atrDF.put(chave, valor);
							}else{
								atrDF.put(chave, 1);
							}
						}
					}
				}

				// gravar dados para o arquivo stemWdST.all
				HashMap<String, Integer> hashChaveFreq_atual = null;
				if (hashStemWdST.containsKey(chave))
					hashChaveFreq_atual = hashStemWdST.get(chave);
				else
					hashChaveFreq_atual = new HashMap<String, Integer>();
				int freqChave = 1;
				if(hashChaveFreq.containsKey(chave)){
					freqChave = hashChaveFreq.get(chave);
					freqChave += 1;
				}
				hashChaveFreq.put(chave, freqChave);
				hashChaveFreq_atual.put(chave, freqChave);
				hashStemWdST.put(chave, hashChaveFreq_atual);
				/*	  Gravar o texto stemmizado (arquivo Maid da PreTexT): */
				textoAtual = textoAtual + " " + chave;

			}
		} //fim do nao radicalizar

		Set<String> listaAtr =  hashAtrFreq.keySet();
		Object[] listaAtributos = listaAtr.toArray();
		for(int i=0;i<listaAtributos.length;i++){
			String chave = listaAtributos[i].toString();
			atributos.add(new AtrFreq(chave,hashAtrFreq.get(chave)));
		}

		hashStemFreq = atrDF;
		Treatment.gravarArquivo2(arq, textoAtual);

		return atributos;
	}


	public void processarPalavrasEN_PT_ES_BigData(File arqIn, File arqOut, String lang,
			boolean remStopWords, boolean radicalizar, HashMap<String,String> stemPal,
			SpanishStemmer spanishStemmer) {


		//StopWords sw = new StopWords(lang); //Objeto para remocao das stopwords dos documentos
		String textoAtual = "";

		try{
			if (!arqIn.exists()) {
				System.out.println("O arquivo nao foi encontrado: " +arqIn.getAbsolutePath());
			}
			else {
				//ATENCAO: o arquivo deve estar no formato ISO8859-15 e deve-se remover os arquivo *.*~ da pasta de entrada.
				//RandomAccessFile arqTexto = new RandomAccessFile(arq, "r");
				//BufferedReader in = new BufferedReader(new FileReader(arq)); // para arquivos UTF-8
				//RandomAccessFile in = new RandomAccessFile(arq, "r");

				String encoding = TestDetector.detectaEncodingImprimeArquivo(arqIn.toString());
				BufferedReader in;
				if (encoding != null) 
					in = new BufferedReader(new InputStreamReader(new FileInputStream(arqIn),encoding));
				else in = new BufferedReader(new FileReader(arqIn));

				String linha;

				//while( (linha = arqTexto.readLine()) != null ) {
				while( (linha = in.readLine()) != null ) {

					if (!linha.trim().equals("")) {

						numDocs++;					
						if(radicalizar == true){
							textoAtual = radicalizar(linha, lang, remStopWords, stemPal, spanishStemmer);
						}

						if (!textoAtual.trim().equals(""))
							Treatment.gravarArquivoGeralApend(arqOut, textoAtual, encoding);

					}
				}

				//arqTexto.close();
				in.close();
			}
		}catch(Exception e){
			System.out.println("Ocorreu um erro ao ler o arquivo " + arqIn.getAbsolutePath() + ".");
		}

	}

	private String radicalizar(String linha, String lang, boolean remStopWords,
			HashMap<String,String> stemPal, SpanishStemmer spanishStemmer) {

		Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalizacao em portugues
		StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em ingles
		String textoAtual = "";

		try {

			// OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
			String textoLimpo = Treatment.clean(linha.toString().toLowerCase()); //Limpeza do arquivo texto

			if(remStopWords==true){
				textoLimpo = GlobalVariables.stoplist.removeStopWords(textoLimpo); //Remocao de StopWords do arquivo texto
			}

			ArrayList<String> palavras = new ArrayList<String>();

			String[] todas_palavras = textoLimpo.split(" "); //Armazena as palavras do texto em um vetor\
			//if(radicalizar == true){
			if (lang.equals("port")) { //Radicalizando as palavras dos documentos
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;

					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(stemPt.wordStemming(chave));
						stemPal.put(chave, stem);
					}					
					textoAtual = textoAtual + " " + stem;
				}

			} else if(lang.equals("ingl")) {
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;

					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(StemmerEn.get(chave));
						stemPal.put(chave, stem);
					}
					textoAtual = textoAtual + " " + stem;
				}

			} else if(lang.equals("esp")) {
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;

					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(spanishStemmer.stemmizarPalavraES(chave));
						stemPal.put(chave, stem);
					}
					textoAtual = textoAtual + " " + stem;
				}
			}
			//	}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return textoAtual;
	}


	/*
			public ArrayList<AtrFreq> processarPalavras(File arq, String lang,
			boolean remStopWords, boolean radicalizar, HashMap<String,String> stemPal, 
			ArrayList<String> names, HashMap<String,Integer> atrDF){

		ArrayList<AtrFreq> atributos = new ArrayList<AtrFreq>();

		StopWords sw = new StopWords(lang); //Objeto para remoção das stopwords dos documentos
		Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalização em português
		StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em inglês
		int numDocsStem = 1;
		String textoAtual = "";

		StringBuffer txt = new StringBuffer();

		try{
			if (!arq.exists()) {
				System.out.println("O arquivo não foi encontrado: " +arq.getAbsolutePath());
			}
			//ATENÇÃO: o arquivo deve estar no formato ISO8859-15 e deve-se remover os arquivo *.*~ da pasta de entrada.
			RandomAccessFile arqTexto = new RandomAccessFile(arq, "r");
			//BufferedReader in = new BufferedReader(new FileReader(arq));
			String linha;

			while( (linha = arqTexto.readLine()) != null ) {
			//while( (linha = in.readLine()) != null ) {
				txt.append(linha + " ");
			} // Leitura do arquivo texto e armazenamento na variável txt

			arqTexto.close();

		}catch(Exception e){
			System.out.println("Ocorreu um erro ao ler o arquivo " + arq.getAbsolutePath() + "." + e);
		}

		numDocs++;
		// OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
		String textoLimpo = trat.clean(txt.toString().toLowerCase()); //Limpeza do arquivo texto

		if(remStopWords==true){
			textoLimpo = sw.removeStopWords(textoLimpo); //Remoção de StopWords do arquivo texto
		}

		ArrayList<String> palavras = new ArrayList<String>();
		HashMap<String, Integer> hashAtrFreq = new HashMap<String, Integer>();

		String[] todas_palavras = textoLimpo.split(" "); //Armazena as palavras do texto em um vetor\
		if(radicalizar == true){
			if(lang.equals("port")){ //Radicalizando as palavras dos documentos
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(stemPt.wordStemming(chave));
						stemPal.put(chave, stem);
					}
					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}
					}

					    Modelo do arquivo 'stemWdST.all' :
					abat : 4(2/390)
			        	abate:2
			        	abatidas:2
					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					// obtendo a frequencia da chave (palavra original) no corpus:
					int freqChave = 1;
					if (hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
						  Gravar o texto stemmizado (arquivo Maid da PreTexT): 
					textoAtual = textoAtual + " " + stem;
				}

			}else{
				for(int i = 0;i<todas_palavras.length;i++){
					String chave = todas_palavras[i];
					chave = chave.trim();
					String stem;
					if(stemPal.containsKey(chave)){
						stem = stemPal.get(chave);
					}else{
						stem = new String(StemmerEn.get(chave));
						stemPal.put(chave, stem);
					}

					if(hashAtrFreq.containsKey(stem)){
						Integer freq = hashAtrFreq.get(stem);
						hashAtrFreq.put(stem, freq + 1);
					}else{
						if(stem.length()>1){
							hashAtrFreq.put(stem, 1);
							if(!names.contains(stem)){
								names.add(stem);
							}
							if(!palavras.contains(stem)){
								palavras.add(stem);
								if(atrDF.containsKey(stem)){
									int valor = atrDF.get(stem);
									valor++;
									atrDF.put(stem, valor);
								}else{
									atrDF.put(stem, 1);
								}
							}
						}

					}

					// gravar dados para o arquivo stemWdST.all
					HashMap<String, Integer> hashChaveFreq_atual = null;
					if (hashStemWdST.containsKey(stem))
						hashChaveFreq_atual = hashStemWdST.get(stem);
					else
						hashChaveFreq_atual = new HashMap<String, Integer>();
					int freqChave = 1;
					if(hashChaveFreq.containsKey(chave)){
						freqChave = hashChaveFreq.get(chave);
						freqChave += 1;
					}
					hashChaveFreq.put(chave, freqChave);
					hashChaveFreq_atual.put(chave, freqChave);
					hashStemWdST.put(stem, hashChaveFreq_atual);
						  Gravar o texto stemmizado (arquivo Maid da PreTexT): 
					textoAtual = textoAtual + " " + stem;

				}
			}
		}else{
			for(int i = 0;i<todas_palavras.length;i++){
				String chave = todas_palavras[i];
				chave = chave.trim();
				if(!stemPal.containsKey(chave)){
					stemPal.put(chave, chave);
				}
				if(hashAtrFreq.containsKey(chave)){
					Integer freq = hashAtrFreq.get(chave);
					hashAtrFreq.put(chave, freq + 1);
				}else{
					if(chave.length()>1){
						hashAtrFreq.put(chave, 1);
						if(!names.contains(chave)){
							names.add(chave);
						}
						if(!palavras.contains(chave)){
							palavras.add(chave);
							if(atrDF.containsKey(chave)){
								int valor = atrDF.get(chave);
								valor++;
								atrDF.put(chave, valor);
							}else{
								atrDF.put(chave, 1);
							}
						}
					}
				}

				// gravar dados para o arquivo stemWdST.all
				HashMap<String, Integer> hashChaveFreq_atual = null;
				if (hashStemWdST.containsKey(chave))
					hashChaveFreq_atual = hashStemWdST.get(chave);
				else
					hashChaveFreq_atual = new HashMap<String, Integer>();
				int freqChave = 1;
				if(hashChaveFreq.containsKey(chave)){
					freqChave = hashChaveFreq.get(chave);
					freqChave += 1;
				}
				hashChaveFreq.put(chave, freqChave);
				hashChaveFreq_atual.put(chave, freqChave);
				hashStemWdST.put(chave, hashChaveFreq_atual);
					  Gravar o texto stemmizado (arquivo Maid da PreTexT): 
				textoAtual = textoAtual + " " + chave;

			}
		}

		Set<String> listaAtr =  hashAtrFreq.keySet();
		Object[] listaAtributos = listaAtr.toArray();
		for(int i=0;i<listaAtributos.length;i++){
			String chave = listaAtributos[i].toString();
			atributos.add(new AtrFreq(chave,hashAtrFreq.get(chave)));
		}

		hashStemFreq = atrDF;
		trat.gravarArquivo2(arq, textoAtual);

		return atributos;
	}
	 */
}