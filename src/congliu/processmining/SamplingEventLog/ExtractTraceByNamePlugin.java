package congliu.processmining.SamplingEventLog;

import java.util.HashSet;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.helper.ProMUIHelper;
import org.processmining.framework.util.ui.widgets.helper.UserCancelledException;

/*
 * this plugin takes a log as input and return a log by keeping only the specified trace names. 
 */
@Plugin(
		name = "Extract traces by name in log",// plugin name
		
		returnLabels = {"An Event Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Big Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to return an event log that only contains traces with input names." 
		)
public class ExtractTraceByNamePlugin {
		@UITopiaVariant(
		        affiliation = "TU/e", 
		        author = "Cong liu", 
		        email = "c.liu.3@tue.nl OR liucongchina@163.com"
		        )
		@PluginVariant(
				variantLabel = "Filtering Event Log, default",
				// the number of required parameters, {0} means one input parameter
				requiredParameterLabels = {0}
				)
		public static XLog extractTrace(UIPluginContext context, XLog originalLog)
		{
			final Progress progress = context.getProgress();
			try {
				// the ProMUI Help really simplified the way to add inputs. 
				//String[] traceIndexToKeep = ProMUIHelper.queryForString(context, "Specify Trace Names to be extracted (Separated by Comma: 1,2,3,...)").split(",");
				String[] traceIndexToKeep = ProMUIHelper.queryForString(context, "Specify Trace Names to be extracted (Separated by Comma: 1,2,3,...)").split("\\s");

				HashSet<String> hSet = new HashSet<String>(traceIndexToKeep.length);
	            for (int i = 0; i < traceIndexToKeep.length; i++)
	                hSet.add(traceIndexToKeep[i].trim());
				return extractTracesByName(originalLog, hSet, progress);
			} catch (NumberFormatException e) {
				context.log(e);
				return null;
			} catch (UserCancelledException e) {
				context.log(e);
				return null;
			}
		}
		
		public static XLog extractTracesByName(XLog log, HashSet<String> traceNameToKeep, Progress progress) {
			XFactory factory =XFactoryRegistry.instance().currentDefault();
			XLog newLog = (XLog)log.clone();
			newLog.clear();
			progress.setMaximum(log.size());
			
			for(XTrace trace: log)
			{
				if(traceNameToKeep.contains(trace.getAttributes().get("concept:name").toString()))
				{
					XTrace newTrace = factory.createTrace((XAttributeMap) trace.getAttributes().clone());
					for (XEvent e : trace) {
						newTrace.add(factory.createEvent((XAttributeMap) e.getAttributes().clone()));
					}
					newLog.add(newTrace);
				}
				progress.inc();
			}

			return newLog;
		}
	
}
