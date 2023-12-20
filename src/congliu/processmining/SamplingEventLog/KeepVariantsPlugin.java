package congliu.processmining.SamplingEventLog;
/*
 * this plug-in takes in an event log and returns another event log. 
 * the returned event log only contains each variant in the input log once.
 * 		
 * the first step is to remove all similar case invariants and keeps only one case for each invariants
 * [<a, b>2, <a, b, c>3]==>[<a, b>,<a, b, c>]
 * refer to the LogEnhancement package
 */

import java.util.Collection;
import java.util.HashMap;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.log.utils.TraceVariantByClassifier;

@Plugin(
		name = "Filtering Event Log (Keep Variants)",// plugin name
		
		returnLabels = {"An Event Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Big Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to return an event log that only contains each variant in the input log once." 
		)
public class KeepVariantsPlugin {
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
	public static XLog keepVariants(UIPluginContext context, XLog originalLog)
	{
		//set the xeventclass
		XEventClasses eventclass = createEventClasses(originalLog);
		//for each trace, we compute its hashcode based on the xeventclass.
		HashMap<Integer, XTrace> hashcodeToTrace = new HashMap<>();
		for(XTrace trace:originalLog)
		{		
			TraceVariantByClassifier tv = new TraceVariantByClassifier(trace, eventclass);
			if(!hashcodeToTrace.keySet().contains(tv.hashCode()))
			{
				hashcodeToTrace.put(tv.hashCode(), trace);
			}
		}
		//construct a mapping from hashcode to traces
		XLog singleVariantLog = (XLog)originalLog.clone();
		singleVariantLog.clear();
		for(int key :hashcodeToTrace.keySet())
		{
			singleVariantLog.add(hashcodeToTrace.get(key));
		}
		//finally re-construct the traces
		return singleVariantLog;
	}
	
	public static XEventClasses createEventClasses(Collection<XTrace> traces) {
		XEventClassifier classifier = obtainClassifier(traces);
		return createEventClasses(traces, classifier);
	}
	public static XEventClasses createEventClasses(Collection<XTrace> traces, XEventClassifier classifier) {
		if (traces instanceof XLog) {
			XLog log = (XLog) traces;
			XLogInfo existingLogInfo = log.getInfo(classifier);
			if (existingLogInfo != null) {
				return existingLogInfo.getEventClasses();
			}
		}
		return deriveEventClasses(classifier, traces);
	}

	private static XEventClasses deriveEventClasses(XEventClassifier classifier, Collection<XTrace> traces) {
		XEventClasses nClasses = new XEventClasses(classifier);
		for (XTrace trace : traces) {
			nClasses.register(trace);
		}
		nClasses.harmonizeIndices();
		return nClasses;
	}

	private static XEventClassifier obtainClassifier(Collection<XTrace> traces) {
		if (traces instanceof XLog) {
			XLog log = (XLog) traces;
			for (XEventClassifier classifier : log.getClassifiers()) {
				if ((classifier.getDefiningAttributeKeys().length == 1
						&& classifier.getDefiningAttributeKeys()[0].equals(XConceptExtension.KEY_NAME))
						|| classifier.equals(XLogInfoImpl.NAME_CLASSIFIER)) {
					return classifier;
				}
			}
			return !log.getClassifiers().isEmpty() ? log.getClassifiers().get(0) : XLogInfoImpl.NAME_CLASSIFIER;
		} else {
			return XLogInfoImpl.NAME_CLASSIFIER;
		}
	}

}
