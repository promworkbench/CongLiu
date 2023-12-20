package designpatterns.strategypattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * this class defines the basic structure of the discovered strategy pattern.
 * @author cliu3
 *
 */
public class StrategyPatternClass extends PatternClass{
	
	private ClassClass context =null;
	private ClassClass strategy =null;
	private MethodClass setStrategy = null;
	
	private MethodClass contextInterface = null;
	private MethodClass algorithmInterface=null;// discovered from the execution data
	
	
	public MethodClass getSetStrategy() {
		return setStrategy;
	}
	public void setSetStrategy(MethodClass methodClass) {
		this.setStrategy = methodClass;
	}

	
	public ClassClass getContext() {
		return context;
	}
	public void setContext(ClassClass context) {
		this.context = context;
	}
	public ClassClass getStrategy() {
		return strategy;
	}
	public void setStrategy(ClassClass strategy) {
		this.strategy = strategy;
	}
	public MethodClass getContextInterface() {
		return contextInterface;
	}
	public void setContextInterface(MethodClass contextInterface) {
		this.contextInterface = contextInterface;
	}
	public MethodClass getAlgorithmInterface() {
		return algorithmInterface;
	}
	public void setAlgorithmInterface(MethodClass algorithmInterface) {
		this.algorithmInterface = algorithmInterface;
	}
	
	/**
	 * the equals determine the way to distinguish two strategy pattern instances
	 */
	
	public int hashCode() {  
        return Objects.hash(context)+Objects.hash(strategy)+2*Objects.hash(setStrategy)+3*Objects.hash(contextInterface)+5*Objects.hash(algorithmInterface);
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
		if (!(other instanceof StrategyPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((StrategyPatternClass)other).hashCode()) // check the hashcode.
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
		return this.context+","+this.strategy+","+this.contextInterface+","+this.algorithmInterface;
		
	}
	
}
