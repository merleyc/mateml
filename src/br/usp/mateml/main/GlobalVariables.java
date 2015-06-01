package br.usp.mateml.main;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.StoplistLoader;
import br.usp.mateml.steps.feature_extraction.Features;
import br.usp.mateml.steps.feature_extraction.ReferenceList;

public class GlobalVariables {

	public static ConfigurationLoader configuration;
	public static ReferenceList referenceList;
	public static StoplistLoader stoplist;
	public static Candidates candidates;
	
	public static void loadConfiguration(String cfgPath) {
		if (configuration == null) {
			configuration = new ConfigurationLoader(cfgPath);
		}
	}

	public static void loadReferenceList(String pathReferenceList) {
		if (referenceList == null) {
			referenceList = new ReferenceList(pathReferenceList);	
		}
	}

	public static void loadStoplist(String pathStoplistFile) {
		if (stoplist == null) {
			stoplist = new StoplistLoader(pathStoplistFile, referenceList);
		}
	}
	
	public static void loadCandidates(int gram, String pathPretextFile) {
		if (candidates == null) {
			candidates = new Candidates(gram, pathPretextFile);
		}
	}

}
