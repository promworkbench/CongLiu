package designpatterns.framework;

import java.util.HashSet;
import java.util.Set;

public class PatternSetImpl implements PatternSet{
	private Set<PatternClass> patternSet= new HashSet<PatternClass>();
	
	public Set<PatternClass> getPatternSet() {
		// TODO Auto-generated method stub
		return patternSet;
	}

	public void setPatternSet(Set<PatternClass> patternSet) {
		// TODO Auto-generated method stub
		this.patternSet = patternSet;
		
	}

	public void add(PatternClass pc) {
		// TODO Auto-generated method stub
		this.patternSet.add(pc);
	}

	public int size() {
		// TODO Auto-generated method stub
		return patternSet.size();
	}

	public void addPatternSet(Set<PatternClass> patternSet) {
		// TODO Auto-generated method stub
		this.patternSet.addAll(patternSet);
	}

}
