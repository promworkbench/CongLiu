package org.processmining.congliu.softwareBehaviorDiscovery;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * this class aims to detect nested method call from event log with lifecycle information. 
 * Input: XLog, each events with lifecycle information
 * Principle: execution interval containment or overlapping. 
 * Output: Main Log+ Mapping<eventclass, sub-log>
 * Limitations: the generated event logs are output to D:\\EventLogs. 
 * @author cliu3
 *
 */
@Plugin(
		// plugin name
		name = "Nested Method Call Detection (Using Lifecycle Information)",
		
		//return labels
		returnLabels = {"Filtered Log"}, 
		// return class
		returnTypes = {List.class},
		
		userAccessible = true,
		help = "This plugin aims to removes the plugin method calling information from the origianl log using lifecycle", 
		
		//input labels, corresponding with the second parameter of main function
		parameterLabels = {"ProM Software Execution Log"} 
		)
		
public class NestedMethodCallDetectionPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Nested Method Call Detection, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public static List<XLog> filtering (PluginContext context, XLog inputlog) throws Exception 
	{
		NestedMethodCallDetection nestedDetection = new NestedMethodCallDetection(inputlog);
		
		XLog filteredMainLog= nestedDetection.getFilteredLog();
		
		FileOutputStream fosgzm = new FileOutputStream ("D:\\EventLogs\\NestedMethodCallDetectionResult\\"+filteredMainLog.getAttributes().get("concept:name")+".xes"); 
		new XesXmlSerializer().serialize(filteredMainLog, fosgzm); 
		fosgzm.close();
		
		HashMap<XEventClass, XLog> xeventclass2Sublog = nestedDetection.getXeventClass2XLog();
		
		System.out.println("the size of xeventclass2sublog is: "+ xeventclass2Sublog.size());
		//traverse the hashmap
		for (Entry<XEventClass, XLog>  entry : xeventclass2Sublog.entrySet()){
			//entry.getKey();
			//entry.getValue();
			FileOutputStream fosgz = new FileOutputStream ("D:\\EventLogs\\NestedMethodCallDetectionResult\\"+entry.getKey().toString().split("\\+")[2]+".xes"); 
			//FileOutputStream fos = new FileOutputStream ("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
			
			new XesXmlSerializer().serialize(entry.getValue(), fosgz); 
            // serialize to xes.gz
			//new XesXmlGZIPSerializer().serialize(log, fosgz);

			fosgz.close();
		}
		
		List<XLog> listLog = new ArrayList<XLog>();
				
		return  listLog;
	}
}