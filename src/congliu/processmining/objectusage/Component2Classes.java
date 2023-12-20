package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.Set;

public class Component2Classes {
	HashMap<String, Set<String>> c2cs;
	
	public Component2Classes()
	{
		c2cs =new HashMap<String, Set<String>>();
	}
	
	public void add(String component, Set<String> classes)
	{
		c2cs.put(component, classes);
	}
	
	public Set<String> getAllComponents()
	{
		return c2cs.keySet();
	}
	
	public Set<String> getClasses(String component)
	{
		return c2cs.get(component);
	}
}
