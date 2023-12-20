package congliu.processmining.SamplingEventLog;
/*
 * this plugin aims to sampling an input example log and returns a small sample log with statistic guarantees. 
 * Step 1: each trace is transformed to a featured vertor, we implement multiple approaches to the transformation
 * Step 2: we compute the similarity for each two traces (vectors) using consine similarity
 * Step 3: using text ranking algorithm to get a sub-set of representative event logs. 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


@Plugin(
		name = "Event Log Sampling",// plugin name
		
		returnLabels = {"A Software Event Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Big Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to pre-process software event log collected by Maikel Leemans XPort instrumentation." 
		)

public class SamplingPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Sampling Big Event Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public static XLog sampling(UIPluginContext context, XLog originalLog)
	{
			
		ConvertTraceToVector ctv = new ConvertTraceToVector();

		//keep an ordered list of traces names. 
		ArrayList<String> TraceIdList = new ArrayList<>();
		
		//convert the log to a map, the key is the name of the trace, and the value is the trace. 
		HashMap<String, XTrace> nameToTrace = new HashMap<>();
		
		for(XTrace trace: originalLog)
		{
			TraceIdList.add(trace.getAttributes().get("concept:name").toString());
			nameToTrace.put(trace.getAttributes().get("concept:name").toString(), trace);
		}
		
		// the similarity matrix of the log
		double[][] matrix = new double[TraceIdList.size()][TraceIdList.size()];
		for(int i=0;i<TraceIdList.size();i++)
		{
			for(int j =0;j<TraceIdList.size();j++)
			{
				//get the trace similarity	
				matrix[i][j]=ctv.CosineSimilarity(ctv.Trace2FeatureMap(nameToTrace.get(TraceIdList.get(i))),ctv.Trace2FeatureMap(nameToTrace.get(TraceIdList.get(j))));
			}
		}
		
		//output the similarity matrix as a text file
		BufferedWriter bw = null;
		try {
			//Specify the file name and path here
			
			File file = new File("D:/My Papers/[10] sampling event log/"+originalLog.getAttributes().get("concept:name").toString()+".txt");
			
			// This logic will make sure that the file gets created if it is not present at the specified location
			if(!file.exists()) 
			{
			  file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			//output the activity
			for(int i =0;i<TraceIdList.size();i++)
			{
				bw.write("\t"+TraceIdList.get(i));
			}
			
			for(int i = 0;i<TraceIdList.size();i++)
			{
				bw.newLine();
				bw.write(TraceIdList.get(i));
				for(int j =0;j<TraceIdList.size();j++)
				{
					bw.write("\t"+matrix[i][j]);
				}
			}
			if(bw!=null)
			bw.close();
		} catch (IOException ioe) {
			   ioe.printStackTrace();
			}
		
		  
		
//		System.out.print(" ");
//		//output the activity
//		for(int i =0;i<TraceIdList.size();i++)
//		{
//			System.out.print("\t"+TraceIdList.get(i));
//		}
//		
//		
//		for(int i = 0;i<TraceIdList.size();i++)
//		{
//			System.out.println();
//			System.out.print(TraceIdList.get(i));
//			for(int j =0;j<TraceIdList.size();j++)
//			{
//				System.out.print("\t"+matrix[i][j]);
//			}
//		}
//		
//		System.out.println();
		return originalLog;
	}	 
	
}
