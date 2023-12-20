package designpatterns.commandpattern;

import java.util.HashSet;

/*
 * this class defines the structure of the detected command pattern instances by DPD. 
 * it is only an intermediate data structure to store the original patterns. 
 */
public class CommandCandidate {
	private String command ="";
	private String receiver ="";
	private HashSet<String> executeSet = new HashSet<>();
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public HashSet<String> getExecuteSet() {
		return executeSet;
	}
	public void setExecuteSet(HashSet<String> executeSet) {
		this.executeSet = executeSet;
	}
	
}
