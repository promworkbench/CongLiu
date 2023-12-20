package org.processmining.congliu.examplecodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class parseInt {

	/*
	 * pase the case id from file
	 */
	public String parseInts(String string)
	{
		String result = "";
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);      
		Matcher m = p.matcher(string);
		result = m.replaceAll("").trim();
		return result;
	}
}
