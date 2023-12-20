package org.processmining.congliu.PreprocessingCSVLog;

import java.util.ArrayList;

public class MappingPackage2Module {
	//the name of a module
	private String modulename;
	//the corresponding packages of the current module
	private ArrayList<String> packages;
	
	public String getModule() {
		return modulename;
	}
	public void setModule(String module) {
		this.modulename = module;
	}
	public ArrayList<String> getPackages() {
		return packages;
	}
	public void setPackages(ArrayList<String> packages) {
		this.packages = packages;
	}
	
}
