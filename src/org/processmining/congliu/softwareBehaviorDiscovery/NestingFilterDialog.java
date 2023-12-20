package org.processmining.congliu.softwareBehaviorDiscovery;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.contexts.uitopia.UIPluginContext;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class NestingFilterDialog extends JPanel{
	
	public NestingFilterDialog(UIPluginContext context, final NestingThresholdValue threshold)
	{
		// adding a slider.
		
		
		final NiceSlider integerSilder = SlickerFactory.instance().createNiceDoubleSlider("Nesting Frequency Threshold Selection",
				0, 1, 0.9, Orientation.HORIZONTAL);
		
		integerSilder.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//parameters.setYourInteger(integerSilder.getSlider().getValue());
				threshold.setValue((double)integerSilder.getSlider().getValue()/10000);
				//System.out.println((double)integerSilder.getSlider().getValue()/10000);
			}
		});
		setOpaque(true);
		add(integerSilder, "0, 0");
		
//		
//		
//		final ProMTextArea text = new ProMTextArea();
//		setOpaque(true);
//		text.setText("Please select the threshold for nesting detection.");
//		add(text, "0, 1");
	}

}
