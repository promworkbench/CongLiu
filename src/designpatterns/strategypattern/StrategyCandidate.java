package designpatterns.strategypattern;

import java.util.HashSet;

/*
 * this class defines the structure of the detected strategy patterns instances by DPD. 
 * it is only an intermediate data structure to store the original patterns. 
 */
public class StrategyCandidate {

	private String context ="";
	private String strategy ="";
	private HashSet<String> contextInterfaceSet = new HashSet<>();
	
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public HashSet<String> getContextInterfaceSet() {
		return contextInterfaceSet;
	}
	public void setContextInterfaceSet(HashSet<String> contextInterfaceSet) {
		this.contextInterfaceSet = contextInterfaceSet;
	}
	
}
