package observerpatterndiscovery;

import java.util.Objects;
import java.util.Set;

/**
 * this class defines the basic structure of the discovered observer pattern.
 * @author cliu3
 *
 */
public class ObserverPatternClass {

	private ClassClass subjectClass =null;
	private ClassClass listernerClass =null;
	private MethodClass notifyMethod=null;
	private Set<MethodClass> de_registeringMethod=null; // based on the current appraoch, it is not easy to distingurish the registing and unregistering.
	//private MethodClass deregisteringMethod=null;
	private MethodClass updateMethod=null;
	
	
	public Set<MethodClass> getDe_registeringMethod() {
		return de_registeringMethod;
	}

	public void setDeregisteringMethod(Set<MethodClass> de_registeringMethod) {
		this.de_registeringMethod = de_registeringMethod;
	}
	
	public ClassClass getSubjectClass() {
		return subjectClass;
	}

	public void setSubjectClass(ClassClass subjectClass) {
		this.subjectClass = subjectClass;
	}

	public ClassClass getListernerClass() {
		return listernerClass;
	}

	public void setListernerClass(ClassClass listernerClass) {
		this.listernerClass = listernerClass;
	}

	public MethodClass getNotifyMethod() {
		return notifyMethod;
	}

	public void setNotifyMethod(MethodClass notifyMethod) {
		this.notifyMethod = notifyMethod;
	}

	public MethodClass getUpdateMethod() {
		return updateMethod;
	}

	public void setUpdateMethod(MethodClass updateMethod) {
		this.updateMethod = updateMethod;
	}

	/**
	 * the equals determine the way to distinguish observer pattern,
	 * <case 1> the same subject class and same listener can be invoked multiple times in different locations, 
	 * this can be realized by adding the line number attribute in the hashcode of MethodClass.
	 * <case 2> the same subject class and same listener invoked in the same location, but with different instances, 
	 * this can be realized by adding the class object information for the current attributes, and add this to the hashcode.
	 */
	public int hashCode() {  
        return Objects.hash(subjectClass)+Objects.hash(listernerClass)+Objects.hash(notifyMethod);
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
		if (!(other instanceof ObserverPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((ObserverPatternClass)other).hashCode()) // check the hashcode.
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
		return this.subjectClass+","+this.listernerClass+","+this.notifyMethod+","+this.updateMethod+","+this.de_registeringMethod;
		
	}
}
