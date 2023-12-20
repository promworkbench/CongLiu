package designpatterns.framework;

import java.util.Set;

/*
 * pattern instance set
 */
public interface PatternSet {
	
	public Set<PatternClass> getPatternSet();

	public void setPatternSet(Set<PatternClass> patternSet);
	
	public void add(PatternClass pc);
	
	public void addPatternSet(Set<PatternClass> patternSet);
	
	public int size();
	
}
