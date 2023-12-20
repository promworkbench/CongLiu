package designpatterns.adapterpattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

public class AdapterPatternSet implements PatternSet{
	
	private Set<PatternClass> adapterPatternSet= new HashSet<PatternClass>();
	
	public Set<PatternClass> getPatternSet() {
		return adapterPatternSet;
	}

	public void setPatternSet(Set<PatternClass> adapterPatternSet) {
		this.adapterPatternSet = adapterPatternSet;
	}
	
	public void add(PatternClass opc)
	{
		adapterPatternSet.add(opc);
	}

	public int size() {
		// TODO Auto-generated method stub
		return adapterPatternSet.size();
	}

	public void addPatternSet(Set<PatternClass> patternSet) {
		// TODO Auto-generated method stub
		adapterPatternSet.addAll(patternSet);
	}
}
