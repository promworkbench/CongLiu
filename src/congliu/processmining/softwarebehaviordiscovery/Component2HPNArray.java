package congliu.processmining.softwarebehaviordiscovery;
/**
 * here we assume that the provided and requested interfaces belongs to component, rather that each hpn, therefore it can be easily extended to 
 * handle a component has multiple hpns. 
 * compoennt, hpns, the provided and requested interfaces of the component
 * 
 */
import java.util.HashSet;

import org.deckfour.xes.classification.XEventClass;

public class Component2HPNArray {
	
	private String ComponentName;
	private HPNArray hpnArray;
	
	// xeventclass set of privided and required transitions (eventclasses)
	private HashSet<XEventClass> PEventClass;
	private HashSet<XEventClass> REventClass;
	
	public Component2HPNArray() {
		this.PEventClass = new HashSet<XEventClass>();
		this.REventClass = new HashSet<XEventClass>();
		this.ComponentName = "";
		this.hpnArray  = new HPNArray();
	}
	
	public String getComponentName() {
		return ComponentName;
	}

	public void setComponentName(String componentName) {
		ComponentName = componentName;
	}

	public HPNArray getHpnArray() {
		return hpnArray;
	}

	public void setHpnArray(HPNArray hpnArray) {
		this.hpnArray = hpnArray;
	}

	public HashSet<XEventClass> getPEventClass() {
		return PEventClass;
	}

	public void setPEventClass(HashSet<XEventClass> pEventClass) {
		this.PEventClass.addAll(pEventClass); // appending
	}
	
	
	public HashSet<XEventClass> getREventClass() {
		return REventClass;
	}

	public void setREventClass(HashSet<XEventClass> rEventClass) {
		this.REventClass.addAll(rEventClass); // appending
	}
}
