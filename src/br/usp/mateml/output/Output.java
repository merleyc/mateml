package br.usp.mateml.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.steps.feature_extraction.Control;
import br.usp.mateml.steps.preprocessing.Treatment;

public class Output {

	/**
	 * Esse metodo gera a matriz de saida a partir dos hashes de ct e suas caracteristicas.
	 * @param candidates 
	 * @param pathOutput 
	 */
	public static void gerarMatriz(ConfigurationLoader configuration, Candidates candidates) {
		createArff(configuration.getPathSaida(), candidates);
	}

	private static boolean createArff (String caminhoSaida, Candidates candidates){

		if (caminhoSaida == null) {
			System.out.println("Error: Output: Creating ARFF: path output file is null.");
			return false;
		} else if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("Error: Output: Creating ARFF: candidate hash is null or empty.");
			return false;
		}

		FastVector      atts;
		FastVector      atributo_tamGram, atributo_cg, atributo_ei, atributo_lr,
		atributo_sintagma, atributo_nsintagma, atributo_pm;
		Instances       data;
		double[]        vals;
		int             i;

		/*		if (hashct_1gram == null) {
			System.out.println("O hash de " + hashct_1gram.toString() + " eh nulo e nao eh possivel gravar seus dados na matriz de saida.");
			return false;
		}*/

		// 1. set up attributes
		atts = new FastVector();
		// - string
		atts.addElement(new Attribute("ct", (FastVector) null));

		// - numeric
		atts.addElement(new Attribute("comGram"));

		// - nominal (Valores possiveis: -1, 1, 2, 3)
		atributo_tamGram = new FastVector();
		atributo_tamGram.addElement("-1");
		for (i = 0; i < 3; i++)
			atributo_tamGram.addElement("" + (i+1));
		atts.addElement(new Attribute("tamGram", atributo_tamGram));

		// - numerics:
		atts.addElement(new Attribute("freq"));

		if (Control.controle_atf)
			atts.addElement(new Attribute("atf"));

		atts.addElement(new Attribute("df"));

		if (Control.controle_tfidf)
			atts.addElement(new Attribute("tfidf"));
		if (Control.controle_tv)
			atts.addElement(new Attribute("tv"));
		if (Control.controle_tvq)
			atts.addElement(new Attribute("tvq"));
		if (Control.controle_tc)
			atts.addElement(new Attribute("tc"));

		atributo_cg = new FastVector();
		if (Control.controle_cg) {
			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_cg.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_cg.addElement("" + (i));
			atts.addElement(new Attribute("cg", atributo_cg));

			// - numerics:
			atts.addElement(new Attribute("freqcg"));
		}

		if (Control.controle_weirdness)
			atts.addElement(new Attribute("weirdness"));

		if (Control.controle_ridf)
			atts.addElement(new Attribute("ridf"));

		if (Control.controle_thd)
			atts.addElement(new Attribute("thd"));

		if (Control.controle_tds)
			atts.addElement(new Attribute("tds"));

		if (Control.controle_glossex)
			atts.addElement(new Attribute("glossex"));

		atributo_ei = new FastVector();
		if (Control.controle_ei) {
			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_ei.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_ei.addElement("" + (i));
			atts.addElement(new Attribute("ei", atributo_ei));
		}

		if (Control.controle_cvalue)
			// - numerics:
			atts.addElement(new Attribute("cvalue"));
		if (Control.controle_ncvalue)	atts.addElement(new Attribute("ncvalue"));

		atributo_sintagma = new FastVector();
		atributo_nsintagma = new FastVector();
		if (Control.controle_sintagmas) {
			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_sintagma.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_sintagma.addElement("" + (i));
			atts.addElement(new Attribute("sintagma", atributo_sintagma));

			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_nsintagma.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_nsintagma.addElement("" + (i));
			atts.addElement(new Attribute("nsintagma", atributo_nsintagma));
		}

		atributo_pm = new FastVector();
		if (Control.controle_pm) {
			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_pm.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_pm.addElement("" + (i));
			atts.addElement(new Attribute("pm", atributo_pm));
		}

		if (Control.controle_TipoPalOriginais) {
			// - numerics:
			atts.addElement(new Attribute("rsubst"));
			atts.addElement(new Attribute("radj"));
			atts.addElement(new Attribute("rverb"));
			atts.addElement(new Attribute("numPalOrig"));
		}

		if (Control.controle_taxonomia) {
			atts.addElement(new Attribute("numIrmaosTax"));
			atts.addElement(new Attribute("nivelTax"));
			atts.addElement(new Attribute("numDocsTax"));
			atts.addElement(new Attribute("numNohsFilhosTax"));
			atts.addElement(new Attribute("numDescendentesTax"));
			atts.addElement(new Attribute("valorTermoTax"));
		}

		atributo_lr = new FastVector();
		if (Control.controle_lr) {
			// - nominal (Valores possiveis: -1, 0, 1)
			atributo_lr.addElement("-1");
			for (i = 0; i <= 1; i++)
				atributo_lr.addElement("" + (i));
			atts.addElement(new Attribute("lr", atributo_lr));
		}

		// 2. create Instances object
		data = new Instances("ExtracaoAM", atts, 0);

		// 3. fill with data
		Set<String> set = candidates.getHashCandidates().keySet();
		Object[] array = set.toArray();

		for (int index=0; index<array.length; index++) {
			int linha = 0;
			NodeCandidate node = candidates.getNode(array[index].toString());

			// first instance
			vals = new double[data.numAttributes()];
			// - string
			vals[linha] = data.attribute(0).addStringValue(node.termo); linha++;
			// - numeric
			vals[linha] = node.compGram; linha++;
			// - nominal
			vals[linha] = atributo_tamGram.indexOf(""+node.tamGram); linha++;
			// - numeric
			vals[linha] = node.freq; linha++;
			if (Control.controle_atf) {vals[linha] = node.atf; linha++;}
			vals[linha] = node.df; linha++;
			if (Control.controle_tfidf)	{vals[linha] = node.tfidf; linha++;}
			if (Control.controle_tv)	{vals[linha] = node.tv; linha++;}
			if (Control.controle_tvq)	{vals[linha] = node.tvq; linha++;}
			if (Control.controle_tc)	{vals[linha] = node.tc; linha++;}
			if (Control.controle_cg) {
				vals[linha] = atributo_cg.indexOf(""+node.corpusGeral); linha++;
				vals[linha] = node.freq_CorpusGeral; linha++;
			}
			if (Control.controle_weirdness) {vals[linha] = node.weirdness; linha++;}
			if (Control.controle_ridf) {vals[linha] = node.ridf; linha++;}
			if (Control.controle_thd) {vals[linha] = node.thd; linha++;}
			if (Control.controle_tds) {vals[linha] = node.tds; linha++;}
			if (Control.controle_glossex) {vals[linha] = node.glossex; linha++;}
			// - nominal
			if (Control.controle_ei)	{vals[linha] = atributo_ei.indexOf(""+node.expr_indicativas); linha++;}
			// - numeric
			if (Control.controle_cvalue)	{vals[linha] = node.cvalue; linha++;}
			if (Control.controle_ncvalue)	{vals[linha] = node.ncvalue; linha++;}
			// - nominal
			if (Control.controle_sintagmas) {
				vals[linha] = atributo_sintagma.indexOf(""+node.sintagmas); linha++;
				vals[linha] = atributo_nsintagma.indexOf(""+node.nucleo_sintagmas); linha++;
			}
			if (Control.controle_pm) {	vals[linha] = atributo_pm.indexOf(""+node.padrao); linha++; }
			// - numeric
			if (Control.controle_TipoPalOriginais) {
				vals[linha] = node.substantivo; linha++;
				vals[linha] = node.adjetivo; linha++;
				vals[linha] = node.verbo; linha++;
				vals[linha] = node.numPalOriginais; linha++;
			}
			//nao coloquei os atributos da taxonomia "parent" e "listaDocumentos"
			if (Control.controle_taxonomia) {
				vals[linha] = node.numIrmaos; linha++;
				vals[linha] = node.nivel; linha++;
				vals[linha] = node.numDocs; linha++;
				vals[linha] = node.numNohsFilhos; linha++;
				vals[linha] = node.numDescendentes; linha++;
				vals[linha] = node.valorTermo; linha++;
			}
			if (Control.controle_lr) {
				// - nominal
				vals[linha] = atributo_lr.indexOf(""+node.listaRef);
				linha++;
			}

			// add
			data.add(new Instance(1.0, vals));

		}

		// 4. output data
		// gravar no arquivo do caminhoSaida
		return (saveArff(caminhoSaida, data));
	}

	private static boolean saveArff(String caminhoSaida, Instances data) {
		try {
			File arqSaida = new File (caminhoSaida);
			Treatment.criarArquivo(arqSaida);

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

}