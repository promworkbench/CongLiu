package org.processmining.congliu.RapidMinerLogProcessing;

public class EnrichedCSVUnit {
	//method name
	private String methodName = null; 
	//method start time
	private String startTime = null; 
	//method end time
	private String endTime = null;
	//class name
	private String className = null; 
	
	//package1 name
	private String package1Name = null;
	
//	//package1 name
//	private String package2Name = null;

	//pluginName
	private String pluginName = null;
	
	
	//used memory
	private String usedMemory = null;
	//total memory
	private String totalMemory = null;
	
	//used cpu
	private String usedCpu = null;

	public void setmethodName (String methodName)
	{
		this.methodName = methodName;
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
	
	public void setpackage1Name (String package1Name )
	{
		this.package1Name = package1Name;
	}
//	public void setpackage2Name  (String setpackage2Name)
//	{
//		this.package2Name = setpackage2Name;
//	}
	
	public void setClassName (String className)
	{
		this.className = className;
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
	
	
	public String getmehodName()
	{
		return methodName;
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
	public String getpackage1Name()
	{
		return this.package1Name;
	}
//	public String getpackage2Name()
//	{
//		return this.package2Name;
//	}
	
	public String getClassName()
	{
		return this.className ;
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
