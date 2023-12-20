package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;

/**
 * this class is defined to store the muliti-instance block detection (filtering)
 * results. it includes two parts, 
 * (1) the main log with multi-instances log being replaced by blocks.
 * (2) the mapping: from (block transition) to (sub-log)
 * @author cliu3
 *
 */
public class EventLogwithBlocksClass {

	// the main software event log with block transitions. 
	private XLog mainLogwithBlocks; 
	
	// the mapping from block eventclass (events) to correponding sub-log. 
	private HashMap<XEventClass, XLog> block2subLog;
	
	//constructor
	public EventLogwithBlocksClass()
	{
		this.mainLogwithBlocks = null;
		this.block2subLog = null;
		//this.nestedEventList = null;
	}

	public XLog getMainLogwithBlocks() {
		return mainLogwithBlocks;
	}

	public void setMainLogwithBlocks(XLog mainLogwithBlocks) {
		this.mainLogwithBlocks = mainLogwithBlocks;
	}

	public HashMap<XEventClass, XLog> getBlock2subLog() {
		return block2subLog;
	}

	public void setClock2subLog(HashMap<XEventClass, XLog> clock2subLog) {
		this.block2subLog = clock2subLog;
	}
	
	

	
	
}
