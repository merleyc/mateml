package br.usp.mateml.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class ConfigurationLoader {
	private String pathCorpus = "";
	private String pathPretext = "";
	private String pathPretextData = "";
	private String pathPretextName = "";
	private String pathPretextStem = "";
	private String pathPretextMaid = "";
	private String pathStoplist = "";
	private String pathGeneralCorpus = "";
	private String pathIndicativePhrases = "";
	private int window_NCvalue = -1;
	private String pathParser = "";
	private String pathReferenceList = "";
	private String pathXml_taxonomy = "";
	private String pathOutput = "";
	private String pathParameters = "./data/";

	private boolean needCreatePretextFiles = false;
	private String language = ""; // options: ingl, port, or esp
	private boolean stemmer = false;
	private boolean stopword = false;
	private boolean useDiretoryWithClass = false; // utilizar diretorio da pasta de entrada como classe
	private boolean freqTrue_binaryFalse = false;

	// temporary files for each domain:
	public final String tmp_fileDataWithoutClass = "./data/discover_withoutClass.data";
	public final String tmp_fileSparseMatrix = "./data/discover_attribDoc_1Gram.txt";
	public final String tmp_fileTfidf = "./data/TfIdf.txt";

	// constructor
	public ConfigurationLoader(String cfgPath) {

		loadCfg(cfgPath);

	}

	/**
	 * Gets cfg file considering a given path.
	 * @param pathCfg path of cfg file.
	 * @return path + name of cfg file.
	 */
	private File getCfgFile(String pathCfg) {
		if (pathCfg == null || pathCfg.isEmpty()) {
			throw new IllegalArgumentException("No information given about cfg file.");
		}

		File nameFileCfg = null;
		String[] listFile = (new File(pathCfg)).list();

		if (listFile != null) {
			for (int index = 0; index < listFile.length; index++) {
				nameFileCfg = new File(pathCfg + "/" + listFile[index]);

				if (nameFileCfg.isFile()) {

					if (!nameFileCfg.exists()) {
						System.out.println("Cfg file not found: " + nameFileCfg);
						break;
					}
					String name = nameFileCfg.getName().toLowerCase();
					if (name.endsWith(".cfg")) {
						return nameFileCfg;
					}

				}
			}
		}

		return null;
	}

	/**
	 * Gets all information of cfg file, which was provided by user. 
	 * @param pathCfg path of cfg file.
	 */
	private void loadCfg (String pathCfg) {
		if (pathCfg == null || pathCfg.isEmpty()) {
			throw new IllegalArgumentException("No information given about cfg file.");
		}

		BufferedReader in = null;

		try {
			File file = getCfgFile(pathCfg);

			if (file == null) {
				throw new IllegalArgumentException("File not found: " + pathCfg);
			}

			in = new BufferedReader(new FileReader(file));

			String line = in.readLine();
			while (line != null) {

				if ((!line.trim().equals("")) && (line.contains("="))) {
					StringTokenizer st = new StringTokenizer(line,"=");

					if (st.hasMoreTokens()) {
						String nameOfParameter = st.nextToken().toLowerCase().trim();
						String valueOfParameter = "";

						if (st.hasMoreTokens()) { 
							valueOfParameter = st.nextToken().trim();
						}
						if (nameOfParameter.trim().toLowerCase().equals("corpus")) {
							pathCorpus = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("pretext")) {
							pathPretext = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("pretextData")) {
							pathPretextData = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("pretextName")) {
							pathPretextName = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("pretextstem")) {
							pathPretextStem = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("pretextmaid")) {
							pathPretextMaid = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("stoplist")) {
							pathStoplist = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("generalcorpus")) {
							pathGeneralCorpus = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("indicativephrases")) {
							pathIndicativePhrases = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("window")) {
							window_NCvalue = Integer.parseInt(valueOfParameter);
						} else if (nameOfParameter.trim().toLowerCase().equals("parser")) {
							pathParser = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("referencelist")) {
							pathReferenceList = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("xmltaxonomy")) {
							pathXml_taxonomy = valueOfParameter;
						} else if (nameOfParameter.trim().toLowerCase().equals("output")) {
							pathOutput = valueOfParameter;
						}

						if (nameOfParameter.trim().toLowerCase().equals("arquivospretext")) {
							if (valueOfParameter.equals("true")) {
								needCreatePretextFiles = true;
								if (nameOfParameter.trim().toLowerCase().equals("stemmer")) {
									if (valueOfParameter.equals("true")) {
										stemmer = true;
									}
									else { stemmer = false; }
								}
								else if (nameOfParameter.trim().toLowerCase().equals("language")) {
									language = valueOfParameter;
								}
								else if (nameOfParameter.trim().toLowerCase().equals("stopword")) {
									if (valueOfParameter.equals("true")) {
										stopword = true;
									}
									else { stopword = false; }
								}
								else if (nameOfParameter.trim().toLowerCase().equals("usardiretoriocomoclasse")) {
									if (valueOfParameter.equals("true")) {
										useDiretoryWithClass = true;
									}
									else { useDiretoryWithClass = false; }
								}
								else if (nameOfParameter.trim().toLowerCase().equals("freqtruebinariofalse")) {
									if (valueOfParameter.equals("true")) {
										freqTrue_binaryFalse = true;
									}
									else { freqTrue_binaryFalse = false; }
								}
							} 
						}
					}
				}
				line = in.readLine();
			}

			boolean statusParameters = validateParameters(needCreatePretextFiles);
			if (!statusParameters) {
				throw new RuntimeException("Invalid file content: " + file);
			}

		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File with extension 'cfg' not found on path: " + pathCfg);
		} catch (IOException e) {
			throw new RuntimeException("Error on reading file on path: " + pathCfg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("Error on closing file on path: " + pathCfg);
				}
			}
		}
	}

	/**
	 * Validates if there is enough information about inputs to continue running the extraction.
	 * @param needCreatePretextFiles if it is necessary to create the PreText files.
	 * @return true if have all need information; otherwise, returns false.
	 */
	private boolean validateParameters(boolean needCreatePretextFiles) {
		boolean statusParameters = true;

		System.out.print("--> Regarding input files: ");

		if (pathCorpus.isEmpty()) {
			System.out.print("\n\nThe 'pathCorpus' has not been given.");
			statusParameters = false;
		}
		if (pathStoplist.isEmpty()) {
			System.out.print("\nThe 'pathStoplist' has not been given.");
			statusParameters = false;
		}
		if (pathGeneralCorpus.isEmpty()) {
			System.out.print("\nThe 'pathCorpusGeral' has not been given.");
			statusParameters = false;
		}
		if (pathIndicativePhrases.isEmpty()) {
			System.out.print("\nThe 'pathExpressoesIndicativas' has not been given.");
			statusParameters = false;
		}
		if (pathParser.isEmpty()) {
			System.out.print("\nThe 'pathParserPalavras' has not been given.");
			statusParameters = false;
		}
		if (pathReferenceList.isEmpty()) {
			System.out.print("\nThe 'pathListaReferencia' has not been given.");
			statusParameters = false;
		}
		/*if (pathXmlTaxonomia.isEmpty()) {
			System.out.print("\nThe 'pathXmlTaxonomia' has not been given.");
			statusParametros = false;
		}*/
		if (pathOutput.isEmpty()) {
			System.out.print("\nThe 'pathSaida' has not been given.");
			statusParameters = false;
		}
		
		if (needCreatePretextFiles) {
			if (pathPretextData.isEmpty()) {
				System.out.print("\nThe 'pathPretextData' has not been given.");
				statusParameters = false;
			}
			if (pathPretextName.isEmpty()) {
				System.out.print("\nThe 'pathPretextName' has not been given.");
				statusParameters = false;
			}
			if (pathPretextStem.isEmpty()) {
				System.out.print("\nThe 'pathPretextStem' has not been given.");
				statusParameters = false;
			}
			if (pathPretextMaid.isEmpty()) {
				System.out.print("\nThe 'pathPretextMaid' has not been given.");
				statusParameters = false;
			}
			if (language.isEmpty()) {
				System.out.print("\n'language' has not been chosen. You can choose between 'port' (Portuguese) or 'ingl' (English).");
				statusParameters = false;
			}
		}			

		if (statusParameters)
			System.out.print("Everything seems to be okay.");
		System.out.println("\n");

		return statusParameters;
	}

	public String getPathCorpus() {
		return pathCorpus;
	}

	public String getPathPretext() {
		return pathPretext;
	}

	public String getPathPretextData() {
		return pathPretextData;
	}

	public String getPathPretextName() {
		return pathPretextName;
	}

	public String getPathPretextStem() {
		return pathPretextStem;
	}

	public String getPathPretextMaid() {
		return pathPretextMaid;
	}

	public String getPathStoplist() {
		return pathStoplist;
	}

	public String getPathCorpusGeral() {
		return pathGeneralCorpus;
	}

	public String getPathExpressoesIndicativas() {
		return pathIndicativePhrases;
	}

	public int getJanelaNCvalue() {
		return window_NCvalue;
	}

	public String getPathParserPalavras() {
		return pathParser;
	}

	public String getPathListaReferencia() {
		return pathReferenceList;
	}

	public String getPathXmlTaxonomia() {
		return pathXml_taxonomy;
	}

	public String getPathSaida() {
		return pathOutput;
	}

	public String getPathParametros() {
		return pathParameters;
	}

	public boolean isCriarArqsPretext() {
		return needCreatePretextFiles;
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

	public boolean isUsarDiretorioComoClasse() {
		return useDiretoryWithClass;
	}

	public boolean isFreqTrueBinarioFalse() {
		return freqTrue_binaryFalse;
	}

}