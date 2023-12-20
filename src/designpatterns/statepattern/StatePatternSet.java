package designpatterns.statepattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

/**
 * this class defines a set of state pattern instances detected from /approved based on the log. 
 * @author cliu3
 *
 */
public class StatePatternSet implements PatternSet{
	private Set<PatternClass> statePatternSet= new HashSet<PatternClass>();

	public Set<PatternClass> getPatternSet() {
		return statePatternSet;
	}

	public void setPatternSet(Set<PatternClass> statePatternSet) {
		this.statePatternSet = statePatternSet;
	}
	
	public void add(PatternClass spc)
	{
		statePatternSet.add(spc);
	}
	
	public void addPatternSet(Set<PatternClass> patternSet)
	{
		statePatternSet.addAll(patternSet);
	}

	public int size() {
		// TODO Auto-generated method stub
		return statePatternSet.size();
	}
	
}
