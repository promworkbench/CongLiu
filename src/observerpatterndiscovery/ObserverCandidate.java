package observerpatterndiscovery;
/*
 * this class defines the structure of the detected observer patterns instances by DPD. 
 */

import java.util.HashSet;

public class ObserverCandidate {

	public String observer ="";
	public String subject ="";
	public HashSet<String> notifySet = new HashSet<>();
	public String getObserver() {
		return observer;
	}
	public void setObserver(String observer) {
		this.observer = observer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public HashSet<String> getNotifySet() {
		return notifySet;
	}
	public void setNotifySet(HashSet<String> notifySet) {
		this.notifySet = notifySet;
	}
	
}
