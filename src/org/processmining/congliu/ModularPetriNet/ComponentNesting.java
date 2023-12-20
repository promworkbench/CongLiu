package org.processmining.congliu.ModularPetriNet;

/**
 * this class is used to store the component and nesting information for each xeventclass
 * @author cliu3
 *
 */
public class ComponentNesting {

	private String component="";
	private String nesting="";
	
	public ComponentNesting(String component, String nesting)
	{
		this.component=component;
		this.nesting=nesting;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getNesting() {
		return nesting;
	}
	public void setNesting(String nesting) {
		this.nesting = nesting;
	}
	
}
