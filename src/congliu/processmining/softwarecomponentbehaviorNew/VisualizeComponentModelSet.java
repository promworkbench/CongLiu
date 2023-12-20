package congliu.processmining.softwarecomponentbehaviorNew;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;

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
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.VisualizeHPNandInteraction2Dot;
import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

/**
 * this visualizer tries to visualize the component behavior model of each component. 
 * without considering the component interaction and interface cardinality information. 
 * @author cliu3
 *
 */

public class VisualizeComponentModelSet {
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
		
		public JComponent visualizeTop(PluginContext context, ComponentModelSet componentModels)
		{
			// get the components
			Set<String> componentSet =componentModels.getComponentSet();
		
			//for each component, we visualize its interface (HPNs) as dot.
			for(String componentName: componentSet)
			{
				Dot dot = convertCom2DOT(componentName,componentModels.getComponentHPN(componentName));
				component2dot.put(componentName, dot);
			}
			
			
			splitPane.setResizeWeight(0.05);
			splitPane.setLeftComponent(componentList);
			
			componentList.setListData(componentSet.toArray());
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
		
		public Dot convertCom2DOT(String componentName, HierarchicalPetriNet hpn)
		{
			Dot dot = new Dot();
			dot.setDirection(GraphDirection.topDown);
			dot.setOption("label", "Component Model: "+componentName);
			dot.setOption("fontsize", "24");
		
			VisualizeHPNandInteraction2Dot.visualizeHPN2Dot(hpn, componentName, dot, null);
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
