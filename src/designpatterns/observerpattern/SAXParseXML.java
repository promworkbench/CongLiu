package designpatterns.observerpattern;

import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import designpatterns.adapterpattern.AdapterCandidate;
import designpatterns.commandpattern.CommandCandidate;
import designpatterns.factorymethodpattern.FactoryMethodCandidate;
import designpatterns.statepattern.StateCandidate;
import designpatterns.strategypattern.StrategyCandidate;
import designpatterns.visitorpattern.VisitorCandidate;

public class SAXParseXML extends DefaultHandler{

	private String instanceType;  // for extending to multiple types of design patterns
	
	//used for observer patterns
	private HashSet<ObserverCandidate> observers;  
	private ObserverCandidate observer;
	
	//used for state patterns
	private HashSet<StateCandidate> states;
	private StateCandidate state;
	
	//used for strategy patterns
	private HashSet<StrategyCandidate> strategies;
	private StrategyCandidate strategy;
	
	//used for adapter patterns
	private HashSet<AdapterCandidate> adapters;
	private AdapterCandidate adapter;
	
	//used for command patterns
	private HashSet<CommandCandidate> commands;
	private CommandCandidate command;
	
	//used for factory method patterns
	private HashSet<FactoryMethodCandidate> factorys;
	private FactoryMethodCandidate factory;
	
	//used for visitor patterns
	private HashSet<VisitorCandidate> visitors;
	private VisitorCandidate visitor;
	
	public HashSet<ObserverCandidate> getObservers() {
		return observers;
	}
	
	public HashSet<StateCandidate> getStates()
	{
		return states;
	}
	
	public HashSet<StrategyCandidate> getStrategies()
	{
		return strategies;
	}
	
	public HashSet<AdapterCandidate> getAdapters()
	{
		return adapters;
	}
	
	public HashSet<FactoryMethodCandidate> getFactorys()
	{
		return factorys;
	}
	
	public HashSet<CommandCandidate> getCommands()
	{
		return commands;
	}
	public HashSet<VisitorCandidate> getVisitors()
	{
		return visitors;
	}
	
//	public ObserverCandidate getObserver() {
//		return observer;
//	}
//	public void setObserver(ObserverCandidate observer) {
//		this.observer = observer;
//	}
	
//	public String getInstanceType() {
//		return instanceType;
//	}
//	public void setInstanceType(String tagName) {
//		this.instanceType = tagName;
//	}
	
	/*
	 * start the parse
	 */
	
	@Override  
    public void startDocument() throws SAXException {  
		
    }  
	
	
	@Override  
    public void startElement(String uri, String localName, String qName,  
            Attributes attributes) throws SAXException {  
        if(qName.equals("pattern"))
        {
        	//get the pattern type, if it is observer pattern, create the observers
        	if(attributes.getValue(0).toString().equals("Observer"))
        	{
        		observers=new HashSet<ObserverCandidate>();  
        		this.instanceType="Observer";  
        	}
        	else if (attributes.getValue(0).toString().equals("State")) 
        	{
        		states=new HashSet<StateCandidate>();
        		this.instanceType ="State";
			}
        	else if(attributes.getValue(0).toString().equals("Strategy"))
        	{
        		strategies=new HashSet<StrategyCandidate>();
        		this.instanceType ="Strategy";
        	}
        	else if(attributes.getValue(0).toString().equals("(Object)Adapter"))
        	{
        		adapters=new HashSet<AdapterCandidate>();
        		this.instanceType ="(Object)Adapter";
        	}
        	else if (attributes.getValue(0).toString().equals("Factory Method"))
        	{
        		factorys = new HashSet<FactoryMethodCandidate>();
        		this.instanceType="Factory Method";
        	}
        	else if(attributes.getValue(0).toString().equals("Command"))
        	{
        		commands = new HashSet<CommandCandidate>();
        		this.instanceType = "Command";
        	}
        	else if (attributes.getValue(0).toString().equals("Visitor"))
        	{
        		visitors = new HashSet<VisitorCandidate>();
        		this.instanceType ="Visitor";
        	}
        	//else if(attributes.getValue(0).toString().equals("other patterns"))
        }
        else if(qName.equals("instance"))
        {
        	if(this.instanceType.equals("Observer"))
        	{
        		observer = new ObserverCandidate();
        	}
        	else if (this.instanceType.equals("State"))
        	{
        		state = new StateCandidate();
        	}
        	else if (this.instanceType.equals("Strategy"))
        	{
        		strategy = new StrategyCandidate();
        	}
        	else if(this.instanceType.equals("(Object)Adapter"))
        	{
        		adapter= new AdapterCandidate();
        	}
        	else if (this.instanceType.equals("Factory Method"))
        	{
        		factory = new FactoryMethodCandidate();
        	}
        	else if (this.instanceType.equals("Command"))
        	{
        		command = new CommandCandidate();
        	}
        	else if (this.instanceType.equals("Visitor"))
        	{
        		visitor = new VisitorCandidate();
        	}
        	//else if(this.instanceType.equals("other patterns"))
        	
        }
        else if (qName.equals("role")) 
        {
        	//if current pattern is observer pattern 
        	if (this.instanceType.equals("Observer"))
        	{
        		if(attributes.getValue(0).equals("Observer"))
            	{
            		this.observer.setObserver(attributes.getValue(1));
            	}
            	else if(attributes.getValue(0).equals("Subject")) {
    				this.observer.setSubject(attributes.getValue(1));
    			}
            	else if (attributes.getValue(0).equals("Notify()"))// there can be multiple notifiers for each observer pattern
            	{
            		HashSet<String> notifySet=this.observer.getNotifySet();
            		notifySet.add(attributes.getValue(1));
            		this.observer.setNotifySet(notifySet);
            	}
        	}
        	else if (this.instanceType.equals("State"))// if the current pattern is state pattern
        	{
        		if(attributes.getValue(0).equals("Context"))
            	{
        			this.state.setContext(attributes.getValue(1));
            	}
        		else if (attributes.getValue(0).equals("State"))
        		{
        			this.state.setState(attributes.getValue(1));
        		}
        		else if (attributes.getValue(0).equals("Request()"))
        		{
        			HashSet<String> requestSet = this.state.getRequestSet();
        			requestSet.add(attributes.getValue(1));
        			this.state.setRequestSet(requestSet);
        		}
        	}
        	else if (this.instanceType.equals("Strategy"))// if the current pattern is Strategy pattern
        	{
        		if(attributes.getValue(0).equals("Context"))
            	{
        			this.strategy.setContext(attributes.getValue(1));
            	}
        		else if (attributes.getValue(0).equals("Strategy"))
        		{
        			this.strategy.setStrategy(attributes.getValue(1));
        		}
        		else if (attributes.getValue(0).equals("ContextInterface()"))
        		{
        			HashSet<String> contextInterSet = this.strategy.getContextInterfaceSet();
        			contextInterSet.add(attributes.getValue(1));
        			this.strategy.setContextInterfaceSet(contextInterSet);
        		}
        	}
        	else if(this.instanceType.equals("(Object)Adapter"))// if the current pattern is adapter pattern
        	{

        		if(attributes.getValue(0).equals("Adaptee"))
            	{
            		this.adapter.setAdaptee(attributes.getValue(1));
            	}
            	else if (attributes.getValue(0).equals("Adapter")) {
    				this.adapter.setAdapter(attributes.getValue(1));
    			}
            	else if (attributes.getValue(0).equals("Request()"))// there can be multiple request for each adapter pattern
            	{
            		HashSet<String> requestSet=this.adapter.getRequestSet();
            		requestSet.add(attributes.getValue(1));
            		this.adapter.setRequestSet(requestSet);
            	}
        	}
        	else if (this.instanceType.equals("Factory Method"))// if the current pattern is factory method
        	{
        		if(attributes.getValue(0).equals("Creator"))
        		{
        			this.factory.setCreator(attributes.getValue(1));
        		}
        		else if (attributes.getValue(0).equals("FactoryMethod()")){// there can be multiple factory methods for each factory method pattern
        			HashSet<String> factorymethodSet=this.factory.getFactoryMethodSet();
        			factorymethodSet.add(attributes.getValue(1));
            		this.factory.setFactoryMethodSet(factorymethodSet);
        		}
        	}
        	else if (this.instanceType.equals("Command"))
        	{
        		if(attributes.getValue(0).equals("Receiver"))
            	{
            		this.command.setReceiver(attributes.getValue(1));
            	}
            	else if (attributes.getValue(0).equals("ConcreteCommand")) {
    				this.command.setCommand(attributes.getValue(1));
    			}
            	else if (attributes.getValue(0).equals("Execute()"))  // there can be multiple execute for each command pattern
            	{
            		HashSet<String> executeSet=this.command.getExecuteSet();
            		executeSet.add(attributes.getValue(1));
            		this.command.setExecuteSet(executeSet);
            	}
        	}
        	else if (this.instanceType.equals("Visitor")) //if the current pattern is visitor method
        	{
        		if(attributes.getValue(0).equals("ConcreteElement"))
            	{
            		this.visitor.setElement(attributes.getValue(1));
            	}
            	else if (attributes.getValue(0).equals("Visitor")) {
    				this.visitor.setVisitor(attributes.getValue(1));
    			}
            	else if (attributes.getValue(0).equals("Accept()"))  // there can be multiple execute for each visitor pattern
            	{
            		HashSet<String> acceptSet=this.visitor.getAcceptSet();
            		acceptSet.add(attributes.getValue(1));
            		this.visitor.setAcceptSet(acceptSet);
            	}
        	}
        	//else if (this.instanceType.equals("other patterns"))
        	
		}
    }  
	
	 @Override  
	    public void endElement(String uri, String localName, String qName)  
	            throws SAXException {  
		 	if(qName.equals("pattern"))
	        {
		 		this.instanceType="";  
	        }
		 	else if(qName.equals("instance") && this.instanceType.equals("Observer"))
		 	{  
	            this.observers.add(this.observer);  
	        }
		 	else if (qName.equals("instance") && this.instanceType.equals("State"))
		 	{
		 		this.states.add(this.state);
		 	}
		 	else if (qName.equals("instance") && this.instanceType.equals("Strategy"))
		 	{
		 		this.strategies.add(this.strategy);
		 	}
		 	else if (qName.equals("instance") && this.instanceType.equals("(Object)Adapter"))
		 	{
		 		this.adapters.add(this.adapter);
		 	}
		 	else if (qName.equals("instance") && this.instanceType.equals("Factory Method"))
		 	{
		 		this.factorys.add(this.factory);
		 	}
		 	else if (qName.equals("instance") && this.instanceType.equals("Command"))
		 	{
		 		this.commands.add(this.command);
		 	}
		 	else if(qName.equals("instance") && this.instanceType.equals("Visitor"))
		 	{
		 		this.visitors.add(this.visitor);
		 	}
		 	//else if (qName.equals("instance") && this.instanceType.equals("other patterns"))
	    }
	
}
