package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.reduction.Murata;
import org.processmining.plugins.petrinet.reduction.MurataParameters;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

/**
 * this plug-ins visualize the execution behavior of each method, in terms of Petri net. 
 * @author cliu3
 *
 */

//plugin name
@Plugin(
		name = "Method Behavior Discovery",// plugin name	
			returnLabels = {"Object Usages"}, //reture labels
			returnTypes = {ObjectUsageSet.class},//return class
			
			//input parameter labels, corresponding with the second parameter of main function
			parameterLabels = {"Software event log"},
			
			userAccessible = true,
			help = "This plugin aims to discover method behavior from a software event log." 
			)
public class MethodBehaviorDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Method behavior Usages, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public ObjectUsageSet methodBehaviorDiscovery(UIPluginContext context, XLog softwareLog) throws ConnectionCannotBeObtained
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Object Usages of: "+XConceptExtension.instance().extractName(softwareLog));
	
		ObjectUsageSet ou = new ObjectUsageSet();
		
		//get the method set of the software event log
		obtainMethods om= new obtainMethods();
		HashSet<String> methods= om.getMethods(softwareLog);
		
		XFactory factory = new XFactoryNaiveImpl();
		
		//for each method, we construct its software event log from the original log.
		obtainMethod2subLog om2l= new obtainMethod2subLog();
		HashMap<String, XLog> method2Log =om2l.getMethod2subLog(softwareLog, methods, factory);
	
		//for each group, we discover its corresponding petri net use inductive miner. 
		MiningParameters IMparameterNew = new MiningParametersIMflc();
		IMparameterNew.setClassifier(softwareLog.getClassifiers().get(0));
		IMparameterNew.setNoiseThreshold((float) 0.2);
		
		for(String g: method2Log.keySet())
		{
			Object[] objs =IMPetriNet.minePetriNet(OrderingEventsNano.ordering(method2Log.get(g), XSoftwareExtension.KEY_STARTTIMENANO), 
					IMparameterNew, new Canceller() {
				public boolean isCancelled() {
					return false;
				}
			});
			
			//System.out.println(objs[0]+"&&"+objs[1]+"&&"+objs[2]);
			// use Petri net reduction rules, based on Murata rules, i.e., Reduce Silent Transitions, Preserve Behavior
			Murata  murata = new Murata ();
			MurataParameters paras = new MurataParameters();
			paras.setAllowFPTSacredNode(false);
			Petrinet pn =(Petrinet) murata.run(context, (Petrinet)objs[0], (Marking)objs[1], paras)[0];
			
			PetriNetMarkings pnm = new PetriNetMarkings();
			pnm.setPn(pn);
			pnm.setInitialM((Marking)objs[1]);
			pnm.setFinalM((Marking)objs[2]);
			ou.addUsages(g, pnm);
		}
		
		return ou;
	
	}
}
