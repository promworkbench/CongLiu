package congliu.processmining.softwarebehaviordiscovery;

import java.util.HashSet;

import org.processmining.plugins.graphviz.dot.DotElement;

/**
 * this class define the component id 
 * a set of provided interfaces (node id)
 * a set of required interfaces (node id)
 * @author cliu3
 *
 */
public class Component2Interfaces {

	private DotElement ComponentID; // the component (dot cluster) ID 
	private final HashSet<DotElement> PTIDs; // the provided transitions (dot) IDs;
	private final HashSet<DotElement> RTIDs; // the required transitions (dot) IDs;
	
	// constructor
	public Component2Interfaces()
	{
		ComponentID = null;
		this.PTIDs = new HashSet<DotElement>();
		this.RTIDs = new HashSet<DotElement>();
	}

	// automatically generated setters and getters
	public DotElement getComponentID() {
		return this.ComponentID;
	}

	public void setComponentID(DotElement componentID) {
		this.ComponentID = componentID;
	}

	public HashSet<DotElement> getPTIDs() {
		return this.PTIDs;
	}

	/**
	 * 
	 * @param pTIDs
	 */
	public void setPTIDs(HashSet<DotElement> pTIDs) 
	{
		this.PTIDs.addAll(pTIDs); // appending
	}

	public HashSet<DotElement> getRTIDs() {
		return this.RTIDs;
	}
	
	/**
	 * 
	 * @param rTIDs
	 */

	public void setRTIDs(HashSet<DotElement> rTIDs) {
		this.RTIDs.addAll(rTIDs); //appending
	}
	
	
	
}
