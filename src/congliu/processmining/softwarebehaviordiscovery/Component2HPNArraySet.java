package congliu.processmining.softwarebehaviordiscovery;

import java.util.HashSet;

/***
 * a set of compoennt to hpns, 
 * @author cliu3
 *
 */
public class Component2HPNArraySet 
{
	private HashSet<Component2HPNArray> component2HPNArray; 
	
	public Component2HPNArraySet()
	{
		this.component2HPNArray= new HashSet<Component2HPNArray>();

	}
	
	public void putC2HPNs(Component2HPNArray c2hpns)
	{
		this.component2HPNArray.add(c2hpns);
	}
	
	public HashSet<Component2HPNArray> getC2HPNs()
	{
		return component2HPNArray;
	}
	
	public int size()
	{
		return component2HPNArray.size();
	}
}
