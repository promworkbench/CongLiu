package congliu.processmining.softwareinterfacediscovery;

import java.util.HashMap;
import java.util.Set;

import observerpatterndiscovery.ClassClass;

/*
 * component is a string, and class is represented as class.method name.  
 */
public class ComponentConfig {
	HashMap<String, Set<ClassClass>> com2class;
	
	public ComponentConfig()
	{
		com2class =new HashMap<String, Set<ClassClass>>();
	}
	
	public void add(String component, Set<ClassClass> classes)
	{
		com2class.put(component, classes);
	}
	
	public Set<String> getAllComponents()
	{
		return com2class.keySet();
	}
	
	public Set<ClassClass> getClasses(String component)
	{
		return com2class.get(component);
	}
}
