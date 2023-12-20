package designpatterns.framework;

public class TemporaConstraint extends Constraint{

	private String firstRole="";
	private String secondRole="";
	
	
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
		return this.getType()+","+this.firstRole+","+this.secondRole;
	}
}
