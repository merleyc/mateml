package br.usp.mateml.steps.feature_extraction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class TaxonomyTraversal {

	private HashMap<Integer, NodeFeatureOfTax> nodosTax = new HashMap<Integer, NodeFeatureOfTax>();
	HashMap<Integer, Vector<Integer>> grafoTaxonomia;

	/**
	 * This method is used to load the xml file to a document and return it
	 *
	 * @param xmlFileName is the xml file name to be loaded
	 * @return Document
	 */
	public static Document getDocument( final String xmlFileName )
	{
		Document document = null;
		SAXReader reader = new SAXReader();
		try
		{
			document = reader.read( xmlFileName );
			System.out.println("carregou o arquivo!");
		}
		catch (DocumentException e)
		{
			System.out.println("Problemas ao carregar o arquivo XML da Taxonomia "+ xmlFileName);
			e.printStackTrace();
		}
		return document;
	}

	public HashMap<Integer, NodeFeatureOfTax> gerarHashTax (String xmlFileName){

		Document document = TaxonomyTraversal.getDocument(xmlFileName);
		HashMap<Integer, Vector<Integer>> grafoTaxonomia = gerarGrafo(document);
		HashMap<Integer, NodeFeatureOfTax> hashTax = calcularCaracteristicas(grafoTaxonomia);
		return hashTax;
	}

	private HashMap<Integer, Vector<Integer>> gerarGrafo(Document document) {
		HashMap<Integer, Vector<Integer>> grafoID = new HashMap<Integer, Vector<Integer>>();

		Element root = document.getRootElement();

		// percorre cada nó da taxonomia
		for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
			Element nodeXml = (Element) i.next();
			NodeFeatureOfTax nodoFilho = new NodeFeatureOfTax();

			// percorre os filhos do nó
			for (Iterator f = nodeXml.elementIterator(); f.hasNext(); ){
				Element item = (Element) f.next();            	

				if (item.getName().equals("id"))
					nodoFilho.id = Integer.parseInt(item.getText());
				else if (item.getName().equals("parent"))
					nodoFilho.parent = Integer.parseInt(item.getText());
				else if (item.getName().equals("documents")) {
					nodoFilho.listaDocumentos = item.getText();
					nodoFilho.numDocs = contarNumDocs(item.getText());
				}
				else if (item.getName().equals("descriptors"))
					nodoFilho.termos = item.getText();//podem ter varios termos
				
				// para buscar os ids de algum termo:
				/*if (nodoFilho.termos != null && nodoFilho.termos.contains("potenc"))
					System.out.println("gerarGrafo: " + nodoFilho.id + " : " + nodoFilho.parent + " : " + nodoFilho.numDocs + " : " + nodoFilho.listaDocumentos);*/
				
			}

			nodosTax.put(nodoFilho.id, nodoFilho);

			if (!grafoID.containsKey(nodoFilho.id)) 
				grafoID.put(nodoFilho.id, new Vector<Integer>());

			// gravando no pai o noh atual como seu filho
			if (nodoFilho.id > 0) { //grafoID se nao eh a raiz da taxonomia, pois a raiz por default eh vazia
				Vector<Integer> conteudo = grafoID.get(nodoFilho.parent);
				if (conteudo == null)
					conteudo = new Vector<Integer>();

				conteudo.add(nodoFilho.id);
				grafoID.put(nodoFilho.parent, conteudo);
			}

		}

		return grafoID;
	}

	/**
	 * 
	 * @param listaDocs Ex.: [texto1StringTokenizer st = new StringTokenizer(listaDocs.trim(), ",");.txt, texto2.txt]
	 * @return numero de documentos listados
	 */
	private int contarNumDocs(String listaDocs) {
		int numDocs = 0;
		StringTokenizer st = new StringTokenizer(listaDocs.trim(), ",");
		numDocs = st.countTokens();		
		return numDocs;
	}

	private HashMap<Integer, NodeFeatureOfTax> calcularCaracteristicas(
			HashMap<Integer, Vector<Integer>> grafoTaxonomia2) {

		this.grafoTaxonomia = grafoTaxonomia2;
		dfs(nodosTax.get(0),0);

		//Util.imprimeHashNodo(nodosTax);

		return nodosTax;
	}

	
	/**
	 * Esse metodo faz um percurso em profundidade do grafo para calcular o nivel de cada noh e o numero de filhos do subgrafo
	 * @param nohAtual nodo atual
	 * @param nivel nivel atual
	 * @return numero de filhos do subgrafo
	 */
	private int dfs(NodeFeatureOfTax nohAtual, int nivel){
		int totalNohsSubgrafo = 1;
		for(int idFilhos : this.grafoTaxonomia.get(nohAtual.id)){
			totalNohsSubgrafo += dfs(this.nodosTax.get(idFilhos), nivel + 1);
		}

		nohAtual.numDescendentes = totalNohsSubgrafo;
		nohAtual.nivel = nivel;
		nohAtual.numNohsFilhos = grafoTaxonomia.get(nohAtual.id).size();
		
		// Calcula quantos irmaos cada filho do noh atual tem:
		for(int idFilho : this.grafoTaxonomia.get(nohAtual.id)){
			NodeFeatureOfTax filho = this.nodosTax.get(idFilho);
			filho.numIrmaos = nohAtual.numNohsFilhos - 1;
			nodosTax.put(idFilho, filho);
		}
		
		this.nodosTax.put(nohAtual.id, nohAtual);
		return totalNohsSubgrafo;
	}

}
