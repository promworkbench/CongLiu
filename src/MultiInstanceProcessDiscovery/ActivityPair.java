package MultiInstanceProcessDiscovery;

import java.util.Objects;

public class ActivityPair {

	private String sourceActivity ="";
	private String targetActivity ="";
	
	public ActivityPair(String source, String target)
	{
		this.sourceActivity =source;
		this.targetActivity =target;
	}

	public String getSourceActivity() {
		return sourceActivity;
	}

	
	public void setSourceActivity(String sourceActivity) {
		this.sourceActivity = sourceActivity;
	}

	public String getTargetActivity() {
		return targetActivity;
	}

	public void setTargetActivity(String targetActivity) {
		this.targetActivity = targetActivity;
	}
	
	public int hashCode() {
		
        return Objects.hash(sourceActivity)*5+Objects.hash(targetActivity)*3;
    }  
	
	public boolean equals(Object other)
	{
		if (this==other)
		{
			return true;
		}
		if (other==null)
		{
			return false;
		}
		if (!(other instanceof ActivityPair))
		{
			return false;
		}
		if (this.hashCode()==((ActivityPair)other).hashCode()) 
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String toString() 
	{
		return this.sourceActivity+"->"+this.targetActivity;
		
	}
	
	
}
