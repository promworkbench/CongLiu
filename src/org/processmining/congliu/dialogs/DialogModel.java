package org.processmining.congliu.dialogs;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMTextArea;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class DialogModel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4922401146531128612L;

	public DialogModel(UIPluginContext context)
	{
		//double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 30, 30 } };
	
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 200 } };
		setLayout(new TableLayout(size));
		setOpaque(true);
		
		//adding a list
		Set<String> values = new HashSet<String>();
		values.add("Option 1");
		values.add("Option 2");
		values.add("Option 3");
		values.add("Option 4");
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String value: values) {
			listModel.addElement(value);
		}
		final ProMList<String> list = new ProMList<String>("Select option", listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final String defaultValue = "Option 1";
		list.setSelection(defaultValue);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					//parameters.setYourString(selected.get(0));
					System.out.println(selected.get(0));
				} else {
					/*
					 * Nothing selected. Revert to selection of default classifier.
					 */
					list.setSelection(defaultValue);
					//parameters.setYourString(defaultValue);
				}
			}
		});
		list.setPreferredSize(new Dimension(100, 100));
		add(list, "0, 0");
		
		
//		// adding a slider
//		final NiceSlider integerSilder = SlickerFactory.instance().createNiceIntegerSlider("Select number ", -10,
//				10, 10, Orientation.HORIZONTAL);
//		integerSilder.addChangeListener(new ChangeListener() {
//
//			public void stateChanged(ChangeEvent e) {
//				//parameters.setYourInteger(integerSilder.getSlider().getValue());
//				System.out.println(integerSilder.getSlider().getValue());
//			}
//		});
//		add(integerSilder, "0, 1");
		
		
		//adding a text
		final ProMTextArea text = new ProMTextArea();
		setOpaque(true);
		text.setText("Simple information on the plugin");
		
		add(text, "0, 1");
	}

}
