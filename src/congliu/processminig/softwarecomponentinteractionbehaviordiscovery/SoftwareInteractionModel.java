package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashMap;

public class SoftwareInteractionModel {

	// the component to interfaces model part
	private ComponentModelsSet componentModelSet = new ComponentModelsSet();
	// the interaction model part
	InteractionModels interactionModels = new InteractionModels();
	// interface to cardinality part
	HashMap<Interface, Integer> interfaceCardinality = new HashMap<Interface, Integer>();
	
	public ComponentModelsSet getComponentModelSet() {
		return componentModelSet;
	}
	public void setComponentModelSet(ComponentModelsSet componentModelSet) {
		this.componentModelSet = componentModelSet;
	}
	public InteractionModels getInteractionModels() {
		return interactionModels;
	}
	public void setInteractionModels(InteractionModels interactionModels) {
		this.interactionModels = interactionModels;
	}
	public HashMap<Interface, Integer> getInterfaceCardinality() {
		return interfaceCardinality;
	}
	public void setInterfaceCardinality(HashMap<Interface, Integer> interfaceCardinality) {
		this.interfaceCardinality = interfaceCardinality;
	}
	
}
