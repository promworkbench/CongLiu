package designpatterns.framework;

public class NumberConstraint extends Constraint{
	
	private String relation="";
	private String firstRole="";
	private String secondRole="";
	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String number) {
		this.relation = number;
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
			return this.getType()+","+this.relation+","+this.firstRole+","+this.secondRole;
		}
}
