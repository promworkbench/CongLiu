package org.processmining.congliu.RapidMinerLogProcessing;

public class PluginUnit {


/*
 * 
 * this is an utility class, stores the basic method information such method name, start time, end time and its belonging plugin. 
 */

	private String startTime = null; 
	private String endTime = null;
	private String pluginName = null;
	
	public PluginUnit ()
	{
		startTime = null; 
		endTime = null;
		pluginName = null;
	}
	
	public void setStartTime (String startTime)
	{
		this.startTime = startTime;
	}
	
	public void setEndTime (String endTime)
	{
		this.endTime = endTime;
	}
	public void setPluginName (String endTime)
	{
		this.pluginName = endTime;
	}
	
	
	public String getStartTime()
	{
		return startTime;
	}
	
	public String getEndTime()
	{
		return endTime;
	}
	public String getPluginName()
	{
		return pluginName;
	}
}
