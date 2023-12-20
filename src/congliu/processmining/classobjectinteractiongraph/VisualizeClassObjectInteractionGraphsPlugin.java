package congliu.processmining.classobjectinteractiongraph;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMaps;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

@Plugin(name = "Visualize Class Object Interaction Graph", 
		returnLabels = { "Dot visualization" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = {"Graph"}, 
		userAccessible = true)
@Visualizer
public class VisualizeClassObjectInteractionGraphsPlugin {
	//the case to dot map
	private HashMap<String, Dot> case2dot = new HashMap<String, Dot>();
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	// set the right part be a dot panel
	private JPanel rightDotpanel= new JPanel();
	// set the left part be a case list. 
	private JList traceList = new JList();

	public static final Color COLOR_LIST_BG = new Color(60, 60, 60);
	public static final Color COLOR_LIST_FG = new Color(180, 180, 180);
	public static final Color COLOR_LIST_SELECTION_BG = new Color(80, 0, 0);
	public static final Color COLOR_LIST_SELECTION_FG = new Color(240, 240, 240);
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "coigSet", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, ClassObjectInteractionGraphSet coigSet) 
	{
		// get the cases.
		Set<String> Cases = coigSet.getArray().keySet();
		
		//for each case, we visualize its class object interaction graph as dot.
		for (String c: Cases)
		{
			Dot dot = convert(coigSet.getArray().get(c));
			case2dot.put(c, dot);
		}
		

		//dfgSplitPane.setDividerLocation(0.8);
		splitPane.setResizeWeight(0.05);
		splitPane.setLeftComponent(traceList);

		traceList.setListData(Cases.toArray());
		traceList.setLayoutOrientation(JList.VERTICAL);// the single line list
		traceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// single selection.
		traceList.setBackground(COLOR_LIST_BG);
		traceList.setForeground(COLOR_LIST_FG);
		traceList.setSelectionBackground(COLOR_LIST_SELECTION_BG);
		traceList.setSelectionForeground(COLOR_LIST_SELECTION_FG);
		traceList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listselected();
			}
		});
				
		//rightDotpanel=new DotPanel(dot);
		//rightDotpanel.repaint();
		
		return splitPane;
	}
	
	public static Dot convert(ClassObjectInteractionGraph ciog)
	{
		Dot dot =new Dot();
		dot.setDirection(GraphDirection.topDown);
		
		// prepare the component color, instance of the same type should use the same color
		HashSet<String> componentSet =new HashSet<String>();
		for(Node node: ciog.getClassObjectInteractionGraph().vertexSet())
		{
			componentSet.add(node.Component);
		}
		
		//set the max weight for each component. 
		HashMap<String,Long> component2color =new HashMap<String, Long>();
		for(String com: componentSet)
		{
			//we use random number to set the base color of differnt component. 
			long range = 1234L;
			Random r = new Random();
			long number = (long)(r.nextDouble()*range); // generate an random long number [0, range)
			component2color.put(com, number);
		}
		
		//set the colour type, i.e., [green, blue, red, grey] for different component
		HashMap<String,Integer> component2type =new HashMap<String, Integer>();
		for(int i=0;i<componentSet.toArray().length;i++)
		{
			component2type.put((String) componentSet.toArray()[i], i+1);
		}
		
		//prepare the clusters, each cluster correponds to a component instance 
		HashSet<String> clusterSet = new HashSet<String>();
		for(Node node: ciog.getClassObjectInteractionGraph().vertexSet())
		{
			clusterSet.add(node.Component+":"+node.compIns);
		}
	
		HashMap<String, DotCluster> comIns2Cluster = new HashMap<String, DotCluster>();
		
		long Maxweight =0;
		int colorType=0;
		for (String clusterStr: clusterSet)
		{
			DotCluster dc =dot.addCluster();
			comIns2Cluster.put(clusterStr, dc);
			dc.setLabel(clusterStr);
			dc.setOption("style", "filled");
			// set the color, instances of the same component should have the same color.
			for(String com: component2color.keySet())
			{
				if(clusterStr.startsWith(com))
				{
					Maxweight=component2color.get(com);
					colorType =component2type.get(com);
				}
			}
			if(colorType%4==1)
			{
				dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapGreen(Maxweight/colorType, Maxweight)));
			}
			else if (colorType%4==2){
				dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue(Maxweight/(colorType), Maxweight)));
			}
			else if (colorType%4==3){
				dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapGreyBlack(Maxweight/(2*colorType), Maxweight)));
			}
			else {
				dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapRed(Maxweight/colorType, Maxweight))); // color may be 0
			}
//			switch (colorType) {
//				case 1 :
//					dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapGreen(Maxweight/(3*colorType), Maxweight)));
//					break;
//				case 2 :
//					dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue(Maxweight/3, Maxweight)));
//					break;
//				case 3 :
//					dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapGreyBlack(Maxweight/5, Maxweight)));
//					break;
//				case 4 :
//					dc.setOption("color", ColourMap.toHexString(ColourMaps.colourMapRed(Maxweight/3, Maxweight)));
//					break;
//			}
		}
		
		//prepare the nodes
		HashMap<Node, DotNode> activity2Node = new HashMap<Node, DotNode>();
		for (Node activity : ciog.getClassObjectInteractionGraph().vertexSet()) 
		{
			DotNode node = getComponentInstance(activity,comIns2Cluster).addNode(activity.Obj+":"+activity.Class);
			//DotNode node = dot.addNode(activity);
			activity2Node.put(activity, node);
			node.setOption("shape", "box");
		}
		
		//prepare the edges
		for (DefaultEdge edge :ciog.getClassObjectInteractionGraph().edgeSet()) 
		{
			Node from = ciog.getClassObjectInteractionGraph().getEdgeSource(edge);
			Node to =  ciog.getClassObjectInteractionGraph().getEdgeTarget(edge);

			DotNode source = activity2Node.get(from);
			DotNode target = activity2Node.get(to);

			DotEdge dotEdge =dot.addEdge(source, target, "");
		}
		
		return dot;
	}

	private void listselected() 
	{
		// two types of mouse operations, if this is not true
		if (!traceList.getValueIsAdjusting())
		{
			//System.out.println(traceList.getSelectedValue());
			rightDotpanel=new DotPanel(case2dot.get(traceList.getSelectedValue()));
			splitPane.setRightComponent(rightDotpanel);
			//rightDotpanel.repaint();
			
		}
	}
	
	// give a node, this function returns its cluster (component instance). 
	public static DotCluster getComponentInstance(Node node, HashMap<String, DotCluster> comIns2cluster)
	{
		DotCluster cluster = null; 
		for(String comIns: comIns2cluster.keySet())
		{
			if ((node.Component+":"+node.compIns).equals(comIns))
			{
				cluster =comIns2cluster.get(comIns);
				break;
			}
		}
		
		return cluster;
	}
	
}
