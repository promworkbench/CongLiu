package org.processmining.congliu.ModularPetriNet;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;


//we use the composition of ModularPetriNet with the hashmap<xeventclass, ModularPetrinetHierarchies> rather than inheritance 
public class ModularPetrinetHierarchies {
	// mapping from XEventClass to DFGExtended
	private HashMap<XEventClass, ModularPetrinetHierarchies> XEventClass2mpnh;
	private ModularPetriNet mpn;
	
	//constructor
	public ModularPetrinetHierarchies()
	{
		this.XEventClass2mpnh = new HashMap<XEventClass, ModularPetrinetHierarchies>();
		this.mpn = new ModularPetriNet();
	}

	public HashMap<XEventClass, ModularPetrinetHierarchies> getXEventClass2mpnh() {
		return XEventClass2mpnh;
	}

	public void setXEventClass2mpnh(HashMap<XEventClass, ModularPetrinetHierarchies> xEventClass2mpnh) {
		XEventClass2mpnh = xEventClass2mpnh;
	}

	public ModularPetriNet getMpn() {
		return mpn;
	}

	public void setMpn(ModularPetriNet mpn) {
		this.mpn = mpn;
	}
	
}
