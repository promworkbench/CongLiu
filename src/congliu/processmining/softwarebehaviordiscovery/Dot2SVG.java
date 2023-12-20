package congliu.processmining.softwarebehaviordiscovery;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot2Image;
import org.processmining.plugins.graphviz.dot.Dot2Image.Type;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.NavigableSVGPanel;

import com.kitfox.svg.Group;
import com.kitfox.svg.Line;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;

import congliu.processmining.svgicon.LoadIconClass;
import congliu.processmining.svgicon.UseFixed;

/**
 * this class aims to adding interfaces to different component (1) convert dot
 * to svg (2) create icon (3) input: component id, interface transition id (4)
 * output: svg with interfaces and line from interface transitions to interfaces
 * 
 * @author cliu3
 *
 */
public class Dot2SVG {

	//ArrayList<Component2Interfaces>
	public static JPanel DOT2SVG(Dot dot, ArrayList<Component2Interfaces> com2InterArray) throws SVGException, IOException {
		// create SVG universe and load icons
		SVGUniverse universe = new SVGUniverse();
		URI uriPIcon = universe.loadSVG(LoadIconClass.getIcon("ProvidedInterface.svg"), "ProvidedInterface.svg");
		URI uriRIcon = universe.loadSVG(LoadIconClass.getIcon("RequiredInterface.svg"), "RequiredInterface.svg");
		SVGDiagram useSVGR = universe.getDiagram(uriRIcon);
		SVGDiagram useSVGP = universe.getDiagram(uriPIcon);
		
		// convert dot to svg
		InputStream dotStream = Dot2Image.dot2imageInputStream(dot, Type.svg);
		URI uri = universe.loadSVG(dotStream, "dot2svg");
		SVGDiagram svg4dot = universe.getDiagram(uri);

		//for each component, we add provided interface to the left side, and the required interfaces to the right side.
		for (int i=0;i< com2InterArray.size();i++)
		{
			// obtain the component id
			DotElement componentID = com2InterArray.get(i).getComponentID();
			HashSet<DotElement> providedInterfaceSet = com2InterArray.get(i).getPTIDs();
			HashSet<DotElement> requestedInterfaceSet = com2InterArray.get(i).getRTIDs();
			
			//obtain the group of the current component 
			Group targetComponent = DotPanel.getSVGElementOf(svg4dot, componentID);// non pointer exception...
			
			//get the rectangle of current component, described as x, y, w, h.
			Rectangle2D box_com = targetComponent.getBoundingBox();
			
			// for each provided Interfaces, provided interfaces are put on the left side of the component cluster
			for (DotElement pi: providedInterfaceSet)
			{
				//get the box of the current transition interface
				Group targetTransition = DotPanel.getSVGElementOf(svg4dot, pi);// get the transition element from svg graph using dot id. 
				Rectangle2D box_t = targetTransition.getBoundingBox();
				
				// create the provided port
				UseFixed use = new UseFixed();
				use.setRefSvg(useSVGP);
				use.setSize((float) (box_t.getHeight()*1.5), (float) (box_t.getHeight()));
				use.setPos((float) (box_com.getX() -box_t.getHeight()*1.5),
						(float) box_t.getY());
				
				use.loaderAddText(null, "provided");
				targetComponent.loaderAddChild(null, use);
				
				//add the line from provided transition to the port
				 	
				Line svgLine = new Line();
				svgLine.addAttribute("x1", AnimationElement.AT_XML, Float.toString((float) box_t.getX()));
				svgLine.addAttribute("x2", AnimationElement.AT_XML, Float.toString((float) box_com.getX()));
				svgLine.addAttribute("y1", AnimationElement.AT_XML, Float.toString((float) (box_t.getY() +box_t.getHeight()/2)));
				svgLine.addAttribute("y2", AnimationElement.AT_XML, Float.toString((float) (box_t.getY() +box_t.getHeight()/2)));
				svgLine.addAttribute("stroke-dasharray", AnimationElement.AT_XML, "5, 5"); // stroke dasharray 
				svgLine.addAttribute("stroke", AnimationElement.AT_XML, "green");
				svgLine.addAttribute("stroke-width", AnimationElement.AT_XML, "2");
				
				targetComponent.loaderAddChild(null, svgLine);
			}
			
			// for each requested interfaces, they are put on the right side of the component cluster
			for (DotElement ri: requestedInterfaceSet)
			{
				Group targetTransition = DotPanel.getSVGElementOf(svg4dot, ri);
				Rectangle2D box_t = targetTransition.getBoundingBox();
				
				//create the provided port
				UseFixed use = new UseFixed();
				use.setRefSvg(useSVGR);
				use.setSize((float) (box_t.getHeight()*1.5), (float) (box_t.getHeight()));
				use.setPos((float) (box_com.getX() +box_com.getWidth()),
						(float) box_t.getY());
				
				use.loaderAddText(null, "required");
				targetComponent.loaderAddChild(null, use);
				
				//add the line from provided transition to the port
				Line svgLine = new Line();
				svgLine.addAttribute("x1", AnimationElement.AT_XML, Float.toString((float) (box_t.getX()+box_t.getWidth())));
				svgLine.addAttribute("x2", AnimationElement.AT_XML, Float.toString((float) (box_com.getX() +box_com.getWidth())));
				svgLine.addAttribute("y1", AnimationElement.AT_XML, Float.toString((float) (box_t.getY() +box_t.getHeight()/2)));
				svgLine.addAttribute("y2", AnimationElement.AT_XML, Float.toString((float) (box_t.getY() +box_t.getHeight()/2)));
				svgLine.addAttribute("stroke-dasharray", AnimationElement.AT_XML, "5, 5"); // stroke dasharray 
				svgLine.addAttribute("stroke", AnimationElement.AT_XML, "green");
				svgLine.addAttribute("stroke-width", AnimationElement.AT_XML, "2");
				
				targetComponent.loaderAddChild(null, svgLine);
			}
		}

		NavigableSVGPanel svgPanel = new NavigableSVGPanel(svg4dot);
		//jpanel.add(svgPanel); // standard swing stuff
		return svgPanel;
	}
}

//// for each interface transition, we need to use the node id to obtain it
//DotElement componentID = com2InterArray.get(0).getComponentID();
//Group target = DotPanel.getSVGElementOf(svg4dot, componentID);// non pointer exception...
//
//// the rectangle, described as x, y, w, h.
//if (target!= null) 
//{
//	Rectangle2D bbTo = target.getBoundingBox();
//
//	//? adding interface?
//	UseFixed use = new UseFixed();
//	use.setRefSvg(useSVGP);
//	use.setPos((float) bbTo.getX() + (float) bbTo.getWidth() / 3,
//			(float) bbTo.getY() + (float) bbTo.getHeight() / 2);
//	use.setSize((float) (bbTo.getWidth()/12.0), (float) (bbTo.getHeight()/10.0));
//	use.loaderAddText(null, "provided");
//	target.loaderAddChild(null, use);
//
////	//add line set-->add 
//	Line svgLine = new Line();
//	//System.out.println(use.getBoundingBox());
//	//svgLine.addAttribute("x1", AnimationElement.AT_XML, Float.toString((float) use.getBoundingBox().getX()));
//	svgLine.addAttribute("x1", AnimationElement.AT_XML, Float.toString((float) bbTo.getX()/2));
//	svgLine.addAttribute("x2", AnimationElement.AT_XML, Float.toString((float) bbTo.getX()));
//	//svgLine.addAttribute("y1", AnimationElement.AT_XML, Float.toString((float) use.getBoundingBox().getY()));
//	svgLine.addAttribute("y1", AnimationElement.AT_XML, Float.toString((float) bbTo.getY()/2));
//	svgLine.addAttribute("y2", AnimationElement.AT_XML, Float.toString((float) bbTo.getY()));
//	svgLine.addAttribute("stroke-dasharray", AnimationElement.AT_XML, "10, 10");
//	svgLine.addAttribute("stroke", AnimationElement.AT_XML, "green");
//	svgLine.addAttribute("stroke-width", AnimationElement.AT_XML, "5");
////
//	target.loaderAddChild(null, svgLine);
//}
