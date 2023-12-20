package congliu.processmining.objectusage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;

/*
 * this class aims to save the component to classes derived by the co-occurence graph clustering to the workspace. 
 */
public class ButtonActionListernerSave implements ActionListener{

	PluginContext context;
	Component2Classes c2c;
	public ButtonActionListernerSave(PluginContext context, Component2Classes c2c){
		
		this.context = context;
		this.c2c=c2c;
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		for(String c: c2c.getAllComponents())
		{
			System.out.println(c);
		}
		System.out.println("Button has been clicked");
		context.getProvidedObjectManager().createProvidedObject("Clustering Results",
				c2c, Component2Classes.class, context);

	   if (context instanceof UIPluginContext) {
	          UIPluginContext uiPluginContext = (UIPluginContext) context;
	          uiPluginContext.getGlobalContext().getResourceManager().getResourceForInstance(c2c)
	                        .setFavorite(true);
		}
	}

}
