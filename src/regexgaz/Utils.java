package regexgaz;

/** 
 * 
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 
 */

import gate.Gate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Utils {
	

	/**
	 * Returns a string representation of the deterministic FSM graph using GML.
	 */
	public static String getFSMgml(Set<FSMState> fsmStates) {
		String res = "graph[ \ndirected 1\n";
		StringBuilder nodes = new StringBuilder(Gate.STRINGBUFFER_SIZE);
		StringBuilder edges = new StringBuilder(Gate.STRINGBUFFER_SIZE);
		Iterator<FSMState> fsmStatesIter = fsmStates.iterator();
		while (fsmStatesIter.hasNext()) {
			FSMState currentState = fsmStatesIter.next();
			int stateIndex = currentState.getIndex();
			nodes.append("node[ id ");
			nodes.append(stateIndex);
			nodes.append(" label \"");
			nodes.append(stateIndex);
			if (currentState.isFinal()) {
				nodes.append(",F\\n");
				nodes.append(currentState.getLookupSet());
			}
			nodes.append("\"  ]\n");
			edges.append(currentState.getEdgesGML());
		}
		res += nodes.toString() + edges.toString() + "]\n";
		return res;
	} 
	
	

	public static void printFsmStates(List<FSMState> states) {
		StringBuilder sb = new StringBuilder();
		//List<FSMState> states = new ArrayList(regexGazetteer.fsmStates);
		Collections.sort(states, 
				(FSMState state1, FSMState state2) -> {
					int res = new Integer(state1.getIndex()).compareTo(state2.getIndex());
					return res;
				}
		);
		for (FSMState state : states) {
			sb.append("\nSTATE : " + state.getIndex() + "\t");
			sb.append("TRANSITIONS : " + (state.isFinal() ? "Final " : state.getEdgesGML()));
		}
		System.out.println(sb);
	}

}
