package congliu.processmining.dynamicclassobjectprocessgraph;

import java.util.HashMap;
import java.util.Set;

import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

public class ClassObjectProcessGraphs {
	private HashMap<String, HierarchicalPetriNet> class2hpn;
	
	public ClassObjectProcessGraphs()
	{
		class2hpn = new HashMap<String, HierarchicalPetriNet> ();
	}
	
	public void addClassObjectProcessGraph(String Class, HierarchicalPetriNet hpn) {
		class2hpn.put(Class, hpn);
	}
	
	public Set<String> getClassSet()
	{
		return class2hpn.keySet();
	}
	
	public HierarchicalPetriNet getClassHPN(String Class) 
	{
		return class2hpn.get(Class);
	}

	
	public int size()
	{
		return class2hpn.size();
	}
	
}
