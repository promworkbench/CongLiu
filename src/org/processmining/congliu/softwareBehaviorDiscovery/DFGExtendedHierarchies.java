package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;

// we use the composition of DFGExtended with the hashmap rather than inheritance 
public class DFGExtendedHierarchies{

	// mapping from XEventClass to DFGExtended
	private HashMap<XEventClass, DFGExtended> XEventClass2DFG;
	private DFGExtended dfgExtended;
	
	public DFGExtendedHierarchies(){
		dfgExtended = new DFGExtended (1); 
		XEventClass2DFG = new HashMap<XEventClass, DFGExtended>();
	}

	public HashMap<XEventClass, DFGExtended> getXEventClass2DFG() {
		return XEventClass2DFG;
	}

	public void setXEventClass2DFG(HashMap<XEventClass, DFGExtended> xEventClass2DFG) {
		XEventClass2DFG = xEventClass2DFG;
	}

	public DFGExtended getDfgExtended() {
		return dfgExtended;
	}

	public void setDfgExtended(DFGExtended dfgExtended) {
		this.dfgExtended = dfgExtended;
	}
	
	
}
