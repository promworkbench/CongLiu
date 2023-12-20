package congliu.processmining.softwarebehaviordiscovery;

import java.util.ArrayList;

import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

/**
 * an array of hpns
 * @author cliu3
 *
 */
public class HPNArray {

	private ArrayList<HierarchicalPetriNet> hpnArray; 
	
	public HPNArray()
	{
		hpnArray = new ArrayList<HierarchicalPetriNet>();
	}
	
	public void addHPN(HierarchicalPetriNet hpn)
	{
		hpnArray.add(hpn);
	}
	
	public int getSize()
	{
		return hpnArray.size();
	}
	
	public ArrayList<HierarchicalPetriNet> getHPNs()
	{
		return hpnArray;
	}
	
	
}
