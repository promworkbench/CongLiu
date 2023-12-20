package designpatterns.factorymethodpattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class FactoryMethodPatternClass extends PatternClass{
	
	private ClassClass creator =null;
	private MethodClass factoryMethod=null;

	
	
	
	public ClassClass getCreator() {
		return creator;
	}

	public void setCreator(ClassClass creator) {
		this.creator = creator;
	}

	public MethodClass getFactoryMethod() {
		return factoryMethod;
	}

	public void setFactoryMethod(MethodClass factoryMethod) {
		this.factoryMethod = factoryMethod;
	}

	/**
	 * the equals determine the way to distinguish state pattern,
	 */
	
	public int hashCode() {  
        return Objects.hash(creator)+Objects.hash(factoryMethod);
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
		if (!(other instanceof FactoryMethodPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((FactoryMethodPatternClass)other).hashCode()) // check the hashcode.
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
		return this.creator+","+this.factoryMethod;
		
	}
	
}
