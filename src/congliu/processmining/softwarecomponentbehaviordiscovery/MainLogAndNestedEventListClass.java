package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashSet;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

//this class is used to store the mainLog and nested event list
public class MainLogAndNestedEventListClass {
	
	// the main sfotware event log. 
	private XLog mainLog =null; 
	
	//nested event list
	//private ArrayList<XEvent> nestedEventList = new ArrayList<XEvent>();
	
	//nested event set, to avoid reperive elements
	private HashSet<XEvent> nestedEventSet = new HashSet<XEvent>();

	public XLog getMainLog() {
		return mainLog;
	}

	public HashSet<XEvent> getNestedEventSet() {
		return nestedEventSet;
	}

	public void setNestedEventSet(HashSet<XEvent> nestedEventSet) {
		this.nestedEventSet = nestedEventSet;
	}

	public void setMainLog(XLog mainLog) {
		this.mainLog = mainLog;
	}

//	public ArrayList<XEvent> getNestedEventList() {
//		return nestedEventList;
//	}
//
//	public void setNestedEventList(ArrayList<XEvent> nestedEventList) {
//		this.nestedEventList = nestedEventList;
//	}
	

	
}
