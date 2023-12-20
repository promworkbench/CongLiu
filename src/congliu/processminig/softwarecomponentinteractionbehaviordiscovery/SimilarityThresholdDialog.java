package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.contexts.uitopia.UIPluginContext;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class SimilarityThresholdDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SimilarityThresholdDialog(UIPluginContext context, final SimilarityThresholdConfiguration configuration)
	{
		// adding a slider
		final NiceSlider doubleSilder = SlickerFactory.instance().createNiceDoubleSlider("Select Similarity Threshold Value", 0.0, 1, 0.8, Orientation.HORIZONTAL);
		doubleSilder.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				configuration.setThresholdValue(doubleSilder.getSlider().getValue());
				//parameters.setYourInteger(integerSilder.getSlider().getValue());
				//System.out.println(doubleSilder.getSlider().getValue());
			}
		});
		add(doubleSilder, "0, 0");
	
	}
}
