package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;

// the recursive definition of a hierarchical petri net with multi-instances.
public class HierarchicalPetrinetMultiInstances {
	// mapping from XEventClass to hierarchical petri net with multi-instances
	private HashMap<XEventClass, HierarchicalPetrinetMultiInstances> XEventClass2hpnmi;
	private PetrinetMultiInstances pnmi;
	
	//constructor
	public HierarchicalPetrinetMultiInstances()
	{
		this.XEventClass2hpnmi = new HashMap<XEventClass, HierarchicalPetrinetMultiInstances>();
		this.pnmi = new PetrinetMultiInstances();
	}
	
	public HashMap<XEventClass, HierarchicalPetrinetMultiInstances> getXEventClass2hpnmi() {
		return XEventClass2hpnmi;
	}

	public void setXEventClass2hpnmi(HashMap<XEventClass, HierarchicalPetrinetMultiInstances> xEventClass2hpnmi) {
		XEventClass2hpnmi = xEventClass2hpnmi;
	}

	public PetrinetMultiInstances getPnmi() {
		return pnmi;
	}

	public void setPnmi(PetrinetMultiInstances pnmi) {
		this.pnmi = pnmi;
	}


	
	
	
}
