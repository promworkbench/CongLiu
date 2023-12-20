package observerpatterndiscovery;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

@Plugin(name = "Visualize Observer Design Patterns", 
returnLabels = { "Dot visualization" }, 
returnTypes = { JComponent.class }, 
parameterLabels = {"Graph"}, 
userAccessible = true)
@Visualizer
public class VisualizeObserverPattern {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "OPSet", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, ObserverPatternSet OPSet) 
	{
		Dot dot = convert(OPSet);
		return new DotPanel(dot);
	}
	
	public static Dot convert(ObserverPatternSet OPSet) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		for(ObserverPatternClass opC: OPSet.getObserverPatternSet())
		{
			DotCluster cluster = dot.addCluster();
			cluster.addNode("Subject Class: "+opC.getSubjectClass());
			cluster.addNode("Listener Class: "+opC.getListernerClass());
			cluster.addNode("Notify Method: "+opC.getNotifyMethod());
			cluster.addNode("Update Method: "+opC.getUpdateMethod());
			cluster.addNode("de/register Methods: "+opC.getDe_registeringMethod());
		}
		
		return dot;
	}
}
