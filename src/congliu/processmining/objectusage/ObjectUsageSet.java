package congliu.processmining.objectusage;
/*
 * this class defines the object usage set,
 * each object usage is defined as a mapping from one class 
 * (or multiple classes) to its usage pattern in Petri net. 
 */

import java.util.HashMap;
import java.util.Set;

public class ObjectUsageSet {
	private HashMap<String, PetriNetMarkings> ObjectUsages;
	
	public ObjectUsageSet()
	{
		ObjectUsages = new HashMap<String, PetriNetMarkings>();
	}
	
	public void addUsages(String name, PetriNetMarkings pn)
	{
		ObjectUsages.put(name, pn);
	}
	
	public PetriNetMarkings getUsage(String name)
	{
		return ObjectUsages.get(name);
	}
	
	public Set<String> getGroups()
	{
		return ObjectUsages.keySet();
	}
}
