package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

public class Interface2HPN {
	Interface inter;
	HierarchicalPetriNet hpn;
	public Interface2HPN(Interface inter, HierarchicalPetriNet hpn)
	{
		this.inter = inter;
		this.hpn= hpn;
	}
	
	public void setInterface(Interface inter) 
	{
		this.inter = inter;
	}
	
	public Interface getInterface()
	{
		return this.inter;
	}
	
	public void setHPN(HierarchicalPetriNet hpn)
	{
		this.hpn= hpn;
	}
	
	public HierarchicalPetriNet getHPN()
	{
		return this.hpn;
	}
}