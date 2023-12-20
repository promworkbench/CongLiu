package org.processmining.congliu.PreprocessingCSVLog;

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

import prompt.csv.data.CSVLog;

/**
 * this class aims to format the original csv recordings from rapid miner, 
 * this time, we focus on the parameter type, return type, class idenfier, package identifier, runtime module, 
 * belonging module, module attribute, method attribute (constructor or method call) for each method. 
 * 
 * The formatted log should satisfy the standard CSV, such as separated by comma. 
 * @author cliu3
 *
 */
public class FormatCSVLog {
/**
 * This class should do the following formatting work
 * (1) separate method, class, package information
 * (2) matching the runtime module information 
 * (3) matching the belonging module information
 * (4) Based on runtime module and belonging module, determine the module attribute {internal, inter, intra}
 */
	//the original csv file archive
	private String originalcsvfilename = null; 
			
	//the start and end time of each module (plugin)
	private String originalmodulefile = null; 
	
	//the mapping from module to packages
	private String orginalmapping = null;
	
	//the obtained file archive
	private String formattedCSVfile = null;
	
	//constructor
	public FormatCSVLog(String originalfile, String moduleinforfile, String mappinginforfile, String formattedfile)
	{
		this.originalcsvfilename = originalfile;
		this.originalmodulefile = moduleinforfile;
		this.orginalmapping = mappinginforfile;
		this.formattedCSVfile = formattedfile;
		
	}
	
	/*
	 * the original data (captured by the kieker framework) is scattered on different .dat files. 
	 * what we need to do is to retrieve these separated files and recognise the case notions. 
	 * here in our case study, each case start with the following method:
	 * "public org.processmining.plugins.log.OpenLogFilePlugin.<init>()" 
	 */
	public void format()
	{
		//open readin file directory containing the original recording. 
		File file = new File(originalcsvfilename); 
		
		File[] filelist = file.listFiles();
		
		List<AttributeUnit> list = new ArrayList<AttributeUnit>();
		
		// count is used to store the number of cases
		int count = 0;
		
		String tempString = "";
		String [] tempList; 
		
		// we need a mapping from module->package, first readin the static module to packages mapping information; from orginalmapping
		ArrayList<MappingPackage2Module> mappingList = ReadinModulePackageMapping();
		//read in the start and end time of each module from originalmodulefile
		List<ModuleUnit> moduleList = ReadinModuleInformation();
		
		for (File f: filelist)
        {
        	//end with .dat
        	if (f.getName().endsWith(".dat"))
        	{
        		System.out.println(f.getName());
        		BufferedReader reader = null;

        		try 
        		{
        			reader = new BufferedReader(new FileReader(f));
        		} 
        		catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		//read by line
        		try {
        			while ((tempString = reader.readLine()) != null)
        			{
        				tempList = tempString.split(";");
        				
        				//discard those noise data. 
        				if(tempList.length!=10)
        				{
        					continue;
        				}
        				/*
        				 * here for this case:
        				 * tempList[0]:$1 it means the recording types (no use)
        				 * tempList[1]: recording id (no use)
        				 * tempList[2]: method related information, 
        				 * tempList[3]: input parameters, separated by \tab
        				 * tempList[4]: method type: constructor, method call. 
        				 * tempList[5]: start time in nanosecond.
        				 * tempList[6]: end time in nanosecond. 
        				 * tempList[7]: return value
        				 */
        				//exclude the redundant recording. 
        				if (tempList[0].equals("$0"))
        				{
        					continue;
        				}
        				
        				//obtain the method related information, tempList[2]
        				if (tempList[2].equals("public org.processmining.plugins.log.OpenLogFilePlugin.<init>()"))
        				{
        					/*
        					 * if it is not the first case, then serialize the current list (a case).
        					 */
        					if (count>0)
        					{
        						
        						FileWriter fw;
            					try {
            						fw = new FileWriter(formattedCSVfile+"\\\\formatted.csv", true);
            					
            						BufferedWriter bw = new BufferedWriter(fw);
            						
            						// list iterator
            						Iterator<AttributeUnit> it = list.iterator();
            				        while(it.hasNext())
            				        {
            				        	AttributeUnit tempattriUnit = it.next();
            							bw.write(tempattriUnit.serialize());
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

        					//case number++, store the next case
        					count++;
        				}
        				
        				AttributeUnit tempAttriUnit = new AttributeUnit();
    					//set the case id attribute
    					tempAttriUnit.setCaseId("Case"+count);
    					
    					/*
    					 * parse the tempList[2] to obtain method name, class identifier, package identifier, 
    					 * argument type, and return type
    					 */					
        				
        				//the brackets content
        				int indexStartBracket =  tempList[2].indexOf("(");
    					int indexEndBracket =  tempList[2].indexOf(")");
        				
    					//set parameter type
    					if(tempList[2].substring(indexStartBracket+1, indexEndBracket).length()==0)
    					{
    						tempAttriUnit.setParameterType("Null");
    					}
    					else
    					{
    						//the argument type is seperated by comma, we would like to replace them with \t
      						tempAttriUnit.setParameterType(tempList[2].substring(indexStartBracket+1, indexEndBracket).replace(",", "\t"));
    					}
        				
        				//obtain the method related information, methodConponent[methodConponent.length-1]
        				String [] methodConponent = tempList[2].substring(0, indexStartBracket).split(" ");
        				
        				//for constructor, the return is null
        				if(methodConponent.length<=2)
        				{
        					tempAttriUnit.setReturnType("Null");
        				}
        				else 
        				{
        					tempAttriUnit.setReturnType(methodConponent[1]);
						}	
        				
        				//obtain the method name, class name, package name
        					
        				String methodRelatedInfor = methodConponent[methodConponent.length-1];	
        				
        				int lastdotindex = methodRelatedInfor.lastIndexOf(".");
        				
        				String methodname = methodRelatedInfor.substring(lastdotindex+1, methodRelatedInfor.length());
        				
    					//set the method name attribute
        				tempAttriUnit.setMethodname(methodname+"()");
        				
        				int secondlastdotindex = methodRelatedInfor.substring(0, lastdotindex).lastIndexOf(".");
        				
        				//set the class identifier
        				tempAttriUnit.setClassiIdentifier(methodRelatedInfor.substring(secondlastdotindex+1, lastdotindex));
    					
    					//set the package identifier
    					tempAttriUnit.setPackageIdentifier(methodRelatedInfor.substring(0, secondlastdotindex));
    					

    					//set the return value
    					tempAttriUnit.setReturnValue(tempList[7]);
    					
    					//set the parameter value tempList[3]: input parameters, separated by \tab
    					tempAttriUnit.setParamterValue(tempList[3]);
    					
    					//set method type, try parseInt, if failed then discard this method recording..
    					int tempInt;
    					try {
    					    tempInt = Integer.parseInt(tempList[4]);
    					  } catch (NumberFormatException e) {
    					    continue;
    					  }
    					if(tempInt==0)
    					{
    						tempAttriUnit.setMethodType("Constructor");
    					}
    					else {
    						tempAttriUnit.setMethodType("MethodCall");
						}
    					
    					//tempList[5]: start time in nanosecond. tempList[6]
    					//start time
    					tempAttriUnit.setStartTime(tempList[5]);
    					//end time nanosecond??
    					tempAttriUnit.setEndTime(tempList[6]);
    					
    					/*
    					 *  match the runtime module information for each method. 
    					 *  those unmatched methods are discarded from our log.  
    					 */
    					    					
    					for (ModuleUnit module: moduleList)
    					{
    						if ((Long.parseLong(tempList[5])-Long.parseLong(module.getStartTime()) >0 )&& 
    								(Long.parseLong(module.getEndTime())-Long.parseLong(tempList[6])>0))
    						{
    							//set the runtime module
    							tempAttriUnit.setRuntimeModule(module.getModuleName());
    							break;
    						}
    					}
    					
    					//if the runtime module cannot be matched, this recording is discarded. 
    					if (tempAttriUnit.getRuntimeModule()==null)
    					{
    						continue;
    					}
    					
    					
    					//set the belonging module, this information should be determined by the package information. 					
    					//System.out.println(mappingList);
    					int flag =0;
    					for(MappingPackage2Module m: mappingList)
    					{
    						for (String pac: m.getPackages())
    							if(methodRelatedInfor.substring(0, secondlastdotindex).startsWith(pac))
    							{
    								tempAttriUnit.setBelongingModule(m.getModule());
    								flag=1;
    								break;
    							}
    						if(flag==1)
    						{    							
    							break;
    						}
    					}
    					if(flag==0)
    					{
    						System.out.println(methodRelatedInfor.substring(0, secondlastdotindex));
    						//for those unmatched methods we need to add the Not available notion for further processing
    						tempAttriUnit.setBelongingModule("Not Available");
    					}
    					flag =0;
    					
     					//set the method attribute{internal, inter, intra}
    					//for packages start with org.deckfour.xes, we label this method with module "XES Library"
    					if (tempAttriUnit.getBelongingModule().equals(tempAttriUnit.getRuntimeModule()))
    					{
    						tempAttriUnit.setMethodAttributed("internal");
    					}
    					else if(tempAttriUnit.getBelongingModule().equals("XES Library"))
    					{
    						tempAttriUnit.setMethodAttributed("intra");
    					}
    					else {
    						tempAttriUnit.setMethodAttributed("inter");
						}
    					
    					list.add(tempAttriUnit);
    					
        			}//while
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }	
		
		//serialize the last case
		FileWriter fw;
		try {
			fw = new FileWriter(formattedCSVfile+"\\\\formatted.csv", true);
		
			BufferedWriter bw = new BufferedWriter(fw);
			
			// list iterator
			Iterator<AttributeUnit> it = list.iterator();
	        while(it.hasNext())
	        {
	        	AttributeUnit tempattriUnit = it.next();
				bw.write(tempattriUnit.serialize());
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
	
	
	//to obtain an ArrayList containing all modules (and its corresponding mapping to packages)
	public ArrayList<MappingPackage2Module> ReadinModulePackageMapping()
	{
		ArrayList<MappingPackage2Module> mappingList = new ArrayList<MappingPackage2Module>();
		
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(orginalmapping));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String tempString= "";
		String [] tempList; 

		//read by line, and add to mappingList,
		try {
			while ((tempString = reader.readLine()) != null)
			{
				//tempString = reader.readLine();
				
				tempList = tempString.split(";");
				
				//obtain (1) module name information, tempListS[0], (2) corresponding packages tempList[1]...
				
				// mapping unit
				MappingPackage2Module mm = new MappingPackage2Module(); 
				ArrayList<String> pacArray = new ArrayList<String>();
				mm.setModule(tempList[0]);
				
				for(int i=1; i<tempList.length;i++)
				{
					pacArray.add(tempList[i]);
				}
				
				mm.setPackages(pacArray);

				mappingList.add(mm);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mappingList;
	}
	
	
	// to obtain an ArrayList containing all module information, start/end time and module name. 
	public List<ModuleUnit> ReadinModuleInformation()
	{
		//module list store all module information in the file pluginName
		List<ModuleUnit> moduleList = new ArrayList<ModuleUnit>();
		
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(originalmodulefile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//each module corresponds two lines
		String tempStringS = "";
		String tempStringE = "";
		String [] tempListS; 
		String [] tempListE; 

		//read by line, and add to moduleList, Mine Fuzzy Model;end;1444287226182170591
		try {
			while ((tempStringS = reader.readLine()) != null)
			{
				tempStringE = reader.readLine();
				
				tempListS = tempStringS.split(";");
				tempListE = tempStringE.split(";");
				
				//obtain (1) module name information, tempListS[0], (2) timestamp, tempListS[2]
				//System.out.println(tempListS[0]+";"+tempListS[2]+";"+tempListE[2]);
				
				// module unit
				ModuleUnit mu = new ModuleUnit(); 
				
				if (tempListS[0].equals(tempListE[0])) //they belong to the same module
				{
					mu.setModuleName(tempListS[0]);
					mu.setStartTime(tempListS[2]);
					mu.setEndTime(tempListE[2]);
				}

				moduleList.add(mu);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		// list iterator
//		Iterator<PluginUnit> it = pluginlist.iterator();
//		
//        while(it.hasNext())
//        {
//        	PluginUnit pu = it.next();
//			System.out.println(pu.getPluginName()+";"+pu.getStartTime()+";"+pu.getEndTime()); 
//        }
		return moduleList;
	}
		
	
	
	public void formatCSV(final CSVLog csvlog)
	{
		//open readin file directory containing the original recording. 
		File file = new File(originalcsvfilename); 
		
		File[] filelist = file.listFiles();
		
		List<AttributeUnit> list = new ArrayList<AttributeUnit>();
		
		// count is used to store the number of cases
		int count = 0;
		
		String tempString = "";
		String [] tempList; 
		
		// we need a mapping from module->package, first readin the static module to packages mapping information; from orginalmapping
		ArrayList<MappingPackage2Module> mappingList = ReadinModulePackageMapping();
		//read in the start and end time of each module from originalmodulefile
		List<ModuleUnit> moduleList = ReadinModuleInformation();
		
		for (File f: filelist)
        {
        	//end with .dat
        	if (f.getName().endsWith(".dat"))
        	{
        		System.out.println(f.getName());
        		BufferedReader reader = null;

        		try 
        		{
        			reader = new BufferedReader(new FileReader(f));
        		} 
        		catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		//read by line
        		try {
        			while ((tempString = reader.readLine()) != null)
        			{
        				tempList = tempString.split(";");
        				
        				//discard those noise data. 
        				if(tempList.length!=10)
        				{
        					continue;
        				}
        				/*
        				 * here for this case:
        				 * tempList[0]:$1 it means the recording types (no use)
        				 * tempList[1]: recording id (no use)
        				 * tempList[2]: method related information, 
        				 * tempList[3]: input parameters, separated by \tab
        				 * tempList[4]: method type: constructor, method call. 
        				 * tempList[5]: start time in nanosecond.
        				 * tempList[6]: end time in nanosecond. 
        				 * tempList[7]: return value
        				 */
        				//exclude the redundant recording. 
        				if (tempList[0].equals("$0"))
        				{
        					continue;
        				}
        				
        				//obtain the method related information, tempList[2]
        				if (tempList[2].equals("public org.processmining.plugins.log.OpenLogFilePlugin.<init>()"))
        				{
        					/*
        					 * if it is not the first case, then serialize the current list (a case).
        					 */
        					if (count>0)
        					{
        						// list iterator
        						Iterator<AttributeUnit> it = list.iterator();
        				        while(it.hasNext())
        				        {
        				        	AttributeUnit tempattriUnit = it.next();
        				        	csvlog.addLine(tempattriUnit.serialize());  
        				        	System.out.println(tempattriUnit.serialize());

        				        }
            					list.clear();
        					}

        					//case number++, store the next case
        					count++;
        				}
        				
        				AttributeUnit tempAttriUnit = new AttributeUnit();
    					//set the case id attribute
    					tempAttriUnit.setCaseId("Case"+count);
    					
    					/*
    					 * parse the tempList[2] to obtain method name, class identifier, package identifier, 
    					 * argument type, and return type
    					 */					
        				
        				//the brackets content
        				int indexStartBracket =  tempList[2].indexOf("(");
    					int indexEndBracket =  tempList[2].indexOf(")");
        				
    					//set parameter type
    					if(tempList[2].substring(indexStartBracket+1, indexEndBracket).length()==0)
    					{
    						tempAttriUnit.setParameterType("Null");
    					}
    					else
    					{
    						//the argument type is seperated by comma, we would like to replace them with \t
      						tempAttriUnit.setParameterType(tempList[2].substring(indexStartBracket+1, indexEndBracket).replace(",", "\t"));
    					}
        				
        				//obtain the method related information, methodConponent[methodConponent.length-1]
        				String [] methodConponent = tempList[2].substring(0, indexStartBracket).split(" ");
        				
        				//for constructor, the return is null
        				if(methodConponent.length<=2)
        				{
        					tempAttriUnit.setReturnType("Null");
        				}
        				else 
        				{
        					tempAttriUnit.setReturnType(methodConponent[1]);
						}	
        				
        				//obtain the method name, class name, package name
        					
        				String methodRelatedInfor = methodConponent[methodConponent.length-1];	
        				
        				int lastdotindex = methodRelatedInfor.lastIndexOf(".");
        				
        				String methodname = methodRelatedInfor.substring(lastdotindex+1, methodRelatedInfor.length());
        				
    					//set the method name attribute
        				tempAttriUnit.setMethodname(methodname+"()");
        				
        				int secondlastdotindex = methodRelatedInfor.substring(0, lastdotindex).lastIndexOf(".");
        				
        				//set the class identifier
        				tempAttriUnit.setClassiIdentifier(methodRelatedInfor.substring(secondlastdotindex+1, lastdotindex));
    					
    					//set the package identifier
    					tempAttriUnit.setPackageIdentifier(methodRelatedInfor.substring(0, secondlastdotindex));
    					

    					//set the return value
    					tempAttriUnit.setReturnValue(tempList[7]);
    					
    					//set the parameter value tempList[3]: input parameters, separated by \tab
    					tempAttriUnit.setParamterValue(tempList[3]);
    					
    					//set method type, try parseInt, if failed then discard this method recording..
    					int tempInt;
    					try {
    					    tempInt = Integer.parseInt(tempList[4]);
    					  } catch (NumberFormatException e) {
    					    continue;
    					  }
    					if(tempInt==0)
    					{
    						tempAttriUnit.setMethodType("Constructor");
    					}
    					else {
    						tempAttriUnit.setMethodType("MethodCall");
						}
    					
    					//tempList[5]: start time in nanosecond. tempList[6]
    					//start time
    					tempAttriUnit.setStartTime(tempList[5]);
    					//end time nanosecond??
    					tempAttriUnit.setEndTime(tempList[6]);
    					
    					/*
    					 *  match the runtime module information for each method. 
    					 *  those unmatched methods are discarded from our log.  
    					 */
    					    					
    					for (ModuleUnit module: moduleList)
    					{
    						if ((Long.parseLong(tempList[5])-Long.parseLong(module.getStartTime()) >0 )&& 
    								(Long.parseLong(module.getEndTime())-Long.parseLong(tempList[6])>0))
    						{
    							//set the runtime module
    							tempAttriUnit.setRuntimeModule(module.getModuleName());
    							break;
    						}
    					}
    					
    					//if the runtime module cannot be matched, this recording is discarded. 
    					if (tempAttriUnit.getRuntimeModule()==null)
    					{
    						continue;
    					}
    					
    					
    					//set the belonging module, this information should be determined by the package information. 					
    					//System.out.println(mappingList);
    					int flag =0;
    					for(MappingPackage2Module m: mappingList)
    					{
    						for (String pac: m.getPackages())
    							if(methodRelatedInfor.substring(0, secondlastdotindex).startsWith(pac))
    							{
    								tempAttriUnit.setBelongingModule(m.getModule());
    								flag=1;
    								break;
    							}
    						if(flag==1)
    						{    							
    							break;
    						}
    					}
    					if(flag==0)
    					{
    						System.out.println(methodRelatedInfor.substring(0, secondlastdotindex));
    						//for those unmatched methods we need to add the Not available notion for further processing
    						tempAttriUnit.setBelongingModule("Not Available");
    					}
    					flag =0;
    					
     					//set the method attribute{internal, inter, intra}
    					//for packages start with org.deckfour.xes, we label this method with module "XES Library"
    					if (tempAttriUnit.getBelongingModule().equals(tempAttriUnit.getRuntimeModule()))
    					{
    						tempAttriUnit.setMethodAttributed("internal");
    					}
    					else if(tempAttriUnit.getBelongingModule().equals("XES Library"))
    					{
    						tempAttriUnit.setMethodAttributed("intra");
    					}
    					else {
    						tempAttriUnit.setMethodAttributed("inter");
						}
    					
    					list.add(tempAttriUnit);
    					
        			}//while
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }	
		
		// add the last case to the csvLog. 
		Iterator<AttributeUnit> it = list.iterator();
        while(it.hasNext())
        {
        	AttributeUnit tempattriUnit = it.next();
        	csvlog.addLine(tempattriUnit.serialize());  
        	System.out.println(tempattriUnit.serialize());
        }
	}
	
	
	//new method
}
