package XESSoftwareExtension;

import java.net.URI;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * This extension defines the software perspective on event logs. 
 * It defines for events 12 attributes, referring to:
 * <ul>
 * <li>The class of the event</li>
 * <li>The class object of the event</li>
 * <li>The package the event</li>
 * <li>The component of the event</li>
 * <li>The caller method of the event</li>
 * <li>The caller class of the event</li>
 * <li>The caller class object of the event</li>
 * <li>The caller package of the event</li>
 * <li>The caller component of the event</li>
 * <li>The start time in nano of the event</li>
 * <li>The end time in nano of the event</li>
 * <li>The nesting flag of the event</li>
 * <li>The thread-id extension of the event</li>
 * <ul>
 * 
 * @author Cong Liu (c.liu.3@tue.nl)
 * 
 */
public class XSoftwareExtension extends XExtension{
	
	protected XSoftwareExtension(String name, String prefix, URI uri) {
		super(name, prefix, uri);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8526567169660264287L;
	
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/software.xesext");
	
	/**
	 * Key for the class attribute.
	 */
	public static final String KEY_CLASS = "software:class";
	
	/**
	 * Key for the class object attribute.
	 */
	public static final String KEY_CLASSOBJ = "software:classobject";
	
	/**
	 * Key for the package attribute.
	 */
	public static final String KEY_PACKAGE = "software:package";
	
	/**
	 * Key for the component attribute.
	 */
	public static final String KEY_COMPONENT = "software:component";
	
	/**
	 * Key for the caller method attribute.
	 */
	public static final String KEY_CALLERMETHOD = "software:callermethod";
	
	/**
	 * Key for the caller class attribute.
	 */
	public static final String KEY_CALLERCLASS = "software:callerclass";
	
	
	/**
	 * Key for the caller class object attribute.
	 */
	public static final String KEY_CALLERCLASSOBJ = "software:callerclassobject";
	
	
	/**
	 * Key for the caller package attribute.
	 */
	public static final String KEY_CALLERPACKAGE = "software:callerpackage";
	
	/**
	 * Key for the caller component attribute.
	 */
	public static final String KEY_CALLERCOMPONENT = "software:callercomponent";
	
	/**
	 * Key for the start time in nano attribute.
	 */
	public static final String KEY_STARTTIMENANO = "software:starttimenano";
	
	/**
	 * Key for the start time in nano attribute.
	 */
	public static final String KEY_ENDTIMENANO = "software:endtimenano";
	
	/**
	 * Key for the nesting attribute.
	 */
	public static final String KEY_NESTING = "software:nesting";
	
	/**
	 * Key for the method parameter type set.
	 */
	public static final String KEY_PARAMETERTYPESET = "software:parametertypeset";
	
	/**
	 * Key for the method parameter value set.
	 */
	public static final String KEY_PARAMETERVALUESET = "software:parametervalueset";
	
	/**
	 * Key for the line number.
	 */
	public static final String KEY_LINENUMBER = "software:linenumber";
	
	/**
	 * Key for the thread id
	 */
	public static final String KEY_THREADID="software:threadid";
	
	/**
	 * Class attribute prototype
	 */
	public static XAttributeLiteral ATTR_CLASS;
	
	/**
	 * Class object attribute prototype
	 */
	public static XAttributeLiteral ATTR_CLASSOBJ;

	/**
	 * package attribute prototype
	 */
	public static XAttributeLiteral ATTR_PACKAGE;
	
	/**
	 * component attribute prototype
	 */
	public static XAttributeLiteral ATTR_COMPONENT;
	
	/**
	 * caller method attribute prototype
	 */
	public static XAttributeLiteral ATTR_CALLERMETHOD;
	
	/**
	 * caller class attribute prototype
	 */
	public static XAttributeLiteral ATTR_CALLERCLASS;
	
	/**
	 * caller class object attribute prototype
	 */
	public static XAttributeLiteral ATTR_CALLERCLASSOBJ;
	
	/**
	 * caller package attribute prototype
	 */
	public static XAttributeLiteral ATTR_CALLERPACKAGE;
	
	/**
	 * caller package attribute prototype
	 */
	public static XAttributeLiteral ATTR_CALLERCOMPONENT;
	
	/**
	 * start time nano attribute prototype
	 */
	public static XAttributeLiteral ATTR_STARTTIMENANO;
	
	/**
	 * end time nano attribute prototype
	 */
	public static XAttributeLiteral ATTR_ENDTIMENANO;
	
	/**
	 * nesting attribute prototype
	 */
	public static XAttributeBoolean ATTR_NESTING;
	
	/**
	 * method parameter type set prototype
	 */
	public static XAttributeLiteral ATTR_PARAMETERTYPESET;
	
	/**
	 * method parameter value set prototype
	 */
	public static XAttributeLiteral ATTR_PARAMETERVALUESET;
	
	/**
	 * Key for the line number.
	 */
	public static XAttributeDiscrete ATTR_LINENUMBER;
	
	/**
	 * Key for the thread id.
	 */
	public static XAttributeDiscrete ATTR_THREADID;
	
	/**
	 * Singleton instance of this extension.
	 */
	private static XSoftwareExtension singleton = new XSoftwareExtension();
	
	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XSoftwareExtension instance() {
		return singleton;
	}
	
	/**
	 * Creates a new instance (hidden constructor).
	 */
	private XSoftwareExtension() {
		super("Software", "software", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		
		ATTR_CLASS = factory.createAttributeLiteral(KEY_CLASS, "__INVALID__", this);
		ATTR_CLASSOBJ =factory.createAttributeLiteral(KEY_CLASSOBJ, "__INVALID__", this);
		ATTR_PACKAGE = factory.createAttributeLiteral(KEY_PACKAGE, "__INVALID__", this);
		ATTR_COMPONENT = factory.createAttributeLiteral(KEY_COMPONENT, "__INVALID__", this);
		ATTR_CALLERMETHOD =factory.createAttributeLiteral(KEY_CALLERMETHOD, "__INVALID__", this);
		ATTR_CALLERCLASS = factory.createAttributeLiteral(KEY_CALLERCLASS, "__INVALID__", this);
		ATTR_CALLERCLASSOBJ =factory.createAttributeLiteral(KEY_CALLERCLASSOBJ, "__INVALID__", this);
		ATTR_CALLERPACKAGE =factory.createAttributeLiteral(KEY_CALLERPACKAGE, "__INVALID__", this);
		ATTR_CALLERCOMPONENT =factory.createAttributeLiteral(KEY_CALLERCOMPONENT, "__INVALID__", this);	
		ATTR_STARTTIMENANO =factory.createAttributeLiteral(KEY_STARTTIMENANO, "__INVALID__", this);
		ATTR_ENDTIMENANO =factory.createAttributeLiteral(KEY_ENDTIMENANO, "__INVALID__", this);
		ATTR_NESTING =factory.createAttributeBoolean(KEY_NESTING, false, this);
		ATTR_LINENUMBER =factory.createAttributeDiscrete(KEY_LINENUMBER, -1, this);
		ATTR_THREADID = factory.createAttributeDiscrete(KEY_THREADID, -1, this);
		ATTR_PARAMETERTYPESET =factory.createAttributeLiteral(KEY_PARAMETERTYPESET, "__INVALID__", this);
		ATTR_PARAMETERVALUESET =factory.createAttributeLiteral(KEY_PARAMETERVALUESET, "__INVALID__", this);
		
		this.eventAttributes.add((XAttribute) ATTR_CLASS.clone());
		this.eventAttributes.add((XAttribute) ATTR_CLASSOBJ.clone());
		this.eventAttributes.add((XAttribute) ATTR_PACKAGE.clone());
		this.eventAttributes.add((XAttribute) ATTR_COMPONENT.clone());
		this.eventAttributes.add((XAttribute) ATTR_CALLERMETHOD.clone());
		this.eventAttributes.add((XAttribute) ATTR_CALLERCLASS.clone());
		this.eventAttributes.add((XAttribute) ATTR_CALLERCLASSOBJ.clone());
		this.eventAttributes.add((XAttribute) ATTR_CALLERPACKAGE.clone());
		this.eventAttributes.add((XAttribute) ATTR_CALLERCOMPONENT.clone());
		this.eventAttributes.add((XAttribute) ATTR_STARTTIMENANO.clone());
		this.eventAttributes.add((XAttribute) ATTR_ENDTIMENANO.clone());
		this.eventAttributes.add((XAttribute) ATTR_NESTING.clone());
		this.eventAttributes.add((XAttribute) ATTR_LINENUMBER.clone());
		this.eventAttributes.add((XAttribute) ATTR_THREADID.clone());
		this.eventAttributes.add((XAttribute) ATTR_PARAMETERTYPESET.clone());
		this.eventAttributes.add((XAttribute) ATTR_PARAMETERVALUESET.clone());
		
		
		// register mapping aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CLASS, "Class");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CLASSOBJ, "Class Object");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_PACKAGE, "Package");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COMPONENT, "Component");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CALLERMETHOD, "Caller Method");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CALLERCLASS, "Caller Class");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CALLERCLASSOBJ, "Caller Class Object");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CALLERPACKAGE, "Caller Package");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CALLERCOMPONENT, "Caller Component");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_STARTTIMENANO, "Start Time in Nano");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_ENDTIMENANO, "End Time in Nano");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_NESTING, "Nesting");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_LINENUMBER, "line number");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_THREADID, "thread id");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_PARAMETERTYPESET, "parameter type set");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_PARAMETERVALUESET, "parameter value set");
	
	}


	/**
	 * Extracts the class attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return Class string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractClass(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CLASS);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its class name.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param class name, as a string
	 **/
	
	public void assignClass(XEvent event, String className) {
		if (className!=null && className.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CLASS.clone();
			attr.setValue(className);
			event.getAttributes().put(KEY_CLASS, attr);
		}
	}
	
	/**
	 * Extracts the class object attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return Class object string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractClassObject(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CLASSOBJ);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its class object.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param class object, as a string
	 **/
	
	public void assignClassObject(XEvent event, String classObj) {
		if (classObj!=null && classObj.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CLASSOBJ.clone();
			attr.setValue(classObj);
			event.getAttributes().put(KEY_CLASSOBJ, attr);
		}
	}
	
	/**
	 * Extracts the package attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return package string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractPackage(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_PACKAGE);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its package.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param package, as a string
	 **/
	
	public void assignPackage(XEvent event, String packageName) {
		if (packageName!=null && packageName.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_PACKAGE.clone();
			attr.setValue(packageName);
			event.getAttributes().put(KEY_PACKAGE, attr);
		}
	}
	
	/**
	 * Extracts the component attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return component string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractComponent(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_COMPONENT);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its component.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param package, as a string
	 **/
	
	public void assignComponent(XEvent event, String com) {
		if (com!=null && com.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_COMPONENT.clone();
			attr.setValue(com);
			event.getAttributes().put(KEY_COMPONENT, attr);
		}
	}
	
	/**
	 * assign to a given event (method call) its line number
	 * @param event
	 * @param num
	 */
	public void assignLineNumber(XEvent event, int num)
	{
		if(num>0)
		{
			XAttributeDiscrete attr = (XAttributeDiscrete)ATTR_LINENUMBER.clone();
			attr.setValue(num);
			event.getAttributes().put(KEY_LINENUMBER, attr);
		}
	}
	/**
	 * extract the line number attribute int from an event.  
	 * @param event
	 * @return
	 */
	
	public int extractLineNumber(XEvent event)
	{
		XAttribute attribute = event.getAttributes().get(KEY_LINENUMBER);
		if (attribute ==null)
		{
			return -1;
		}
		else {
			return (int) ((XAttributeDiscrete) attribute).getValue();
		}
	}
	
	/**
	 * assign the thread id to the event
	 * @param event
	 * @param id
	 */
	public void assignThreadID(XEvent event, int id)
	{
		if(id>0)
		{
			XAttributeDiscrete attr = (XAttributeDiscrete)ATTR_THREADID.clone();
			attr.setValue(id);
			event.getAttributes().put(KEY_THREADID, attr);
		}
	}
	
	
	public int extractThreadID(XEvent event)
	{
		XAttribute attribute = event.getAttributes().get(KEY_THREADID);
		if (attribute ==null)
		{
			return -1;
		}
		else {
			return (int) ((XAttributeDiscrete) attribute).getValue();
		}
	}
	
	
	
	public void assignParameterTypeSet(XEvent event, String para) {
		if (para!=null)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_PARAMETERTYPESET.clone();
			attr.setValue(para);
			event.getAttributes().put(KEY_PARAMETERTYPESET, attr);
		}
	}
	
	/**
	 * extract the parameter type set from the event. 
	 * @param event
	 * @return
	 */
	public String extractParameterTypeSet(XEvent event)
	{
		XAttribute attribute = event.getAttributes().get(KEY_PARAMETERTYPESET);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	public void assignParameterValueSet(XEvent event, String para) {
		if (para!=null)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_PARAMETERVALUESET.clone();
			attr.setValue(para);
			event.getAttributes().put(KEY_PARAMETERVALUESET, attr);
		}
	}
	
	/**
	 * extract the parameter value set from the event. 
	 * @param event
	 * @return
	 */
	public String extractParameterValueSet(XEvent event)
	{
		XAttribute attribute = event.getAttributes().get(KEY_PARAMETERVALUESET);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	
	/**
	 * Assigns to a given event (method call) its caller method.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param caller method, as a string
	 **/
	
	public void assignCallermethod(XEvent event, String callermethod) {
		if (callermethod!=null && callermethod.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CALLERMETHOD.clone();
			attr.setValue(callermethod);
			event.getAttributes().put(KEY_CALLERMETHOD, attr);
		}
	}
	
	/**
	 * Extracts the caller method attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return caller method string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractCallermethod(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CALLERMETHOD);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	

	
	/**
	 * Extracts the caller class attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return caller class string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractCallerclass(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CALLERCLASS);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its caller class.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param caller class, as a string
	 **/
	
	public void assignCallerclass(XEvent event, String callerclass) {
		if (callerclass!=null && callerclass.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CALLERCLASS.clone();
			attr.setValue(callerclass);
			event.getAttributes().put(KEY_CALLERCLASS, attr);
		}
	}
	
	/**
	 * Extracts the caller class object attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return caller class object string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractCallerclassobject(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CALLERCLASSOBJ);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its caller class object.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param caller class object, as a string
	 **/
	
	public void assignCallerclassobject(XEvent event, String callerclassobj) {
		if (callerclassobj!=null && callerclassobj.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CALLERCLASSOBJ.clone();
			attr.setValue(callerclassobj);
			event.getAttributes().put(KEY_CALLERCLASSOBJ, attr);
		}
	}
	
	/**
	 * Extracts the caller package attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return caller package string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractCallerpackage(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CALLERPACKAGE);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its caller package.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param caller package, as a string
	 **/
	
	public void assignCallerpackage(XEvent event, String callerpackage) {
		if (callerpackage!=null && callerpackage.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CALLERPACKAGE.clone();
			attr.setValue(callerpackage);
			event.getAttributes().put(KEY_CALLERPACKAGE, attr);
		}
	}
	
	/**
	 * Extracts the caller component attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return caller component string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractCallercomponent(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_CALLERCOMPONENT);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its caller component.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param caller component, as a string
	 **/
	
	public void assignCallercomponent(XEvent event, String callercomponent) {
		if (callercomponent!=null && callercomponent.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CALLERCOMPONENT.clone();
			attr.setValue(callercomponent);
			event.getAttributes().put(KEY_CALLERCOMPONENT, attr);
		}
	}
	
	/**
	 * Extracts the start time in nano attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return start time in nano string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractStarttimenano(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_STARTTIMENANO);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its start time in nano.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param start time in nano, as a string
	 **/
	
	public void assignStarttimenano(XEvent event, String starttimenano) {
		if (starttimenano!=null && starttimenano.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_STARTTIMENANO.clone();
			attr.setValue(starttimenano);
			event.getAttributes().put(KEY_STARTTIMENANO, attr);
		}
	}
	
	/**
	 * Extracts the start time in nano attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return start time in nano string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public String extractEndtimenano(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_ENDTIMENANO);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its end time in nano.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param end time in nano, as a string
	 **/
	
	public void assignEndtimenano(XEvent event, String endtimenano) {
		if (endtimenano!=null && endtimenano.trim().length()>0)
		{
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_ENDTIMENANO.clone();
			attr.setValue(endtimenano);
			event.getAttributes().put(KEY_ENDTIMENANO, attr);
		}
	}
	
	/**
	 * Extracts nesting attribute string from an event (method call).
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return nesting flag for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	
	public boolean extractNesting(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_NESTING);
		if (attribute == null) {
			return false;
		} else {
			return ((XAttributeBoolean) attribute).getValue();
		}
	}
	
	/**
	 * Assigns to a given event (method call) its end time in nano.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param end time in nano, as a string
	 **/
	
	public void assignNesting(XEvent event, boolean nesting) {

			XAttributeBoolean attr = (XAttributeBoolean) ATTR_NESTING.clone();
			attr.setValue(nesting);
			event.getAttributes().put(KEY_NESTING, attr);

	}
}
