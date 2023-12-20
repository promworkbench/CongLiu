package congliu.processminig.XPortSoftwareEventlogTransformation;

public class GetMathodClassPackages {
	public static String extractMethod(String s)
	{
		String args1[] = s.split("\\(");
		String args[]=args1[0].split("\\.");		
		
		return args[args.length-1]+"()";
	}
	
	public static String extractClass(String s)
	{
		String args1[] = s.split("\\(");
		String args[]=args1[0].split("\\.");		
				
		return args[args.length-2];
	}
	
	public static String extractPackage(String s)
	{
		String args1[] = s.split("\\(");
		String args[]=args1[0].split("\\.");		
		String Package = args[0];
		for (int i=1;i<args.length-2;i++)
		{
			Package=Package+"."+args[i];
		}
		
		return Package;
	}
	
	public static String extractConstructorClass(String s)
	{
		String args1[] = s.split("\\(");
		String args[]=args1[0].split("\\.");		
		
		return args[args.length-1];
	}
	
	public static String extractConstructorPackage(String s)
	{
		String args1[] = s.split("\\(");
		String args[]=args1[0].split("\\.");		
		
		String Package = args[0];
		for (int i=1;i<args.length-1;i++)
		{
			Package=Package+"."+args[i];
		}
		
		return Package;
	}
	
	/*
	 * return the parameter type of a method
	 * E.g., P.C.M(P1, P2, P3) will return P1, P2, P3
	 */
	public static String extractParameterSet(String s)
	{
		String args1[] = s.split("\\(");
		//String args[]=args1[1].split("\\)");
		
		return args1[1].replaceAll("\\)", "");
		
	}
}
