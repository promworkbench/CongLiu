package observerpatterndiscovery;

import java.util.HashSet;
import java.util.Set;

public class ClassObjectsMethodSet {

	private HashSet<ClassObjectsMethod> classObjectsMethodSet = new HashSet<>();
	
	public int getSize()
	{
		return classObjectsMethodSet.size();
	}
	
	public void add(ClassObjectsMethod com)
	{
		classObjectsMethodSet.add(com);
	}
	
	public HashSet<ClassObjectsMethod> getAll()
	{
		return classObjectsMethodSet;
	}
	
	/**
	 * get the invoked class set
	 * @return
	 */
	public Set<ClassClass> getClassSet()
	{
		Set<ClassClass> classSet = new HashSet<>();
		
		for(ClassObjectsMethod com: classObjectsMethodSet)
		{
			classSet.add(com.getInvokedClass());
		}
		
		return classSet;
	}
	
	/**
	 * get the object set of the current class
	 * @param c
	 * @return
	 */
	public Set<String> getObjectSet(ClassClass c)
	{
		for(ClassObjectsMethod com: classObjectsMethodSet)
		{
			if(com.getInvokedClass().equals(c))
			{
				return com.getInvokedClassObjects();
			}
		}
		return new HashSet<String>();
	}
	
	/**
	 * get the method of the current class
	 * @param c
	 * @return
	 */
	public MethodClass getMethod(ClassClass c)
	{
		MethodClass mc = new MethodClass();
		for(ClassObjectsMethod com: classObjectsMethodSet)
		{
			if(com.getInvokedClass().equals(c))
			{
				mc=com.getInvokedMethod();
				break;
			}
		}
		return mc;
	}
}
