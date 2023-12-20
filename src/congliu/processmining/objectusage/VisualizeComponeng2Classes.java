package congliu.processmining.objectusage;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

@Plugin(name = "Visualize Component to Classes Mapping", 
returnLabels = { "Dot visualization" }, 	
returnTypes = { JComponent.class }, 	
parameterLabels = {"Visualize Component to Classes Mapping"}, 	
userAccessible = true)
@Visualizer
public class VisualizeComponeng2Classes {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong Liu", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "cog", requiredParameterLabels = {0})
	
	public JComponent visualize(UIPluginContext context, Component2Classes c2c) 
	{	
		Dot dot = convert(c2c);
		return new DotPanel(dot);
	}
	
	public static Dot convert(Component2Classes c2c)
	{
		Dot dot = new Dot();
		//dot.setDirection(GraphDirection.leftRight);
		dot.setDirection(GraphDirection.topDown);
		//for each component, we create a cluster, and each class we create a node. 
		
		for(String c: c2c.getAllComponents())
		{
			DotCluster cluster=dot.addCluster();
			cluster.setLabel(c);
			cluster.setOption("style","dashed");
			
			for(String cl:c2c.getClasses(c))
			{
				cluster.addNode(cl);
			}
			
		}
		
		return dot;
	}
}
