package org.processmining.congliu.RapidMinerLogProcessing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

public class XLogFunctions {

	//logs
	public static String getID(XLog log) {
		try {
			return XConceptExtension.instance().extractName(log); 
		} catch (Exception ex) {
			return "";
		}
	}
	
	public static String[] getEvents(XLog log) {
		ArrayList<String> evts = new ArrayList<String>();
		for (int i=0; i<log.size(); i++) {
			XTrace trace = log.get(i);
			for (int j=0; j<trace.size(); j++) {
				String event = getName(trace.get(j));
				if (!evts.contains(event))
					evts.add(event);
			}			
		}
		return toStringArray(evts);
	}
	private static String[] toStringArray(ArrayList<String> list) {
		String[] result = new String[list.size()];
		for (int i=0; i<list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
	
	public static String[] getAttributes(XLog log) {
		ArrayList<String> atts = new ArrayList<String>();
		for (int i=0; i<log.size(); i++) {
			XTrace trace = log.get(i);
			addAttribute(atts, trace.getAttributes());
			for (int j=0; j<trace.size(); j++) {
				XEvent event = trace.get(j);
				addAttribute(atts, event.getAttributes());
			}			
		}
		return toStringArray(atts);
	}
	private static void addAttribute(ArrayList<String> atts, XAttributeMap map)
	{
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i].toString();
			if (!atts.contains(key))
				atts.add(key);
		}
	}
	
	//traces
	public static String getName(XTrace trace) {
		String res = "";
		try {
			res = XConceptExtension.instance().extractName(trace); 
		} catch (Exception ex) {
		}
		return res;
	}
	public static void setName(XTrace trace, String name) {
		try {
			trace.getAttributes().put("concept:name",new XAttributeLiteralImpl("concept:name", name)); 
		} catch (Exception ex) {
		}
	}
	public static String getID(XTrace trace) {
		try {
			return XConceptExtension.instance().extractName(trace); 
		} catch (Exception ex) {
			return "";
		}
	}
	
	public static void addEventToTrace(XTrace trace, XEvent newEvent) {
		for (int i = 0; i < trace.size(); i++) {
			XEvent event = trace.get(i);
			if (getTime(event).getTime() > getTime(newEvent).getTime()) {
				trace.add(i, newEvent);
				return;
			}
		}
		trace.add(newEvent); // if no position was found, insert event at the end
	}
		
	//events
	public static String getName(XEvent event) {
		String res = "";
		try {
			res = XConceptExtension.instance().extractName(event); 
		} catch (Exception ex) {
		}
		return res;
	}
	public static void setName(XEvent event, String name) {
		try {
			event.getAttributes().put("concept:name",new XAttributeLiteralImpl("concept:name", name)); 
		} catch (Exception ex) {
		}
	}
	public static String getTransition(XEvent event) {
		String res = "";
		try {
			res = XLifecycleExtension.instance().extractTransition(event);
		} catch (Exception ex) {
		}
		return res;
	}
	public static void setTransition(XEvent event, String transition) {
		try {
			event.getAttributes().put("lifecycle:transition",new XAttributeLiteralImpl("lifecycle:transition", transition));
		} catch (Exception ex) {
		}
	}
	public static Date getTime(XEvent event) {
		Date res = new Date();
		try {
			res = XTimeExtension.instance().extractTimestamp(event); 
		} catch (Exception ex) {
		}
		return res;
	}
	public static void setTime(XEvent event, Date date) {
		try {
			event.getAttributes().put("time:timestamp",new XAttributeTimestampImpl("time:timestamp", date)); 
		} catch (Exception ex) {
		}
	}
	public static String getResource(XEvent event) {
		String res = "";
		try {
			res = XOrganizationalExtension.instance().extractResource(event); 
		} catch (Exception ex) {
		}
		return res;
	}
	public static void setResource(XEvent event, String resource) {
		try {
			event.getAttributes().put("organizational:resource",new XAttributeLiteralImpl("organizational:resource", resource)); 
		} catch (Exception ex) {
		}
	}
	
	//Attributes
	public static boolean equals(XAttribute att1, XAttribute att2) {
		if (att1.getClass().equals(XAttributeBooleanImpl.class) &&
			att2.getClass().equals(XAttributeBooleanImpl.class) )
				return (((XAttributeBooleanImpl)att1).getValue() == ((XAttributeBooleanImpl)att2).getValue());
		else if ( (
				    att1.getClass().equals(XAttributeContinuousImpl.class) || 
				    att1.getClass().equals(XAttributeDiscreteImpl.class)     
				  ) &&
				  (
				    att2.getClass().equals(XAttributeContinuousImpl.class) || 
					att2.getClass().equals(XAttributeDiscreteImpl.class)     
				  )
				) {
				double d1 = (att1.getClass().equals(XAttributeContinuousImpl.class)) ?
						    ((XAttributeContinuousImpl)att1).getValue() : 
						    ((XAttributeDiscreteImpl)att1).getValue();
				double d2 = (att2.getClass().equals(XAttributeContinuousImpl.class)) ?
						    ((XAttributeContinuousImpl)att2).getValue() : 
						    ((XAttributeDiscreteImpl)att2).getValue();
				return d1 == d2;
		}
		else if (att1.getClass().equals(XAttributeLiteralImpl.class) &&
				 att2.getClass().equals(XAttributeLiteralImpl.class) )
			return ((XAttributeLiteralImpl)att1).getValue().equals(((XAttributeLiteralImpl)att2).getValue());
		else if (att1.getClass().equals(XAttributeTimestampImpl.class) &&
				 att2.getClass().equals(XAttributeTimestampImpl.class) )
			return ((XAttributeTimestampImpl)att1).getValue().compareTo(((XAttributeTimestampImpl)att2).getValue()) == 0;
		else
			return false;
	}
	public static String getStringValue(XAttribute att) {
		if (att.getClass().equals(XAttributeBooleanImpl.class))
			return ((XAttributeBooleanImpl)att).getValue() ? "true" : "false";
		else if (att.getClass().equals(XAttributeContinuousImpl.class))
			return Double.toString(((XAttributeContinuousImpl)att).getValue());
		else if (att.getClass().equals(XAttributeDiscreteImpl.class))
			return Long.toString(((XAttributeDiscreteImpl)att).getValue());
		else if (att.getClass().equals(XAttributeLiteralImpl.class))
			return ((XAttributeLiteralImpl)att).getValue();
		else if (att.getClass().equals(XAttributeTimestampImpl.class)) {
			SimpleDateFormat f = new SimpleDateFormat();
			f.applyPattern("yyyy.MM.dd HH:mm:ss");
			return f.format(((XAttributeTimestampImpl)att).getValue());
		}
		else
			return "";
	}
	
	public static double getDoubleValue(XAttribute att) {
		if (att.getClass().equals(XAttributeContinuousImpl.class))
			return ((XAttributeContinuousImpl)att).getValue();
		else {
			try {
				return Double.parseDouble(getStringValue(att));
			}
			catch (NumberFormatException nfe) {
				return 0.0;
			}
		}
	}
	public static long getLongValue(XAttribute att) {
		if (att.getClass().equals(XAttributeDiscreteImpl.class))
			return ((XAttributeDiscreteImpl)att).getValue();
		else {
			try {
				return Long.parseLong(getStringValue(att));
			}
			catch (NumberFormatException nfe) {
				return 0;
			}
		}
	}
	
	public static void putLiteral(XAttributeMap attMap, String key, String value) {
		attMap.put(key, new XAttributeLiteralImpl(key, value));
	}

	public static void putTimestamp(XAttributeMap attMap, String key, Date value) {
		attMap.put(key, new XAttributeTimestampImpl(key, value));
	}

	public static XAttributeMap copyAttMap(XAttributeMap srcAttMap) {
		XAttributeMap destAttMap = new XAttributeMapImpl();
		Iterator<XAttribute> attit = srcAttMap.values().iterator();
		while (attit.hasNext()) {
			XAttribute att = attit.next();
			String key = att.getKey();
			att = (XAttribute) att.clone();
			destAttMap.put(key, att);
		}
		return destAttMap;
	}
}
