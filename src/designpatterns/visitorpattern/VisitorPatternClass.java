package designpatterns.visitorpattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * this class defines the basic structure of the discovered visitor pattern.
 * @author cliu3
 *
 */
public class VisitorPatternClass extends PatternClass{

	private ClassClass element =null;
	private ClassClass visitor =null;
	private MethodClass accept = null;
	private MethodClass visit=null;// discovered from the execution data
	
	public ClassClass getElement() {
		return element;
	}
	public void setElement(ClassClass element) {
		this.element = element;
	}
	public ClassClass getVisitor() {
		return visitor;
	}
	public void setVisitor(ClassClass visitor) {
		this.visitor = visitor;
	}
	public MethodClass getAccept() {
		return accept;
	}
	public void setAccept(MethodClass accept) {
		this.accept = accept;
	}
	public MethodClass getVisit() {
		return visit;
	}
	public void setVisit(MethodClass visit) {
		this.visit = visit;
	}
	
	/**
	 * the equals determine the way to distinguish two visitor pattern instances
	 */
	
	public int hashCode() {  
        return Objects.hash(element)+Objects.hash(visitor)+2*Objects.hash(accept)+3*Objects.hash(visit);
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
		if (!(other instanceof VisitorPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((VisitorPatternClass)other).hashCode()) // check the hashcode.
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
		return this.element+","+this.visitor+","+this.accept+","+this.visit;
		
	}
	
	
}
