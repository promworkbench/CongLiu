package designpatterns.framework;
/*
 * the parent constraint class, it has three sub-classes, log-level constraints, temporal constraints, invocation constraints
 */
public class Constraint {

	//the attribute that are share by different types of constraints i.e., the type
	
	private String type="";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
