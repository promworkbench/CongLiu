package congliu.processmining.classobjectinteractiongraph;

import java.util.Objects;

// each graph node is first extended with 
// (1) class obj, class name information
// (2) component and component instance instance information,  
public class Node {
	String Obj;// the object information
	String Class;// the class information
	String compIns; // the component instance id of the current obj.
	String Component;//the component name
	
	//HashMap<String, HashSet<String>> method2objs;// the caller method of current objects to other objects. 
	public Node(String Obj, String Class, String compo, String CompIns)
	{
		this.Obj = Obj;
		this.Class = Class;
		this.compIns = CompIns;
		this.Component = compo;
	}
	
    public int hashCode() {  
		//System.out.println(Obj+Obj.hashCode());
        //return Obj.hashCode();     
        return Objects.hash(Obj,Class);
    }  
	// rewrite equals()
//	public boolean equals(Node obj) {
//		// TODO Auto-generated method stub
//		if (Obj.equals(obj.Obj))
//		{
//			return true;
//		}
//		else 
//		{
//			return false;
//		}
//	}
	
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
		if (!(other instanceof Node))
		{
			return false;
		}
		if (this.hashCode()==((Node)other).hashCode())
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
		return this.Obj+":"+this.Class;
		
	}


}