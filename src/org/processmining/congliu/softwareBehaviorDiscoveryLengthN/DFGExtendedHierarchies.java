package org.processmining.congliu.softwareBehaviorDiscoveryLengthN;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.congliu.softwareBehaviorDiscovery.DFGExtended;

// we use the composition of DFGExtended with the hashmap<xeventclass, DFGExtended> rather than inheritance 
public class DFGExtendedHierarchies{

	// mapping from XEventClass to DFGExtended
	private HashMap<XEventClass, DFGExtendedHierarchies> XEventClass2DFGH;
	
	private DFGExtended dfgExtended;
	
	public DFGExtendedHierarchies(){
		dfgExtended = new DFGExtended (1); 
		XEventClass2DFGH = new HashMap<XEventClass, DFGExtendedHierarchies>();
	}

	public HashMap<XEventClass, DFGExtendedHierarchies> getXEventClass2DFGH() {
		return XEventClass2DFGH;
	}

	public void setXEventClass2DFGH(HashMap<XEventClass, DFGExtendedHierarchies> xEventClass2DFG) {
		XEventClass2DFGH = xEventClass2DFG;
	}

	public DFGExtended getDfgExtended() {
		return dfgExtended;
	}

	public void setDfgExtended(DFGExtended dfgExtended) {
		this.dfgExtended = dfgExtended;
	}
	
	
}
