package br.usp.mateml.util;
import java.util.regex.*;

public class Regex {

	public static void getStringInsideParentheses (String[] args) {
		String example = "United Arab Emirates Dirham (AED)";
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(example);
		while(m.find()) {
			System.out.println(m.group(1));    
		}
	}

}