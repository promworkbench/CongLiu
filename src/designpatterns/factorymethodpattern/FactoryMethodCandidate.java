package designpatterns.factorymethodpattern;

import java.util.HashSet;

public class FactoryMethodCandidate {

	private String creator ="";
	private HashSet<String> factoryMethodSet = new HashSet<>();
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public HashSet<String> getFactoryMethodSet() {
		return factoryMethodSet;
	}
	public void setFactoryMethodSet(HashSet<String> factoryMethodSet) {
		this.factoryMethodSet = factoryMethodSet;
	}
	
	
}
