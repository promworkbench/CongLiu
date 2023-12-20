package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import org.deckfour.xes.model.XEvent;

public class XEventAndInterface {
		private XEvent event;
		private Interface inter;
		
		public XEventAndInterface(XEvent event, Interface inter)
		{
			this.event= event;
			this.inter = inter;
		}
		public Interface getInterface()
		{
			return this.inter;
		}
		public XEvent getEvent()
		{
			return this.event;
		}
	
}
