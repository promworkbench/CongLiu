//
//--- Example Dot 2 SVG and add Icon ---
//
//Dot dot = new Dot();
//DotNode node = dot.addNode("Test Node");
//String nodeId = node.getId();
//
//SVGUniverse universe = new SVGUniverse();
//universe.loadSVG(MyClass.getIcon(), "icon.svg");
//SVGDiagram useSVG = universe.getDiagram("icon.svg");
//
//InputStream dotStream = Dot2Image.dot2imageInputStream(dot, Type.svg);
//URI uri = universe.loadSVG(dotStream, "dotsvg");
//SVGDiagram svg = universe.getDiagram(uri);
//
//Group target = (Group) svg.getElement(nodeId);
//Rectangle2D bbTo = target.getBoundingBox();
//
//UseFixed use = new UseFixed();
//use.setRefSvg(useSVG);
//use.setPos(x, y);
//use.setSize(toWidth, toHeight);
//
//target.loaderAddChild(null, use);
//
//
//Line svgLine = new Line();
//svgLine.setAttribute("x1", AnimationElement.AT_XML, Float.toString(x1));
//svgLine.setAttribute("x2", AnimationElement.AT_XML, Float.toString(x2));
//svgLine.setAttribute("y1", AnimationElement.AT_XML, Float.toString(y1));
//svgLine.setAttribute("y2", AnimationElement.AT_XML, Float.toString(y2));
//svgLine.setAttribute("stroke-dasharray", AnimationElement.AT_XML, "10, 10");
//svgLine.setAttribute("stroke", AnimationElement.AT_XML, "black");
//
//target.loaderAddChild(null, svgLine);
//
//
//svgPanel = new NavigableSVGPanel(svg);
//jpanel.add(svgPanel); // standard swing stuff
//
//---
//
//icon.svg in package folder of my.package
//
//package my.package;
//class MyClass {
//	public static InputStream getIcon() {
//		return MyClass.class.getResourceAsStream("icon.svg");
//	}
//}
//
//--- Utility class ---
//
//
///**
// * Fixed implementation of SVG Use
// * 
// * @author mleemans
// *
// */
//public class UseFixed extends Use {
//
//    private static final long serialVersionUID = -133899355113182382L;
//    
//    private SVGDiagram svg;
//
//    public void setRefSvg(SVGDiagram svg) throws SVGElementException {
//        this.svg = svg;
//        addAttribute("xlink:href", AnimationElement.AT_XML, svg.getXMLBase()
//                .toString());
//    }
//    
//    public void setPos(float x, float y) throws SVGElementException {
//        addAttribute("x", AnimationElement.AT_XML, Float.toString(x));
//        addAttribute("y", AnimationElement.AT_XML, Float.toString(y));
//    }
//    
//    public void setSize(float wTo, float hTo) throws SVGElementException {
//        addAttribute("width", AnimationElement.AT_XML, Float.toString(wTo));
//        addAttribute("height", AnimationElement.AT_XML, Float.toString(hTo));
//    }
//
//    protected void build() throws SVGException {
//        super.build();
//        AffineTransform refXform = getFromParent("refXform");
//
//        // transform absolute width height to scale/relative width height
//        float width = getFloatFromParent("width");
//        float height = getFloatFromParent("height");
//        float scaleW = width / svg.getWidth();
//        float scaleH = height / svg.getHeight();
//        refXform.scale(scaleW, scaleH);
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> T getFromParent(String fieldName) {
//        try {
//            Field field = Use.class.getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return (T) field.get(this);
//        } catch (Exception e) {
//            throw new SVGUtilException("Cannot access field in Use", e);
//        }
//    }
//
//    private float getFloatFromParent(String fieldName) {
//        try {
//            Field field = Use.class.getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return field.getFloat(this);
//        } catch (Exception e) {
//            throw new SVGUtilException("Cannot access field in Use", e);
//        }
//    }
//}