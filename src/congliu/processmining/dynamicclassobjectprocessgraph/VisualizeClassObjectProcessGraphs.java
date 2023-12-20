package congliu.processmining.dynamicclassobjectprocessgraph;

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
 * this visualizer tries to visualize the dynamic class object graphs discovered. 
 * @author cliu3
 *
 */
public class VisualizeClassObjectProcessGraphs {
	//the component to dot map
	private HashMap<String, Dot> class2dot = new HashMap<String, Dot>();
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	// set the right part be a dot panel
	private JPanel rightDotpanel= new JPanel();
	// set the left part be a component list. 
	private JList classList = new JList();
	
	public static final Color COLOR_LIST_BG = new Color(60, 60, 60);
	public static final Color COLOR_LIST_FG = new Color(180, 180, 180);
	public static final Color COLOR_LIST_SELECTION_BG = new Color(80, 0, 0);
	public static final Color COLOR_LIST_SELECTION_FG = new Color(240, 240, 240);
	
	@Plugin(name = "Visualize Dynamic Class Object Process Graphs", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "SoftwareInteractionModel" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	
	public JComponent visualizeTop(PluginContext context, ClassObjectProcessGraphs classObjGraphs)
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Dynamic Class Object Process Graphs");
				
		// get the classes
		Set<String> classSet =classObjGraphs.getClassSet();
	
		//for each class, we visualize its (HPNs) as dot.
		for(String className: classSet)
		{
			Dot dot = convertClass2DOT(className,classObjGraphs.getClassHPN(className));
			class2dot.put(className, dot);
		}
		
		
		splitPane.setResizeWeight(0.05);
		splitPane.setLeftComponent(classList);
		
		classList.setListData(classSet.toArray());
		classList.setLayoutOrientation(JList.VERTICAL);// the single line list
		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// single selection.
		classList.setBackground(COLOR_LIST_BG);
		classList.setForeground(COLOR_LIST_FG);
		classList.setSelectionBackground(COLOR_LIST_SELECTION_BG);
		classList.setSelectionForeground(COLOR_LIST_SELECTION_FG);
		classList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listselected();
			}
		});
		return splitPane;
	}
	public Dot convertClass2DOT(String className, HierarchicalPetriNet hpn)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		dot.setOption("label", "Dynamic Class Object Process Graph");
		dot.setOption("fontsize", "24");
	
		VisualizeHPNandInteraction2Dot.visualizeHPN2Dot(hpn, className, dot, null);
		return dot;
	}
	
	
	private void listselected() 
	{
		// two types of mouse operations, if this is not true
		if (!classList.getValueIsAdjusting())
		{
			//System.out.println(traceList.getSelectedValue());
			rightDotpanel=new DotPanel(class2dot.get(classList.getSelectedValue()));
			splitPane.setRightComponent(rightDotpanel);
			//rightDotpanel.repaint();
		}
	}
}
