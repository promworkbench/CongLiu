package org.processmining.congliu.PreprocessingCSVLog;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * This plugin aims to format the kieker recorded software (RapidProm) event log
 * this time, we focus on the parameter type, return type, class idenfier, package identifier, runtime module, 
 * belonging module, module attribute, method attribute (constructor or method call) for each method. 
 * The formatted log should satisfy the standard CSV, such as separated by comma. 
 * @author cliu3
 *
 */

	@Plugin(
			name = "RapidMiner Log Re-processing",// plugin name
			
			returnLabels = {"Formatted CSV Log"}, //reture labels
			returnTypes = {String.class},//reture class
			
			//input parameter labels, corresponding with the second parameter of main function
			parameterLabels = {"RapidMiner Recording"},
			
			userAccessible = true,
			help = "This plugin aims to format the kieker recorded software (RapidProm) event log." 
			)

public class LogProcessingPlugin {
	
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Formatting RapidMiner Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {}
			)
	
    public static String format(UIPluginContext context) 
    {
		//config the input parameters, original log, package2module mapping, start and end time of each module, destination file location
		//adding a set of dialogs to select which attribute to be used for splitting
		FileChooserConfiguration fcCSVdirectory = new FileChooserConfiguration();
		FileChooserConfiguration fcmodulefile = new FileChooserConfiguration();
		FileChooserConfiguration fcmappingfile = new FileChooserConfiguration();
		FileChooserConfiguration fcformattedCSVdirectory = new FileChooserConfiguration();
		//FileChooserDialog dialog = new FileChooserDialog(context, fc);
		//InteractionResult result = context.showWizard("Choose File", true, true, new FileChooserPanel(fc));
		new FileChooserPanel(fcCSVdirectory);
		new FileChooserPanel(fcmodulefile);
		new FileChooserPanel(fcmappingfile);
		new FileChooserPanel(fcformattedCSVdirectory);
		System.out.println(fcCSVdirectory.getFilename());
		System.out.println(fcmodulefile.getFilename());
		System.out.println(fcmappingfile.getFilename());
		System.out.println(fcformattedCSVdirectory.getFilename());
//		if (result != InteractionResult.FINISHED) {
//			return null;
//		}
		
		
//		//the original csv file archive
//		String originalcsvfilename = "D:\\BSR-Project-New Stage\\On Runtime monitoring\\SmallCase\\SourceKiekerData"; 
//		
//		//the start and end time of each module (plugin)
//		String originalmodulefile = "D:\\BSR-Project-New Stage\\On Runtime monitoring\\SmallCase\\module.txt"; 
//		
//		//the mapping from module to packages
//		String orginalmapping = "D:\\BSR-Project-New Stage\\On Runtime monitoring\\SmallCase\\mapping.txt";
//		
//		//the obtained file archive
//		String formattedCSVfile = "D:\\BSR-Project-New Stage\\On Runtime monitoring\\SmallCase";
		
		
		
		//FormatCSVLog formatCSV = new FormatCSVLog(originalcsvfilename, originalmodulefile, orginalmapping,formattedCSVfile);
		FormatCSVLog formatCSV = new FormatCSVLog(fcCSVdirectory.getFilename(), fcmodulefile.getFilename(), fcmappingfile.getFilename()
				,fcformattedCSVdirectory.getFilename());
		formatCSV.format();
		
		// if it can return a csv object? and then visualize it 
		//CsvReader
		return "Success";
    }
}
