package congliu.processmining.objectusage;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer tries to visualize the object usage model of each group. 
 * @author cliu3
 *
 */
public class VisualizeObjectUsagePatterns {
	//the component to dot map
	private HashMap<String, Dot> group2dot = new HashMap<String, Dot>();
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	// set the right part be a dot panel
	private JPanel rightDotpanel= new JPanel();
	// set the left part be a component list. 
	private JList groupList = new JList();
	private JScrollPane scrollPane = new JScrollPane(groupList);// add scroller to the list
	
	public static final Color COLOR_LIST_BG = new Color(60, 60, 60);
	public static final Color COLOR_LIST_FG = new Color(180, 180, 180);
	public static final Color COLOR_LIST_SELECTION_BG = new Color(80, 0, 0);
	public static final Color COLOR_LIST_SELECTION_FG = new Color(240, 240, 240);
	
	@Plugin(name = "Visualize Object Usage Patterns", 
			returnLabels = {"Dot visualization"}, 
			returnTypes = {JComponent.class}, 
			parameterLabels = {"ObjectUsageSet"}, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeTop(PluginContext context, ObjectUsageSet objectUsageSet)
	{	
		// get the groups.
		Set<String> Groups = objectUsageSet.getGroups();
		
		//for each group, we visualize its usage (PN) as dot.
		for(String g: Groups)
		{
			PetriNetMarkings pnm = objectUsageSet.getUsage(g);
			Dot dot = convertGroup2dot(g, pnm.getPn(), pnm.getInitialM(), pnm.getFinalM());
			group2dot.put(g, dot);
		}
		
		splitPane.setResizeWeight(0.05);
		splitPane.setLeftComponent(scrollPane);
		groupList.setListData(Groups.toArray());
		groupList.setLayoutOrientation(JList.VERTICAL);// the single line list
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// single selection.
		groupList.setBackground(COLOR_LIST_BG);
		groupList.setForeground(COLOR_LIST_FG);
		groupList.setSelectionBackground(COLOR_LIST_SELECTION_BG);
		groupList.setSelectionForeground(COLOR_LIST_SELECTION_FG);
		groupList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listselected();
			}
		});
		return splitPane;
	}
	
	public Dot convertGroup2dot(String group,Petrinet pn, Marking initialM, Marking finalM)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		dot.setOption("label", group);
		dot.setOption("fontsize", "24");
	
		return (Dot)VisualizePN.visualizePN2Dot(pn, initialM, finalM, dot);
	}
	
	private void listselected() 
	{
		// two types of mouse operations, if this is not true
		if (!groupList.getValueIsAdjusting())
		{
			//System.out.println(traceList.getSelectedValue());
			rightDotpanel=new DotPanel(group2dot.get(groupList.getSelectedValue()));
			splitPane.setRightComponent(rightDotpanel);
			//rightDotpanel.repaint();
			
		}
	}
}
