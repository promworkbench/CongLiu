package org.processmining.congliu.RapidMinerLogProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/*
 * this class aims to enrich event logs (CSV) with class, package1, package 2 items, and add the plugin name to each method recording according to the time.. 
 */
public class EnrichMethodsWithPluginClassPackages {

	/*
	 * the input is method level event logs, the plugin names, start and end file. 
	 */
	
	// the plug in file. "D:\\KiekerData\\CaseStudy002\\RapidMinerLog.txt"
	private String pluginsName = null; 
	
	//the input file archive
	private String methodlevefilename = null;
	
	// the obtained file archive
	private String enrichedfilename = null; 
	
	
	//constructor, original csv file name, enriched csv file name
	public EnrichMethodsWithPluginClassPackages(String PluginName, String methodlevelFileName, String enrichedFileName)
	{
		this.pluginsName = PluginName;

		this.methodlevefilename = methodlevelFileName;
		this.enrichedfilename = enrichedFileName;
	}
	
	
	//
	
	public void enriching()
	{
		//load in all plugin information
		List<PluginUnit> pluginlist = plugins();
		
		//open readin file directory
		File file = new File(methodlevefilename); 
		
		File[] filelist = file.listFiles();
		
		//EnrichedCSVUnit list
		
		List<EnrichedCSVUnit> list = new ArrayList<EnrichedCSVUnit>();
		
		int count = 1;
		for (File f: filelist)
        {
        	//end with .dat
        	if (f.getName().endsWith(".csv"))
        	{
        		//output filename
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
        				
        				//obtain the method related information, tempList[0]
        				//System.out.println(tempList[0]);
        				
        				//delete the brackets content
        				int indexStart =  tempList[0].indexOf("(");
        				
        				String method= tempList[0].substring(0, indexStart);
        				
        				//obtain the method name, methodConponent[methodConponent.length -1]
        				String [] methodConponent = method.split(" ");
        				
        				String methodName = methodConponent[methodConponent.length-1];
        				

        				// obtain the class name.
        				int lastindex = methodName.lastIndexOf(".");
        				String className = methodName.substring(0, lastindex);
        				
        				//obtain the package 1 name
        				lastindex = className.lastIndexOf(".");
        				String package1Name = className.substring(0, lastindex);
        				
//        				//obtain the package 2 name
//        			    lastindex = package1Name.lastIndexOf(".");
//        				String package2Name = package1Name.substring(0, lastindex);

        				// obtain the plugin information
        				String temppluginName = "Not Determined"; 
        				//iterate the pluginlist, and find the time interval 
        				
        				for (PluginUnit p: pluginlist)
        				{
        					if ((Long.parseLong(tempList[3])-Long.parseLong(p.getStartTime()) >0 )&& (Long.parseLong(p.getEndTime()) -Long.parseLong(tempList[4])>0))
        					{
        						temppluginName = p.getPluginName();
        						break;
        					}
        				}

        				
        				
        				//create a new csvunit item
        				EnrichedCSVUnit eci = new EnrichedCSVUnit();
        				eci.setmethodName(methodName);
        				eci.setusedMemory(tempList[1]);
        				eci.settotalMemory(tempList[2]);
        				eci.setStartTime(tempList[3]);
        				eci.setEndTime(tempList[4]);
        				eci.setusedCpu(tempList[5]);
        				eci.setPluginName(temppluginName);
        				eci.setClassName(className);
        				eci.setpackage1Name(package1Name);
        				//eci.setpackage2Name(package2Name);
        				
        				//add the newly created eci to our list
        				list.add(eci);
	        			
        			}
        			//close the CSV file reader
        			reader.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	
        	//serialization the current XES log  
    		FileWriter fw;
    		EnrichedCSVUnit tempClass;
    		try {
    			fw = new FileWriter(enrichedfilename+"\\\\EnrichedMethodLevelLog"+count+".csv", true);
    		
    			BufferedWriter bw = new BufferedWriter(fw);
    			
    			// list iterator
    			Iterator<EnrichedCSVUnit> it = list.iterator();
    			
    	        while(it.hasNext())
    	        {
    	        	tempClass = it.next();
    				bw.write(tempClass.getmehodName()+";"+tempClass.getStartTime()+";"+tempClass.getEndTime()+";"+tempClass.getPluginName()
    				+";"+tempClass.getClassName()+";"+tempClass.getpackage1Name()+";"+tempClass.getusedMemory()
    				+";"+tempClass.gettotalMemory()+";"+tempClass.getusedCpu());
//    				System.out.println(tempClass.getmehodName()+";"+tempClass.getStartTime()+";"+tempClass.getEndTime()+";"+tempClass.getPluginName()
//    				+";"+tempClass.getClassName()+";"+tempClass.getpackage1Name()+";"+tempClass.getpackage2Name()+";"+tempClass.getusedMemory()
//    				+";"+tempClass.gettotalMemory()+";"+tempClass.getusedCpu());
    				bw.newLine();    
    	        }

    			bw.flush(); //update to file
    			bw.close();
    			fw.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//write file by file
    		list.clear();
    		count++;
        }
	}//method

	
	
	public List<PluginUnit> plugins()
	{
		//plugin list stored all plugin information in the file pluginName
		List<PluginUnit> pluginlist = new ArrayList<PluginUnit>();
		
		
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(pluginsName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//each plugin has to lines
		String tempStringS = "";
		String tempStringE = "";
		String [] tempListS; 
		String [] tempListE; 


		
		//read by line, and add to pluginList, Mine Fuzzy Model;end;1444287226182170591
		try {
			while ((tempStringS = reader.readLine()) != null)
			{
				tempStringE = reader.readLine();
				
				
				tempListS = tempStringS.split(";");
				tempListE = tempStringE.split(";");
				
				//obtain (1) plugin name information, tempList[0], (2) timestamp, tempList[2]
				//System.out.println(tempListS[0]+";"+tempListS[2]+";"+tempListE[2]);
				
				// plugin unit
				PluginUnit pu = new PluginUnit(); 
				
				if (tempListS[0].equals(tempListE[0]))
				{
					pu.setPluginName(tempListS[0]);
					pu.setStartTime(tempListS[2]);
					pu.setEndTime(tempListE[2]);
				}
				System.out.println(pu.getPluginName()+";"+pu.getStartTime()+";"+pu.getEndTime());
				//add the newly created plugin unit to pluginlist. 
				pluginlist.add(pu);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// list iterator
		Iterator<PluginUnit> it = pluginlist.iterator();
		
        while(it.hasNext())
        {
        	PluginUnit pu = it.next();
			//System.out.println(pu.getPluginName()+";"+pu.getStartTime()+";"+pu.getEndTime()); 
        }
		return pluginlist;
	}
}
