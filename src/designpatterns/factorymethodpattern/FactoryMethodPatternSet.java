package designpatterns.factorymethodpattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

public class FactoryMethodPatternSet implements PatternSet{
	
	private Set<PatternClass> factoryPatternSet= new HashSet<PatternClass>();
	
	public Set<PatternClass> getPatternSet() {
		return factoryPatternSet;
	}

	public void setPatternSet(Set<PatternClass> factoryPatternSet) {
		this.factoryPatternSet = factoryPatternSet;
	}
	
	public void add(PatternClass fpc)
	{
		factoryPatternSet.add(fpc);
	}

	public int size() {
		// TODO Auto-generated method stub
		return factoryPatternSet.size();
	}

	public void addPatternSet(Set<PatternClass> patternSet) {
		// TODO Auto-generated method stub
		factoryPatternSet.addAll(patternSet);
	}
}
