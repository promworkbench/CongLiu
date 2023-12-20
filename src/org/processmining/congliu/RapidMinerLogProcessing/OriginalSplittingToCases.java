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
 * this class aims to split and extract the original execution data (method information, start and end time, memory used...) from the mixed file. 
 */
public class OriginalSplittingToCases {
		
	//the original csv file archive
		private  String originalcsvfilename = null; 
		
		//the obtained file archive
		private String splitfilename = null;
		
		
		//constructor
		public OriginalSplittingToCases(String OriginalCSVFileName, String FilteredFileName)
		{
			this.originalcsvfilename = OriginalCSVFileName;
			this.splitfilename = FilteredFileName;
		}
		
		//the main splitting..
		public void splitting ()
		{
			//open readin file directory
			File file = new File(originalcsvfilename); 
			
			File[] filelist = file.listFiles();
			
			//class list, type CSVUnit [], like a case

			List<String> list = new ArrayList<String>();
			int count = 0;

			String tempString = "";
			String [] tempList; 
			String tempS = "";
			String [] memoryString;
			String [] cpuString;
			
			for (File f: filelist)
	        {
	        	//end with .dat
	        	if (f.getName().endsWith(".dat"))
	        	{
	        		//output filename
	        		//System.out.println(f.getName());
	        		BufferedReader reader = null;

	        		try {
	        			reader = new BufferedReader(new FileReader(f));
	        		} catch (FileNotFoundException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	        		
	        		//read by line
	        		try {
	        			while ((tempString = reader.readLine()) != null)
	        			{
	        				tempList = tempString.split(";");
	        				
	        				if (tempList[0].equals("$0"))
	        				{
	        					continue;
	        				}
	        				
	        				//obtain the method related information, tempList[2]
	        				//System.out.println(tempList[2]);
	        				
	        				if (tempList[2].equals("public org.processmining.plugins.log.OpenLogFilePlugin.<init>()"))
	        				{

	        					if (count!=0)
	        					{
	        						FileWriter fw;
	            					try {
	            						fw = new FileWriter(splitfilename+"\\\\MethodLevelLog"+count+".csv", true);
	            					
	            						BufferedWriter bw = new BufferedWriter(fw);
	            						
	            						// list iterator
	            						Iterator<String> it = list.iterator();
	            						
	            				        while(it.hasNext())
	            				        {
	            				        	tempS = it.next();
	            							bw.write(tempS);
	            							//System.out.println(tempS);
	            							bw.newLine();    
	            				        }

	            						bw.flush(); //update to file
	            						bw.close();
	            						fw.close();
	            					} catch (IOException e) {
	            						// TODO Auto-generated catch block
	            						e.printStackTrace();
	            					}
	            					list.clear();
	        					}
	        					
	        					count++;
	        					//the start of one case
	        				}
	        				
	        				memoryString =tempList[3].split(",");
	        				cpuString =tempList[7].split(",");
	        				// method name, used memory, total memory, start time, end time, cpu usage
	    					list.add(tempList[2]+";"+memoryString[0]+";"+tempList[4]+";"+tempList[5]+";"+tempList[6]+";"+cpuString[0]);
	    					
	        			}//while
	        		} catch (IOException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	        	}
	        }	
			
			//write the last case
			FileWriter fw;
			try {
				fw = new FileWriter(splitfilename+"\\\\MethodLevelLog"+count+".csv", true);
			
				BufferedWriter bw = new BufferedWriter(fw);
				
				// list iterator
				Iterator<String> it = list.iterator();
				
		        while(it.hasNext())
		        {
		        	tempS = it.next();
					bw.write(tempS);
					//System.out.println(tempS);
					bw.newLine();    
		        }

				bw.flush(); //update to file
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			

}
