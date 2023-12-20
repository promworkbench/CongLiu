package designpatterns.adapterpattern;

import java.util.HashSet;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import observerpatterndiscovery.ClassClass;

@Plugin(name = "Visualize Class Type Hierarchy", 
returnLabels = { "Dot visualization" }, 	
returnTypes = { JComponent.class }, 	
parameterLabels = {"Visualize Class Type Hierarchy"}, 	
userAccessible = true)
@Visualizer
public class VisualizeClassTypeHierarchy {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong Liu", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "cog", requiredParameterLabels = {0})
	
	public JComponent visualize(UIPluginContext context, ClassTypeHierarchy cth) 
	{	
		Dot dot = convert(cth);
		return new DotPanel(dot);
	}
	
	public static Dot convert(ClassTypeHierarchy cth)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		//dot.setDirection(GraphDirection.topDown);
		
		//for each group we create a cluster, and each class we create a node. 
		for(HashSet<ClassClass> cc: cth.getAllCTH())
		{
			DotCluster cluster=dot.addCluster();
			cluster.setOption("style","dashed");
			
			for(ClassClass c: cc)
			{
				DotNode tempNode =cluster.addNode(c.toString());
				tempNode.setOption("shape", "box");
			}
		}
		
		return dot;
	}
	
}
