package congliu.processmining.objectusage;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * this class defines Petri net with initial and final markings. 
 * @author cliu3
 *
 */
public class PetriNetMarkings {

	private Petrinet pn;
	private Marking initialM;
	private Marking finalM;
	public Petrinet getPn() {
		return pn;
	}
	public void setPn(Petrinet pn) {
		this.pn = pn;
	}
	public Marking getInitialM() {
		return initialM;
	}
	public void setInitialM(Marking initialM) {
		this.initialM = initialM;
	}
	public Marking getFinalM() {
		return finalM;
	}
	public void setFinalM(Marking finalM) {
		this.finalM = finalM;
	}
	
	
}
