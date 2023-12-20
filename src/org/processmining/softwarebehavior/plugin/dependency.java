package org.processmining.softwarebehavior.plugin;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.flexiblemodel.FlexStartTaskNodeConnection;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexFactory;
import org.processmining.models.flexiblemodel.FlexNode;
import org.processmining.models.flexiblemodel.SetFlex;
import org.processmining.models.flexiblemodel.StartTaskNodesSet;


/*
 * Class outline:
 * remove or keep the exceptional cases in the log
 * Input parameters:
 * (1)Log indicates the original log
 * OutPut parameters: 
 * (1)output the normal or exceptional log
*/


//public class dependency {
//		@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "test", email = "test")
//		@Plugin(name = "Produce Flexible Model for Validation", 
//				parameterLabels = {}, 
//				returnLabels = { "Flexible Model Validation" , "Start Tasks" }, 
//				returnTypes = { Flex.class , StartTaskNodesSet.class }, 
//				userAccessible = true, 
//				help = "Produces a Flexible Model and its start task")
	
//	@Plugin(
//			name = "Detectdependency", 
//			parameterLabels = { "Log"}, 
//			returnLabels = { "Normal log" }, 
//			returnTypes = { XLog.class },
//			userAccessible = true, 
//			help = "The plugin is used to detect the deviation in the event log"
//		)
//	@PluginVariant(variantLabel = "Detectdependency", requiredParameterLabels = {0})
//	@UITopiaVariant(uiLabel = "Detectdependency", affiliation = "TU/e", author = "G.Li", email = "G.Li.3@tue.nl")
//	public XLog Dependency(UIPluginContext context, XLog Log) throws CancellationException
//	 {
//		XLog  oldLog = (XLog) Log.clone();//keep the input log unchanged
//			
//
//			return oldLog;
//			
//		
//	}
	
	public class dependency {
		@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "test", email = "test")
		@Plugin(name = "Produce kfgz Flexible Model for Validation", 
				parameterLabels = {}, 
				returnLabels = { "Flexible Model Validation" , "Start Tasks" }, 
				returnTypes = { Flex.class , StartTaskNodesSet.class }, 
				userAccessible = true, 
				help = "Produces a Flexible Model and its start task")
		public Object[] petriNetProduce(UIPluginContext context) {
		final Flex flexDiagram = FlexFactory.newFlex("Flexible Model 1");
		FlexNode casestart=flexDiagram.addNode("hello");
		FlexNode bz02= flexDiagram.addNode("BZO2");
		FlexNode bz04=flexDiagram.addNode("BZ04");
		FlexNode bz05=flexDiagram.addNode("BZ05");
		FlexNode bz08=flexDiagram.addNode("BZ08");
		FlexNode bz30=flexDiagram.addNode("BZ30");
		FlexNode bz09=flexDiagram.addNode("BZ09");
		FlexNode bz10=flexDiagram.addNode("BZ10");
		FlexNode bz12=flexDiagram.addNode("BZ12");
		FlexNode bz14=flexDiagram.addNode("BZ14");
		FlexNode bz16=flexDiagram.addNode("BZ16");
		FlexNode bz18=flexDiagram.addNode("BZ18");
		FlexNode bz28=flexDiagram.addNode("BZ28");
		FlexNode bz20=flexDiagram.addNode("BZ20");
		
		flexDiagram.addArc(casestart, bz02);
		flexDiagram.addArc(bz02, bz04);
		flexDiagram.addArc(bz04, bz02);
		flexDiagram.addArc(bz04, bz05);
		flexDiagram.addArc(bz05, bz04);
		flexDiagram.addArc(bz04, bz08);
		flexDiagram.addArc(bz08, bz09);
		flexDiagram.addArc(bz08, bz30);
		flexDiagram.addArc(bz09, bz10);
		flexDiagram.addArc(bz10, bz12);
		flexDiagram.addArc(bz12, bz14);
		flexDiagram.addArc(bz14, bz16);
		flexDiagram.addArc(bz16, bz18);
		flexDiagram.addArc(bz18, bz20);
		flexDiagram.addArc(bz20, bz18);
		flexDiagram.addArc(bz18, bz28);
		
//		SetFlex setIni=new SetFlex();
//		setIni.add(casestart);
//		
//		SetFlex setBZ02=new SetFlex();
//		setBZ02.add(bz02);
//
//		SetFlex setBZ04=new SetFlex();
//		setBZ04.add(bz04);
//		
//		SetFlex setBZ05=new SetFlex();
//		setBZ05.add(bz05);
//		
//		SetFlex setBZ08=new SetFlex();
//		setBZ08.add(bz08);
//		
//		SetFlex setBZ30=new SetFlex();
//		setBZ30.add(bz30);
//		
//		SetFlex setBZ09=new SetFlex();
//		setBZ09.add(bz09);
//		
//		SetFlex setBZ10=new SetFlex();
//		setBZ10.add(bz10);
//		
//		SetFlex setBZ12=new SetFlex();
//		setBZ12.add(bz12);
//		
//		SetFlex setBZ14=new SetFlex();
//		setBZ14.add(bz14);
//		
//		SetFlex setBZ16=new SetFlex();
//		setBZ16.add(bz16);
//		
//		SetFlex setBZ18=new SetFlex();
//		setBZ18.add(bz18);
//		
//		SetFlex setBZ28=new SetFlex();
//		setBZ28.add(bz28);
//		
//		SetFlex setBZ20=new SetFlex();
//		setBZ20.add(bz20);
//		
//		casestart.addOutputNodes(setBZ02);
//		
//		bz02.addInputNodes(setIni);
//		bz02.addInputNodes(setBZ04);
//		bz02.addOutputNodes(setBZ04);
//		
//		bz04.addInputNodes(setBZ02);
//		bz04.addInputNodes(setBZ05);
//		bz04.addOutputNodes(setBZ02);
//		bz04.addOutputNodes(setBZ05);
//		bz04.addOutputNodes(setBZ08);
//		
//		
//		bz05.addInputNodes(setBZ04);
//		bz05.addOutputNodes(setBZ04);
//		
//		bz08.addInputNodes(setBZ04);
//		bz08.addOutputNodes(setBZ09);
//		bz08.addOutputNodes(setBZ30);
//		
//		bz09.addInputNodes(setBZ08);
//		bz09.addOutputNodes(setBZ10);
//		
//		bz10.addInputNodes(setBZ09);
//		bz10.addOutputNodes(setBZ12);
//		
//		bz12.addInputNodes(setBZ10);
//		bz12.addOutputNodes(setBZ14);
//		
//		bz14.addInputNodes(setBZ12);
//		bz14.addOutputNodes(setBZ16);
//		
//		bz16.addInputNodes(setBZ14);
//		bz16.addOutputNodes(setBZ18);
//		
//		bz18.addInputNodes(setBZ16);
//		bz18.addInputNodes(setBZ20);
//		bz18.addOutputNodes(setBZ28);
//		bz18.addOutputNodes(setBZ20);
//		
//		bz28.addInputNodes(setBZ18);
//		
//		bz20.addInputNodes(setBZ18);
//		bz20.addOutputNodes(setBZ18);
//		
//		bz30.addInputNodes(setBZ08);
//		
//		casestart.commitUpdates();
//	    bz02.commitUpdates();
//		bz04.commitUpdates();
//	    bz05.commitUpdates();
//		bz08.commitUpdates();
//		bz30.commitUpdates();
//		bz09.commitUpdates();
//	    bz10.commitUpdates();
//		bz12.commitUpdates();
//		bz14.commitUpdates();
//	    bz16.commitUpdates();
//	    bz18.commitUpdates();
//	    bz28.commitUpdates();
//	    bz20.commitUpdates();
		
		
		
		
		
	    StartTaskNodesSet startTaskNodeSet =new StartTaskNodesSet();
	    SetFlex setFlex = new SetFlex();
	    setFlex.add(casestart);
	    startTaskNodeSet.add(setFlex);
	    
		// create connection between flexible model and start task node
		context.addConnection(new FlexStartTaskNodeConnection(flexDiagram.getLabel(), flexDiagram, startTaskNodeSet));

		// return objects
		return new Object[] { flexDiagram, startTaskNodeSet };
		}
}


