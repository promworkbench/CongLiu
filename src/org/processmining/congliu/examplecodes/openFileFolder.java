package org.processmining.congliu.examplecodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class openFileFolder {

	public openFileFolder(String path)
	{
		//open readin file
		File file = new File(path); 
		
		File[] filelist = file.listFiles();
		for (File f: filelist)
        {
			f.getName();
			BufferedReader reader = null;
			try {
				
    			reader = new BufferedReader(new FileReader(f));
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
			try {
    			while (reader.readLine() != null)
    			{
    				// the main body to handle the log. 
    				//tempList = tempString.split(";");
    			}
    			//close the CSV file reader
    			reader.close();
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        }
	}
}
