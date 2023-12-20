package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class InteractionModels 
{
	private HashMap<XEventClass, Petrinet> interactionModel = new HashMap<XEventClass, Petrinet>();
	public void addInteractionModel(XEventClass xevent, Petrinet pn)
	{
		interactionModel.put(xevent, pn);
	}
	public HashMap<XEventClass, Petrinet> getInteractionModels() {
		return interactionModel;
	}
	
}
