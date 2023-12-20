package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashSet;
/**
 * this class defines the component model, one component has multiple hpns.
 * @author cliu3
 *
 */
public class ComponentModels {
	private String component ="";// component name
	// a set of interface to hpn
	private HashSet<Interface2HPN> i2hpn = new HashSet<Interface2HPN>();
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public HashSet<Interface2HPN> getI2hpn() {
		return i2hpn;
	}
	public void setI2hpn(HashSet<Interface2HPN> i2hpn) {
		this.i2hpn = i2hpn;
	}
	
}
