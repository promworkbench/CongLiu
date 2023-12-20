package designpatterns.observerpattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
/**
 * this class defines a set of observer pattern instances detected from the log. 
 * @author cliu3
 *
 */
public class ObserverPatternSet implements PatternSet{

	private Set<PatternClass> observerPatternSet= new HashSet<PatternClass>();

	public Set<PatternClass> getPatternSet() {
		return observerPatternSet;
	}

	public void setPatternSet(Set<PatternClass> observerPatternSet) {
		this.observerPatternSet = observerPatternSet;
	}
	
	public void add(PatternClass opc)
	{
		observerPatternSet.add(opc);
	}

	public int size() {
		// TODO Auto-generated method stub
		return observerPatternSet.size();
	}

	public void addPatternSet(Set<PatternClass> patternSet) {
		// TODO Auto-generated method stub
		
		observerPatternSet.addAll(patternSet);
		
	}


	
}
