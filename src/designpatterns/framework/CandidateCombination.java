package designpatterns.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CandidateCombination {

	public static ArrayList<HashMap<String, Object>> combination(HashMap<String, ArrayList<Object>> role2values)
	{
		//create map from role2values
		final Map<String,Object[]> map = new HashMap<String,Object[]>();
		for(String key: role2values.keySet())
		{
			map.put(key, role2values.get(key).toArray());
		}
		
		//creat the combination by taking map as input
		final CombinationsIterator iterator = new CombinationsIterator(map);
		ArrayList<Object []> tempResult = new ArrayList<>();
		while (iterator.hasNext()) {
			tempResult.add(iterator.Next());
//		    System.out.println(
//		        org.apache.commons.lang3.ArrayUtils.toString(iterator.Next())
//		    );
		}
		
		
		//convert tempResult to ArrayList<HashMap<String, Object>> result
		//key order
		ArrayList<String> roleList = iterator.getKeyOrder();
				
		//convert tempResult to ArrayList<HashMap<String, Object>> result, 
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		
		for(Object [] objs: tempResult)
		{
			HashMap<String, Object> ele = new HashMap<String, Object>();
			for(int i=0; i< objs.length;i++)
			{
				ele.put(roleList.get(i), objs[i]);
			}
			result.add(ele);
		}
				
		
		return result;
	}
	
	public static void main(String []args)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		ArrayList<Object> list1 = new ArrayList<>();
		list1.add("register");
		list1.add("unregister");
		ArrayList<Object> list2 = new ArrayList<>();
		list2.add("unregister");
		list2.add("register");
		
		ArrayList<Object> list3 = new ArrayList<>();
		list3.add("init");
		list3.add("setState");
		
		ArrayList<Object> list4 = new ArrayList<>();
		list4.add("nnn");
		
		role2values.put("A",list1);
		role2values.put("B",list2);
		role2values.put("C", list3);
		role2values.put("D", list4);
		
		ArrayList<HashMap<String, Object>> combined =combination(role2values);
		System.out.println(combined);
	}
}
