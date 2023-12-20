package org.processmining.congliu.ModifiedRegionMiner;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.models.EventLogArray;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.reduction.Murata;
import org.processmining.plugins.petrinet.reduction.MurataInput;
import org.processmining.plugins.petrinet.reduction.MurataOutput;

/** Utils to improve resulting merged nets
 *  
 * @author Anna Kalenkova
 *
 */
public class AcceptingPetriNetUtils {

	/**
	 * Color each region
	 * 
	 * @param mergedNet
	 * @param eventLogArray
	 */
	public static void colorMergedNet(AcceptingPetriNet mergedNet, EventLogArray eventLogArray) {
		Set<Transition> sharedTransitions = new HashSet<Transition>();
		Set<Transition> coloredTransitions = new HashSet<Transition>();
		Set<Transition> logTransitions = new HashSet<Transition>();
		for (int i = 0; i < eventLogArray.getSize(); i++) {
			XLog eventLog = eventLogArray.getLog(i);
			Color logColor = new Color((int) (Math.random() * 0x1000000));
			for (int j = 0; j < eventLog.size(); j++) {
				XTrace trace = eventLog.get(j);
				for (int k = 0; k < trace.size(); k++) {
					XEvent event =  trace.get(k);
					String eventName = event.getAttributes().get("concept:name").toString();
					String lifecycle = "";
					if(event.getAttributes().get("lifecycle:transition") != null) {
						lifecycle = event.getAttributes().get("lifecycle:transition").toString();
					}
					for(Transition transition : mergedNet.getNet().getTransitions()) {
						if ((transition.getLabel().equals(eventName + "+" + lifecycle)) 
								&& !sharedTransitions.contains(transition)) {
							if(!coloredTransitions.contains(transition)) {
								transition.getAttributeMap().put(AttributeMap.FILLCOLOR, logColor);
								logTransitions.add(transition);
							} else {
								transition.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
								sharedTransitions.add(transition);
							}
						}
					}
				}
			}
			coloredTransitions.addAll(logTransitions);
		}
	}
	
	/**
	 * Simplify the structure
	 * 
	 * @param context
	 * @param net
	 * @return
	 */
	public static void reduce(PluginContext context, AcceptingPetriNet net) {
		Murata murata = new Murata();
		MurataInput input = new MurataInput(net.getNet(), net.getInitialMarking());
		input.setVisibleSacred(net.getNet());
		for (Place place : net.getInitialMarking().baseSet()) {
			input.addSacred(place);
		}
		for (Marking finalMarking : net.getFinalMarkings()) {
			for (Place place : finalMarking.baseSet()) {
				input.addSacred(place);
			}
		}
		input.allowRule(MurataInput.CSM);
		MurataOutput output = null;
		try {
			output = murata.run(context, input);
		} catch (ConnectionCannotBeObtained e) {

		}
		Marking initialMarking = net.getInitialMarking();
		Set<Marking> finalMarkings = net.getFinalMarkings();
		net.init(output.getNet());
		Map<Place, Place> placeMap = output.getPlaceMapping();
		Marking subInitialMarking = new Marking();
		for (Place place : initialMarking.baseSet()) {
			subInitialMarking.add(placeMap.get(place), initialMarking.occurrences(place));
		}
		net.setInitialMarking(subInitialMarking);
		Set<Marking> subFinalMarkings = new HashSet<Marking>();
		for (Marking finalMarking : finalMarkings) {
			Marking subFinalMarking = new Marking();
			for (Place place : finalMarking.baseSet()) {
				subFinalMarking.add(placeMap.get(place), finalMarking.occurrences(place));
			}
			subFinalMarkings.add(subFinalMarking);
		}
		net.setFinalMarkings(subFinalMarkings);
	}
	
	/**
	 * Remove equal source and sink nodes
	 * 
	 * @param net
	 */
	public static void removeEqualSourceAndSinkNodes(AcceptingPetriNet net) {
		Set<Place> placesToRemove = new HashSet<Place>();
		Marking initialMarking = net.getInitialMarking();
		Set<Marking> finalMarkings = net.getFinalMarkings();
		for(Place place : initialMarking) {
			Set<Place> equalPlaces = retrievePlacesEqualByOutput(net, place);
			for(Place equalPlace : equalPlaces) {
				if(!placesToRemove.contains(equalPlace)) {
					placesToRemove.add(place);
					break;
				}
			}
		}
		for (Marking marking : net.getFinalMarkings()) {
			for (Place place : marking) {
				Set<Place> equalPlaces = retrievePlacesEqualByInput(net, place);
				for (Place equalPlace : equalPlaces) {
					if (!placesToRemove.contains(equalPlace)) {
						placesToRemove.add(place);
						break;
					}
				}
			}
		}
		// Remove places and update markings
		for(Place place : placesToRemove) {
			net.getNet().removePlace(place);
			if(initialMarking.contains(place)) {
				initialMarking.remove(place);
			}
			for(Marking finalMarking: finalMarkings) {
				if(finalMarking.contains(place)) {
					finalMarking.remove(place);
				}
			}
		}
	}
	
	private static Set<Place> retrievePlacesEqualByOutput (AcceptingPetriNet net, Place place) {
		Set<Transition> outTransitions1 = collectOutTransitions(net, place);
		Set<Place> equalPlaces = new HashSet<Place>();
		for(Place eqPlace : net.getNet().getPlaces()) {
			if(!place.equals(eqPlace)) {
				Set<Transition> outTransitions2 = collectOutTransitions(net, eqPlace);
				if ((outTransitions1.containsAll(outTransitions2)) && (outTransitions2.containsAll(outTransitions1))) {
					equalPlaces.add(eqPlace);
				}
			}
		}
		return equalPlaces;
	}
	
	private static Set<Place> retrievePlacesEqualByInput (AcceptingPetriNet net, Place place) {
		Set<Transition> inTransitions1 = collectInTransitions(net, place);
		Set<Place> equalPlaces = new HashSet<Place>();
		for(Place eqPlace : net.getNet().getPlaces()) {
			if(!place.equals(eqPlace)) {
				Set<Transition> inTransitions2 = collectInTransitions(net, eqPlace);
				if ((inTransitions1.containsAll(inTransitions2)) && (inTransitions2.containsAll(inTransitions1))) {
					equalPlaces.add(eqPlace);
				}
			}
		}
		return equalPlaces;
	}
	
	 private static Set<Transition> collectOutTransitions(AcceptingPetriNet net, Place place) {
	        Set<Transition> outTransitions = new HashSet<Transition>();
	        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getNet()
	                .getOutEdges(place);
	        for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : outEdges) {
	        	outTransitions.add((Transition) outEdge.getTarget());
	        }
	        return outTransitions;
	    }
	    
		private static Set<Transition> collectInTransitions(AcceptingPetriNet net, Place place) {
			Set<Transition> inTransitions = new HashSet<Transition>();
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getNet().getInEdges(
					place);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : inEdges) {
				inTransitions.add((Transition) inEdge.getSource());
			}
			return inTransitions;
		}
}
