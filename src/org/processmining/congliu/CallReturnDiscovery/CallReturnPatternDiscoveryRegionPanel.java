package org.processmining.congliu.CallReturnDiscovery;

import java.awt.Dimension;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.framework.util.ui.widgets.ProMList;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class CallReturnPatternDiscoveryRegionPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CallReturnPatternDiscoveryRegionPanel(Set<String> attributes, final CallReturnPatternDiscoveryConfiguration configuration) {
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));

		setOpaque(false);
		
		DefaultListModel listModel = new DefaultListModel();
		for (String attribute: attributes) {
			listModel.addElement(attribute);
		}
		final ProMList<String> list = new ProMList<String>("Select plugin name", listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					configuration.setPluginName(selected.get(0));
				} else {
					/*
					 * Nothing selected. Revert to selection of default classifier.
					 */
					list.setSelectedIndex(0);
					configuration.setPluginName("Alpha Miner");
				}
			}
		});
		list.setPreferredSize(new Dimension(100, 100));
		add(list, "0, 0");
	}
}
