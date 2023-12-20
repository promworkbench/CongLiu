package org.processmining.congliu.pluginbehaviorfiltering;

public class ActivityUnit {
		
		//method name
		private String activityName = null; 
		
		//method start time
		private long startTime ; 
		
		//method end time
		private long endTime ;

		

		public void setActivityName (String activityName)
		{
			this.activityName = activityName;
		}
		
		public void setStartTime (long startTime)
		{
			this.startTime = startTime;
		}
		
		public void setEndTime (long endTime)
		{
			this.endTime = endTime;
		}


		
		
		
		public String getActivityName()
		{
			return activityName;
		}
		
		public long getStartTime()
		{
			return startTime;
		}
		
		public long getEndTime()
		{
			return endTime;
		}


}
