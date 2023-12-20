package congliu.processmining.classobjectinteractiongraph;

/**
 * this class define the component and instance type for each class object.
 * @author cliu3
 *
 */
public class ComponentInstance {

	private String component ="";
	private String instance ="";
	
	public ComponentInstance(String com, String ins)
	{
		component =com;
		instance =ins;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
}
