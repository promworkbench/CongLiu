package congliu.processminig.XPortSoftwareEventlogTransformation;

public class testStrings {

	public static void main (String []args)
	{
		
		String tempPara = "a,b";
		if(tempPara.contains(","))
		{
			for(String para: tempPara.split("\\,"))
			{
				System.out.println(para);
			}
		}
		else
		{
			System.out.println(tempPara);
		}
		
//		String source="mxICanvas";
//		String target="mxRectangle";
//		
//		System.out.println(Objects.hash(source));
//		System.out.println(Objects.hash(target));
//		
//		System.out.println(Objects.hash(source));
//		System.out.println(Objects.hash(target));
//		
//		System.out.println(Objects.hash(source)+Objects.hash(target));
//		System.out.println(Objects.hash(target)+Objects.hash(source));
//		
//		System.out.println(Objects.hash(source,target));
//		System.out.println(Objects.hash(target,source));
		
//		ArrayList<String> nodes = new ArrayList<>();
//		nodes.add("c1");
//		nodes.add("c2");
//		nodes.add("c3");
//		nodes.add("c4");
//		nodes.add("c5");
//		
//		for(int i =0; i<nodes.size();i++)
//		{
//			for(int j=i+1;j<nodes.size();j++)
//			{
//				System.out.println(nodes.get(i)+"->"+nodes.get(j));
//			}
//		}


		
//		String t="[(source 1,1)]";
//		String [] split1 = t.split("\\(");
//		
//		String [] split2 = split1[1].split("\\,");
//		System.out.println(split2[0]);
//		
//		String s = "BookstoreExample.BookstoreStarter()";
		

		
//			System.out.println(extractConstructorClass(s));
//		
//			System.out.println(extractConstructorPackage(s));
		
	}
	
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
}
