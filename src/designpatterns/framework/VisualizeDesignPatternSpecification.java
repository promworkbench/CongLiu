package designpatterns.framework;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class VisualizeDesignPatternSpecification {
	@Plugin(name = "Visualize Design Pattern Specification", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "PatternSpecificatin" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Design Pattern Specification", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeSpecification(PluginContext context, DesignPatternSpecification patternSpecification)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		String patternName = "Name: "+patternSpecification.getPatternName();
		
		String roleSet = "Roles: "+patternSpecification.getRoleSet();
		
		String mainRole = "Main Role: "+ patternSpecification.getMainRole();
		
		String logConstraints = "Log Constraints: "+ patternSpecification.getLogConstraintSet();
		
		String instanceConstraints = "Invocation Constraints: "+ patternSpecification.getInstanceConstraintSet();
		
		DotNode tab = dot.addNode("{"+patternName+"}|"+"{"+roleSet+"}|"+"{"+mainRole+"}|"+"{"+logConstraints+"}|"+"{"+instanceConstraints+"}");
		//DotNode tab = dot.addNode("{1|2|3}|{4|500|6}|{7|8|9}");
	    tab.setOption("shape", "record");
	    return new DotPanel(dot);
	}
}
