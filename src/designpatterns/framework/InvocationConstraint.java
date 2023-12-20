package designpatterns.framework;

public class InvocationConstraint extends Constraint{
	
	private String cardinality="";
	private String firstRole="";
	private String secondRole="";
	
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public String getFirstRole() {
		return firstRole;
	}
	public void setFirstRole(String firstRole) {
		this.firstRole = firstRole;
	}
	public String getSecondRole() {
		return secondRole;
	}
	public void setSecondRole(String secondRole) {
		this.secondRole = secondRole;
	}
	
	// write all informations
	public String toString() 
	{
		return this.getType()+","+this.cardinality+","+this.firstRole+","+this.secondRole;
	}
}
