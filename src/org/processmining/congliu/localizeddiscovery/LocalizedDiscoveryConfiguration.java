package org.processmining.congliu.localizeddiscovery;

public class LocalizedDiscoveryConfiguration {
	private String regionName;
	//private boolean toEnrich;
	
	public LocalizedDiscoveryConfiguration(String regionName) {
		
		this.regionName = regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionName() {
		return regionName;
	}
}
