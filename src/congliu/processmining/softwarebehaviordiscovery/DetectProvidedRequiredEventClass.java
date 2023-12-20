package congliu.processmining.softwarebehaviordiscovery;

import java.util.HashSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * this class is used to detect provided and required event classes from event logs, 
 * Interface type, 0: internal component event, 1: required event, 2: provided event
 * provided 
 * @author cliu3
 *
 */
public class DetectProvidedRequiredEventClass {

	/**
	 * 
	 * @param log
	 * @return
	 */
	public static HashSet<XEventClass> providedEventclass(XLog log, XEventClassifier classifier)
	{
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(log, classifier);
		HashSet<XEventClass> pEventSet = new HashSet<XEventClass>();
		
		for (XTrace trace: log)
		{
			for (XEvent event: trace)
			{
				if (event.getAttributes().get("concept:name").toString().contains("main()"))
				{
					continue;
				}
				if (event.getAttributes().get("Interface_Type").toString().equals("2"))
				{
					pEventSet.add(Xloginfo.getEventClasses().getClassOf(event));
				}
			}
		}
		
		for (XEventClass xeventc: pEventSet)
		{
			System.out.println("P: "+ xeventc);
		}
		
		return pEventSet;
	}
	
	/**
	 * 
	 * @param log
	 * @return
	 */
	public static HashSet<XEventClass> requiredEventclass(XLog log, XEventClassifier classifier)
	{
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(log, classifier);
		HashSet<XEventClass> rEventSet = new HashSet<XEventClass>();
		
		for (XTrace trace: log)
		{
			for (XEvent event: trace)
			{
				if (event.getAttributes().get("Interface_Type").toString().equals("1"))
				{
					rEventSet.add(Xloginfo.getEventClasses().getClassOf(event));
				}
			}
		}
		
		for (XEventClass xeventc: rEventSet)
		{
			System.out.println("R: "+ xeventc);
		}
		
		return rEventSet;
	}
}
