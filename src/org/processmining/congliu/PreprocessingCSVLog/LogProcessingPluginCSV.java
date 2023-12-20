package org.processmining.congliu.PreprocessingCSVLog;
/**
 * This plug-in functions quite same with the LogProcessingPlugin. The difference is that this one return a CSV object.  
 */
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import prompt.csv.data.CSVLog;
	
	@Plugin(
			name = "RapidMiner Log Re-processing (CSV)",// plugin name
			
			returnLabels = {"Formatted CSV Log"}, //reture labels
			returnTypes = {CSVLog.class},//reture class
			
			//input parameter labels, corresponding with the second parameter of main function
			parameterLabels = {"RapidMiner Recording"},
			
			userAccessible = true,
			help = "This plugin aims to format the kieker recorded software (RapidProm) event log." 
			)
public class LogProcessingPluginCSV {


	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Formatting RapidMiner Log, CSV",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {}
			)

		public static CSVLog formatCSV(UIPluginContext context) throws Exception 
		{
			CSVLog log = new CSVLog("CSV File");
		//config the input parameters, original log, package2module mapping, start and end time of each module, destination file location
		//adding a set of dialogs to select which attribute to be used for splitting
			FileChooserConfiguration fcCSVdirectory = new FileChooserConfiguration();
			FileChooserConfiguration fcmodulefile = new FileChooserConfiguration();
			FileChooserConfiguration fcmappingfile = new FileChooserConfiguration();
			FileChooserConfiguration fcformattedCSVdirectory = new FileChooserConfiguration();	
			
			new FileChooserPanel(fcCSVdirectory);
			new FileChooserPanel(fcmodulefile);
			new FileChooserPanel(fcmappingfile);
			new FileChooserPanel(fcformattedCSVdirectory);
			
			FormatCSVLog formatCSV = new FormatCSVLog(fcCSVdirectory.getFilename(), fcmodulefile.getFilename(), fcmappingfile.getFilename()
					,fcformattedCSVdirectory.getFilename());
			// the input parameter is the csvlog (final)
			
			formatCSV.formatCSV(log);
			
			// do some preparation work, like initialize the fieldsNumber attribute for the CSVlog. 
			log.prepare();
			
			return log;
		}

}