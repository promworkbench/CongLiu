package designpatterns.framework;

import java.util.Arrays;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParseDesignPatternSpecification extends DefaultHandler{

	private DesignPatternSpecification specification = new DesignPatternSpecification();
	
	//the log-level constraint set 
	private HashSet<Constraint> logConstraintSet;
	
	//the -level constraint set
	private HashSet<Constraint> instanceConstraintSet;
	
	
	public DesignPatternSpecification getPatternSpecification() {
		return specification;
	}
	
	@Override  
    public void startDocument() throws SAXException {  
		
    }  
	
	@Override  
    public void startElement(String uri, String localName, String qName,  
            Attributes attributes) throws SAXException { 
	
		if(qName.equals("pattern"))
		{
			//set the pattern name attribute
			specification.setPatternName(attributes.getValue(0).toString());
		}
		else if (qName.equals("MainRole"))
		{
			specification.setMainRole(attributes.getValue(0).toString());
		}
		else if (qName.equals("Roles"))
		{
			String []roles = attributes.getValue(0).toString().split(",");
			HashSet<String> roleSet = new HashSet<String>(Arrays.asList(roles));

			//set the rols set
			specification.setRoleSet(roleSet);
		}
		else if(qName.equals("LogLevel"))
        {
			logConstraintSet = new HashSet<>();
			
        }
		else if (qName.equals("InvocationLevel")) 
		{
			instanceConstraintSet = new HashSet<>();
		}
		
		else if(qName.equals("Constraint"))
		{
			// instance-level constraints, invocation constraints
			if(attributes.getValue(0).toString().equals("invocation"))
			{
				InvocationConstraint ic = new InvocationConstraint();
				ic.setType(attributes.getValue(0).toString());
				ic.setCardinality(attributes.getValue(1).toString());
				ic.setFirstRole(attributes.getValue(2).toString());
				ic.setSecondRole(attributes.getValue(3).toString());
				
				instanceConstraintSet.add(ic);
			}
			// instance-level constraints, temporal constraints
			else if(attributes.getValue(0).toString().equals("temporal"))	
			{
				TemporaConstraint tc = new TemporaConstraint();
				tc.setType(attributes.getValue(0).toString());
				tc.setFirstRole(attributes.getValue(1).toString());
				tc.setSecondRole(attributes.getValue(2).toString());
				
				instanceConstraintSet.add(tc);
			}
			//instance-level constraints, number constraints
			else if (attributes.getValue(0).toString().equals("number"))
			{
				NumberConstraint nc = new NumberConstraint();
				nc.setType(attributes.getValue(0).toString());
				nc.setRelation(attributes.getValue(1).toString());
				nc.setFirstRole(attributes.getValue(2).toString());
				nc.setSecondRole(attributes.getValue(3).toString());
				
				instanceConstraintSet.add(nc);
			}
			else {
				LogConstraint lc = new LogConstraint();
				lc.setType(attributes.getValue(0).toString());
				lc.setRelation(attributes.getValue(1).toString());
				lc.setFirstRole(attributes.getValue(2).toString());
				lc.setSecondRole(attributes.getValue(3).toString());
				
				logConstraintSet.add(lc);
			}
		}

	}
		 @Override  
		    public void endElement(String uri, String localName, String qName)  
		            throws SAXException { 
			 if(qName.equals("LogLevel"))
		     {
			 		this.specification.setLogConstraintSet(logConstraintSet);  
		     }
			 else if (qName.equals("InvocationLevel")) {
				 this.specification.setInstanceConstraintSet(instanceConstraintSet);  
			}
		 }
		 
}
