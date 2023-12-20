package MultiInstanceProcessDiscovery;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class VisualizeHierarchicalPetriNet {
	@Plugin(name = "Visualize Hierarchical Petri Net", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "SoftwareInteractionModel" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeTop(PluginContext context, HierarchicalPetriNet hpn)
	{
		Dot dot = convertCom2DOT("",hpn);
		
		return new DotPanel(dot);
	}
	
	public Dot convertCom2DOT(String componentName, HierarchicalPetriNet hpn)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		//dot.setDirection(GraphDirection.leftRight);
		//dot.setOption("label", "Component Model: "+componentName);
		dot.setOption("fontsize", "24");
	
		VisualizeHPNandInteraction2Dot.visualizeHPN2Dot(hpn, componentName, dot, null);
		return dot;
	}
}
