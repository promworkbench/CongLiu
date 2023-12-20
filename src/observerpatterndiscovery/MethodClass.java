package observerpatterndiscovery;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * this class defines the method class, with package, class, method, input parameter types, line number.
 * @author cliu3
 *
 */
public class MethodClass {

	private String packageName;
	private String className;
	private String methodName;
	private int lineNumber;
	private Set<String> parameterSet;
	
	/**
	 *constructor
	 * @param packageName
	 * @param className
	 * @param methodName
	 * @param lineNumber
	 * @param parameterSet
	 */
	public MethodClass(String packageName, String className, String methodName, int lineNumber, Set<String> parameterSet)
	{
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
		this.parameterSet = parameterSet;
	}
	
	/**
	 * default constructor
	 */
	public MethodClass()
	{
		this.packageName = "";
		this.className = "";
		this.methodName = "";
		this.lineNumber = -1;
		this.parameterSet = new HashSet<String>();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Set<String> getParameterSet() {
		return parameterSet;
	}

	public void setParameterSet(Set<String> parameterSet) {
		this.parameterSet = parameterSet;
	}
	
	public int hashCode() {  
        return Objects.hash(packageName)+Objects.hash(className)+ Objects.hash(methodName);
        /**
         * if we think about the 
         */
    }  
	
	public boolean equals(Object other)
	{
		if (this==other)
		{
			return true;
		}
		if (other==null)
		{
			return false;
		}
		if (!(other instanceof MethodClass))
		{
			return false;
		}
		if (this.hashCode()==((MethodClass)other).hashCode()) // check the hashcode.
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String toString() 
	{
		return this.packageName+"."+this.className+"."+this.methodName;
		
	}
	
}
