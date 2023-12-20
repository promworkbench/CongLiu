package congliu.processmining.softwareprocessmining;

import java.util.ArrayList;

public class Component2Classes {
	//the name of a component
	private String component;
	
	//the corresponding classes of the current component
	private ArrayList<String> classes;
	
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public ArrayList<String> getClasses() {
		return classes;
	}
	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}
	
	public String toString()
	{
		return "LC: "+component+";"+classes;
		
	}
}
