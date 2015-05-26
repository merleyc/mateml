package br.usp.mateml.loaders;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import br.usp.mateml.main.GlobalVariables;
import br.usp.mateml.steps.feature_extraction.ReferenceList;
import br.usp.mateml.steps.preprocessing.Treatment;
import br.usp.mateml.util.TestDetector;

public class StoplistLoader {

	private final HashMap<String, Boolean> hash_stoplist = new HashMap<String, Boolean>();

	public StoplistLoader(String pathStoplistFile, ReferenceList referenceList) {
		loadStopwords(pathStoplistFile, referenceList);
	}

	/**
	 * Este metodo armaze as stopwords no hash listaStoplist.
	 * @param pathReferenceList 
	 * @param caminho do arquivo .txt no formato da PreTexT contendo as stopwords.
	 */
	private void loadStopwords(String pathStoplistFile, ReferenceList referenceList) {		
		File arqStoplist = new File(pathStoplistFile);
		boolean armazenouStopword = false;
		
		try {
			if (arqStoplist.exists() == false) {
				System.out.println("Nao encontrou o arquivo de stopword: " + pathStoplistFile);
				return;
			}
			String encoding = TestDetector.detectaEncodingImprimeArquivo(pathStoplistFile);
			BufferedReader in;
			if (encoding != null) {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arqStoplist),encoding));
			} else {
				in = new BufferedReader(new FileReader(arqStoplist));
			}

			//BufferedReader in = new BufferedReader(new FileReader(arqStoplist));
			String line = in.readLine();

			while (line != null) {
				line = Treatment.tratar_termo(line, false);
				if ( GlobalVariables.referenceList == null || !GlobalVariables.referenceList.containsKey(line) ) {
					hash_stoplist.put(line, true);
				}
				armazenouStopword = true;
				line = in.readLine();
			}
			in.close();

			if (armazenouStopword)
				System.out.println("Stopwords armazenadas.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isStoplistEmpty() {
		if (hash_stoplist.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Este metodo verifica se a string eh uma stopword a partir de uma stoplist dada previamente.
	 * @param string a ser verificada.
	 * @return true se a string eh uma stopword.
	 */
	public boolean isStopword(String str){
		if (str != null && hash_stoplist.containsKey(str)) {// && (ReferenceList.hash_listaReferencia == null || !ReferenceList.hash_listaReferencia.containsKey(str))){
			return true;
		}
		return false;
	}
	
	/**
	 * Este m�todo remove a(s) stopword(s) do in�cio do candidato a termo (ct).
	 * @param sintagma
	 * @return
	 */
	public String removerStopwordDoInicio(String sintagma) {
		StringTokenizer st = new StringTokenizer(sintagma.trim(), "<>");
		String restante="";
		int gramas = 0;
		boolean iniciocomstopword = true,
				jaharmazenou = false;
		while (st.hasMoreTokens()) {
			gramas++;
			String next = st.nextToken();
			if (!next.equals("")) {
				iniciocomstopword = iniciocomstopword && isStopword(next);
				if (!iniciocomstopword) {
					if (gramas > 1 && jaharmazenou) {
						restante += "<>" + next;
					}
					else {
						restante += next;
						jaharmazenou = true;
					}
				}
			}
		}

		return restante.trim();
	}

	public String removerStopwordDoFinal(String sintagma) {
		String restante = "";
		String [] palavras = sintagma.split("<>");
		int tam = palavras.length,
				gramas = 0;
		boolean finalizar = false,
				jaharmazenou = false;

		for (int i=tam-1; i>=0 && !finalizar; i--) {
			if (!isStopword(palavras[i])) {
				finalizar = true;
				for (int j=0; j<=i; j++) {
					gramas++;
					if (gramas > 1 && jaharmazenou)
						restante += "<>" + palavras[j];
					else {
						restante += palavras[j];
						jaharmazenou = true;
					}
				}

			}
		}

		return restante.trim();
	}
	
	public String removeStopWords(String str){
		String[] terms = str.split(" ");
		String new_str = "";

		for(int i=0; i < terms.length; i++){
			String termo = terms[i];

			if(termo.startsWith("\n")){
				new_str = new_str.concat("\n");
			}
			termo = termo.trim();
			boolean quebra = false;
			if(termo.contains("\n")){
				quebra = true;
			}
			String[] terms2 = termo.split("\n");
			for(int j=0; j < terms2.length; j++){
				String termo2 = terms2[j].trim();
				if(j == terms2.length - 1){
					quebra = false;
				}

				String termoAux = Treatment.tratar_termo(termo2, false);

				if(!isStopword(termoAux)){
					if(termo2.equals(".")){
						new_str=new_str.concat(" . ");
						if(quebra == true){new_str=new_str.concat("\n");}
						continue;
					}
					if(!(termo2.length()<=2)){
						new_str=new_str.concat(termo2+" ");
						if(quebra == true){new_str=new_str.concat("\n");}
						continue;
					}   

				}else{
					new_str = new_str + " @ ";
					if(quebra == true){new_str=new_str.concat("\n");}
				}    
			}
		}
		return  new_str.trim();
	}
}