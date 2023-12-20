package designpatterns.statepattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * this class defines the basic structure of the discovered state pattern.
 * @author cliu3
 *
 */
public class StatePatternClass extends PatternClass{
	
	private ClassClass context =null;
	private ClassClass state =null;
	private MethodClass setState = null;
	private MethodClass request=null;
	
	/*
	 * the handle method should be added on the basis on dynamic analysis. 
	 */
	
	private MethodClass handle = null;

	public MethodClass getSetState() {
		return setState;
	}

	public void setSetState(MethodClass setState) {
		this.setState = setState;
	}

	public ClassClass getContext() {
		return context;
	}

	public void setContext(ClassClass context) {
		this.context = context;
	}

	public ClassClass getState() {
		return state;
	}

	public void setState(ClassClass state) {
		this.state = state;
	}

	public MethodClass getRequest() {
		return request;
	}

	public void setRequest(MethodClass request) {
		this.request = request;
	}

	public MethodClass getHandle() {
		return handle;
	}

	public void setHandle(MethodClass handle) {
		this.handle = handle;
	}
	
	/**
	 * the equals determine the way to distinguish state pattern,
	 */
	
	public int hashCode() {  
        return Objects.hash(context)+Objects.hash(state)+2*Objects.hash(setState)+3*Objects.hash(request)+5*Objects.hash(handle);
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
		if (!(other instanceof StatePatternClass))
		{
			return false;
		}
		if (this.hashCode()==((StatePatternClass)other).hashCode()) // check the hashcode.
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
		return this.context+","+this.state+","+this.setState+","+this.request+","+this.handle;
		
	}
}
