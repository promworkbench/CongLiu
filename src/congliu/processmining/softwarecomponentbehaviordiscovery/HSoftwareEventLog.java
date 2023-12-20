package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;

/**
 * this class define the hierarchical software event log
 * its structure is (1) mainLog; (2) mapping<eventclass, HSoftwareLog>
 * it is constructed recursively. 
 * @author cliu3
 *
 */
public class HSoftwareEventLog {
	// the main sfotware event log. 
	private XLog mainLog; 
	
//	//nested event list
//	private ArrayList<XEvent> nestedEventList = new ArrayList<XEvent>();
	
	// the mapping from nested eventclass (events) to correponding sub-log. 
	private HashMap<XEventClass, HSoftwareEventLog> subLogMapping;
	
	//constructor
	public HSoftwareEventLog()
	{
		this.mainLog = null;
		this.subLogMapping = null;
		//this.nestedEventList = null;
	}

//	public ArrayList<XEvent> getNestedEventList() {
//		return nestedEventList;
//	}
//
//	public void setNestedEventList(ArrayList<XEvent> nestedEventList) {
//		this.nestedEventList = nestedEventList;
//	}

	public XLog getMainLog() {
		return mainLog;
	}

	public void setMainLog(XLog mainLog) {
		this.mainLog = mainLog;
	}

	public HashMap<XEventClass, HSoftwareEventLog> getSubLogMapping() {
		return subLogMapping;
	}

	public void setSubLogMapping(HashMap<XEventClass, HSoftwareEventLog> subLogMapping) {
		this.subLogMapping = subLogMapping;
	}
	
	
	

}
