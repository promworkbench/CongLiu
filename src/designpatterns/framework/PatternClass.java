package designpatterns.framework;

/*
 * the base class of concrete pattern class, i.e., observer pattern class, state pattern class
 */
public class PatternClass {

	private String patternName="";

	private int traceNumber =0;// the number of traces in the log. 
	
	public int getTraceNumber() {
		return traceNumber;
	}

	public void setTraceNumber(int traceNumber) {
		this.traceNumber = traceNumber;
	}

	public int getInvocationNumber() {
		return invocationNumber;
	}

	public void setInvocationNumber(int invocationNumber) {
		this.invocationNumber = invocationNumber;
	}

	private int invocationNumber=0;// the number of invocations in the log. 
	
	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}
			
}
