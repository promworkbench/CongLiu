package cong.liu.processmining.classinteractiongraph;

import congliu.processmining.objectusage.Component2Classes;

public class Component2ClassesWrapper {
	Component2Classes value;

	public Component2ClassesWrapper(Component2Classes value) {
       this.value = value;
   } 
	
	public void setValue(Component2Classes value) {
		this.value = value;
	}
	
	public Component2Classes getValue()
	{
		return this.value;
	}
}
