package org.processmining.congliu.RapidMinerLogProcessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class CaseByCaseSerialization {

	/**
	 * this function aims to serialization xes log case by case
	 * @param trace
	 * @param xesfilename
	 */
	public void saveCasesToLocal(XTrace trace, String xesfilename)
    {  
    	try
    	{
			BufferedWriter bw  = new BufferedWriter(new FileWriter(xesfilename,true));//设置为附加方式 true为附加方式
			
			bw.newLine();
			bw.write("\t"+ "<trace>");
			bw.newLine();
			//add case id
			bw.write("\t\t"+ "<string key=\"concept:name\" value=\""+XLogFunctions.getName(trace)+"\"/>");
			bw.newLine();
			
			for(XEvent event: trace)
			{
				saveEventToLocal(event, bw);
			}
    		
			bw.write("\t"+ "</trace>");
			bw.newLine();
			
			bw.flush();
 			bw.close();
		}
    	catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
	/**
	 * localization the case
	 * @param trace
	 * @throws IOException 
	 */
	public static void saveEventToLocal(XEvent event, BufferedWriter bw ) throws IOException
    {  
		

		//add start event
		bw.write("\t\t"+ "<event>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<String key=\"concept:name\" value=\""+event.getAttributes().get("concept:name").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"lifecycle:transition\" value=\""+event.getAttributes().get("lifecycle:transition").toString()+"\"/>");
		bw.newLine();
		 
		//XLogFunctions.getTime(event).getTime()
		bw.write("\t\t\t"+ "<date key=\"time:timestamp\" value=\""+event.getAttributes().get("time:timestamp").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"org:resource\" value=\""+event.getAttributes().get("org:resource").toString()+"\"/>");
		bw.newLine();

		bw.write("\t\t\t"+ "<string key=\"plugin\" value=\""+event.getAttributes().get("plugin").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"class\" value=\""+event.getAttributes().get("class").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"package1\" value=\""+event.getAttributes().get("package1").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"package2\" value=\""+event.getAttributes().get("package2").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"usedMemory\" value=\""+event.getAttributes().get("usedMemory").toString()+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"totalMemory\" value=\""+event.getAttributes().get("totalMemory").toString()+"\"/>");
		bw.newLine();

		bw.write("\t\t\t"+ "<string key=\"cpuPecent\" value=\""+event.getAttributes().get("cpuPecent").toString()+"\"/>");
		bw.newLine();


		bw.write("\t\t"+ "</event>");
		bw.newLine();
		
    }
	
}
