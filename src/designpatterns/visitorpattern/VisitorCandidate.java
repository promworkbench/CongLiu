package designpatterns.visitorpattern;

import java.util.HashSet;

/*
 * this class defines the structure of the detected visitor patterns instances by DPD. 
 * it is only an intermediate data structure to store the original patterns. 
 */
public class VisitorCandidate {
	private String element ="";
	private String visitor ="";
	private HashSet<String> acceptSet = new HashSet<>();
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getVisitor() {
		return visitor;
	}
	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}
	public HashSet<String> getAcceptSet() {
		return acceptSet;
	}
	public void setAcceptSet(HashSet<String> acceptSet) {
		this.acceptSet = acceptSet;
	}
	
}
