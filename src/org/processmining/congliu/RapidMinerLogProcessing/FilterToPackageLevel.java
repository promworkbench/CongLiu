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


public class FilterToPackageLevel 
{
	/*
	 * the input is method level event logs
	 */
	
	//the input file archive
	private String methodlevefilename = null;
	
	// the obtained file archive
	private String packagefilename = null; 
	
	public FilterToPackageLevel(String method, String packagefile)
	{
		this.methodlevefilename = method; 
		this.packagefilename = packagefile;
	}
	
	//filtering method-level logs to package-level
		public void filteringToPackageLevel () throws IOException
		{
			
			//open readin file directory
			File file = new File(methodlevefilename); 
			
			File[] filelist = file.listFiles();
			
			//EnrichedCSVUnit list
			
			List<PackageUnit> list = new ArrayList<PackageUnit>();
			
			int count = 1;
			for (File f: filelist)
	        {
        		//output filename
        		System.out.println(f.getName());
        		BufferedReader reader = null;

        		try 
        		{
        			reader = new BufferedReader(new FileReader(f));
        		} catch (FileNotFoundException e) 
        		{
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		String tempString = "";
        		String [] tempList; 
        	
        		
        		while ((tempString = reader.readLine()) != null)
    			{
    				tempList = tempString.split(";");
    				
    				//create a new package unit item
    				PackageUnit pu = new PackageUnit();
    				
    				pu.setStartTime(tempList[1]);
    				pu.setEndTime(tempList[2]);
    				pu.setPluginName(tempList[3]);
    				pu.setpackageName(tempList[5]);
    				pu.setusedMemory(tempList[6]);
    				pu.settotalMemory(tempList[7]);
    				pu.setusedCpu(tempList[8]);

    				//add the newly created eci to our list
    				if (list.isEmpty())
    				{
    					list.add(pu);
    				}
    				else 
    				{
    					//get the last added item
    					PackageUnit lastItem = list.get(list.size()-1);
    					
    					//if the last added package is the same with the current one. then updating its time information 
    					
    					if (lastItem.getpackageName().equals(pu.getpackageName()))
    					{
    						
    						pu.setStartTime(lastItem.getStartTime());
    						pu.setPluginName(lastItem.getPluginName());
    						pu.setusedCpu(lastItem.getusedCpu());
    						list.remove(list.size()-1);
    						
    						list.add(pu);
    					}
    					else 
    					{
    						list.add(pu);
    					}
    				}
    			}

        		reader.close();
        		
        		//serialization of package-level event log
        		FileWriter fw;
        		PackageUnit temp;
      
        		fw = new FileWriter(packagefilename+"\\\\PackageLevelLog"+count+".csv", true);
        		
    			BufferedWriter bw = new BufferedWriter(fw);
    			
    			// list iterator
    			Iterator<PackageUnit> it = list.iterator();
    			
    	        while(it.hasNext())
    	        {
    	        	temp = it.next();
    				bw.write(temp.getpackageName()+";"+temp.getStartTime()+";"+temp.getEndTime()+";"+temp.getPluginName()+";"+
    						temp.getusedMemory()+";"+temp.gettotalMemory()+";"+temp.getusedCpu());
    				bw.newLine();    
    	        }

    			bw.flush(); //update to file
    			bw.close();
    			fw.close();
    			
        		count++;
        		list.clear();
    			
	    		        	
	        }//for
			
			
			
		}
	
}
