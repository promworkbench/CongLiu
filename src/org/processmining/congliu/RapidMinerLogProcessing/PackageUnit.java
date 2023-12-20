package org.processmining.congliu.RapidMinerLogProcessing;

public class PackageUnit {

	//package name
	private String packageName = null; 
	//method start time
	private String startTime = null; 
	//method end time
	private String endTime = null;

	//pluginName
	private String pluginName = null;

	//used memory
	private String usedMemory = null;
	//total memory
	private String totalMemory = null;
	
	//used cpu
	private String usedCpu = null;

	
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
	
	public void setpackageName (String packageName)
	{
		this.packageName = packageName;
	}

	
	public void setusedMemory (String usedmemory)
	{
		this.usedMemory = usedmemory;
	}
	
	public void settotalMemory (String totalmemory)
	{
		this.totalMemory = totalmemory;
	}
	
	public void setusedCpu (String usedCpu)
	{
		this.usedCpu= usedCpu;
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
	public String getpackageName()
	{
		return this.packageName;
	}

	
	public String getusedMemory ()
	{
		return this.usedMemory ;
	}
	
	public String gettotalMemory ()
	{
		return this.totalMemory ;
	}
	
	public String getusedCpu ()
	{
		return this.usedCpu;
	}
}
