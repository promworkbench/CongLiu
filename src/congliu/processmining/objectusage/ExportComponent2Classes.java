package congliu.processmining.objectusage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
	name = "Export Component to Classes Configuration (.txt)",
	returnLabels = {},
	returnTypes = {},
	parameterLabels = { "Exported Component to Classes Configuration","file" },
	userAccessible = true
)
@UIExportPlugin(
	description = "Export Component to Classes Configuration (.txt)",//show the type in window for recording files
	extension = "txt" // the suffix name
)
public class ExportComponent2Classes {
	@PluginVariant(
			variantLabel = "Export Component to Classes Configuration (.txt)",
			requiredParameterLabels = { 0, 1 }
		) 
	public void exportComponent2Classes(PluginContext context, Component2Classes c2c, File file) throws Exception {
		
		context.log("Export started..."); 
		System.out.println("component2class configuration output starts");
		
		FileOutputStream fos = new FileOutputStream(file);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for(String component: c2c.getAllComponents())
		{
			StringBuffer classes =new StringBuffer();
			for(String c: c2c.getClasses(component))
			{
				classes.append(";"+c);
			}
			
			bw.write(component+classes);
			System.out.println(component+classes);
			bw.newLine();
		}
		bw.close();
		
		context.log("Export complete!");
	} 
}
