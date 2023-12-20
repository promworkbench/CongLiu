package designpatterns.statepattern;

import java.util.HashSet;

/*
 * this class defines the structure of the detected state patterns instances by DPD. 
 * it is only an intermediate data structure to store the original patterns. 
 */
public class StateCandidate {
	
	private String context ="";
	private String state ="";
	private HashSet<String> requestSet = new HashSet<>();
	
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
//	public String getRequest() {
//		return request;
//	}
//	public void setRequest(String request) {
//		this.request = request;
//	}
	public HashSet<String> getRequestSet() {
		return requestSet;
	}
	public void  setRequestSet(HashSet<String> requestSet) {
		this.requestSet = requestSet;
	}
}
