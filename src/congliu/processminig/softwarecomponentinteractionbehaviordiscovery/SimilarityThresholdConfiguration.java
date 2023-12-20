package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

public class SimilarityThresholdConfiguration {
	private double thresholdvalue;
	//private boolean toEnrich;
	
	public SimilarityThresholdConfiguration(double thresholdvalue) {
		
		this.thresholdvalue = thresholdvalue;
	}

	public void setThresholdValue(double thresholdvalue) {
		this.thresholdvalue = thresholdvalue;
	}

	public double getThresholdValue() {
		return thresholdvalue;
	}
}
