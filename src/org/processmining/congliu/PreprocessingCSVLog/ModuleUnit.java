package org.processmining.congliu.PreprocessingCSVLog;

/**
 * this class is used to store the module information, including module name, start time and end time.
 * @author cliu3
 *
 */
public class ModuleUnit {
	private String startTime = null; 
	private String endTime = null;
	private String moduleName = null;
	
	//source-->generate setters and getters.
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
