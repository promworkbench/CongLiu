package congliu.processmining.objectusage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import cong.liu.processmining.classinteractiongraph.Component2ClassesWrapper;

/**
 * this class provides an interactive manner to visualize class co-occurence graph. 
 * In addition, the filtered graph can be exported to the ProM workspace.
 * @author cliu3
 *
 */

@Plugin(name = "Visualize Co-occurance Graph with slider", 
returnLabels = { "Dot visualization" }, 	
returnTypes = { JComponent.class }, 	
parameterLabels = { "Flexible Co-occurance Graph" }, 	
userAccessible = true)
@Visualizer
public class VisualizeCoocurenceGraphWithSlider {
	
	// define the main splitPane as the main visualization 
	private ProMSplitPane splitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
	// set the right part be a dot panel, a slider and a export button
	private JPanel rightDotpanel= new JPanel();
	// set the left part be a dot panel
	private JPanel leftDotpanel= new JPanel();
	private Dot dot=null;
	private JSlider slider= new JSlider(JSlider.VERTICAL);//create the slider
	private JButton exportButton = new JButton("Export");//create a button
	private PluginContext Fcontext;
	// create a new cog
	SimpleWeightedGraph<String, DefaultWeightedEdge> Newg = 
			new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	CooccuranceGraph currentCOG =new CooccuranceGraph(Newg);
	
	Component2Classes c2cs =new Component2Classes();
	final Component2ClassesWrapper c2csWrapper= new Component2ClassesWrapper(c2cs);
		
	int LowestBound = 0;
	int HighestBound = 0;
	int DefaultValue = 0;    //initial frames per second
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong Liu", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "cog", requiredParameterLabels = {0})
	public JComponent visualize(UIPluginContext context, final CooccuranceGraph cog) 
	{	
		Fcontext=context;
		//currentCOG=cog;
		splitPane.setResizeWeight(0.9);
		
		// the default setting of 
		c2csWrapper.setValue(ClusteringClassCooccurenceGraph.getClusters(cog));
		
		//set the maximal value for the slider
		HighestBound = ClusteringClassCooccurenceGraph.getHighestVaule(cog);
				
		/**
		 * add a slider to set the threshold. on the right panel.
		 */
		dot = VisualizeCooccuranceGraph.convert(cog);
		
		leftDotpanel=new DotPanel(dot);
		splitPane.setLeftComponent(leftDotpanel);// set the left panel
		
		//create a slider 
		slider.setMaximum(HighestBound);
		slider.setMinimum(LowestBound);
		slider.setValue(DefaultValue);
		if(HighestBound<500)
		{
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
		}
		else if(HighestBound <2000)
		{
			slider.setMajorTickSpacing(30);
			slider.setMinorTickSpacing(1);
		}
		else if (HighestBound <10000)
		{
			slider.setMajorTickSpacing(200);
			slider.setMinorTickSpacing(1);
		}
		else {
			slider.setMajorTickSpacing(500);
			slider.setMinorTickSpacing(1);
		}
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setName("Edge Filtering Threshold");
		
		rightDotpanel.setLayout(new BorderLayout());
		rightDotpanel.add(slider, BorderLayout.CENTER);
		splitPane.setRightComponent(rightDotpanel);// set the right part of the splitpane.
		
		//add change listerner for the slider.
		slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				  int frequency = slider.getValue();
			      // filterting the co-occurence graph based on the selected value. 
				  currentCOG =ClusteringClassCooccurenceGraph.filterEdges(frequency,cog);
				  dot = VisualizeCooccuranceGraph.convert(currentCOG);
				  c2csWrapper.setValue(ClusteringClassCooccurenceGraph.getClusters(currentCOG));

			      leftDotpanel=new DotPanel(dot);
			      splitPane.setLeftComponent(leftDotpanel);
//			      leftDotpanel.repaint();
			}
		});

		//add action for the button
		rightDotpanel.add(exportButton, BorderLayout.SOUTH);
	
//		exportButton.addActionListener(new ButtonActionListernerSave(context, c2csWrapper.getValue()));
		
		exportButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Button has been clicked");
				Fcontext.getProvidedObjectManager().createProvidedObject("Clustering Results",
						c2csWrapper.getValue(), Component2Classes.class, Fcontext);
//				  for(String c: c2csWrapper.getValue().getAllComponents())
//					{
//						System.out.println(c+":"+c2csWrapper.getValue().getClasses(c));
//					}
			   if (Fcontext instanceof UIPluginContext) {
			          UIPluginContext uiPluginContext = (UIPluginContext) Fcontext;
			          uiPluginContext.getGlobalContext().getResourceManager().getResourceForInstance(c2csWrapper.getValue()).setFavorite(true);
				}
			}
		});
		return splitPane;
	}
	
}
