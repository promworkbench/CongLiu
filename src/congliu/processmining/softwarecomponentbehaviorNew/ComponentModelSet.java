package congliu.processmining.softwarecomponentbehaviorNew;

import java.util.HashMap;
import java.util.Set;

import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

public class ComponentModelSet {

	private HashMap<String, HierarchicalPetriNet> component2HPN; 
	
	public ComponentModelSet()
	{
		component2HPN= new HashMap<String, HierarchicalPetriNet>();
	}
	
	public void addComponentModel(String com, HierarchicalPetriNet hpn) {
		component2HPN.put(com, hpn);
	}
	
	public Set<String> getComponentSet()
	{
		return component2HPN.keySet();
	}
	
	public HierarchicalPetriNet getComponentHPN(String com) 
	{
		return component2HPN.get(com);
	}

	
	public int size()
	{
		return component2HPN.size();
	}
}
