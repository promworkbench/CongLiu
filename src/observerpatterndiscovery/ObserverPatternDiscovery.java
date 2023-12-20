package observerpatterndiscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;

/**
 * this class defines the functions of observer pattern discovery.
 * @author cliu3
 *
 */
public class ObserverPatternDiscovery {

	/**
	 * for the input original log, we first construct the class ==> method log. 
	 * @param originalLog
	 * @return
	 */
	public Class2MethodSet getClass2MethodSet(XLog originalLog)
	{
		Class2MethodSet class2methodset = new Class2MethodSet();
		
		for(XTrace trace: originalLog)
		{
			for(XEvent event: trace)
			{
				//the current class is included in the class2methodset, then add the corresponding method to 
				ClassClass currentClass = new ClassClass(
						XSoftwareExtension.instance().extractPackage(event), 
						XSoftwareExtension.instance().extractClass(event));
				
				MethodClass currentMethod = new MethodClass();
				currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
				currentMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
				currentMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
				currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
				Set<String> currentParameterSet = new HashSet<String>();
				
				String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);
				//System.out.println(tempPara);
				if(tempPara.contains(","))
				{
					for(String para: tempPara.split("\\,"))
					{
						currentParameterSet.add(para);
					}
				}
				else
				{
					currentParameterSet.add(tempPara);
				}
				
				currentMethod.setParameterSet(currentParameterSet);
				
				if(class2methodset.getClassSet().contains(currentClass))// get the 
				{
					class2methodset.getMethodSet(currentClass).add(currentMethod);
				}
				else
				{	// createt a new method set and add to the class2methodset
					Set<MethodClass> methodSet = new HashSet<MethodClass>(); 
					methodSet.add(currentMethod);
					class2methodset.add(currentClass, methodSet);
				}
					
			}
		}
		
		return class2methodset;
	}
	
	/**
	 * this class return the invoked class (with the correspond methods) to objects mapping, 
	 * @param trace: input trace
	 * @param subjectClassObject: subject class object
	 * @param method: caller method name in String
	 * @return
	 */
	public ClassObjectsMethodSet getInvokedClassandObjects(XTrace trace, String subjectClassObject, String method)
	{
		ClassObjectsMethodSet class2objectsMethod = new ClassObjectsMethodSet();
		
		//mapping from Class to objects set
		HashMap<ClassClass, Set<String>> class2objects = new HashMap<>();
				
		//mapping from class to method
		HashMap<ClassClass, MethodClass> class2method = new HashMap<>();
		
		for(XEvent event: trace)
		{
			//if the caller object==subjectClassObject && caller method ==method
			if(XSoftwareExtension.instance().extractCallerclassobject(event).equals(subjectClassObject)
					&&XSoftwareExtension.instance().extractCallermethod(event).equals(method))
			{
				
				ClassClass currentClass = new ClassClass(
						XSoftwareExtension.instance().extractPackage(event), 
						XSoftwareExtension.instance().extractClass(event));
				
				MethodClass currentMethod = new MethodClass();
				currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
				currentMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
				currentMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
				currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
				Set<String> currentParameterSet = new HashSet<String>();
				
				String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);

				if(tempPara.contains(","))
				{
					for(String para: tempPara.split("\\,"))
					{
						currentParameterSet.add(para);
					}
				}
				else
				{
					currentParameterSet.add(tempPara);
				}
				
				currentMethod.setParameterSet(currentParameterSet);
				
				String currentObject =XSoftwareExtension.instance().extractClassObject(event);
				//add to the class2method mapping
				class2method.put(currentClass, currentMethod);
				
				//the current class is included in the class2objects, then add the corresponding obejct
				if(class2objects.keySet().contains(currentClass))// get the 
				{
					class2objects.get(currentClass).add(currentObject);
				}
				else
				{	// createt a new object set and add to the class2objects
					Set<String> methodSet = new HashSet<String>(); 
					methodSet.add(currentObject);
					class2objects.put(currentClass, methodSet);
				}
			}
		}
		
		for(ClassClass c: class2objects.keySet())
		{
			ClassObjectsMethod com = new ClassObjectsMethod();
			com.setInvokedClass(c);
			com.setInvokedClassObjects(class2objects.get(c));
			com.setInvokedMethod(class2method.get(c));
			
			class2objectsMethod.add(com);
		}
		return class2objectsMethod;
	}
	
	
}
