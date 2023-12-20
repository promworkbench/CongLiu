package org.processmining.congliu.ModifiedRegionMiner;


public class MinePetriNetUsingRegionsConfiguration {

	private String regionName;
	//private boolean toEnrich;
	
	public MinePetriNetUsingRegionsConfiguration(String regionName) {
		
		this.regionName = regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionName() {
		return regionName;
	}
	
//	public void setToEnrich(boolean toEnrich) {
//		this.toEnrich = toEnrich;
//	}
//
//	public boolean isToEnrich() {
//		return toEnrich;
//	}
}
