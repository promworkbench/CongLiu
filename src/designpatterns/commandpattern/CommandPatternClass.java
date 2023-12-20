package designpatterns.commandpattern;

import java.util.Objects;

import designpatterns.framework.PatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * this class defines the basic structure of the discovered command pattern instance.
 * @author cliu3
 *
 */
public class CommandPatternClass extends PatternClass{

	// class-level roles
	private ClassClass invoker =null;
	private ClassClass command = null;
	private ClassClass receiver =null;
	
	// class-level roles
	private MethodClass call = null;
	private MethodClass execute=null;
	private MethodClass action=null;
	
	public ClassClass getInvoker() {
		return invoker;
	}
	public void setInvoker(ClassClass invoker) {
		this.invoker = invoker;
	}
	public ClassClass getCommand() {
		return command;
	}
	public void setCommand(ClassClass command) {
		this.command = command;
	}
	public ClassClass getReceiver() {
		return receiver;
	}
	public void setReceiver(ClassClass receiver) {
		this.receiver = receiver;
	}
	public MethodClass getCall() {
		return call;
	}
	public void setCall(MethodClass call) {
		this.call = call;
	}
	public MethodClass getExecute() {
		return execute;
	}
	public void setExecute(MethodClass execute) {
		this.execute = execute;
	}
	public MethodClass getAction() {
		return action;
	}
	public void setAction(MethodClass action) {
		this.action = action;
	}
	
	/**
	 * the equals determine the way to distinguish a command pattern instance
	 */
	
	public int hashCode() {  
        return Objects.hash(invoker)+Objects.hash(command)+Objects.hash(receiver)+2*Objects.hash(call)+3*Objects.hash(execute)+5*Objects.hash(action);
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
		if (!(other instanceof CommandPatternClass))
		{
			return false;
		}
		if (this.hashCode()==((CommandPatternClass)other).hashCode()) // check the hashcode.
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
		return this.invoker+","+this.command+","+this.receiver+","+this.call+","+this.execute+","+this.action;
	}
}
