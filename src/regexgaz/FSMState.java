package regexgaz;

/*
 * Verónica Santamaría
 * modified version of GATE's FSMState.java
 * Dec 2016

 *  FSMState.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 11/07/2000
 *
 *  $Id: FSMState.java 17593 2014-03-08 10:03:19Z markagreenwood $
 */


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Implements a state of the deterministic finite state machine of the
 * gazetteer.
 *
 */
public class FSMState implements Serializable {

	private static final long serialVersionUID = -3339572027660481558L;
	/**
	 * The transition function of this state
	 */
	protected RegexGazetteer.CharMap transitionFunction = new RegexGazetteer.CharMap();

	protected Set<Lookup> lookupSet;

	/**
	 * The unique id of this state. This value is never used by the algorithms
	 * but it can be useful for graphical representations.
	 */
	protected int myIndex;

	/**
	 * Class member used to generate unique ids for the instances.
	 */
	private static int index;
	static {
		index = 0;
	}

	/**
	 * Constructs a new FSMState object and adds it to the list of states of the
	 * {@link RegexGazetteer} provided as owner.
	 *
	 * @param owner a {@link RegexGazetteer} object
	 */
	public FSMState(RegexGazetteer owner) {
		myIndex = index++;
		owner.fsmStates.add(this);
	}

	/**
	 * Adds a new value to the transition function
	 */
	public void put(char chr, FSMState state) {
		transitionFunction.put(chr, state);
	}

	/**
	 * Gets the transition function of this state
	 */
	public FSMState next(char chr) {
		return (FSMState) transitionFunction.get(chr);
	}

	/**
	 * Checks whether this state is a final one
	 */
	public boolean isFinal() {
		if (lookupSet == null) {
			return false;
		}
		return !lookupSet.isEmpty();
	}

	/**
	 * Returns a set of {@link Lookup} objects describing the types of lookups
	 * the phrase for which this state is the final one belongs to
	 */
	public Set<Lookup> getLookupSet() {
		return lookupSet;
	}

	/**
	 * Adds a new lookup description to this state's lookup descriptions set
	 */
	public void addLookup(Lookup lookup) {
		if (lookupSet == null) {
			lookupSet = new HashSet<>(4);
		}

		lookupSet.add(lookup);
	}

	/**
	 * Returns the unique ID of this state
	 */
	public int getIndex() {
		return myIndex;
	}

	
	/**
	 * Returns a GML (Graph Modeling Language) representation of the edges
	 * emerging from this state
	 */
	public String getEdgesGML() {
		String res = "";
		char currentChar;
		FSMState nextState;
		for (int i = 0; i < transitionFunction.itemsKeys.length; i++) {
			currentChar = transitionFunction.itemsKeys[i];
			nextState = next(currentChar);
			res += "\nedge "
					+ "[ source " + myIndex 
					+ " target " + nextState.getIndex() 
					+ " label \"'" + currentChar + "'\" ]\n";
		}
		return res;
	}
}
