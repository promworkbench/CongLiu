package observerpatterndiscovery;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * this class defines the invoked strucutre, a class, multiple class object, a method
 * @author cliu3
 *
 */
public class ClassObjectsMethod {

	private ClassClass invokedClass = new ClassClass();
	private MethodClass invokedMethod = new MethodClass();
	private Set<String> invokedClassObjects = new HashSet<String>();
	
	public ClassClass getInvokedClass() {
		return invokedClass;
	}
	public void setInvokedClass(ClassClass invokedClass) {
		this.invokedClass = invokedClass;
	}
	public MethodClass getInvokedMethod() {
		return invokedMethod;
	}
	public void setInvokedMethod(MethodClass invokedMethod) {
		this.invokedMethod = invokedMethod;
	}
	public Set<String> getInvokedClassObjects() {
		return invokedClassObjects;
	}
	public void setInvokedClassObjects(Set<String> invokedClassObjects) {
		this.invokedClassObjects = invokedClassObjects;
	}	
	
	public int hashCode() {  
        return Objects.hash(invokedClass)+Objects.hash(invokedMethod);
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
		if (!(other instanceof ClassObjectsMethod))
		{
			return false;
		}
		if (this.hashCode()==((ClassObjectsMethod)other).hashCode()) // check the hashcode.
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
		return this.invokedClass+","+this.invokedMethod+","+this.invokedClassObjects;
		
	}
}
