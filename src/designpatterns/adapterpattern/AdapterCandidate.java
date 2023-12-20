package designpatterns.adapterpattern;

import java.util.HashSet;

public class AdapterCandidate {

	private String adapter ="";
	private String adaptee ="";
	private HashSet<String> requestSet = new HashSet<>();
	
	
	public String getAdapter() {
		return adapter;
	}
	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}
	public String getAdaptee() {
		return adaptee;
	}
	public void setAdaptee(String adaptee) {
		this.adaptee = adaptee;
	}
	public HashSet<String> getRequestSet() {
		return requestSet;
	}
	public void setRequestSet(HashSet<String> request) {
		this.requestSet = request;
	}
	
	
	
}
