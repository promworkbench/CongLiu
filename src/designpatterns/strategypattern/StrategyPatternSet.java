package designpatterns.strategypattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

/**
 * this class defines a set of strategy pattern instances detected from /approved based on the log. 
 * @author cliu3
 *
 */
public class StrategyPatternSet implements PatternSet{
	private Set<PatternClass> strategyPatternSet= new HashSet<PatternClass>();

	public Set<PatternClass> getPatternSet() {
		return strategyPatternSet;
	}

	public void setPatternSet(Set<PatternClass> strategyPatternSet) {
		this.strategyPatternSet = strategyPatternSet;
	}
	
	public void add(PatternClass spc)
	{
		strategyPatternSet.add(spc);
	}
	
	public void addPatternSet(Set<PatternClass> patternSet)
	{
		strategyPatternSet.addAll(patternSet);
	}

	public int size() {
		// TODO Auto-generated method stub
		return strategyPatternSet.size();
	}
	
}
