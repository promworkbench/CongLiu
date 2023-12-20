package org.processmining.congliu.RapidMinerLogProcessing;

public class testInProM {
	
	public static void main(String []args) throws Exception
	{
//		//split the original log to obtain the cases 
//		OriginalSplittingToCases osc =new OriginalSplittingToCases ("D:\\KiekerData\\CaseStudy003\\OriginalEventLog", "D:\\KiekerData\\CaseStudy003\\MethodLevelLog10Case");
//		osc.splitting();
		
//		//enrich the cases with class, package, plugin information 
//		EnrichMethodsWithPluginClassPackages em = new EnrichMethodsWithPluginClassPackages("D:\\KiekerData\\CaseStudy003\\RapidMinerLog.txt", "D:\\KiekerData\\CaseStudy003\\MethodLevelLog10Case", "D:\\KiekerData\\CaseStudy003\\EnrichedMethodLevelLog10Case");
//		em.enriching();
		
//		//filter method level event to package level
//		FilterToPackageLevel fp = new FilterToPackageLevel("D:\\KiekerData\\CaseStudy003\\EnrichedMethodLevelLog10Case", "D:\\KiekerData\\CaseStudy003\\EnrichedPackageLevelLog10Case");
//		fp.filteringToPackageLevel();
		
//		//convert csv to xes
//		GeneralCSVToXES gc = new GeneralCSVToXES("D:\\KiekerData\\CaseStudy002\\EnrichedPluginMethodLevelLog12Case", "D:\\KiekerData\\CaseStudy002\\EnrichedMethodLevelLog.xes");
//		gc.EnrichedConvert();
		
		ImprovedCSVToXES icx = new ImprovedCSVToXES("D:\\KiekerData\\CaseStudy003\\packageLevelLog.xes.gz", "D:\\KiekerData\\CaseStudy003\\EnrichedPackageLevelLog10Case");
		icx.improvedConversion();
	}
}
