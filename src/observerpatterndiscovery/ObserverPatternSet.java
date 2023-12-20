package observerpatterndiscovery;

import java.util.HashSet;
import java.util.Set;

public class ObserverPatternSet {

	private Set<ObserverPatternClass> observerPatternSet= new HashSet<ObserverPatternClass>();

	public Set<ObserverPatternClass> getObserverPatternSet() {
		return observerPatternSet;
	}

	public void setObserverPatternSet(Set<ObserverPatternClass> observerPatternSet) {
		this.observerPatternSet = observerPatternSet;
	}
	
	public void add(ObserverPatternClass opc)
	{
		observerPatternSet.add(opc);
	}
	
}
