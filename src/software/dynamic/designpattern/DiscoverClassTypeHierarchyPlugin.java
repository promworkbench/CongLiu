package software.dynamic.designpattern;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import observerpatterndiscovery.ClassClass;
import software.designpattern.ObserverPatternDiscoveryAndChecking;

/**
 * this plug-in aims to discover the class type hierarchy information included in a software event log. 
 * The main idea is to identify a set of classes that have the same object. 
 * @author cliu3
 *
 */
@Plugin(
		name = "Class Type Hierarchy Discovery From Software Event Log",// plugin name
		
		returnLabels = {"ClassTypeHierarchy Patterns"}, //reture labels
		returnTypes = {ClassTypeHierarchy.class},//return class
		
		//input parameter labels
		parameterLabels = {"Software event log"},
		
		userAccessible = true,
		help = "This plugin aims to improve the Design Pattern results discovered from DPD tool." 
		)
public class DiscoverClassTypeHierarchyPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Class Type Hierarchy Discovery From Software Event Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public ClassTypeHierarchy discoveryClassTypeHierarchyFromLog(UIPluginContext context, XLog softwareLog)
	{
		return discoveryClassTypeHierarchy(softwareLog);
	}
	
	public static ClassTypeHierarchy discoveryClassTypeHierarchy(XLog softwareLog)
	{
		ClassTypeHierarchy  cth = new ClassTypeHierarchy();
		//create a map from object id to its class set
		HashMap<String, HashSet<ClassClass>> object2ClassSet = new HashMap<>();
		
		//create a set that store all packages that has already exist in the caller and calee. 
		//then for parameters, only classes belongs to these packages will be considered. 
		HashSet<String> existingPackages = new HashSet<String>();
		
//		//create a set that store all java library packages
//		HashSet<String> apiPackages = new HashSet<String>();
//		apiPackages.add("java.lang"); 
//		apiPackages.add("java.awt"); 
//		apiPackages.add("java.util"); 
//		
//		//create a set that store all java type classes we also remove classes int, long, double, String 
//		HashSet<String> typeClasses = new HashSet<>();
//		typeClasses.add("int");
//		typeClasses.add("long");
//		typeClasses.add("double");
//		typeClasses.add("int[]");
//		typeClasses.add("long[]");
//		typeClasses.add("String[]");
//		typeClasses.add("double[]");
//		typeClasses.add("boolean");
	
		for(XTrace trace : softwareLog)
		{
			for(XEvent event: trace)
			{
				//some special classes are not considered, (1) int, doubel...(2) anonymous inner class
				if(XSoftwareExtension.instance().extractClass(event).contains("$")||
						XSoftwareExtension.instance().extractCallerclass(event).contains("$"))
				{
					continue;
				}
				//for the callee object ==> callee package +callee class +callee method 
				ClassClass calleeC = new ClassClass();
				calleeC.setClassName(XSoftwareExtension.instance().extractClass(event));
				calleeC.setPackageName(XSoftwareExtension.instance().extractPackage(event));
				
				//the current object is not included in the map
				if(!XSoftwareExtension.instance().extractClassObject(event).equals("null")
						&&!XSoftwareExtension.instance().extractClassObject(event).equals("0"))//static object should not be considered. 
				{
					if(!object2ClassSet.containsKey(XSoftwareExtension.instance().extractClassObject(event)))
					{
						HashSet<ClassClass> tempH = new HashSet<>();
						tempH.add(calleeC);
						existingPackages.add(calleeC.getPackageName());
						object2ClassSet.put(XSoftwareExtension.instance().extractClassObject(event),tempH);
						
					}
					else{
						object2ClassSet.get(XSoftwareExtension.instance().extractClassObject(event)).add(calleeC);
						existingPackages.add(calleeC.getPackageName());
					}
				}
				
				
				//for the caller object==>caller package +caller class
				ClassClass callerC = new ClassClass();
				callerC.setClassName(XSoftwareExtension.instance().extractCallerclass(event));
				callerC.setPackageName(XSoftwareExtension.instance().extractCallerpackage(event));
				
				//the current object is not included in the map
				if(!XSoftwareExtension.instance().extractCallerclassobject(event).equals("null")
						&&!XSoftwareExtension.instance().extractCallerclassobject(event).equals("0"))//static method is not included
				{
					if(!object2ClassSet.containsKey(XSoftwareExtension.instance().extractCallerclassobject(event)))
					{
						HashSet<ClassClass> tempH = new HashSet<>();
						tempH.add(callerC);
						existingPackages.add(callerC.getPackageName());
						object2ClassSet.put(XSoftwareExtension.instance().extractCallerclassobject(event),tempH);
					}
					else{
						object2ClassSet.get(XSoftwareExtension.instance().extractCallerclassobject(event)).add(callerC);
						existingPackages.add(callerC.getPackageName());
					}
				}
			}
			
		}

		for(XTrace trace : softwareLog)
		{
			for(XEvent event: trace)
			{
				HashMap<ClassClass,String> paras =ObserverPatternDiscoveryAndChecking.constructParameterMapping(event);
				
				for(ClassClass c: paras.keySet())
				{
					//some anonymous inner classes are not considered
					//we only consider classes whose package is included in existingPackages

					if(!existingPackages.contains(c.getPackageName())||c.getClassName().contains("$"))
					{
						continue;
					}
					
					
					if(paras.get(c)!=null)
					{
						//the current object is not included in the map
						if(!object2ClassSet.containsKey(paras.get(c)))
						{
							HashSet<ClassClass> tempH = new HashSet<>();
							tempH.add(c);
							object2ClassSet.put(paras.get(c),tempH);
						}
						else{
							object2ClassSet.get(paras.get(c)).add(c);
						}
					}
					
				}
			}
		}			

		for(String o:object2ClassSet.keySet())
		{
			if(!o.contains("@"))
			{
				System.out.println(o+":"+object2ClassSet.get(o));
				cth.addCTHbyMerging(object2ClassSet.get(o));// combine the hashset that share elements. 
			}
		}

		return cth;
	}
	
}
