package br.usp.mateml.feature_extraction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import br.usp.mateml.candidates.Candidates;
import br.usp.mateml.candidates.NodeCandidate;
import br.usp.mateml.feature_extraction.taxonomy.NodeFeatureOfTax;
import br.usp.mateml.feature_extraction.taxonomy.TaxonomyTraversal;
import br.usp.mateml.loaders.ConfigurationLoader;
import br.usp.mateml.loaders.PretextLoader;
import br.usp.mateml.main.GlobalVariables;
import br.usp.mateml.output.SparseMatrix;
import br.usp.mateml.output.Output;
import br.usp.mateml.output.DiscoverClass;
import br.usp.mateml.util.Util;

public class Features {

	/**  Esse metodo obtem caracteristicas para cada candidato a termo (ct):
	 */
	public void calculateFeatures(int ngramas, Candidates candidates,
			ConfigurationLoader configuration, PretextLoader pretextLoader) {

		if (candidates.hashCandidatesIsEmpty()) {
			System.out.println("O hash de " + ngramas + "-gramas eh nulo e as caracteristicas dos cts nao podem ser calculadas.");
		} else {

			try {
				TFIDF tfidf = new TFIDF();
				TC tc = new TC();
				TV tv = new TV();
				TVQ tvq = new TVQ();
				ATF atf = new ATF();
				TDS tds = new TDS();
				THD thd = new THD();
				RIDF ridf = new RIDF();				
				GeneralLanguageCorpus corpusLinguaGeral = new GeneralLanguageCorpus();
				NCvalue ncvalue = new NCvalue();
				Cvalue cvalue = new Cvalue();
				Phrases sintagmas = new Phrases();
				POS pos = new POS();
				IndicativePhrases expressoesIndicativas = new IndicativePhrases();
				ReferenceList referenceList = new ReferenceList(null);
				
				// Calcula Average Term Frequency in Corpus:
				candidates.atualizar_hashATF(candidates, atf);

				// Calcular TFIDF:
				HashMap<String, Double> hashTFIDF = tfidf.calculate_TFIDF(candidates);
				candidates.atualizar_hashTFIDF(hashTFIDF, candidates);
				hashTFIDF = null;		

				// Obtendo dados para calcular o TV, TVQ, TC.
				String arqPretextData = configuration.getCaminhoPretextData();

				if (!Util.arquivoOK(arqPretextData))
					System.out.println("Nao foi possivel calcular as caracteristicas TV, TVQ e TC.");
				else {
					DiscoverClass retiraClasseDiscover = new DiscoverClass(
							arqPretextData, configuration.arqDataSemClasse);
					SparseMatrix matrizEsparsaAtribDoc = new SparseMatrix(
							configuration.arqDataSemClasse, configuration.arqMatrizEsparsa);
					
					if (matrizEsparsaAtribDoc != null)
						System.out.println("Matriz esparsa criada.");
					Vector<String> NamesCts = pretextLoader.obterTermoDaMatrizName(
							configuration.getCaminhoPretextName());
					if (NamesCts != null)
						System.out.println("Matriz Names armazenada.");

					// Calcular TV:
					HashMap<String, Double> hashTv = tv.calculaTV(configuration.arqMatrizEsparsa, NamesCts);
					candidates.atualizar_hashTv(hashTv, candidates);
					hashTv = null;

					// Calcular TVQ:
					HashMap<String, Double> hashTvq = tvq.calculaTVQ(configuration.arqMatrizEsparsa, NamesCts);
					candidates.atualizar_hashTvq(hashTvq, candidates);
					hashTvq = null;

					// Calcular TC:
					HashMap<String, Double> hashTc = tc.calculaTC(configuration.arqMatrizEsparsa, NamesCts, configuration.arqTfidf);
					candidates.atualizar_hashTc(hashTc, candidates);
					hashTc = null;

					Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);
				}

				// Obter frequencia a partir do Corpus de Lingua Geral:
				HashMap<String, NodeCandidate> hashCG = corpusLinguaGeral.carregarHash_corpusGeral(
						configuration.getCaminhoCorpusGeral());
				candidates.atualizar_hashCG(hashCG, candidates);

				// Obter o valor da medida Weirdness:
				candidates.atualizar_hashWeirdness(hashCG, candidates);

				// Obter o valor da medida RIDF:
				candidates.atualizar_hashRIDF(candidates, ridf);

				// Obter o valor da medida THD:
				candidates.atualizar_hashTHD(hashCG, candidates, thd);

				// Obter o valor da medida THD:
				candidates.atualizar_hashTDS(hashCG, candidates, tds);

				// Obter o valor da medida GlossEx:
				candidates.atualizar_hashGlossEx(0.2, 0.8, hashCG, candidates);

				// Encontrar cts que estao perto de Expressoes Indicativas:
				HashMap<String, Boolean> palavrasPertoEI =
						expressoesIndicativas.obterExprIndicativas(configuration, pretextLoader);
				candidates.atualizar_hashEI(palavrasPertoEI, candidates);
				palavrasPertoEI = null;

				// Calcular C-Value:
				HashMap<String, Double> hashCvalue = cvalue.calcularCvalue(
						configuration.getCaminhoPretextStem(), candidates, pretextLoader);
				candidates.atualizar_hashCvalue(hashCvalue, candidates);
				hashCvalue = null;

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Calcular NC-Value:
				HashMap<String, Double> hashNCvalue = ncvalue.calcularNCvalue(
						configuration, configuration.getJanelaNCvalue(), pretextLoader, candidates);
				candidates.atualizar_hashNCvalue(hashNCvalue, candidates);
				hashNCvalue= null;

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Verificar se o candidato a termo faz parte de um Sintagma Nominal:
				HashMap<String, Character> hashSintagmas = sintagmas.percorrerArqParser_Sintagmas(
						configuration, pretextLoader);
				candidates.atualizar_hashSintagmas(hashSintagmas, candidates);
				hashSintagmas = null;

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Verificar se o candidato a termo pertence a algum padrao morfossintatico dado:
				HashMap<String, Boolean> hashUnigramasPadroes = pos.percorrerArqParser_PM(
						configuration, pretextLoader);
				candidates.atualizar_hashPadroesMorfossintaticos(hashUnigramasPadroes, candidates);
				hashUnigramasPadroes = null;

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Buscar os tipos das palavras originais (substantivos, adjetivos e verbos):
				HashMap<String, Vector<Integer>> hashTipoPalOriginais = pos.obterTiposPalavrasOriginais(
						configuration, pretextLoader);
				candidates.atualizar_hashTiposPalavrasOriginais(hashTipoPalOriginais, candidates);
				hashTipoPalOriginais = null;

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Obter caracteristicas identificadas na taxonomia do mesmo dominio do corpus:
				String arqXmlTax = configuration.getCaminhoXmlTaxonomia();
				if (arqXmlTax.equals("") || arqXmlTax == null || arqXmlTax.length() < 0) {
					System.out.println("Nao ha arquivo XML da taxonomia do dominio: " + arqXmlTax +
							".\nNao foi possivel calcular as caracteristicas usando a taxonomia.");
				}
				else {
					TaxonomyTraversal pt = new TaxonomyTraversal();
					HashMap<Integer, NodeFeatureOfTax> hashTax = pt.gerarHashTax(arqXmlTax);
					//Atualizar hash de candidatos a termos com as caracteristicas identificadas na taxonomia:
					candidates.atualizar_hashCaracteristicasTaxonomia(hashTax, candidates);
				}		

				Output.gerarMatriz(GlobalVariables.configuration, GlobalVariables.candidates);

				// Encontrar cts que estao presentes na lista de referencia:
				if (referenceList.isReferenceListEmpty())
					referenceList = new ReferenceList(configuration.getCaminhoListaReferencia()); // armazena palavras da lista de refer�ncia
				//candidates.atualizar_hashLR(referenceList, candidates);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}