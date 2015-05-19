package br.usp.mateml.feature_extraction;

public class ATF {

	/**
	 * Average Term Frequency in Corpus - calculated by dividing the total frequency
	 *  of a term in a corpus by its document frequency.
	 *  ( http://code.google.com/p/jatetoolkit/wiki/JATEIntro )
	 */
	public double calculaATF(int freqCT, int docFreqCT) {
		
		double atf = ( (double) freqCT / (double) docFreqCT );
		
		return atf;
	}
	
}