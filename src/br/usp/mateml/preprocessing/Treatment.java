package br.usp.mateml.preprocessing;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.StringTokenizer;

import br.usp.mateml.main.GlobalVariables;
import br.usp.mateml.util.TestDetector;

/**
 *
 * @author Merley da Silva Conrado
 */
public class Treatment {

	public static HashMap<String, HashMap<String, Boolean> > hashLog = new HashMap<String, HashMap<String, Boolean>>(); // na cabeça tem a palavra tratada e na calda tem a palavra original.

	/**
	 * Este metodo verifica se uma string eh um numero.
	 * @param string a ser verificada.
	 * @return false se nao eh numero e true se eh numero.
	 */
	public static boolean ehNumero(String termo){
		boolean ehNumero = false;
		char caractere;
		int tam = termo.length();

		for (int i=0; i<tam; i++) {
			caractere = termo.charAt(i);
			if (caractere == '1' || caractere == '2' || caractere == '3' || caractere == '4' || caractere == '5' || caractere == '6'
					|| caractere == '7' || caractere == '8' || caractere == '9' || caractere == '0')
				ehNumero = true;
			else {
				ehNumero = false;
				break;
			}
		}
		return ehNumero;

	}

	/**
	 * 	Este mï¿½todo substitui todos os caracteres "c" por "c2" na string "s".
	 * @param c caracter a ser substituï¿½do.
	 * @param c2 novo caracter.
	 * @param s string a ser substituï¿½da.
	 * @return nova string apï¿½s substituiï¿½ï¿½o.
	 */
	public static String replaceAll (char c, char c2, String s){
		String novaString = "";
		for (int i=0;i<s.length();i++){
			if (s.charAt(i)==c) novaString+=c2;
			else novaString+=s.charAt(i);
		}
		return novaString;
	}

	/**
	 * Este mï¿½todo retira a pontuaï¿½ï¿½o de textos na lï¿½ngua portuguesa, exceto: _ < >.
	 * @param palavra ï¿½ a palavra do texto que serï¿½ percorrida para remoï¿½ï¿½o de pontuaï¿½ï¿½o.
	 * @return palavra sem pontuaï¿½ï¿½o.
	 */
	public static String removerPontuacao (String palavra){
		palavra = palavra.trim();
		int tam = palavra.length();
		String palavra_nova="";
		for (int y=0; y<tam; y++){
			if (palavra.charAt(y)==('.') || palavra.charAt(y)==(',') || palavra.charAt(y)==(';') || 
					palavra.charAt(y)==(':') || palavra.charAt(y)==('-') || palavra.charAt(y)==('"') ||
					palavra.charAt(y)==('`') || palavra.charAt(y)==('`') || palavra.charAt(y)==('\'') || 
					palavra.charAt(y)==('(') || palavra.charAt(y)==('\\') || palavra.charAt(y)==('/') ||
					palavra.charAt(y)==(')') || palavra.charAt(y)==('[') || palavra.charAt(y)==(']') ||
					palavra.charAt(y)==('{') || palavra.charAt(y)==('}') ||	palavra.charAt(y)==('|') ||
					palavra.charAt(y)==('?') ||	palavra.charAt(y)==('!') || palavra.charAt(y)==('%') ||
					palavra.charAt(y)==('#') ||	palavra.charAt(y)==('$') || palavra.charAt(y)==('&') ||
					palavra.charAt(y)==('_') ||	palavra.charAt(y)==('§') || palavra.charAt(y)==('*') ||
					palavra.charAt(y)==('¬') ||	palavra.charAt(y)==('ª') || palavra.charAt(y)==('º') ||
					palavra.charAt(y)==('¢') ||	palavra.charAt(y)==('£'))
				palavra_nova = palavra_nova;
			else {
				palavra_nova = palavra_nova+palavra.charAt(y);
			}    	  
		}
		return palavra_nova;
	}

	/**
	 * Este mï¿½todo retira acento de textos na lï¿½ngua portuguesa.
	 * @param palavra ï¿½ a palavra do texto que serï¿½ percorrida para remoï¿½ï¿½o de acentos.
	 * @return palavra sem acento.
	 */
	public static String removerAcentos (String palavra){
		palavra = palavra.trim();
		int tam = palavra.length();
		String palavra_nova="";
		for (int y=0; y<tam; y++){
			if (palavra.charAt(y)==('à') || palavra.charAt(y)==('ã') || palavra.charAt(y)==('á') || 
					palavra.charAt(y)==('ä') || palavra.charAt(y)==('â')) palavra_nova =palavra_nova+'a';
			else if (palavra.charAt(y)==('À') || palavra.charAt(y)==('Ã') || palavra.charAt(y)==('Á') || 
					palavra.charAt(y)==('Ä') || palavra.charAt(y)==('Â')) palavra_nova =palavra_nova+'A';
			else if (palavra.charAt(y)==('é') || palavra.charAt(y)==('è') ||
					palavra.charAt(y)==('ê') || palavra.charAt(y)==('ë')) palavra_nova =palavra_nova+'e';
			else if (palavra.charAt(y)==('É') || palavra.charAt(y)==('È') ||
					palavra.charAt(y)==('Ê') || palavra.charAt(y)==('Ë')) palavra_nova =palavra_nova+'E';
			else if (palavra.charAt(y)==('í') || palavra.charAt(y)==('ì') ||
					palavra.charAt(y)==('î') || palavra.charAt(y)==('ï')) palavra_nova =palavra_nova+'i';
			else if (palavra.charAt(y)==('Í') || palavra.charAt(y)==('Ì') ||
					palavra.charAt(y)==('Î') || palavra.charAt(y)==('Ï')) palavra_nova =palavra_nova+'I';
			else if (palavra.charAt(y)==('ó') || palavra.charAt(y)==('ò') || palavra.charAt(y)==('õ') ||
					palavra.charAt(y)==('ô') || palavra.charAt(y)==('ö')) palavra_nova =palavra_nova+'o';
			else if (palavra.charAt(y)==('Ó') || palavra.charAt(y)==('Ò') || palavra.charAt(y)==('Õ') ||
					palavra.charAt(y)==('Ô') || palavra.charAt(y)==('Ö')) palavra_nova =palavra_nova+'O';
			else if (palavra.charAt(y)==('ú') || palavra.charAt(y)==('ù') ||
					palavra.charAt(y)==('û') || palavra.charAt(y)==('ü')) palavra_nova =palavra_nova+'u';
			else if (palavra.charAt(y)==('Ú') || palavra.charAt(y)==('Ù') ||
					palavra.charAt(y)==('Û') || palavra.charAt(y)==('Ü')) palavra_nova =palavra_nova+'U';
			else if (palavra.charAt(y)==('ñ')) palavra_nova =palavra_nova+'n';
			else if (palavra.charAt(y)==('Ñ')) palavra_nova =palavra_nova+'N';
			else if (palavra.charAt(y)==('ç')) palavra_nova =palavra_nova+'c';
			else if (palavra.charAt(y)==('Ç')) palavra_nova =palavra_nova+'C';
			// remove barra para nÃ£o dar erro na execuÃ§Ã£o da NSP:
			else if (palavra.charAt(y)==('/')) palavra_nova =palavra_nova;
			else if (palavra.charAt(y)==(':')) palavra_nova =palavra_nova;			
			else {
				palavra_nova = palavra_nova+palavra.charAt(y);
			}    	  
		}
		return palavra_nova;
	}


	/**
	 * Este mï¿½todo verifica se jï¿½ existe o arquivo no diretï¿½rio (caminho) passado por
	 * parï¿½metro, caso Nï¿½O exista, cria-o.
	 * @param caminho ï¿½ o arquivo no diretï¿½rio a ser criado.
	 * @throws IOException 
	 */
	public static void criarArquivo (File arquivo) throws IOException{
		try{
			if (!arquivo.exists())	
				arquivo.createNewFile();
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Nao foi possivel criar o arquivo: " + arquivo.toString());
		}
	}


	public static void criarDiretorio (String caminhoSaida) {
		File dir = new File(caminhoSaida);  
		if (!dir.exists() && !dir.mkdir())
			System.out.println("Nao foi possivel criar o diretorio: " + caminhoSaida);  
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	/**
	 * Entra com um candidato a termo (ct) e remove palavras que sï¿½o formadas totalmente
	por nï¿½meros ou palavras formadas por apenas 1 caractere alï¿½m de
	outros tratamentos especificados abaixo.
	Caso sobre alguma(s) palavra(s) na string, retorna-a,
	senï¿½o, retorna a string vazia.
	 * @param ct candidato a termo a ser tratado.
	 * @return a string tratada.
	 */
	public static String tratar_termo(String ct, boolean removerStopwords) {
		String resultado = "";

		ct = ct.trim().toLowerCase().replaceAll(" ", "<>");
		ct = Treatment.removerAcentos(ct); // remove os acentos do termo
		ct = Treatment.removerPontuacao(ct); // remove as pontuaï¿½ï¿½es do termo, exceto: _ < >
		ct = ct.replaceAll("_", "<>");

		if (ct.contains("<>")) {
			StringTokenizer st = new StringTokenizer(ct, "<>");

			while (st.hasMoreTokens()) {
				String tokens = st.nextToken();
				if (tokens.isEmpty()) continue;
				if (!Treatment.ehNumero(tokens) && tokens.trim().length()>1)
					resultado += tokens + "<>";
				//TODO confirmar o que fazer nesse caso: publicacao jornalistica DE A sociedade brasileira de computacao
			}
		}
		else {
			if (!Treatment.ehNumero(ct) && ct.trim().length()>1)
				resultado += ct;
		}

		if (removerStopwords) {
			resultado = GlobalVariables.stoplist.removerStopwordDoInicio(resultado); // verifica se inicia (1º palavra) com stopword e, caso afirmativo, remove-a
			resultado = GlobalVariables.stoplist.removerStopwordDoFinal(resultado); // verifica o termo termina com stopword (ultima palavra) e, caso afirmativo, remove-a

			if (GlobalVariables.stoplist.isStopword(resultado))
				resultado = "";
		}
		if (temCaracterEstranho(resultado))
			resultado = "";

		return resultado;	
	}

	/**
	 * Esse metodo recebe uma String qualquer e retorna verdadeiro se a mesma contï¿½m qualquer caracter estranho.
	 * @param grama String a ser testada.
	 * @return verdadeiro se a string testada nao contem caracter estranho e false se contem.
	 */
	public static boolean temCaracterEstranho(String grama) {

		String allowed="abcdefghijklmnopqrstuvwxyz\n";
		grama = grama.toLowerCase();

		for (int i=0; i < grama.length(); i++) {
			String ch = String.valueOf(grama.charAt(i));
			if (!allowed.contains(ch)) 
				return true;
		}
		return false; // a string nï¿½o contï¿½m caracter estranho.
	}


	public static void filtrarARFF(String caminhoEntrada, String caminhoSaida) {
		try {
			//Abrindo e testando o arquivo de entrada:
			File arqEntrada = new File(caminhoEntrada);
			if (arqEntrada.exists() == false) {
				System.out.println("Nao encontrou o arquivo de entrada da ARFF: " + caminhoEntrada);
				return;
			}
			//BufferedReader in = new BufferedReader(new FileReader(caminhoEntrada));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoEntrada);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoEntrada),encoding));
			else in = new BufferedReader(new FileReader(caminhoEntrada));


			//Criando o arquivo de saida:
			File arqSaida = new File (caminhoSaida);
			criarArquivo(arqSaida);
			FileWriter writer = new FileWriter(arqSaida);
			PrintWriter out = new PrintWriter(writer);

			//Lendo os dados iniciais do arquivo ARFF:
			String line = in.readLine();
			while (line != null && !line.equals("@data")) {
				out.println(line); //Salvando a linha atual no arquivo de saida.
				line = in.readLine();
				int num_termo = 0;	}
			out.println(line); //Salvando a linha que contem "@data" no arquivo de saida.
			line = in.readLine();

			//Lendo e filtrando os demais dados do arquivo ARFF:
			while (line != null) {
				if (!line.equals("")) {
					//Filtrando os dados:
					if (manterLinha(line))
						out.println(line);
					else System.out.println(line);
				}
				line = in.readLine();
			}				

			//Fechando arquivos:
			in.close();
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Esse metodo verifica se a linah do arquivo ARFF deve ser mantida ou nao de acordo com regras.
	 * @param line eh a linha atual do arquivo ARFF
	 * @return Verdadeiro se essa linha deve ser mantida, caso contrÃ¡rio, retorna Falso. 
	 */
	private static boolean manterLinha(String line) {
		StringTokenizer st = new StringTokenizer(line,",");
		String str_df = "0", str_sn = "0", str_pm = "0";
		String teste;
		//Considerando que o DF estah na quinta posicao do arquivo ARFF, o Sintagma na 15th e o PM na 17th posicao:
		for (int i=1; st.hasMoreTokens() && i<=17; i++) {
			if (i == 5) str_df = st.nextToken();
			else if (i == 15) str_sn = st.nextToken();
			else if (i == 17) str_pm = st.nextToken();
			else teste = st.nextToken();
		}

		//Convertendo para inteiros:
		int df = Integer.parseInt(str_df);
		int sn = Integer.parseInt(str_sn);
		int pm = Integer.parseInt(str_pm);

		//filtrando os exemplos (linhas):
		//if (df < 2 || (sn == 0 && pm == 0)) {
		if (df < 2) {
			return false;
		}
		return true;
	}

	public static int contarTermosConsiderados(String caminhoEntrada) {
		int num_termo = 0;
		try {
			//Abrindo e testando o arquivo de entrada:
			File arqEntrada = new File(caminhoEntrada);
			if (arqEntrada.exists() == false) {
				System.out.println("Nao encontrou o arquivo de entrada da ARFF: " + caminhoEntrada);
				return -1;
			}
			//BufferedReader in = new BufferedReader(new FileReader(caminhoEntrada));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoEntrada);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoEntrada),encoding));
			else in = new BufferedReader(new FileReader(caminhoEntrada));


			//Lendo os dados iniciais do arquivo ARFF:
			String line = in.readLine();
			while (line != null && !line.equals("@data"))
				line = in.readLine();
			line = in.readLine();

			//Lendo e filtrando os demais dados do arquivo ARFF:
			while (line != null) {
				if (!line.equals("")) {
					//Contando quantidade de termos verdadeiros:
					num_termo += ehConsideradoTermo(line);
				}
				line = in.readLine();
			}				

			//Fechando arquivo:
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return num_termo;
	}

	private static int ehConsideradoTermo(String line) {
		StringTokenizer st = new StringTokenizer(line,",");
		String str_termo = "0";		
		int termo = 0;
		String teste;

		//Considerando que o LR estah na 22th posicao do arquivo ARFF:
		for (int i=1; st.hasMoreTokens() && i<=22; i++) {
			if (i == 22) {
				str_termo = st.nextToken();
				termo = Integer.parseInt(str_termo);
				if (termo == 1)
					System.out.println(line);
			}
			else teste = st.nextToken();
		}
		return termo;
	}

	/**
	 * Esse metodo verifica se o CT *unigrama* contem na lista de termos de referencia do dominio.
	 * @param hashLR eh um hash contendo as os termos de referencia *stemizados*.
	 */
	public static void verificaTermosNaLR(String caminhoEntrada, HashMap<String, Boolean> hashLR) {
		String ct = "", ctNaoLR = "", str_lr = "";
		int num_ctNaoLR = 0, lr = 0;
		try {
			//Abrindo e testando o arquivo de entrada e hash com termos de referencia (termos verdadeiros):
			File arqEntrada = new File(caminhoEntrada);
			if (arqEntrada.exists() == false) {
				System.out.println("Nao encontrou o arquivo de entrada da ARFF: " + caminhoEntrada);
				return;
			}
			if (hashLR.isEmpty()) {
				System.out.println("O hash com os termos de referencia estah vazio");
				return;
			}

			//BufferedReader in = new BufferedReader(new FileReader(caminhoEntrada));
			String encoding = TestDetector.detectaEncodingImprimeArquivo(caminhoEntrada);
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoEntrada),encoding));
			else in = new BufferedReader(new FileReader(caminhoEntrada));


			//Lendo os dados iniciais do arquivo ARFF:
			String line = in.readLine();
			while (line != null && !line.equals("@data"))
				line = in.readLine();
			line = in.readLine();

			//Lendo e filtrando os demais dados do arquivo ARFF:
			while (line != null) {
				if (!line.equals("")) {

					//Considerando que o candidato a termo estÃ¡ na 1Âª posiÃ§Ã£o do arquivo ARFF e a LR na 22Âª posiÃ§Ã£o:
					StringTokenizer st = new StringTokenizer(line,",");
					for (int i=1; st.hasMoreTokens() && i<=22; i++) {
						if (i == 1) ct = st.nextToken();
						if (i == 22) {
							str_lr = st.nextToken();
							lr = Integer.parseInt(str_lr);
						}
						else st.nextToken();
					}

					if (!hashLR.containsKey(ct) && lr == 1) {				
						ctNaoLR += "\n" + ct;
						num_ctNaoLR++;
					}

				}
				line = in.readLine();
			}

			System.out.println("Os seguintes " + num_ctNaoLR + " candidatos foram considerados termos mas nao estao na LR:" + ctNaoLR);

			//Fechando arquivo:
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Recebe string qualquer e retorna uma String sem os pronomes.
	 * Por exemplo, para "tornam-se", retorna "tornam".
	 * Outro exemplo, para "deber-se-iam", retorna "deberiam".
	 */
	public static  String cleanPronomes (String str){
		String newStr = str;

		if (str.contains("-")) {
			StringTokenizer st = new StringTokenizer(str.trim(), "-");
			if (st.hasMoreTokens()) {
				String primeiraParte = st.nextToken();
				String segundaParte = "",
						terceiraParte = "";
				boolean segundaParteEhPronome = false,
						terceiraParteEhPronome = false;

				for (int i=0; st.hasMoreTokens(); i++) {
					if (i == 0) {
						segundaParte = st.nextToken();
						segundaParte = segundaParte.trim();
						if (segundaParte.equals("me") || segundaParte.equals("te") || 
								segundaParte.equals("se") || segundaParte.equals("o") || 
								segundaParte.equals("a") || segundaParte.equals("lhe") || 
								segundaParte.equals("nos") || segundaParte.equals("vos") ||
								segundaParte.equals("os") || segundaParte.equals("as") ||
								segundaParte.equals("lhes") || segundaParte.equals("la") ||
								segundaParte.equals("las") || segundaParte.equals("lo") ||
								segundaParte.equals("los") || segundaParte.equals("no") || 
								segundaParte.equals("nos")  || segundaParte.equals("na") || 
								segundaParte.equals("nas")  ) {
							segundaParteEhPronome = true;
						}
					}
					else if (i == 1) {
						terceiraParte = st.nextToken();
						terceiraParte = terceiraParte.trim();
						if (terceiraParte.equals("me") || terceiraParte.equals("te") || 
								terceiraParte.equals("se") || terceiraParte.equals("o") || 
								terceiraParte.equals("a") || terceiraParte.equals("lhe") || 
								terceiraParte.equals("nos") || terceiraParte.equals("vos") ||
								terceiraParte.equals("os") || terceiraParte.equals("as") ||
								terceiraParte.equals("lhes")) {
							terceiraParteEhPronome = true;
						}
					}
					else {
						st.nextToken();
						if (str.equals("palmeira-de-pescoço-marrom")) {
							str = str+"";
						}
						System.out.println("Classe Tratamento - Metodo cleanString: " + str);

					}
				}

				if (!segundaParteEhPronome && !terceiraParteEhPronome)
					newStr = str;
				else if (segundaParteEhPronome)
					newStr = primeiraParte + terceiraParte;
			}
		}

		return newStr;
	}


	/** Recebe um conjunto de strings separadas por espco e retorna uma String apenas com caracteres permitidos.
	 * 
	 * Modificado por Merley da Silva Conrado. */
	public static String clean(String linha){
		//String allowed="abcdefghijklmnopqrstuvwxyz\n";

		String allowed ="ãàáäâaÀÃÁÄÂAbBçcÇCdDéèêëeÉÈÊËEfFgGhHíìîïiÍÌÎÏIjJkKlLmMnNóòõôöoÓÒÕÔÖOpPqQrRsStTúùûüuÚÙÛÜUvVxXwWyYzZ\n";
		StringBuffer new_str = new StringBuffer("");
		String palavraOriginal = "",
				new_line = "";
		boolean modificouString = false;
		linha = linha.toLowerCase();

		StringTokenizer st = new StringTokenizer(linha," ");
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			palavraOriginal = str;

			/* OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
			strAux = Util.removerAcentos(palavra);
			strAux = Util.removerPontuacao(strAux);
			 */

			str = cleanPronomes(str);
			if (!str.trim().equals(palavraOriginal.trim()))
				modificouString = true;

			for (int i=0; i < str.length(); i++) {
				String ch = String.valueOf(str.charAt(i));
				if (allowed.contains(ch)) { //char allowed?  
					new_str=new_str.append(ch);
				}
				else {
					modificouString = true;
					if (ch.equals("-"))
						new_str=new_str.append("");
					else 
						new_str=new_str.append(" ");
				}
			}

			// gravar o arquivo de log que contem <palavraLimpa>: <lista de palavras originais>
			if (modificouString) {
				
				String strAux = new_str.toString().trim();
				gravarHashLog(strAux, palavraOriginal);
			}
			
			modificouString = false;
			new_line += " " + new_str;
			new_str = new StringBuffer("");
		}

		return new_line.toString().trim();

	}

/**
 * Esse metodo grava o hash de log que contem <palavraLimpa>: <lista de palavras originais>
 * @param strAux
 * @param palavraOriginal
 */
	private static void gravarHashLog(String strAux, String palavraOriginal) {
		HashMap<String, Boolean> hashLog_palOrig = new HashMap<String, Boolean>();
		
		if (hashLog.containsKey(strAux)) {
			hashLog_palOrig = hashLog.get(strAux);
			if (!hashLog_palOrig.containsKey(palavraOriginal)) {
				hashLog_palOrig.put(palavraOriginal, true);
				hashLog.put(strAux, hashLog_palOrig);
			}
		}
		else {
			if (!hashLog_palOrig.containsKey(palavraOriginal))
				hashLog_palOrig.put(palavraOriginal, true);
			hashLog.put(strAux, hashLog_palOrig);
		}
		
	}

	/** Recebe um conjunto de strings separadas por espco e retorna uma String apenas com caracteres permitidos.
	 * 
	 * Modificado por Merley da Silva Conrado. */
	public static String clean_antigo(String str){
		//String allowed="abcdefghijklmnopqrstuvwxyz\n";

		String allowed="-ãáäâaÀÃÁÄÂAbBçcÇCdDéèêëeÉÈÊËEfFgGhHíìîïiÍÌÎÏIjJkKlLmMnNóòõôöoÓÒÕÔÖOpPqQrRsStTúùûüuÚÙÛÜUvVxXwWyYzZ\n";
		StringBuffer new_str= new StringBuffer("");
		String palavraOriginal = "";
		boolean modificouString = false;
		str=str.toLowerCase();
		/*
		String strFinal = "", strAux = "";
		StringTokenizer st = new StringTokenizer(str," ");
		while (st.hasMoreTokens()) {
			String palavra = st.nextToken();
		 */			/* OS RESULTADOS DOS STEM FICAM MELHORES SEM REMOVER ACENTOS DAS PALAVRAS!!!
			strAux = Util.removerAcentos(palavra);
			strAux = Util.removerPontuacao(strAux);*/
		/*			strAux = Util.removerPontuacao(palavra);
			strFinal += " " + strAux.toLowerCase();
		}
		if (strFinal.contains("?"))
			System.out.println(strFinal + "Tem '?'.");
		return strFinal;

		 */	
		for(int i=0; i < str.length(); i++){
			String ch = String.valueOf(str.charAt(i));
			if(allowed.contains(ch)){ //char allowed?  
				new_str=new_str.append(ch);
				palavraOriginal += ch;
			}else{
				//TODO poderia remover o - se apos ele aparecesse algum pronome. Assim, limparia os verbos com -<pronome>.
				if (ch.equals("-")) {
					new_str=new_str.append("");
					modificouString = true;
					palavraOriginal += ch;
				}
				else {
					new_str=new_str.append(" ");
					if (modificouString && !palavraOriginal.trim().equals("-") && palavraOriginal.trim().length()>1) {
						String palavraLimpa = pegarUltimaPalavra(new_str.toString());
						gravarHashLog(palavraLimpa,palavraOriginal);

					}
					modificouString = false;
					palavraOriginal = "";
				}
			}
		}
		String new_str2 = new_str.toString();
		for(int i=0; i < 15; i++) new_str2=new_str2.replace("  ", " ");

		if (new_str2.contains("?"))
			System.out.println(new_str2 + "Tem '?'.");

		return new_str2.trim();

	}

	/* Considerando que as palavras sao separadas por espaço, esse metodo retorna a ultima palavra da linha/frase.
	 * Por exemplo, dado a linha do arquivo "uma populaçao compreende os individuos de uma especie dentro de uma dada area",
	 * o metodo retornarah "area".
	 * */
	private static String pegarUltimaPalavra(String linha) {
		StringTokenizer st = new StringTokenizer(linha.trim(), " ");
		String next = "";

		if (st.hasMoreTokens()) {
			do {
				next = "";
				next = st.nextToken();
			} while (st.hasMoreTokens());
		}
		return next;
	}

	public static boolean gravarArquivoGeral(String arqSaida, String data) {
		try {
			File f = new File(arqSaida);
			String path = f.getParent();
			Treatment.criarDiretorio(path);
			Treatment.criarArquivo(f);
			//BufferedReader in = new BufferedReader(new FileReader(arq)); // para arquivos UTF-8
			/*FileWriter writer = new FileWriter(f);
			PrintWriter out = new PrintWriter(writer);*/

			Writer writer = new OutputStreamWriter(
					new FileOutputStream(arqSaida), "UTF-8");
			BufferedWriter fout = new BufferedWriter(writer);
			fout.write(data);
			fout.close();
			/*			out.print(data);

			out.flush();
			out.close();*/

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static boolean gravarArquivoGeralApend(File arqSaida, String data, String encoding) {
		try {
			
			String path = arqSaida.getParent();
			Treatment.criarDiretorio(path);
			Treatment.criarArquivo(arqSaida);
			//BufferedReader in = new BufferedReader(new FileReader(arq)); // para arquivos UTF-8
			/*FileWriter writer = new FileWriter(f);
			PrintWriter out = new PrintWriter(writer);*/

			Writer writer = new OutputStreamWriter(
					new FileOutputStream(arqSaida, true), encoding);
			BufferedWriter fout = new BufferedWriter(writer);
			fout.write(data);
			fout.newLine();
			fout.close();
	            
			/*			out.print(data);

			out.flush();
			out.close();*/

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	public static boolean gravarArquivo2(File arq, String data) {
		try {
			// obtendo nome do arquivo:
			int iBarra = 0;
			String caminhoArq = arq.toString();
			if (caminhoArq.lastIndexOf("/") > 0)
				iBarra = caminhoArq.lastIndexOf("/");
			else if (caminhoArq.lastIndexOf("\\") > 0)
				iBarra = caminhoArq.lastIndexOf("\\");
			String nomeArqSaida = caminhoArq.substring(iBarra+1, caminhoArq.length());

			String caminhoSaida = GlobalVariables.configuration.getCaminhoPretextMaid();
			criarDiretorio(caminhoSaida);

			File arqSaida = new File (caminhoSaida + "/" + nomeArqSaida);
			criarArquivo(arqSaida);

			FileWriter writer = new FileWriter(arqSaida);
			PrintWriter out = new PrintWriter(writer);
			out.print(data);

			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static String getExtension(File f)
	{
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
			ext = s.substring(i+1).toLowerCase();

		return ext;
	}

}