package designpatterns.visitorpattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

/**
 * this class defines a set of visitor pattern instances detected from /approved based on the log. 
 * @author cliu3
 *
 */
public class VisitorPatternSet implements PatternSet {
	
	private Set<PatternClass> visitorPatternSet= new HashSet<PatternClass>();

	public Set<PatternClass> getPatternSet() {
		return visitorPatternSet;
	}

	public void setPatternSet(Set<PatternClass> visitorPatternSet) {
		this.visitorPatternSet = visitorPatternSet;
	}
	
	public void add(PatternClass spc)
	{
		visitorPatternSet.add(spc);
	}
	
	public void addPatternSet(Set<PatternClass> patternSet)
	{
		visitorPatternSet.addAll(patternSet);
	}

	public int size() {
		// TODO Auto-generated method stub
		return visitorPatternSet.size();
	}
}
