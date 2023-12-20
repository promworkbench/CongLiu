package congliu.processmining.softwareprocessmining;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileChooserPanel extends JPanel{
	
	public FileChooserPanel(final FileChooserConfiguration configuration)
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		fc.setDialogTitle("Select the Software Execution Data and Mapping");
		
		// the initial(default) directory
		fc.setCurrentDirectory(new java.io.File("D:\\[6]"));
		
		int retrivel = fc.showOpenDialog(this);
		
		if(retrivel == JFileChooser.APPROVE_OPTION)
		{
			configuration.setFilename(fc.getSelectedFile().getAbsolutePath());
		}
	}

}
