package designpatterns.framework;
/*
 * this class gives a general description of the design pattern specification
 * it is imported from the xml-based file
 */

import java.util.HashSet;

public class DesignPatternSpecification {

	/*
	 * these class variables same for all kinds of pattern specification. 
	 */
	
	//the name of current pattern
	private String patternName = "";
	
	//the role set of the current pattern
	private HashSet<String> roleSet = new HashSet<>();
	
	//main role of the current pattern, this is used to define the invocations
	private String mainRole ="";
	
	//the log-level constraint set 
	private HashSet<Constraint> logConstraintSet = new HashSet<>();
	
	//the instance-level constraint set
	private HashSet<Constraint> instanceConstraintSet = new HashSet<>();

			
	public String getMainRole() {
		return mainRole;
	}

	public void setMainRole(String mainRole) {
		this.mainRole = mainRole;
	}


	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public HashSet<String> getRoleSet() {
		return roleSet;
	}

	public void setRoleSet(HashSet<String> roleSet) {
		this.roleSet = roleSet;
	}

	public HashSet<Constraint> getLogConstraintSet() {
		return logConstraintSet;
	}

	public void setLogConstraintSet(HashSet<Constraint> logConstraintSet) {
		this.logConstraintSet = logConstraintSet;
	}

	public HashSet<Constraint> getInstanceConstraintSet() {
		return instanceConstraintSet;
	}

	public void setInstanceConstraintSet(HashSet<Constraint> instanceConstraintSet) {
		this.instanceConstraintSet = instanceConstraintSet;
	}
	

}
