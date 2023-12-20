package org.processmining.congliu.ModifiedRegionMiner;

import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.plugins.log.logfilters.AttributeFilterParameters;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class MinePetriNetUsingRegionsDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	private MinePetriNetUsingRegionsConfiguration configuration;

	public MinePetriNetUsingRegionsDialog(UIPluginContext context, XLog log, 
			final MinePetriNetUsingRegionsConfiguration configuration) {
		this.configuration = configuration;
		
		double size1[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 200 } };
		setLayout(new TableLayout(size1));
		setOpaque(true);
		
		AttributeFilterParameters parameters = new AttributeFilterParameters(context, log);
		add(new RegionsPanel(parameters.getFilter().keySet(), configuration), "0, 0");
		
		
		final ProMTextArea text = new ProMTextArea();
//		double size2[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 100} };
//		setLayout(new TableLayout(size2));
		setOpaque(true);
		text.setText("It is a modified version of Region-based Miner tailored for software event logs");
		
		add(text, "0, 1");
	}
}
