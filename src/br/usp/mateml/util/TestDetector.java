package br.usp.mateml.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.mozilla.universalchardet.UniversalDetector;

public class TestDetector {
	
	public static String detectaEncodingImprimeArquivo(String arquivo) {
		String encoding = detectarEncoding(arquivo);

		//imprimirArquivo(arquivo, encoding);

		return encoding;
	}

	private static String detectarEncoding(String arquivo) {
		String encoding = "";
		byte[] buf = new byte[4096];
		try {

			java.io.FileInputStream fis = new java.io.FileInputStream(arquivo);

			// (1)	
			UniversalDetector detector = new UniversalDetector(null);

			// (2)
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// (3)
			detector.dataEnd();

			// (4)
			encoding = detector.getDetectedCharset();
			if (encoding == null) {
				System.out.println("No encoding detected: " + arquivo);
			} /*else {
				System.out.println(arquivo + ": " + encoding);
			}
*/
			// (5)
			detector.reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encoding;
	}

	private static void imprimirArquivo(String arquivo, String encoding) {
		try {
			BufferedReader in;
			if (encoding != null) 
				in = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo),encoding));
			else in = new BufferedReader(new FileReader(arquivo));
			
			String linha = in.readLine();
			while (linha != null) {
				System.out.println(linha);
				linha = in.readLine();
			}
			in.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}