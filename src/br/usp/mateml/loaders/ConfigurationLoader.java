package br.usp.mateml.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class ConfigurationLoader {
	private String caminhoCorpus = "";
	private String caminhoPretext1 = "";
	private String caminhoPretext2 = "";
	private String caminhoPretext3 = "";
	private String caminhoPretextData = "";
	private String caminhoPretextName = "";
	private String caminhoPretextStem = "";
	private String caminhoPretextMaid = "";
	private String caminhoStoplist = "";
	private String caminhoCorpusGeral = "";
	private String caminhoExpressoesIndicativas = "";
	private int janelaNCvalue = -1;
	private String caminhoParserPalavras = "";
	private String caminhoListaReferencia = "";
	private String caminhoXmlTaxonomia = "";
	private String caminhoSaida = "";
	private String caminhoParametros = "./data/";

	private boolean criarArqsPretext = false;
	private String language = ""; // options ingl, port or esp
	private boolean stemmer = false;
	private boolean stopword = false;
	private String caminhoStoplistIngl = "";
	private String caminhoStoplistPort = "";
	private String caminhoStoplistEsp= "";
	private boolean usarDiretorioComoClasse = false; // utilizar diretorio da pasta de entrada como classe
	private boolean FreqTrueBinarioFalse = false;

	// temporary files for each domain:
	public final String arqDataSemClasse = "./data/discover_sem_classe.data";
	public final String arqMatrizEsparsa = "./data/discoveresparsaAtribDoc1Gram.txt";
	public final String arqTfidf = "./data/saidaTfIdf.txt";

	// constructor
	public ConfigurationLoader(String parameters) {
		try {
			loadCfg(parameters);
		} catch (IOException e) {
			throw new RuntimeException("Error on reading file on path: " + parameters);
		}
	}

	private File getCfgFile(String cfgPath) {
		File nameCfgFile = null;
		String[] listFile = (new File(cfgPath)).list();

		if (listFile != null) {
			for (int index = 0; index < listFile.length; index++) {
				nameCfgFile = new File(cfgPath + "/" + listFile[index]);

				if (nameCfgFile.isFile()) {

					if (!nameCfgFile.exists()) {
						System.out.println("Cfg file not found: " + nameCfgFile);
						break;
					}
					String name = nameCfgFile.getName().toLowerCase();
					if (name.endsWith(".cfg")) {
						return nameCfgFile;
					}

				}
			}
		}

		return null;
	}

	/**
	 * Esse metodo le o arquivo de configuracao e obtem os caminhos dos arquivos de entrada.
	 */
	private void loadCfg (String parameters) throws IOException{
		File file = getCfgFile(parameters);
		BufferedReader in = null;

		try {
			if (file == null) {
				throw new IllegalArgumentException("File not found.");
			}

			in = new BufferedReader(new FileReader(file));

			String line = in.readLine();
			while (line != null) {

				if ((!line.trim().equals("")) && (line.contains("="))) {
					StringTokenizer st = new StringTokenizer(line,"=");

					if (st.hasMoreTokens()) {
						String nomeParametro = st.nextToken().toLowerCase().trim();
						String valorParametro = "";

						if (st.hasMoreTokens()) { 
							valorParametro = st.nextToken().trim();
						}
						if (nomeParametro.equals("caminhocorpus"))
							caminhoCorpus = valorParametro;
						else if (nomeParametro.equals("caminhopretext1"))
							caminhoPretext1 = valorParametro;
						else if (nomeParametro.equals("caminhopretextdata"))
							caminhoPretextData = valorParametro;
						else if (nomeParametro.equals("caminhopretextname"))
							caminhoPretextName = valorParametro;
						else if (nomeParametro.equals("caminhopretextstem"))
							caminhoPretextStem = valorParametro;
						else if (nomeParametro.equals("caminhopretextmaid"))
							caminhoPretextMaid = valorParametro;
						else if (nomeParametro.equals("caminhostoplist"))
							caminhoStoplist = valorParametro;
						else if (nomeParametro.equals("caminhocorpusgeral"))
							caminhoCorpusGeral = valorParametro;
						else if (nomeParametro.equals("caminhoexpressoesindicativas"))
							caminhoExpressoesIndicativas = valorParametro;
						else if (nomeParametro.equals("janelancvalue"))
							janelaNCvalue = Integer.parseInt(valorParametro);
						else if (nomeParametro.equals("caminhoparserpalavras"))
							caminhoParserPalavras = valorParametro;
						else if (nomeParametro.equals("caminholistareferencia"))
							caminhoListaReferencia = valorParametro;
						else if (nomeParametro.equals("caminhoxmltaxonomia"))
							caminhoXmlTaxonomia = valorParametro;
						else if (nomeParametro.equals("caminhosaida"))
							caminhoSaida = valorParametro;


						if (nomeParametro.equals("arquivospretext"))
							if (valorParametro.equals("true"))
								criarArqsPretext = true;
						if (criarArqsPretext) {
							if (nomeParametro.equals("stemmer")) {
								if (valorParametro.equals("true"))
									stemmer = true;
								else stemmer = false;
							}
							else if (nomeParametro.equals("language"))
								language = valorParametro;
							else if (nomeParametro.equals("stopword")) {
								if (valorParametro.equals("true"))
									stopword = true;
								else stopword = false;
							}
							else if (nomeParametro.equals("caminhostoplistingl"))
								caminhoStoplistIngl = valorParametro;
							else if (nomeParametro.equals("caminhostoplistport"))
								caminhoStoplistPort = valorParametro;
							else if (nomeParametro.equals("caminhostoplistesp"))
								caminhoStoplistEsp= valorParametro;
							else if (nomeParametro.equals("usardiretoriocomoclasse")) {
								if (valorParametro.equals("true"))
									usarDiretorioComoClasse = true;
								else usarDiretorioComoClasse = false;
							}
							else if (nomeParametro.equals("freqtruebinariofalse")) {
								if (valorParametro.equals("true"))
									FreqTrueBinarioFalse = true;
								else FreqTrueBinarioFalse = false;
							}
						}
					}
				}
				line = in.readLine();
			}

			boolean statusParametros = validarParametros(criarArqsPretext);
			if (!statusParametros) {
				throw new RuntimeException("Invalid file content: " + file);
			}

		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File with extension 'cfg' not found on path: " + parameters);
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	private boolean validarParametros(boolean criarArqsPretext) {
		boolean statusParametros = true;

		System.out.print("--> Sobre arquivos de entrada: ");

		if (caminhoCorpus.isEmpty()) {
			System.out.print("\n\nNao ha diretorio assignado para 'CaminhoCorpus'.");
			statusParametros = false;
		}


		if (caminhoStoplist.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoStoplist'.");
			statusParametros = false;
		}
		if (caminhoCorpusGeral.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoCorpusGeral'.");
			statusParametros = false;
		}
		if (caminhoExpressoesIndicativas.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoExpressoesIndicativas'.");
			statusParametros = false;
		}
		if (caminhoParserPalavras.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoParserPalavras'.");
			statusParametros = false;
		}
		if (caminhoListaReferencia.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoListaReferencia'.");
			statusParametros = false;
		}
		/*if (caminhoXmlTaxonomia.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoXmlTaxonomia'.");
			statusParametros = false;
		}*/
		if (caminhoSaida.isEmpty()) {
			System.out.print("\nNao ha diretorio assignado para 'caminhoSaida'.");
			statusParametros = false;
		}		

		if (criarArqsPretext) {
			if (caminhoPretextData.isEmpty()) {
				System.out.print("\nNao ha diretorio assignado para 'caminhoPretextData'.");
				statusParametros = false;
			}
			if (caminhoPretextName.isEmpty()) {
				System.out.print("\nNao ha diretorio assignado para 'caminhoPretextName'.");
				statusParametros = false;
			}
			if (caminhoPretextStem.isEmpty()) {
				System.out.print("\nNao ha diretorio assignado para 'caminhoPretextStem'.");
				statusParametros = false;
			}
			if (caminhoPretextMaid.isEmpty()) {
				System.out.print("\nNao ha diretorio assignado para 'caminhoPretextMaid'.");
				statusParametros = false;
			}
			if (language.isEmpty()) {
				System.out.print("\nNao ha escolha para 'language'. Voce pode escolher entre 'port', para portugues, e 'engl', para ingles.");
				statusParametros = false;
			}
			if (stopword) {
				if (caminhoStoplistIngl.isEmpty() && caminhoStoplistPort.isEmpty() && caminhoStoplistPort.isEmpty()) {
					System.out.print("\nNao ha diretorio assignado para 'stopword' para criar os arquivos similares aos da PreTexT.");
					statusParametros = false;
				}
			}
		}			

		if (statusParametros)
			System.out.print(" nenhum problema constatado");
		System.out.println("\n");

		return statusParametros;
	}

	public String getCaminhoCorpus() {
		return caminhoCorpus;
	}

	public String getCaminhoPretext1() {
		return caminhoPretext1;
	}

	public String getCaminhoPretext2() {
		return caminhoPretext2;
	}

	public String getCaminhoPretext3() {
		return caminhoPretext3;
	}

	public String getCaminhoPretextData() {
		return caminhoPretextData;
	}

	public String getCaminhoPretextName() {
		return caminhoPretextName;
	}

	public String getCaminhoPretextStem() {
		return caminhoPretextStem;
	}

	public String getCaminhoPretextMaid() {
		return caminhoPretextMaid;
	}

	public String getCaminhoStoplist() {
		return caminhoStoplist;
	}

	public String getCaminhoCorpusGeral() {
		return caminhoCorpusGeral;
	}

	public String getCaminhoExpressoesIndicativas() {
		return caminhoExpressoesIndicativas;
	}

	public int getJanelaNCvalue() {
		return janelaNCvalue;
	}

	public String getCaminhoParserPalavras() {
		return caminhoParserPalavras;
	}

	public String getCaminhoListaReferencia() {
		return caminhoListaReferencia;
	}

	public String getCaminhoXmlTaxonomia() {
		return caminhoXmlTaxonomia;
	}

	public String getCaminhoSaida() {
		return caminhoSaida;
	}

	public String getCaminhoParametros() {
		return caminhoParametros;
	}

	public boolean isCriarArqsPretext() {
		return criarArqsPretext;
	}

	public String getLanguage() {
		return language;
	}

	public boolean isStemmer() {
		return stemmer;
	}

	public boolean isStopword() {
		return stopword;
	}

	public String getCaminhoStoplistIngl() {
		return caminhoStoplistIngl;
	}

	public String getCaminhoStoplistPort() {
		return caminhoStoplistPort;
	}

	public String getCaminhoStoplistEsp() {
		return caminhoStoplistEsp;
	}

	public boolean isUsarDiretorioComoClasse() {
		return usarDiretorioComoClasse;
	}

	public boolean isFreqTrueBinarioFalse() {
		return FreqTrueBinarioFalse;
	}

}