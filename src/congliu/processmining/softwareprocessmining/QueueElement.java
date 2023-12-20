package congliu.processmining.softwareprocessmining;

public class QueueElement {

	private String preRecording; 
	private int nestingLevel;
	
	public String getPreRecording() {
		return preRecording;
	}
	public void setPreRecording(String preRecording) {
		this.preRecording = preRecording;
	}
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}	
}
