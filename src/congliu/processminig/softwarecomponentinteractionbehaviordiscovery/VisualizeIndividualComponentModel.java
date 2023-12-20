package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer tries to visualize the component behavior model of each component. 
 * without considering the component interaction and interface cardinality information. 
 * @author cliu3
 *
 */

public class VisualizeIndividualComponentModel {
	
	//the component to dot map
	private HashMap<String, Dot> component2dot = new HashMap<String, Dot>();
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	// set the right part be a dot panel
	private JPanel rightDotpanel= new JPanel();
	// set the left part be a component list. 
	private JList componentList = new JList();
	
	public static final Color COLOR_LIST_BG = new Color(60, 60, 60);
	public static final Color COLOR_LIST_FG = new Color(180, 180, 180);
	public static final Color COLOR_LIST_SELECTION_BG = new Color(80, 0, 0);
	public static final Color COLOR_LIST_SELECTION_FG = new Color(240, 240, 240);
	
	
	@Plugin(name = "Visualize Software Component Models", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "SoftwareInteractionModel" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
					requiredParameterLabels = {0})// it needs one input parameter
	public JComponent visualizeTop(PluginContext context, SoftwareInteractionModel softwareInteractionModel)
	{
		
		// store all interfaces to interface dotcluster
		ComponentModelsSet compoenntModels =softwareInteractionModel.getComponentModelSet();  
		
		HashSet<ComponentModels> component2HPNSet =compoenntModels.getComponent2HPNSet();
		
		// get the components.
		HashSet<String> Components = new HashSet<String>();
		for (ComponentModels comM:component2HPNSet)
		{
			Components.add(comM.getComponent());
		}
		
		//for each component, we visualize its interface (HPNs) as dot.
		for(String component: Components)
		{
			Dot dot = convertCom2DOT(component,component2HPNSet);
			component2dot.put(component, dot);
		}
		
		splitPane.setResizeWeight(0.05);
		splitPane.setLeftComponent(componentList);
		
		componentList.setListData(Components.toArray());
		componentList.setLayoutOrientation(JList.VERTICAL);// the single line list
		componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// single selection.
		componentList.setBackground(COLOR_LIST_BG);
		componentList.setForeground(COLOR_LIST_FG);
		componentList.setSelectionBackground(COLOR_LIST_SELECTION_BG);
		componentList.setSelectionForeground(COLOR_LIST_SELECTION_FG);
		componentList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listselected();
			}
		});
		return splitPane;
	}
	
	public static Dot convertCom2DOT(String component,HashSet<ComponentModels> component2HPNSet)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		dot.setOption("label", "Component Model: "+component);
		dot.setOption("fontsize", "24");
	
		//go through each component model, 
		for(ComponentModels com :component2HPNSet)
		{
			if(com.getComponent().equals(component))
			{
				for(Interface2HPN Interface2HPN: com.getI2hpn())// handle each component interface. 
				{
					DotCluster InterfaceCluster =dot.addCluster();
					//InterfaceCluster.setOption("label", Interface2HPN.getInterface().toString());
					InterfaceCluster.setOption("label", Interface2HPN.getInterface().getId());
					InterfaceCluster.setOption("penwidth", "3.0");
					InterfaceCluster.setOption("style","dashed");
					VisualizeHPNandInteraction2Dot.visualizeHPN2Dot(Interface2HPN.getHPN(), 
							Interface2HPN.getInterface().toString(),InterfaceCluster, null);
				}
			}
		}
		
		return dot;
	}
	
	private void listselected() 
	{
		// two types of mouse operations, if this is not true
		if (!componentList.getValueIsAdjusting())
		{
			//System.out.println(traceList.getSelectedValue());
			rightDotpanel=new DotPanel(component2dot.get(componentList.getSelectedValue()));
			splitPane.setRightComponent(rightDotpanel);
			//rightDotpanel.repaint();
			
		}
	}
	
}
