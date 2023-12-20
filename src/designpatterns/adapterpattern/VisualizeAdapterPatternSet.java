package designpatterns.adapterpattern;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
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
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import designpatterns.framework.PatternClass;

/**
 * this visualizer tries to visualize the detected adapter pattern instance. 
 * @author cliu3
 *
 */
public class VisualizeAdapterPatternSet {
	
	//the pattern instance to dot mapping
	private HashMap<String, Dot> pattern2dot = new HashMap<String, Dot>();
	
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	
	// set the right part be a dot panel
	private JPanel rightDotpanel= new JPanel();
	
	// set the left part be a component list. 
	private JList patternList = new JList();
	
	public static final Color COLOR_LIST_BG = new Color(60, 60, 60);
	public static final Color COLOR_LIST_FG = new Color(180, 180, 180);
	public static final Color COLOR_LIST_SELECTION_BG = new Color(80, 0, 0);
	public static final Color COLOR_LIST_SELECTION_FG = new Color(240, 240, 240);
	
	@Plugin(name = "Visualize Adapter Pattern Instances", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "PatternInstance" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Adapter Pattern Instances", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeTop(PluginContext context, AdapterPatternSet patternSet)
	{
		Set<String> instanceSet =new HashSet<>();
		
		//for each pattern instance, we visualize its specification as dot.
		int i=1;
		for(PatternClass instance: patternSet.getPatternSet())
		{
			Dot dot = convertInstance2DOT((AdapterPatternClass)instance);
			instanceSet.add("instance"+i);
			pattern2dot.put("instance"+i, dot);
			i++;
			
		}
		
		splitPane.setResizeWeight(0.05);
		splitPane.setLeftComponent(patternList);
		
		patternList.setListData(instanceSet.toArray());
		patternList.setLayoutOrientation(JList.VERTICAL);// the single line list
		patternList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// single selection.
		patternList.setBackground(COLOR_LIST_BG);
		patternList.setForeground(COLOR_LIST_FG);
		patternList.setSelectionBackground(COLOR_LIST_SELECTION_BG);
		patternList.setSelectionForeground(COLOR_LIST_SELECTION_FG);
		patternList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listselected();
			}
		});
		return splitPane;
	}
	public static Dot convertInstance2DOT(AdapterPatternClass pattern)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		
		// prepare the table
		String Adapter= "Adapter Class: "+pattern.getAdapterClass();
		String Adaptee ="Adaptee Class: "+pattern.getAdapteeClass();
		String request = "request Method: "+pattern.getRequestMethod();
		System.out.println(pattern.getRequestMethod().toString());
		String specificRequest = "specificRequest Method: "+pattern.getSpecificRequestMethod();
		String traceInvocation= "Trace/Invocation Count: "+pattern.getTraceNumber()+"/"+pattern.getInvocationNumber();
		
		DotNode tab = dot.addNode("{"+Adapter+"}|"+"{"+Adaptee+"}|"+"{"+request+"}|"+"{"+specificRequest+"}|"+"{"+traceInvocation+"}");
		//DotNode tab = dot.addNode("{1|2|3}|{4|500|6}|{7|8|9}");
	    tab.setOption("shape", "record");
	    return dot;
	}
	private void listselected() 
	{
		// two types of mouse operations, if this is not true
		if (!patternList.getValueIsAdjusting())
		{
			//System.out.println(traceList.getSelectedValue());
			rightDotpanel=new DotPanel(pattern2dot.get(patternList.getSelectedValue()));
			splitPane.setRightComponent(rightDotpanel);
			//rightDotpanel.repaint();
		}
	}
}
