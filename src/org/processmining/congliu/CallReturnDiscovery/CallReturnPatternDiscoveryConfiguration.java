package org.processmining.congliu.CallReturnDiscovery;

public class CallReturnPatternDiscoveryConfiguration {

	private String pluginName;
	
	public CallReturnPatternDiscoveryConfiguration (String pluginName) {
		
		this.pluginName = pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginName() {
		return pluginName;
	}
}
