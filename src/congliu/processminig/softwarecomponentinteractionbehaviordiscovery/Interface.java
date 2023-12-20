package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.Objects;

/**
 * this class defines the interface..
 * It is uniquely identifiable by method(the caller) and component (the callee).
 * @author cliu3
 *
 */
public class Interface {

	private String method; 
	private String component;
	private String id;
	
	public Interface(String method, String component)
	{
		this.method = method;
		this.component=component;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public String getComponent()
	{
		return this.component;
	}
	
	//rewrite hashcode and equals
    public int hashCode() {      
        return Objects.hash(method,component);
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
		if (!(other instanceof Interface))
		{
			return false;
		}
		if (this.hashCode()==((Interface)other).hashCode())
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
		return this.method+":"+this.component;
	}
	
	// return the id of current interface 
	public String getId()
	{
		return this.id;
	}
	public void setId(String id)
	{
		this.id =id; 
	}
}
