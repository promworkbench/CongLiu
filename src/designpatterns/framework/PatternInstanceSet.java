package designpatterns.framework;

import java.util.Set;

/*
 * the interface of different pattern instance
 */
public interface PatternInstanceSet {
	public Set<PatternClass> getPatternSet();

	public void setSetPatternSet(Set<PatternClass> patternSet);
	
	public void add(PatternClass pc);
}
