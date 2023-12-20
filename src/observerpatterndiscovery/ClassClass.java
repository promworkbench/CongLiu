package observerpatterndiscovery;

import java.util.Objects;

public class ClassClass {
	private String packageName;
	private String className;
	
	/**
	 * default constructor
	 */
	public ClassClass()
	{
		this.packageName = "";
		this.className = "";
	}
	
	public ClassClass(String packageName, String className)
	{
		this.packageName = packageName;
		this.className = className;
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
	
	public int hashCode() {  
        return Objects.hash(packageName)+Objects.hash(className);
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
		if (!(other instanceof ClassClass))
		{
			return false;
		}
		if (this.hashCode()==((ClassClass)other).hashCode()) // check the hashcode.
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
		return this.packageName+"."+this.className;
		
	}
	

}
