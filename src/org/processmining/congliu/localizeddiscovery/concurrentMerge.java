package org.processmining.congliu.localizeddiscovery;

import java.util.HashMap;
import java.util.List;

import org.processmining.processtree.Block;
import org.processmining.processtree.Edge;
import org.processmining.processtree.Event;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.Task;
import org.processmining.processtree.impl.AbstractBlock;
import org.processmining.processtree.impl.AbstractBlock.And;
import org.processmining.processtree.impl.AbstractBlock.Def;
import org.processmining.processtree.impl.AbstractBlock.DefLoop;
import org.processmining.processtree.impl.AbstractBlock.Or;
import org.processmining.processtree.impl.AbstractBlock.PlaceHolder;
import org.processmining.processtree.impl.AbstractBlock.Seq;
import org.processmining.processtree.impl.AbstractBlock.Xor;
import org.processmining.processtree.impl.AbstractBlock.XorLoop;
import org.processmining.processtree.impl.AbstractEvent.Message;
import org.processmining.processtree.impl.AbstractEvent.TimeOut;
import org.processmining.processtree.impl.AbstractTask.Automatic;
import org.processmining.processtree.impl.AbstractTask.Manual;
import org.processmining.processtree.impl.EdgeImpl;
import org.processmining.processtree.impl.ProcessTreeImpl;

public class concurrentMerge {

	
	public ProcessTree concurrentModelMerge(List<ProcessTree> treelist)
	{
		ProcessTree treemerge = new ProcessTreeImpl("merged model");
		
		//the following is the key
		HashMap<Node, Node> oldNew = new HashMap<Node, Node>();
		HashMap<Block, Integer> childrenSize = new HashMap<Block, Integer>();
		HashMap<Block, HashMap<Integer, Edge>> childrenMap = new HashMap<Block, HashMap<Integer,Edge>>();
		
	
		
		//how to merge the sub-models(trees) according to their execution order. 
		// try to use some existing work on process tree merge (PTMerge)
		
		for (ProcessTree pt: treelist)
		{
			//add nodes
			for(Node n: pt.getNodes())
			{
				Node np = cloneNode(n);
				oldNew.put(n, np);//Key:old, Value:new 
				if (np instanceof Block)//if np is a block, then record its outgoing edges
				{
					childrenMap.put((Block)np, new HashMap<Integer, Edge>());
					childrenSize.put((Block)np, ((Block)n).getOutgoingEdges().size());
				}
				np.setProcessTree(treemerge);
				treemerge.addNode(np);				
			}
			
			//add edges
			for(Edge e: pt.getEdges())
			{
				//obtain the value
				Block parent = (Block) oldNew.get(e.getSource());
				Edge ep = new EdgeImpl(e.getID(), parent, oldNew.get(e.getTarget()), e.getExpression());
				// record the edge information
				childrenMap.get(parent).put(e.getSource().getOutgoingEdges().indexOf(e), ep);
				treemerge.addEdge(ep);				
			}
			
		}
		
		// add edges for each block
		for(Block n: childrenMap.keySet()){
			for(int i = 0; i < childrenSize.get(n); i++){
				n.addOutgoingEdge(childrenMap.get(n).get(i));
			}
		}
			

		// new root node of treemerge, it should be a concurrent node. 
		Block root  = new AbstractBlock.And("root"); 
		treemerge.addNode(root);
		//connect the root of each sub-tree with the new one
		for (ProcessTree pt: treelist)
		{
			Edge rootedge = new EdgeImpl(root, oldNew.get(pt.getRoot()));
			treemerge.addEdge(rootedge);
			root.addOutgoingEdge(rootedge);
		}
		
		root.setProcessTree(treemerge);
		treemerge.setRoot(root);

		
		return treemerge;
	}
	private Node cloneNode(Node n){
		if(n instanceof Block.Seq){
			return new Seq(n.getID(), n.getName());
		}
		else if(n instanceof Block.And){
			return new And(n.getID(), n.getName());
		}
		else if(n instanceof Block.Xor){
			return new Xor(n.getID(), n.getName());
		}
		else if(n instanceof Block.Def){
			return new Def(n.getID(), n.getName());
		}
		else if(n instanceof Block.Or){
			return new Or(n.getID(), n.getName());
		}
		else if(n instanceof Block.XorLoop){
			return new XorLoop(n.getID(), n.getName());
		}
		else if(n instanceof Block.DefLoop){
			return new DefLoop(n.getID(), n.getName());
		}
		else if(n instanceof Block.PlaceHolder){
			return new PlaceHolder(n.getID(), n.getName());
		}
		else if(n instanceof Task.Automatic){
			return new Automatic(n.getID(), n.getName());
		}
		else if(n instanceof Task.Manual){
			return new Manual(n.getID(), n.getName());
		}
		else if(n instanceof Event.TimeOut){
			return new TimeOut(n.getID(), n.getName(), ((TimeOut) n).getMessage());
		}
		else if(n instanceof Event.Message){
			return new Message(n.getID(), n.getName(), ((Message) n).getMessage());
		}
		else{
			throw new NullPointerException();
		}
	}
}
