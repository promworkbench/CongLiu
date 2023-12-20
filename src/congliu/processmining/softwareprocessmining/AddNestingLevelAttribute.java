package congliu.processmining.softwareprocessmining;

import java.util.ArrayList;
import java.util.HashSet;

public class AddNestingLevelAttribute {
	
	// this method aims to add nesting level attribute to each method call. 
	public static HashSet<ArrayList<String>> NestingLevelIdentification(HashSet<ArrayList<String>> compInstanceSet, Component2Classes com2class)
	{
		HashSet<ArrayList<String>> compInstanceSetExtended = new HashSet<ArrayList<String>>();
		
		// parse each trace, i.e., the instance
		for (ArrayList<String> instance :compInstanceSet)
		{			
			compInstanceSetExtended.add(parseEachTrace(instance, com2class));
		}
		return compInstanceSetExtended;
	}
	
	
	/**
	 * adding nesting level trace by trace
	 * @param instance
	 * @param com2class
	 * @return
	 */
	public static ArrayList<String> parseEachTrace(ArrayList<String> instance, Component2Classes com2class)
	{
		// use hashset to avoid repetive elements.
		HashSet<String> instanceSetExtended = new HashSet<String>();
				
		//Initialize the queue. poll(): retrive and remove the head of this queue , i.e., top-level events of each traces
		HashSet<QueueElement> initialMethodSet = new HashSet<QueueElement>();
		
		// initialize the initial method set
		
		/* the following code is used to set those methods calling from the other component 
		 * nesting-level==1, i.e., set it called from main. After talking with Boudewijn, it it not reasonable. 
		//we first check if this trace has the main() caller
		int flagMain = 0;
		
		for (String recording: instance)
		{
			if (recording.split(";")[2].equals("main()"))
			{
				QueueElement qe = new QueueElement();
				qe.setPreRecording(recording);
				qe.setNestingLevel(0);
				initialMethodSet.add(qe);
				flagMain = 1; //note this trace has main
			}
		}
		
		// then check if the current methodcall is called from other component
		for (String recording: instance)
		{
			if (!recording.split(";")[2].equals("main()"))
			{
				// in case no main (); if the caller of this method is not inculded in the current component, we set it 0 nesting.
				if ((flagMain==0)&&(!com2class.getClasses().contains(recording.split(";")[7])))
				{
					QueueElement qe = new QueueElement();
					qe.setPreRecording(recording);
					qe.setNestingLevel(0);
					initialMethodSet.add(qe);
				}
				// in case there is main, if the caller of this method is not inculded in the current component, we set it 1 nesting.
				else if ((flagMain==1)&&(!com2class.getClasses().contains(recording.split(";")[7])))
				{
					QueueElement qe = new QueueElement();
					qe.setPreRecording(recording);
					qe.setNestingLevel(1);
					initialMethodSet.add(qe);
				}
			}
		}
		*/
		
		/*
		 * to reconstruct the nesting level, where main and method called from other components are labeled as nesting==0
		 */
		
		for (String recording: instance)
		{
			//if the method is main or called from other component, it is labeled with nesting=length ==0. 
			if (recording.split(";")[2].equals("main()") |(!com2class.getClasses().contains(recording.split(";")[7])))
			{
				QueueElement qe = new QueueElement();
				qe.setPreRecording(recording);
				qe.setNestingLevel(0);
				initialMethodSet.add(qe);// contains all top level events
			}
		}
		
		ArrayList<QueueElement> initialMethodList = new ArrayList<QueueElement>(initialMethodSet);
		
		while (initialMethodList.size()!=0)
		{
			//get the head element
			QueueElement qe = initialMethodList.get(0);
			initialMethodList.remove(0);
			
			instanceSetExtended.add(qe.getPreRecording()+";"+qe.getNestingLevel());
			
			/**
			 * to make sure the current event (callee) belongs to the current component
			 * here, we assume that if an event (callee) does not belongs to the current component, it cannot call others. 
			 */
			if (com2class.getClasses().contains(qe.getPreRecording().split(";")[1]))
			{
				for (String recording: instance)
				{
					// the current object and method is the caller of other recording
					if ((qe.getPreRecording().split(";")[3].equals(recording.split(";")[9])) 
							&& qe.getPreRecording().split(";")[2].equals(recording.split(";")[8]))
					{
						QueueElement tempqe = new QueueElement();
						tempqe.setPreRecording(recording);
						tempqe.setNestingLevel(qe.getNestingLevel()+1);
						initialMethodList.add(tempqe);
					}
				}
			}

		}
		
	    // Creating a List of HashSet elements
		ArrayList<String> instanceExtended = new ArrayList<String>(instanceSetExtended);
		return instanceExtended;
	}

}
