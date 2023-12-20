package org.processmining.congliu.PreprocessingCSVLog;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileChooserPanel extends JPanel{
	
	public FileChooserPanel(final FileChooserConfiguration configuration)
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setDialogTitle("Ordered as CSV directory, module file, mapping file, final directory");
		fc.setCurrentDirectory(new java.io.File("D:\\BSR-Project-New Stage\\On Runtime monitoring\\SmallCase"));
		int retrivel = fc.showOpenDialog(this);
		if(retrivel == JFileChooser.APPROVE_OPTION)
		{
			configuration.setFilename(fc.getSelectedFile().getAbsolutePath());
		}
	}

}
