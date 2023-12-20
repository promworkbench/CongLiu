package designpatterns.adapterpattern;

import java.util.ArrayList;
import java.util.HashSet;

import observerpatterndiscovery.ClassClass;

/*
 * this class defines the class type hierarchy
 */
public class ClassTypeHierarchy {

	private ArrayList<HashSet<ClassClass>> cth = new ArrayList<>();
	
	public void addCTH (HashSet<ClassClass> e)
	{
		cth.add(e);
	}
	
	public void  RemoveCTH (HashSet<ClassClass> e)
	{
		cth.remove(e);
	}
	
	public ArrayList<HashSet<ClassClass>> getAllCTH()
	{
		return cth;
	}
	
//	//if a new e is similar to an existing one, then we do not add the new one
//	public void addCTHwithoutDuplication(HashSet<ClassClass> e)
//	{
//		if(!cth.contains(e))
//		{
//			cth.add(e);
//		}
//	}
//	
	//if a new e share some elements with an existing one, then merge them
	public void addCTHbyMerging(HashSet<ClassClass> e)
	{		
		int flag =0;
		if(cth.size()>0)
		{
			for(int i =0;i<cth.size();i++)
			{
				if(interactionNumber(cth.get(i), e)>0)
				{
					flag =1;
					cth.get(i).addAll(e);
				}
			}
			if(flag==0)
			{
				cth.add(e);
			}
		}
		else {
			cth.add(e);
		}
		
	}
	
	
	/*
	 * the interact number of elements of two hashset
	 */
	public int interactionNumber(HashSet<ClassClass> group1, HashSet<ClassClass> group2)
	{
		HashSet<ClassClass> temp1 = new HashSet<ClassClass>();
		temp1.addAll(group1);
		temp1.retainAll(group2);
		
		return temp1.size();
	}
}
