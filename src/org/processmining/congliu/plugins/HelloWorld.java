package org.processmining.congliu.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
public class HelloWorld {
	@Plugin(
		name = "My Hello World",
		returnLabels = {"Hello World String"},
		returnTypes = {String.class},
		userAccessible = true,
		help = "Produces the string: 'Hello world'", 
		parameterLabels = { } 
		)
		
	@UITopiaVariant(
        affiliation = "TU/e", 
        author = "Cong liu", 
        email = "c.liu.3@tue.nl"
        )
        
        public static String helloWorld(PluginContext context, XLog log) {
				context.log("lc-Debug", MessageLevel.DEBUG);
				context.log("lc-Error", MessageLevel.ERROR);
				context.log("lc-Normal", MessageLevel.NORMAL);
				context.log("lc-Warning", MessageLevel.WARNING);
				context.log("lc-Test", MessageLevel.TEST);
                return "This is the first try to implememt ProM plug-ins";
        }
		

}
