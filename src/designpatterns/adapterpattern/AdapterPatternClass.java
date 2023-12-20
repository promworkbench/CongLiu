package designpatterns.adapterpattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class AdapterPatternClass extends PatternClass{
	
	private ClassClass adapterClass =null;
	private ClassClass adapteeClass =null;
	private MethodClass requestMethod=null;
	
	/*
	 * the specificRequest method should be added on the basis on dynamic analysis. 
	 */
	
	private MethodClass specificRequestMethod = null;

	public ClassClass getAdapterClass() {
		return adapterClass;
	}

	public void setAdapterClass(ClassClass adapterClass) {
		this.adapterClass = adapterClass;
	}

	public ClassClass getAdapteeClass() {
		return adapteeClass;
	}

	public void setAdapteeClass(ClassClass adapteeClass) {
		this.adapteeClass = adapteeClass;
	}

	public MethodClass getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(MethodClass requestMethod) {
		this.requestMethod = requestMethod;
	}

	public MethodClass getSpecificRequestMethod() {
		return specificRequestMethod;
	}

	public void setSpecificRequestMethod(MethodClass specificRequestMethod) {
		this.specificRequestMethod = specificRequestMethod;
	}
	
	/**
	 * the equals determine the way to distinguish state pattern,
	 */
	
	public int hashCode() {  
        return Objects.hash(adapterClass)+Objects.hash(adapteeClass)+Objects.hash(requestMethod)+Objects.hash(specificRequestMethod);
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
		if (!(other instanceof AdapterPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((AdapterPatternClass)other).hashCode()) // check the hashcode.
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
		return this.adapterClass+","+this.adapteeClass+","+this.requestMethod+","+this.specificRequestMethod;
		
	}
	
}
