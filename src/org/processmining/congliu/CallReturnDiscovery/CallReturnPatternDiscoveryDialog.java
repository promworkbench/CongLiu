package org.processmining.congliu.CallReturnDiscovery;

import java.util.Set;

import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextArea;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

/**
 * this dialog to used to select which plugin to analyze
 * @author cliu3
 *
 */
public class CallReturnPatternDiscoveryDialog extends JPanel{

	private CallReturnPatternDiscoveryConfiguration configuration;
	
	public CallReturnPatternDiscoveryDialog(UIPluginContext context, Set<String> pluginSet, 
			final CallReturnPatternDiscoveryConfiguration configuration) 
	{
		this.configuration = configuration;
		
		double size1[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 200 } };
		setLayout(new TableLayout(size1));
		setOpaque(true);
		
		add(new CallReturnPatternDiscoveryRegionPanel(pluginSet, configuration), "0, 0");
		
		
		final ProMTextArea text = new ProMTextArea();
//		double size2[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 100} };
//		setLayout(new TableLayout(size2));
		setOpaque(true);
		text.setText("Please select the plugin you are interested for discovering Call-and-Return Pattern");
		
		add(text, "0, 1");
	}

}