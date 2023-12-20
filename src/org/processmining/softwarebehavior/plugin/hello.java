//package org.processmining.softwarebehavior.plugin;
//
//import javax.swing.JOptionPane;
//import javax.swing.tree.DefaultMutableTreeNode;
//
//
//public class hello
//{
//   public static void main(String args[])
//
//    {
//      System.out.println("HelloWorld!");
//      JOptionPane.showMessageDialog(null, "hello");
//      
//      DefaultMutableTreeNode top = new DefaultMutableTreeNode("top");
//		DefaultMutableTreeNode a1 = new DefaultMutableTreeNode("a1");
//		DefaultMutableTreeNode b1 = new DefaultMutableTreeNode("b1");
//		DefaultMutableTreeNode c1 = new DefaultMutableTreeNode("c1");
//		DefaultMutableTreeNode a2 = new DefaultMutableTreeNode("a2");
//		DefaultMutableTreeNode b2 = new DefaultMutableTreeNode("b2");
//		DefaultMutableTreeNode c2 = new DefaultMutableTreeNode("c2");
////		a1.add(a2);
//		
//		
//		top.add(a1);
//		top.add(b1);
//		top.add(c1);
//		
//		a1.add(a2);
//		
//		DefaultMutableTreeNode child = top.getFirstLeaf();
//		
//		
//		JOptionPane.showMessageDialog(null, a1.getChildCount()+a1.getFirstChild().toString());
//		JOptionPane.showMessageDialog(null, "topNumber"+top.getChildCount()+child.getChildCount()+child.toString());
//    }
//}

package org.processmining.softwarebehavior.plugin;  
  
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
  
public class hello {  
  
private JFrame jframeMain = new JFrame("my tree");  
private JPanel jpanelMain = new JPanel();  
  
private JTree jtree;  
  


private DefaultMutableTreeNode dmtmRoot = new DefaultMutableTreeNode("china");  
  
private DefaultMutableTreeNode dmtmSichuan = new DefaultMutableTreeNode(  
"Sichan");  
  
private DefaultMutableTreeNode dmtmChengdu = new DefaultMutableTreeNode(  
"Chengdu");  
  
private DefaultMutableTreeNode dmtmDujiangyan = new DefaultMutableTreeNode(  
"Dujiangyan");  
  
private DefaultMutableTreeNode dmtmChongQing = new DefaultMutableTreeNode(  
"Chongqing");  
  
private DefaultMutableTreeNode dmtmSPB = new DefaultMutableTreeNode("Shapingba");  
  
private DefaultMutableTreeNode dmtm;  
  
private JScrollPane jspMain;  
  

private TreePath movePath;  
  
public void init() {  
jtree = new JTree(dmtmRoot);  



jspMain = new JScrollPane(jtree);  
dmtmRoot.add(dmtmSichuan);  
dmtmSichuan.add(dmtmChengdu);  
dmtmSichuan.add(dmtmDujiangyan); 
//dmtmRoot.add(dmtmSichuan); 
dmtmRoot.add(dmtmChongQing); 
dmtmChongQing.add(dmtmSPB);  
 
JOptionPane.showMessageDialog(null, dmtmRoot.getChildCount()); 
DefaultMutableTreeNode child = (DefaultMutableTreeNode) dmtmRoot.getFirstChild();
JOptionPane.showMessageDialog(null, child.getChildCount()+child.toString()); 
  
for (int i = 0; i < 10; i++) {  
dmtm = new DefaultMutableTreeNode(i);  
dmtmRoot.add(dmtm);  
}  
  
// è®¾ç½®å…¶æ²¡æœ‰è¿žçº¿  
jtree.putClientProperty("JTree.lineStyle", "None");  
// è®¾ç½®æ˜¯å�¦æ˜¾çŽ°å…¶æ ¹èŠ‚ç‚¹çš„å›¾æ ‡  
jtree.setShowsRootHandles(true);  
  
jpanelMain.setLayout(new BorderLayout());  
jpanelMain.add(jspMain);  
jframeMain.add(jpanelMain);  
jframeMain.pack();  
jframeMain.setLocationRelativeTo(null);  
jframeMain.setSize(264, 400);  
jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
jframeMain.setVisible(true);  
  
 
jtree.setEditable(true);  
  
  
MouseListener ml = new MouseAdapter() {  
  

TreePath tp;  
  

@Override  
public void mousePressed(MouseEvent e) {  
// TODO Auto-generated method stub  
// super.mousePressed(e);  

tp = jtree.getPathForLocation(e.getX(), e.getY());  
if (tp != null) {  
movePath = tp;//   
}  
}  


@Override  
public void mouseReleased(MouseEvent e) {  
// TODO Auto-generated method stub  
// super.mouseReleased(e);  

tp = jtree.getPathForLocation(e.getX(), e.getY());  
if (tp != null && movePath != null) {  

if (movePath.isDescendant(tp) && movePath != tp) {  
System.out.println("illeagle operation");  
return;  
}  

else if (movePath != tp) {  

System.out.println(tp.getLastPathComponent());  

  

DefaultMutableTreeNode dmtnLastPath = (DefaultMutableTreeNode) tp  
.getLastPathComponent();  

DefaultMutableTreeNode dmtnFirstPath = (DefaultMutableTreeNode) movePath  
.getLastPathComponent();  
  

dmtnLastPath.add(dmtnFirstPath);  

jtree.updateUI();  
}  
}  
  
}  
};  
  
jtree.addMouseListener(ml);  
  
}  
  
public static void main(String[] args) {  
new hello().init();  
}  
}