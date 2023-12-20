package congliu.processmining.objectusage;

import java.util.Objects;

public class Edge
{
	String source;
	String target;
	public Edge(String s, String t)
	{
		source=s;
		target=t;
	}
	
    public int hashCode() {  
		//System.out.println(Obj+Obj.hashCode());
        //return Obj.hashCode();     
        
    	//return Objects.hash(source,target);
    	/**
    	 * this is because the edge is undirected and so, we (A,B) and (B,A) should have the same hashcode.
    	 */
    	return Objects.hash(source)+Objects.hash(target);
    }  
    
    public String toString() 
	{
		return this.source+":"+this.target;
		
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
		if (!(other instanceof Edge))
		{
			return false;
		}
		if (this.hashCode()==((Edge)other).hashCode())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
    
//	public boolean equals(Edge obj) {
//		// TODO Auto-generated method stub
//		if (source.equals(obj.source)&&target.equals(obj.target))
//		{
//			return true;
//		}
//		else 
//		{
//			return false;
//		}
//	}
}