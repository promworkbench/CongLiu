package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashSet;

/**
 * a group of component model.
 * @author cliu3
 *
 */
public class ComponentModelsSet {
	private HashSet<ComponentModels> component2HPNSet; 
	
	public ComponentModelsSet()
	{
		component2HPNSet= new HashSet<ComponentModels>();
	}

	public HashSet<ComponentModels> getComponent2HPNSet() {
		return component2HPNSet;
	}

	public void addComponent2HPNSet(ComponentModels componenModels) {
		component2HPNSet.add(componenModels);
	}
	
	public int size()
	{
		return component2HPNSet.size();
	}
	
}
