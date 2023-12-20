package org.processmining.congliu.softwareBehaviorDiscovery;

import org.deckfour.xes.classification.XEventClass;

/**
 * this class extends the XEventClass to contain component and nested information 
 * @author cliu3
 *
 */
public class XEventClassExtended implements Comparable<XEventClassExtended>{

	private XEventClass xeventclass =null; // the event class name based on the classifier 
	private String componentName ="";
	private String nestedFlag=""; //true or false
	public XEventClass getXeventclass() {
		return xeventclass;
	}
	public void setXeventclass(XEventClass xeventclass) {
		this.xeventclass = xeventclass;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getNestedFlag() {
		return nestedFlag;
	}
	public void setNestedFlag(String nestedFlag) {
		this.nestedFlag = nestedFlag;
	}
	
	// equals method should first define the hashcode. this is used to detect same elements in set 
	public int hashCode() {
		return this.xeventclass.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof XEventClassExtended) {
			return this.xeventclass.equals(((XEventClassExtended)o).getXeventclass());
		} else {
			return false;
		}
	}
	
	//implement the comparable interface
	public int compareTo(XEventClassExtended o) {
		// TODO Auto-generated method stub
		return this.xeventclass.compareTo(o.getXeventclass());
	}

}
