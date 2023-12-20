package designpatterns.observerpattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * this class defines the basic structure of the discovered observer pattern.
 * @author cliu3
 *
 */
public class ObserverPatternClass extends PatternClass{

	private ClassClass subjectClass =null;
	private ClassClass listernerClass =null;
	private MethodClass notifyMethod=null;
	
	/*
	 * the update, register and de-register method should be added on the basis on dynamic analysis. 
	 */
	
	private MethodClass registerMethod = null;
	private MethodClass de_registerMethod =null;

	private MethodClass updateMethod=null;
	
	
	
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
	public MethodClass getRegisterMethod() {
		return registerMethod;
	}

	public void setRegisterMethod(MethodClass registerMethod) {
		this.registerMethod = registerMethod;
	}

	public MethodClass getDe_registerMethod() {
		return de_registerMethod;
	}

	public void setDe_registerMethod(MethodClass de_registerMethod) {
		this.de_registerMethod = de_registerMethod;
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
        return Objects.hash(subjectClass)+Objects.hash(listernerClass)+7*Objects.hash(notifyMethod)+
        		5*Objects.hash(updateMethod)+2*Objects.hash(registerMethod)+3*Objects.hash(de_registerMethod);
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
	
	// write all informations
	public String toString() 
	{
		return this.subjectClass+","+this.listernerClass+","+this.notifyMethod+","+this.updateMethod+","+this.registerMethod+","+this.de_registerMethod;
		
	}

	
}
