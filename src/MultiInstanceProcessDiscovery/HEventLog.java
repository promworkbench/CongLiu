package MultiInstanceProcessDiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;

/**
 * this class define the hierarchical event log
 * its structure is (1) mainLog; (2) mapping<XEventClass, HEventLog> where the XEventClass refers to a nested activity that is included in the mainLog. 
 * it is constructed recursively. 
 * @author cliu3
 *
 */
public class HEventLog {
	// the main event log. 
	private XLog mainLog; 
	
	// the mapping from nested activities (name) to corresponding sub-log. Note that only complete events are included. 
	private HashMap<XEventClass, HEventLog> subLogMapping;
	
	//constructor
	public HEventLog()
	{
		this.mainLog = null;
		this.subLogMapping = null;
	}

	public XLog getMainLog() {
		return mainLog;
	}

	public void setMainLog(XLog mainLog) {
		this.mainLog = mainLog;
	}

	public HashMap<XEventClass, HEventLog> getSubLogMapping() {
		return subLogMapping;
	}

	public void setSubLogMapping(HashMap<XEventClass, HEventLog> subLogMapping) {
		this.subLogMapping = subLogMapping;
	}
}
