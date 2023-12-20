package org.processmining.congliu.ModularPetriNet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
/**
 * this class defines the modular Petri net, it composed of (1) petri net; 
 * and (2) a mapping from xeventclass to componenAndNesting.
 * @author cliu3
 *
 */
public class ModularPetriNet {

	private Petrinet pn;
	private HashMap<XEventClass, ComponentNesting> xevent2compNest;
	
	public ModularPetriNet()
	{
		this.pn = null; 
		this.xevent2compNest= new HashMap<XEventClass, ComponentNesting>();
	}
	public ModularPetriNet(Petrinet pn, HashMap<XEventClass, ComponentNesting> xevent2compNest)
	{
		this.pn =pn; 
		this.xevent2compNest= xevent2compNest;
	}
	public Petrinet getPn() {
		return pn;
	}
	public void setPn(Petrinet pn) {
		this.pn = pn;
	}
	public HashMap<XEventClass, ComponentNesting> getXevent2compNest() {
		return xevent2compNest;
	}
	public void setXevent2compNest(HashMap<XEventClass, ComponentNesting> xevent2compNest) {
		this.xevent2compNest = xevent2compNest;
	}
	
	public Set<String> getComponentSet()
	{
		HashSet<String> componentSet = new HashSet<String>();
		for(XEventClass xevent: xevent2compNest.keySet())
		{
			componentSet.add(xevent2compNest.get(xevent).getComponent());
		}
		
		return componentSet;
	}
	
}
