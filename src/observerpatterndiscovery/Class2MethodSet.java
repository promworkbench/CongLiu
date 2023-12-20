package observerpatterndiscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * this class defines the class to its methods set. 
 * @author cliu3
 *
 */
public class Class2MethodSet {

	private HashMap<ClassClass, Set<MethodClass>> class2Methodset = new HashMap<ClassClass, Set<MethodClass>> ();

	public HashMap<ClassClass, Set<MethodClass>> getClass2MethodSet() {
		return class2Methodset;
	}

	public void setClass2MethodSet(HashMap<ClassClass, Set<MethodClass>> class2MethodSet) {
		this.class2Methodset = class2MethodSet;
	}
	
	public void add(ClassClass cl, Set<MethodClass> m)
	{
		class2Methodset.put(cl, m);
	}
	
	/**
	 * get the current class set
	 */
	
	public Set<ClassClass> getClassSet()
	{
		return class2Methodset.keySet();
	}
	
	public Set<MethodClass> getMethodSet(ClassClass cl)
	{
		return class2Methodset.get(cl);
	}

	/**
	 * parameter set of a class (sum of all parameter of all methods)
	 * @param cl
	 * @return
	 */
	public Set<String> getMethodParameterSet(ClassClass cl)
	{
		Set<MethodClass> methodSet= class2Methodset.get(cl);
		Set<String> parameterSet = new HashSet<String>();
		for(MethodClass method: methodSet)
		{
			//System.out.println(method.getParameterSet());
			for(String para: method.getParameterSet())
			{
				if(para.length()>0)
				{
					parameterSet.add(para);
				}
				
			}
		}
		return parameterSet;
	}
	
	public String toString() 
	{
		String output = "";
		for(ClassClass c: class2Methodset.keySet())
		{
			output=output+"\n"+c;
			for(MethodClass m: class2Methodset.get(c))
			{
				output = output+"-"+m;
			}
		}
		return output;
		
	}
	
}
