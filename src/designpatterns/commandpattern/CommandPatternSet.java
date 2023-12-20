package designpatterns.commandpattern;

import java.util.HashSet;
import java.util.Set;

import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;

/**
 * this class defines a set of command pattern instances detected from/approved based on the log. 
 * @author cliu3
 *
 */
public class CommandPatternSet implements PatternSet{
	
	private Set<PatternClass> commandPatternSet= new HashSet<PatternClass>();

	public Set<PatternClass> getPatternSet() {
		return commandPatternSet;
	}

	public void setPatternSet(Set<PatternClass> commandPatternSet) {
		this.commandPatternSet = commandPatternSet;
	}
	
	public void add(PatternClass spc)
	{
		commandPatternSet.add(spc);
	}
	
	public void addPatternSet(Set<PatternClass> patternSet)
	{
		commandPatternSet.addAll(patternSet);
	}

	public int size() {
		// TODO Auto-generated method stub
		return commandPatternSet.size();
	}
}
