package congliu.processmining.classobjectinteractiongraph;

/**
 * a set of class object interaction graph, each refers to one case. 
 */
import java.util.HashMap;

public class ClassObjectInteractionGraphSet {

	private HashMap<String, ClassObjectInteractionGraph> caseid2cig = new HashMap<String, ClassObjectInteractionGraph>();

	public void setArray(String traceName, ClassObjectInteractionGraph coig)
	{
		caseid2cig.put(traceName, coig);
	}
	
	public HashMap<String, ClassObjectInteractionGraph> getArray()
	{
		return caseid2cig;
	}
}
