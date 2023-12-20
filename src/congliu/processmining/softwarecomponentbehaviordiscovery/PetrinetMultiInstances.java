package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

/**
 * this method defined the basic of petri net extended with multi-instances.
 * 
 * The base petri net is an ordinary one which can be discovered using exsiting mining algorithm. 
 * specially, it contains block transitions correspond with a sub-net each. 
 * @author cliu3
 *
 */
public class PetrinetMultiInstances {
	
	// the petri net part
	Petrinet pn = null;
	// the block to sub-net mapping. a block is a transition in the main petri net. 
	HashMap<XEventClass, Petrinet> block2subnet= new HashMap<XEventClass, Petrinet>();
	
	//getters and setters
	public Petrinet getPn() {
		return pn;
	}
	public void setPn(Petrinet pn) {
		this.pn = pn;
	}
	public HashMap<XEventClass, Petrinet> getBlock2subnet() {
		return block2subnet;
	}
	public void setBlock2subnet(HashMap<XEventClass, Petrinet> block2subnet) {
		this.block2subnet = block2subnet;
	}
	
	
	
	
}
