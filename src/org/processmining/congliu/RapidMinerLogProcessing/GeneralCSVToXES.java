package org.processmining.congliu.RapidMinerLogProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;




public class GeneralCSVToXES {

	private  XLog log = null; //new XLogImpl(XAttributeMap attributeMap); 
	
	
	private  String csvfilename = null; 
	
	private  String xesfilename = null; 
	
	public GeneralCSVToXES(String CSVFileName, String XESFileName) throws Exception
	{
		xesfilename = XESFileName; 
		csvfilename = CSVFileName;

		// we choose not to use, Xlog for serialization, but to write to files case by case
//		try {
//			FileInputStream fs = new FileInputStream (xesfilename); 			
//			log = new XesXmlParser().parse(fs).get(0);
//			fs.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/*
	 * pase the case id from file
	 */
	public String parseInt(String string)
	{
		String result = "";
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);      
		Matcher m = p.matcher(string);
		result = m.replaceAll("").trim();
		return result;
	}
	
	
	/*
	 * read in the class-level CSV files, different from others they are stored in separated files. 
	 */
	public void EnrichedConvert () throws IOException
	{
	
		//open readin file
		File file = new File(csvfilename); 
		
		File[] filelist = file.listFiles();
		//XTrace trace;
		BufferedWriter bw ;
		for (File f: filelist)
        {
			//new trace, trace name
			//XAttributeMap attMapTrace = new XAttributeMapImpl();
			//XLogFunctions.putLiteral(attMapTrace, "concept:name", "Case:"+ parseInt(f.getName()));
			// initialize a trace with the current time 
			//trace = new XFactoryNaiveImpl().createTrace(attMapTrace);
			
			bw  = new BufferedWriter(new FileWriter(xesfilename,true));//设置为附加方式 true为附加方式
			
			bw.newLine();
			bw.write("\t"+ "<trace>");
			bw.newLine();
			//add case id
			bw.write("\t\t"+ "<string key=\"concept:name\" value=\""+"Case:"+ parseInt(f.getName())+"\"/>");
			bw.newLine();
			
			
        	if (f.getName().endsWith(".csv"))
        	{
        		System.out.println(f.getName());
        		BufferedReader reader = null;

        		try {
        			reader = new BufferedReader(new FileReader(f));
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		String tempString = "";
        		String [] tempList; 
        		
        		//read by line, and convert to XES
        		try {
        			while ((tempString = reader.readLine()) != null)
        			{
        				tempList = tempString.split(";");

        			//create the event 
        			//String resource, String name,  String timestamp, String lifecycle, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu			
        			//saveEventToLocal(BufferedWriter bw, String methodname, String lifecycle, String timestamp, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu	)
        			
        			//XEvent eventStart = createEnrichedEvent("C.Liu", tempList[0], tempList[1], "start", tempList[3],tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
        			
        			saveEventToLocal(bw, tempList[0], "start", tempList[1], tempList[3],tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
        			
        			saveEventToLocal(bw, tempList[0], "start", tempList[2], tempList[3],tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
        			//add the created start event to trace
        			//trace.add(eventStart);
        			
        			
        			//XEvent eventEnd = createEnrichedEvent("C.Liu", tempList[0], tempList[2], "complete", tempList[3], tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
        							
        			//add the created end event to trace
        			//trace.add(eventEnd);
        				
        			}
        			
        			//close the CSV file reader
        			reader.close();
        			
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	//System.out.println(f.getName()+"trace created");
//    		//add trace to log
//    		log.add(trace);
//        	// serializaion log to local case by case
//        	CaseByCaseSerialization cc =new CaseByCaseSerialization();
//        	cc.saveCasesToLocal(trace, xesfilename);
        	
        	//System.out.println(f.getName()+"serialization finished");
        	
        	//trace.clear();
        	
        	
        	bw.write("\t"+ "</trace>");
			bw.newLine();
			
			bw.flush();
 			bw.close();
        	
        }
		
//		//serialization the current XES log 
//		try {
//			FileOutputStream fos = new FileOutputStream (xesfilename); 
//			//new FileWriter(fileName,true)
//			new XesXmlSerializer().serialize(log, fos); 
//			
//
//			fos.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		bw  = new BufferedWriter(new FileWriter(xesfilename,true));//attachment
		bw.write("</log>");
		bw.newLine();
		
		bw.flush();
		bw.close();
	}
	
	/*
	 * resource, activity name, timestamp, lifecycle, component. 
	 */
	public XEvent createEnrichedEvent(String resource, String name,  String timestamp, String lifecycle, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu)
	{
		XAttributeMap attMap = new XAttributeMapImpl();

		//convert time
		Date dateStart = new Date(Long.parseLong(timestamp)/1000000);  
		
		XLogFunctions.putLiteral(attMap, "org:resource", resource);
		XLogFunctions.putLiteral(attMap, "concept:name", name);
		XLogFunctions.putTimestamp(attMap, "time:timestamp", dateStart);
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", lifecycle);
		XLogFunctions.putLiteral(attMap, "plugin", plugin);
		XLogFunctions.putLiteral(attMap, "class", className);
		XLogFunctions.putLiteral(attMap, "package1", package1Name);
		XLogFunctions.putLiteral(attMap, "package2", package2Name);
		
		XLogFunctions.putLiteral(attMap, "usedMemory", usedmemory);
		XLogFunctions.putLiteral(attMap, "totalMemory", totalmemory);
		XLogFunctions.putLiteral(attMap, "cpuPercent", usedcpu);
		
		XEvent event = new XEventImpl(attMap);
		
		return event;
		

	}
	
	
	public static void saveEventToLocal(BufferedWriter bw, String methodname, String lifecycle, String timestamp, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu	) throws IOException
    {  
		//String resource, String name,  String timestamp, String lifecycle, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu			
		//XEvent eventStart = createEnrichedEvent("C.Liu", tempList[0], tempList[1], "start", tempList[3],tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
		
		

		//add start event
		bw.write("\t\t"+ "<event>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<String key=\"concept:name\" value=\""+methodname+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"lifecycle:transition\" value=\""+lifecycle+"\"/>");
		bw.newLine();
		
		Date date = new Date(Long.parseLong(timestamp)/1000000);  
		 
		//XLogFunctions.getTime(event).getTime()
		bw.write("\t\t\t"+ "<date key=\"time:timestamp\" value=\""+date+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"org:resource\" value=\""+"C.Liu"+"\"/>");
		bw.newLine();

		bw.write("\t\t\t"+ "<string key=\"plugin\" value=\""+plugin+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"class\" value=\""+className+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"package1\" value=\""+package1Name+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"package2\" value=\""+package2Name+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"usedMemory\" value=\""+usedmemory+"\"/>");
		bw.newLine();
		
		bw.write("\t\t\t"+ "<string key=\"totalMemory\" value=\""+totalmemory+"\"/>");
		bw.newLine();

		bw.write("\t\t\t"+ "<string key=\"cpuPecent\" value=\""+usedcpu+"\"/>");
		bw.newLine();


		bw.write("\t\t"+ "</event>");
		bw.newLine();
		
    }
	
	
	
}