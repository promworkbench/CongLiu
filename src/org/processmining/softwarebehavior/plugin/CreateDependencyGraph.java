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
 * Class outline:show the dependency relation between plugins
 * Idea:create the hierarchy of plugin dependency by iteratively parsing the log
 * Input parameters:(1)Log indicates the original log
 * OutPut parameters: 
 * (1)output the dependency graph
*/

public class CreateDependencyGraph {
	@Plugin(
	name = "Mine plug-in Calling Graph",
	parameterLabels = { "Log"}, 
	returnLabels = { "Flexible Model Validation" , "Start Tasks" }, 
	returnTypes = { Flex.class , StartTaskNodesSet.class },
	userAccessible = true, 
	help = "The plugin is used to detect the deviation in the event log"
	)
	@PluginVariant(variantLabel = "Mine plug-in Calling Graph", requiredParameterLabels = {0})
	@UITopiaVariant(uiLabel = "Mine plug-in Calling Graph", affiliation = "TU/e", author = "G.Li", email = "G.Li.3@tue.nl")
	public Object[] createDependencyGraph(UIPluginContext context, XLog inputLog) throws CancellationException
	 {
		XLog  Log = (XLog) inputLog.clone();//keep the input log unchanged
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
		
		List<Pair<FlexNode,FlexNode>> dependencyList = new ArrayList<Pair<FlexNode,FlexNode>>();
		for (int i = 0; i < Log.size(); i++) //i represents the number of the already created deviations
		{
			XTrace trace = Log.get(i);
			int checkStartPosition=0;// record the start of the part which are not processed
			int checkCompletePosition=trace.size()-1;// record the end of the part which are not processed
						
			createSectionTree(checkStartPosition,checkCompletePosition,trace,top, dependencyList);
		}
		return showDepency(dependencyList,context);
	 }
	
	private TreePath movePath; 	
	public void showTree(DefaultMutableTreeNode dmtmRoot)
	{
		final JTree jtree = new JTree(dmtmRoot);  
		JFrame jframeMain = new JFrame("my tree");  
		JPanel jpanelMain = new JPanel();  
		JScrollPane jspMain = new JScrollPane(jtree);  
		jtree.putClientProperty("JTree.lineStyle", "None");  
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
		final Flex flexDiagram = FlexFactory.newFlex("Flexible Model for dependency");
		Map<String,FlexNode> mapList = new HashMap<String,FlexNode>();
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
			}

		
			if(!pluginNode.contains(subsequentNode.toString()))
			{
				pluginNode.add(subsequentNode.toString());
				FlexNode node=flexDiagram.addNode(subsequentNode.toString());
				mapList.put(subsequentNode.toString(), node);
			}
			
			flexDiagram.addArc(mapList.get(precedingNode.toString()), mapList.get(subsequentNode.toString()));
		}
		
		    StartTaskNodesSet startTaskNodeSet =new StartTaskNodesSet();
		    SetFlex setFlex = new SetFlex();

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
			
			if(sectionStartName.equals(sectionCompleteName))
			{
				sectionCompletePosition = s;				
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(sectionCompleteName);
				if(sectionStartPosition+1<sectionCompletePosition)// there is/are sub-section(s) inside
				{
					createSectionTree(sectionStartPosition+1, sectionCompletePosition-1, trace, child, dependencyList);//check a part of trace
				}
				father.add(child);
				FlexNode nodeFather = flexDiagram.addNode(father.toString());

				FlexNode nodeChild = flexDiagram.addNode(child.toString());

				Pair<FlexNode,FlexNode> dependencyRelation = new Pair<FlexNode, FlexNode>(nodeFather,nodeChild);
				if(!dependencyList.contains(dependencyRelation))
					dependencyList.add(dependencyRelation);
				
				//after the detected section, if there exist more sections
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
	