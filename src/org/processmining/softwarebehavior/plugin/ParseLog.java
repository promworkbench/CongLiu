package org.processmining.softwarebehavior.plugin;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.flexiblemodel.FlexStartTaskNodeConnection;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexFactory;
import org.processmining.models.flexiblemodel.FlexNode;
import org.processmining.models.flexiblemodel.SetFlex;
import org.processmining.models.flexiblemodel.StartTaskNodesSet;




/*
 * Class outline:
 * remove or keep the exceptional cases in the log
 * Input parameters:
 * (1)Log indicates the original log
 * OutPut parameters: 
 * (1)output the normal or exceptional log
*/
public class ParseLog {

//	@Plugin(
//			name = "ParseLog", 
//			parameterLabels = { "Log"}, 
//			returnLabels = { "Normal log" }, 
//			returnTypes = { XLog.class },
//			userAccessible = true, 
//			help = "The plugin is used to detect the deviation in the event log"
//		)
//	@PluginVariant(variantLabel = "ParseLog", requiredParameterLabels = {0})
//	@UITopiaVariant(uiLabel = "ParseLog", affiliation = "TU/e", author = "G.Li", email = "G.Li.3@tue.nl")
////	public XLog detectDeviation(UIPluginContext context, XLog inputLog) throws CancellationException
	
//	
	
	@Plugin(
	name = "Mine the plug-in Calling Graph", 
	parameterLabels = { "Log"}, 
	returnLabels = { "Flexible Model Validation" , "Start Tasks" }, 
	returnTypes = { Flex.class , StartTaskNodesSet.class },
	userAccessible = true, 
	help = "The plugin is used to detect the deviation in the event log"
	)
	@PluginVariant(variantLabel = "Mine the plug-in Calling Graph", requiredParameterLabels = {0})
	@UITopiaVariant(uiLabel = "Mine the plug-in Calling Graph", affiliation = "TU/e", author = "G.Li", email = "G.Li.3@tue.nl")
	public Object[] detectDeviation(UIPluginContext context, XLog inputLog) throws CancellationException
	 {
		XLog  Log = (XLog) inputLog.clone();//keep the input log unchanged
		JTree tree;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
//		Map<FlexNode,FlexNode> dependencyList = new HashMap<FlexNode,FlexNode>();
		
//		Set<FlexNode,FlexNode> list = new HashSet<FlexNode,FlexNode>();
		
		List<Pair<FlexNode,FlexNode>> dependencyList = new ArrayList<Pair<FlexNode,FlexNode>>();
		for (int i = 0; i < Log.size(); i++) //i represents the number of the already created deviations
		{
			XTrace trace = Log.get(i);
			int checkStartPosition=0;// record the start of the part which are not processed
			int checkCompletePosition=trace.size()-1;// record the end of the part which are not processed
			
//			XEvent tempStartEvent = oldTrace.get(startPosition);
//			String lifeStartCycle = tempStartEvent.getAttributes().get("lifecycle:transition").toString();
//			if(!lifeStartCycle.equals("start")) JOptionPane.showMessageDialog(null, "The first event do not have the 'start' lifecycle in ParseLog.java");
//			
//			XEvent tempCompleteEvent = oldTrace.get(completePosition);
//			String lifeCompleteCycle = tempCompleteEvent.getAttributes().get("lifecycle:transition").toString();
//			if(!lifeCompleteCycle.equals("complete")) JOptionPane.showMessageDialog(null, "The last event do not have the 'complete' lifecycle in ParseLog.java");
//			
//			XConceptExtension conceptExtension = XConceptExtension.instance();
//			String eventStartName = conceptExtension.extractName(tempStartEvent);
//			String eventCompleteName = conceptExtension.extractName(tempCompleteEvent);
//			if(!eventStartName.equals(eventCompleteName)) JOptionPane.showMessageDialog(null, "The start and complete events do not have the same activity name");
//			
//			
//			DefaultMutableTreeNode top = new DefaultMutableTreeNode(eventStartName);
//			tree = new JTree(top);
//			
//			if(startPosition+1<completePosition)
//			{
//				startPosition++;
//				completePosition--;
//				
//				XEvent sectionStartEvent = oldTrace.get(startPosition);
//				
//				
//				DefaultMutableTreeNode father = top;
//				
//				
//				
//				
//				for(int s=sectionStartPosition+1;s<=completePosition;s++)
//				{
//					
//					String sectionStartName = conceptExtension.extractName(sectionStartEvent);
//					
//					XEvent sectionCompleteEvent = oldTrace.get(s);
//					String sectionCompleteName = conceptExtension.extractName(sectionCompleteEvent);
//					if(sectionStartName.equals(sectionCompleteName))
//					{
//						sectionCompletePosition = s;
//						
//						DefaultMutableTreeNode child = new DefaultMutableTreeNode(sectionCompleteName);
//						father.add(child);
//						
//						
//						if(sectionStartPosition+1<sectionCompletePosition)// there is section(s) inside
//						{
//							
//						}
//					}
//					if(s<completePosition) 
//					{
//						s++;
//						sectionStartEvent = oldTrace.get(s);						
//					}
//					
//					
//				}
//				
//				
//				
//				
//			}
//			
//			else 
//			{
////				end
//			}
//			
//			DefaultMutableTreeNode category = new DefaultMutableTreeNode("Books for Java Programmers");
//			top.add(category);
//			
//			for(int j = 0; j < oldTrace.size(); j++)
//			{
//				XEvent tempEvent = oldTrace.get(startPosition);
//				String lifeCycle = tempEvent.getAttributes().get("lifecycle:transition").toString();
//				if(!lifeCycle.equals("start")) JOptionPane.showMessageDialog(null, "The event do not have the start lifecycle in ParseLog.java");
//				
//				
//				DefaultMutableTreeNode top = new DefaultMutableTreeNode("The Java Series");
//				tree = new JTree(top);
//				
//				DefaultMutableTreeNode category = new DefaultMutableTreeNode("Books for Java Programmers");
//				top.add(category);
//			}
			
//			DefaultMutableTreeNode top = new DefaultMutableTreeNode("top");
//			DefaultMutableTreeNode a1 = new DefaultMutableTreeNode("a1");
//			DefaultMutableTreeNode b1 = new DefaultMutableTreeNode("b1");
//			DefaultMutableTreeNode c1 = new DefaultMutableTreeNode("c1");
//			DefaultMutableTreeNode a2 = new DefaultMutableTreeNode("a2");
//			DefaultMutableTreeNode b2 = new DefaultMutableTreeNode("b2");
//			DefaultMutableTreeNode c2 = new DefaultMutableTreeNode("c2");
//			JOptionPane.showMessageDialog(null, "The start and complete events do not have the same activity name");
			
//			tree = new JTree(top);
			
			
			createSectionTree(checkStartPosition,checkCompletePosition,trace,top, dependencyList);
		}
//		DefaultMutableTreeNode tryChinld = (DefaultMutableTreeNode) top.getFirstChild().getChildAt(2);
//		JOptionPane.showMessageDialog(null, tryChinld.getChildCount()+tryChinld.toString());
		

//		JOptionPane.showMessageDialog(null, top.getFirstChild().getChildCount());
//		
		
//		JOptionPane.showMessageDialog(null, dependencyList.size());
//		for(int num=0;num<dependencyList.size();num++)
//		{
//			JOptionPane.showMessageDialog(null, "first"+dependencyList.get(num).getKey().toString()+"second"+dependencyList.get(num).getValue().toString());
//			
//		}
//		showTree(top);
		return showDepency(dependencyList,context);
	 }
	
	private TreePath movePath; 
	
	public void showTree(DefaultMutableTreeNode dmtmRoot)
	{
		final JTree jtree = new JTree(dmtmRoot);  
		JFrame jframeMain = new JFrame("my tree");  
		JPanel jpanelMain = new JPanel();  
		JScrollPane jspMain = new JScrollPane(jtree);  
//		TreePath movePath = null;  
		// è®¾ç½®å…¶æ²¡æœ‰è¿žçº¿  
		jtree.putClientProperty("JTree.lineStyle", "None");  
		// è®¾ç½®æ˜¯å�¦æ˜¾çŽ°å…¶æ ¹èŠ‚ç‚¹çš„å›¾æ ‡  
		jtree.setShowsRootHandles(true);  
		  
		jpanelMain.setLayout(new BorderLayout());  
		jpanelMain.add(jspMain);  
		jframeMain.add(jpanelMain);  
		jframeMain.pack();  
		jframeMain.setLocationRelativeTo(null);  
		jframeMain.setSize(264, 400);  
		jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		jframeMain.setVisible(true);  
		  
		 
		jtree.setEditable(true);  
		  
		  
		MouseListener ml = new MouseAdapter() {  
		  

		TreePath tp;  
		  

		@Override  
		public void mousePressed(MouseEvent e) {  
		// TODO Auto-generated method stub  
		// super.mousePressed(e);  

		tp = jtree.getPathForLocation(e.getX(), e.getY());  
		if (tp != null) {  
		movePath = tp;//   
		}  
		}  
		  
		// å½“é¼ æ ‡æ�¾å¼€çš„æ—¶å€™  
		@Override  
		public void mouseReleased(MouseEvent e) {  
		// TODO Auto-generated method stub  
		// super.mouseReleased(e);  

		tp = jtree.getPathForLocation(e.getX(), e.getY());  
		if (tp != null && movePath != null) {  

		if (movePath.isDescendant(tp) && movePath != tp) {  
		System.out.println("illeagle operation");  
		return;  
		}  

		else if (movePath != tp) {  

		System.out.println(tp.getLastPathComponent());  

		  

		DefaultMutableTreeNode dmtnLastPath = (DefaultMutableTreeNode) tp  
		.getLastPathComponent();  

		DefaultMutableTreeNode dmtnFirstPath = (DefaultMutableTreeNode) movePath  
		.getLastPathComponent();  
		  

		dmtnLastPath.add(dmtnFirstPath);  

		jtree.updateUI();  
		}  
		}  
		  
		}  
		};  
		  
		jtree.addMouseListener(ml);  
	}
	
	public Object[] showDepency(List<Pair<FlexNode,FlexNode>> dependencyList, UIPluginContext context)
	{
		final Flex flexDiagram = FlexFactory.newFlex("Flexible Model 1");
//		FlexNode casestart=flexDiagram.addNode("hello");
//		DefaultMutableTreeNode top = (DefaultMutableTreeNode) dmtmRoot.getFirstChild();
		Map<String,FlexNode> mapList = new HashMap<String,FlexNode>();
//		List<FlexNode> pluginNode = new ArrayList<FlexNode>();
		List<String> pluginNode = new ArrayList<String>();
		for(int i=0;i<dependencyList.size();i++)
		{
			FlexNode precedingNode = dependencyList.get(i).getFirst();
			FlexNode subsequentNode = dependencyList.get(i).getSecond();
			
			if(!pluginNode.contains(precedingNode.toString()))
			{
				pluginNode.add(precedingNode.toString());
				FlexNode node=flexDiagram.addNode(precedingNode.toString());
				mapList.put(precedingNode.toString(), node);
//				JOptionPane.showMessageDialog(null, "There already exist prenode"+precedingNode.toString());
				
			}
//			else
//			{
//				JOptionPane.showMessageDialog(null, "There already exist prenode"+precedingNode.toString());
//			}
//			
			if(!pluginNode.contains(subsequentNode.toString()))
			{
				pluginNode.add(subsequentNode.toString());
				FlexNode node=flexDiagram.addNode(subsequentNode.toString());
				mapList.put(subsequentNode.toString(), node);
//				JOptionPane.showMessageDialog(null, "There already exist subnode"+subsequentNode.toString());
			}
			
//			else
//			{
//				JOptionPane.showMessageDialog(null, "There already exist subnode"+subsequentNode.toString());
//			}
			flexDiagram.addArc(mapList.get(precedingNode.toString()), mapList.get(subsequentNode.toString()));
		}
		
	
		
		
		 StartTaskNodesSet startTaskNodeSet =new StartTaskNodesSet();
		    SetFlex setFlex = new SetFlex();
//		    setFlex.add(casestart);
		    startTaskNodeSet.add(setFlex);
		    
			// create connection between flexible model and start task node
			context.addConnection(new FlexStartTaskNodeConnection(flexDiagram.getLabel(), flexDiagram, startTaskNodeSet));

			// return objects
			return new Object[] { flexDiagram, startTaskNodeSet };


	}
	
	
	
	
	public void createSectionTree(int checkStartPosition, int checkCompletePosition, XTrace trace, DefaultMutableTreeNode father, List<Pair<FlexNode,FlexNode>> dependencyList)//check a part of trace
	{
		int sectionStartPosition = checkStartPosition;
		int sectionCompletePosition=0;

		final Flex flexDiagram = FlexFactory.newFlex("Flexible Model 1");
		XEvent sectionStartEvent = trace.get(sectionStartPosition);
		XConceptExtension conceptExtension = XConceptExtension.instance();
		String sectionStartName = conceptExtension.extractName(sectionStartEvent);
		
		for(int s=sectionStartPosition+1;s<=checkCompletePosition;s++)
		{
					
			XEvent sectionCompleteEvent = trace.get(s);
			String sectionCompleteName = conceptExtension.extractName(sectionCompleteEvent);
//			JOptionPane.showMessageDialog(null, "outside"+sectionStartName+"end"+sectionCompleteName);
			if(sectionStartName.equals(sectionCompleteName))
			{
				sectionCompletePosition = s;
				
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(sectionCompleteName);
				
				
//				JOptionPane.showMessageDialog(null, "equal"+sectionStartName+"end"+sectionCompleteName);
				if(sectionStartPosition+1<sectionCompletePosition)// there is/are sub-section(s) inside
				{
//					JOptionPane.showMessageDialog(null, "enter in subsection"+sectionStartPosition+"end"+sectionCompletePosition);
//					JOptionPane.showMessageDialog(null, "enter in subsection"+sectionStartName+"end"+sectionCompleteName);
					createSectionTree(sectionStartPosition+1, sectionCompletePosition-1, trace, child, dependencyList);//check a part of trace
				}
				father.add(child);
				FlexNode nodeFather = flexDiagram.addNode(father.toString());

				FlexNode nodeChild = flexDiagram.addNode(child.toString());
//				@SuppressWarnings("unchecked")
				Pair<FlexNode,FlexNode> dependencyRelation = new Pair<FlexNode, FlexNode>(nodeFather,nodeChild);
				if(!dependencyList.contains(dependencyRelation))
					dependencyList.add(dependencyRelation);
				//after the detected section, there exist more sections
				if(sectionCompletePosition <checkCompletePosition) 
				{
					s = sectionCompletePosition+1;
					sectionStartEvent = trace.get(s);	
					sectionStartPosition = s;
				    sectionStartName = conceptExtension.extractName(sectionStartEvent);
				}
			}						
			
		}
	}

}
	
////	public XLog createDeviation(XLog oldLog,double deviationProbability,int deviationCountInTrace) 
//	{
//		XLog  oldLog = (XLog) Log.clone();//keep the input log unchanged
//		XLog  changeableLog = (XLog) Log.clone();//keep the input log unchanged
//		XLog  traceLog = (XLog) Log.clone();//keep the input log unchanged
//		
//		//Definition of myself
//		double deviationProbability = 0;
//		int deviationCountInTrace = 3;
//		
//
//		//create the new log and copy the attributes of the old log to the new one
//		XAttributeMap logattlist = XLogFunctions.copyAttMap(oldLog.getAttributes());
//		XLog newLog = new XLogImpl(logattlist);
//		
//		CreateDeviationFrame parameters = new CreateDeviationFrame(6);
//		InteractionResult interActionResult = context.showConfiguration("DeviationDetecting", parameters);
//		
//		if (interActionResult.equals(InteractionResult.CANCEL)) {
//			context.getFutureResult(0).cancel(true);
//			JOptionPane.showMessageDialog(null, "cancle", "world", JOptionPane.ERROR_MESSAGE);
//			return null;
//		}
//		
//		if (interActionResult.equals(InteractionResult.CONTINUE)) {
////			context.getFutureResult(0).;
//			
//		
//			 double readDeviationPercentage=parameters.getDeviationPercentage();
//		       JOptionPane.showMessageDialog(null,"DeviationPercentage:"+readDeviationPercentage);
//		       deviationProbability=readDeviationPercentage;
//		}
//		
//		
//		  
//		XLogInfo info = XLogInfoFactory.createLogInfo(oldLog);
//		String []tempEventName = new String[info.getEventClasses().size()];
//		
//		List namelist = new ArrayList();
////		String []changeEventName = new String[info.getEventClasses().size()];
//		String []changeEventName = {"A","E","G","D","H","B","C","I","F"};// original order{"A","B","C","D","E","F","G","H","I"}
//		Map<String, String> changeMap=new HashMap();
//		
//		int n=0;
//		
//		try {  
//		       
//            File writename = new File("D:\\eventclass.txt"); // 
//            writename.createNewFile(); //   
//            BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
//
//
//
//		for (XEventClass eventClass : info.getNameClasses().getClasses())
//		{
//			tempEventName[n]=eventClass.toString();
////			changeMap.put(eventClass.toString(), changeEventName[n]);
//			n++;
//			out.write("add:"+eventClass.toString());
//			out.write("\r\n");
//		}
//		
//        out.flush(); //  
//        out.close(); // 
//		}
//		catch (Exception e) 
//		{  
//        e.printStackTrace();  
//         }	
///*		for(int j=0;j<info.getEventClasses().size();j++)
//		{
//			int randomName= (int)(Math.random()*info.getEventClasses().size());
//		}*/
//		
//		
//		//randomly select deviationProbability*traceNum traces, and keep their IDs
//		List randomIDList = new ArrayList();
//		for (int i = 0; i < deviationProbability*Log.size(); i++) 
//		{
//			int logSize=changeableLog.size();
//			int randomPosition=(int)(Math.random()*logSize);//äº§ç”Ÿ[0,logSize-1]çš„æ•´æ•°éš�æœºæ•°
//			XConceptExtension conceptExtension = XConceptExtension.instance();
//			String traceID = conceptExtension.extractName(changeableLog.get(randomPosition));
//			randomIDList.add(traceID);
//			changeableLog.remove(randomPosition);
//		
//		}
//		
//		int nAdd=0,nReduce=0,nReplace=0;
//		for (int i = 0; i < Log.size(); i++) //i represents the number of the already created deviations
//		{
////			double traceNum=oldLog.size();
//			
////			XTrace oldTrace = oldLog.get(i);
//			
//			XConceptExtension conceptExtension = XConceptExtension.instance();
//			String traceID = conceptExtension.extractName(traceLog.get(i));
//			
//			XTrace oldTrace = traceLog.get(i);
//			XTrace newTrace;
//			
//			//create the new trace and copy the attributes of the old trace to the new one
//			if(!randomIDList.contains(traceID))
//			{
//				XAttributeMap newAttributeMap = oldTrace.getAttributes();
//				XLogFunctions.putLiteral(newAttributeMap,"Flag", "Normal");
//				newTrace = new XTraceImpl(XLogFunctions.copyAttMap(newAttributeMap));
//				for (int j = 0; j < oldTrace.size(); j++)
//				{
//					XEvent oldEvent = oldTrace.get(j);
//					XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//					newTrace.add(newEvent);	
//				}
//			}
//			else
//			{
//				XAttributeMap newAttributeMap = oldTrace.getAttributes();
//				XLogFunctions.putLiteral(newAttributeMap,"Flag", "Exceptional");
//				newTrace = new XTraceImpl(XLogFunctions.copyAttMap(newAttributeMap));
//	
//				//generate the probability of inserting a deviation event,random()ä¼šè‡ªåŠ¨äº§ç”Ÿä¸€ä¸ª(positive sign, greater than or equal to 0.0 and less than 1.0)çš„å�Œç²¾åº¦éš�æœºæ•°
//				double randomNumber=Math.random();
//				
//				for(int deviationNum=0;deviationNum<deviationCountInTrace;deviationNum++)
//				{
//				//generate the place of the insertion
//					int randomPosition=(int)(Math.random()*oldTrace.size());//äº§ç”Ÿ[0,oldTrace.size()-1]çš„æ•´æ•°éš�æœºæ•°
//						
//					newTrace.clear();
//					//copy the part before the insertion
//					for (int j = 0; j < randomPosition; j++)
//					{
//						XEvent oldEvent = oldTrace.get(j);
//						XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//						newTrace.add(newEvent);
//						
//					}
//	
//	
//				//add a deviation event into the trace
//	
////				String s=oldTrace.getAttributes().get("concept:name" ).toString();	
////				if( Integer.parseInt(s)<=deviationProbability*oldLog.size())
//
//					//randomly select the deviation type
//					int deviationType=(int)(Math.random()*3);
//					
//					//insert an event
//					if(deviationType==0)
//					{
//						//insert a event
//						Date time;
//						int randomEventName=(int)(Math.random()*info.getEventClasses().size());//äº§ç”Ÿ[0,info.getEventClasses().size()-1]çš„æ•´æ•°éš�æœºæ•°
//						time = XLogFunctions.getTime(oldTrace.get(0));
//						newTrace.add(makeEvent(tempEventName[randomEventName], time));
//	
//						//copy the part behind the insertion
//						for (int j = randomPosition; j < oldTrace.size(); j++)
//						{
//							XEvent oldEvent = oldTrace.get(j);
//							XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//							newTrace.add(newEvent);
//						}
//						nAdd++;
//						
//					}
//					//reduce an event
//					else if(deviationType==1)
//					{
//						//copy the part behind the insertion
//						if(oldTrace.size()==1) randomPosition=-1;
//						for (int j = randomPosition+1; j < oldTrace.size(); j++)
//						{
//							XEvent oldEvent = oldTrace.get(j);
//							XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//							newTrace.add(newEvent);
//						}
//						nReduce++;
//					}
//					
//					//replace an event
//					else if(deviationType==2)
//					{
//	
//						Date time;
//						boolean equalFlag=false;
//						String eventName;
//						int randomEventName;
//						do
//						{							
//							randomEventName=(int)(Math.random()*info.getEventClasses().size());//return a number in [0,info.getEventClasses().size()-1]
//							eventName = conceptExtension.extractName(oldTrace.get(randomPosition));
//							
//							if(tempEventName[randomEventName].equals(eventName))
//							{
//								equalFlag=true;
//							}
//							else
//							{
//								equalFlag=false;
//								time = XLogFunctions.getTime(oldTrace.get(0));
//								newTrace.add(makeEvent(tempEventName[randomEventName], time));
//							}
//						}while(equalFlag);				
//						
//						 
//						//copy the part behind the insertion
//						for (int j = randomPosition+1; j < oldTrace.size(); j++)
//						{
//							XEvent oldEvent = oldTrace.get(j);
//							XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//							newTrace.add(newEvent);
//						}	
//						nReplace++;
//					}
//					
//					//change an numerical attribute
//					else if(deviationType==4)
//					{
//						newTrace.clear();
//			
//						//copy the part behind the insertion
//						for (int j = 0; j < oldTrace.size(); j++)
//						{
//							XEvent oldEvent = oldTrace.get(j);
//							
//							XEvent newEvent =  changeNumericalAttribute(oldEvent);
//							newTrace.add(newEvent);
//						}
////							nAdd++;
//					}
//					
//					//change model
///*					else if(deviationType==3)
//					{
//						newTrace.clear();
//						
//						
//						//copy the part behind the insertion
//						for (int j = 0; j < oldTrace.size(); j++)
//						{
//							XEvent oldEvent = oldTrace.get(j);
//							
//							String oldName= oldEvent.getAttributes().get("concept:name").toString();
//							
//							Date time;
//							String newName=changeMap.get(oldName);
//							time = XLogFunctions.getTime(oldTrace.get(0));
//							newTrace.add(makeEvent(newName, time));
//						}
////							nAdd++;
//					}*/
//				
//					//prepare for the next deviation
//					oldTrace.clear();
//					for (int j = 0; j < newTrace.size(); j++)
//					{
//						XEvent oldEvent = newTrace.get(j);
//						XEvent newEvent = new XEventImpl(XLogFunctions.copyAttMap(oldEvent.getAttributes()));
//						oldTrace.add(newEvent);
//					}
//				
//				}
//			}
//			/////////////////////////////ranking start	
//			try {  
//			       
//	            File writename = new File("D:\\deviationnumber.txt"); // 
//	            writename.createNewFile(); //   
//	            BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
//
//				out.write("add:"+nAdd+"_reduce:"+nReduce+"_replace:"+nReplace);
//	    		out.write("\r\n");
//	    		
//
//	            out.flush(); //  
//	            out.close(); // 
//
//	        } 
//			catch (Exception e) 
//			{  
//	        e.printStackTrace();  
//	         }		
//	/////////////////////////////ranking end
//			
//			//add the new trace to the new log
//			newLog.add(newTrace);
////			context.getProgress().inc();
//		}
//		return newLog;
//	}
//
//	private XEvent makeEvent(String name, Date time) {
//		XAttributeMap attMap = new XAttributeMapImpl();
//		XLogFunctions.putLiteral(attMap, "concept:name", name);
////		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
//		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "complete");
//		XLogFunctions.putLiteral(attMap, "org:resource", "artificial");
//		XLogFunctions.putTimestamp(attMap, "time:timestamp", time);
//		XEvent newEvent = new XEventImpl(attMap);
//		return newEvent;
//	}
//	//change the numerical attribute
//	private XEvent changeNumericalAttribute(XEvent oldEvent) {
//		XAttributeMap attMap = new XAttributeMapImpl();
//		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
////		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "complete");
//		XLogFunctions.putLiteral(attMap, "org:resource", oldEvent.getAttributes().get("org:resource").toString());
//		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
//		Long numericalAttribute=Long.parseLong(oldEvent.getAttributes().get("Amount").toString());
//		numericalAttribute = 1000 - numericalAttribute;
//		XLogFunctions.putLiteral(attMap, "Amount",numericalAttribute.toString());
//
//		XEvent newEvent = new XEventImpl(attMap);
//		return newEvent;
//	}
//	
//	 public static void createAndShowGUI()
//	    {
//	       JFrame frame = new JFrame("Hello world");
//	       frame.setLayout(null);
//	       JComponent panel = new CheckBoxDemo(6);
//	       panel.setBackground(Color.red);
//	       panel.setBounds(0, 0, 600, 300);
//	       frame.add(panel);
//	       frame.pack();
//	       frame.setSize(600, 400);
//	       frame.setVisible(true);
//	      
//	    }
//	 
//}
//
//
