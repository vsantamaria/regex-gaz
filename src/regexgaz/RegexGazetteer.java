package regexgaz;

/*
 * RegexGazeteer.java
 *
 * Verónica Santamaría 
 * modified version of GATE's DefaultGazetteer.java to process regex entries
 * Dec 2016
 *
 * Copyright (c) 1998-2005, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * Valentin Tablan, 03/07/2000
 * borislav popov 24/03/2002
 *
 * $Id:DefaultGazetteer.java 17806 2014-04-11 09:10:02Z markagreenwood $
 */
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.Utils;
import gate.creole.CustomDuplication;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.util.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import static gate.Utils.addAnn;
import static gate.Utils.stringFor;

/**
 * This component is responsible for doing lists lookup. The implementation is
 * based on finite state machines. The phrases to be recognized should be listed
 * in a set of files, one for each type of occurrences. The gazetteer is build
 * with the information from a file that contains the set of lists (which are
 * files as well) and the associated type for each list. The file defining the
 * set of lists should have the following syntax: each list definition should be
 * written on its own line and should contain:
 * <ol>
 * <li>the file name (required) </li>
 * <li>the major type (required) </li>
 * <li>the minor type (optional)</li>
 * <li>the language(s) (optional) </li>
 * </ol>
 * The elements of each definition are separated by &quot;:&quot;. The following
 * is an example of a valid definition: <br>
 * <code>personmale.lst:person:male:english</code> Each list file named in the
 * lists definition file is just a list containing one entry per line. When this
 * gazetteer will be run over some input text (a Gate document) it will generate
 * annotations of type Lookup having the attributes specified in the definition
 * file.
 */
@CreoleResource(name = "Regex Gazetteer",
		comment = "A list lookup component.",
		icon = "gazetteer"
)
public class RegexGazetteer extends AbstractGazetteer
		implements CustomDuplication {

	private static final long serialVersionUID = -8976141132455436099L;

	protected FSMState initialState;

	protected Set<FSMState> fsmStates;

	protected String gazetteerFeatureSeparator;

	protected Map<LinearNode, GazetteerList> listsByNode;

	/**
	 * Does the actual loading and parsing of the lists. This method must be
	 * called before the gazetteer can be used
	 */
	@Override
	public Resource init() throws ResourceInstantiationException {
		fsmStates = new HashSet<>();
		initialState = new FSMState(this);
		if (listsURL == null) {
			throw new ResourceInstantiationException(
					"No URL provided for gazetteer creation!");
		}
		definition = new LinearDefinition();
		definition.setSeparator(Strings.unescape(gazetteerFeatureSeparator));
		definition.setURL(listsURL);
		definition.load();
		int linesCnt = definition.size();
		listsByNode = definition.loadLists();
		Iterator<LinearNode> inodes = definition.iterator();

		int nodeIdx = 0;
		LinearNode node;
		while (inodes.hasNext()) {
			node = inodes.next();
			fireStatusChanged("Reading " + node.toString());
			fireProgressChanged(++nodeIdx * 100 / linesCnt);
			readList(node);
		}
		fireProcessFinished();
		return this;
	}

	/**
	 * Reads one lst file (~node)
	 *
	 * @param node the node
	 */
	protected void readList(LinearNode node)
			throws ResourceInstantiationException {
		String listName, majorType, minorType, languages, annotationType;
		if (null == node) {
			throw new ResourceInstantiationException(" LinearNode node is null ");
		}

		listName = node.getList();
		majorType = node.getMajorType();
		minorType = node.getMinorType();
		languages = node.getLanguage();
		annotationType = node.getAnnotationType();
		GazetteerList gazList = listsByNode.get(node);
		if (null == gazList) {
			throw new ResourceInstantiationException("gazetteer list not found by node");
		}

		Iterator<GazetteerNode> iline = gazList.iterator();

		Lookup defaultLookup = new Lookup(listName, majorType, minorType, languages, annotationType);
		defaultLookup.list = node.getList();
		Lookup lookup;
		String entry;
		while (iline.hasNext()) {
			GazetteerNode gazNode = iline.next();
			entry = gazNode.getEntry();

			Map<String, Object> entryFeatures = gazNode.getFeatureMap();
			if (entryFeatures == null) {
				//lookup without additional features
				lookup = defaultLookup;
			} else {
				// lookup with additional features
				lookup = new Lookup(listName, majorType, minorType, languages, annotationType);
				lookup.list = node.getList();
				lookup.features = entryFeatures;
			}
			addEntry(entry, lookup);
		}
	}

	/**
	 * Adds one entry to the list of phrases recognized by this gazetteer
	 *
	 * @param entry the phrase to be added
	 * @param lookup the description of the annotation to be added when this
	 * phrase is recognized
	 */
	public void addEntry(String entry, Lookup lookup) {
		char currentChar;
		FSMState currentState = initialState;
		FSMState nextState;
		boolean isSpace;

		for (int i = 0; i < entry.length(); i++) {
			currentChar = entry.charAt(i);
			isSpace = Character.isSpaceChar(currentChar) || Character.isWhitespace(currentChar);
			if (isSpace) {
				currentChar = ' ';
			}
			nextState = currentState.next(currentChar);
			if (nextState == null) {
				nextState = new FSMState(this);
				currentState.put(currentChar, nextState);
				if (isSpace) {
					nextState.put(' ', nextState);
				}
			}
			currentState = nextState;
		}
		currentState.addLookup(lookup);
	}

	@Override
	public void execute() throws ExecutionException {
		interrupted = false;
		AnnotationSet annotationSet;
		if (document == null) {
			throw new ExecutionException("No document to process!");
		}
		if (annotationSetName == null || "".equals(annotationSetName)) {
			annotationSet = document.getAnnotations();
		} else {
			annotationSet = document.getAnnotations(annotationSetName);
		}
		fireStatusChanged("Performing look-up in " + document.getName() + "...");

		String content = document.getContent().toString();
		Set<LinearNode> nodes = listsByNode.keySet();

		for (LinearNode node : nodes) {
			searchMatches(node, content, annotationSet);
		}

		fireProcessFinished();
		fireStatusChanged("Look-up complete!");
	}

	private void searchMatches(LinearNode node, String content, AnnotationSet annotationSet) {
		GazetteerList gazList = listsByNode.get(node);
		for (GazetteerNode gazNode : gazList.getEntries()) {
			String entry = gazNode.getEntry();
			//if first or last character is a letter add \b to entry to match whole words only
			if (entry.substring(0, 1).matches("\\p{L}")) {
				entry = "\\b" + entry;
			}
			if (entry.substring(entry.length() - 1).matches("\\p{L}")) {
				entry = entry + "\\b";
			}
			Pattern pattern = Pattern.compile(entry, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				
				Long matchStart = (long)matcher.start();
				Long matchEnd = (long)matcher.end();
				
				if (longestMatchOnly) {
					String type = node.getAnnotationType();
					//if there are annotations of the same type covering the matched region, continue.
					AnnotationSet coveringAnnots = annotationSet.getCovering(type, matchStart, matchEnd);
					if (!coveringAnnots.isEmpty()){ 
						continue;
					}
					//remove annotations of the same type contained in the matched region
					AnnotationSet containedAnnots = annotationSet.get(type, matchStart, matchEnd);
					annotationSet.removeAll(containedAnnots);
				}
				Lookup lookup = createLookup(gazNode, node);
				addLookupsToDoc(lookup, matchStart, matchEnd, annotationSet);
			}
		}
	}
	
		private Lookup createLookup(GazetteerNode gazNode, LinearNode node) {
		Lookup lookup;
		if (node.getAnnotationType() != null) {
			lookup = new Lookup(node.getList(), node.getMajorType(),
					node.getMinorType(), node.getLanguage(), node.getAnnotationType());
		} else {
			lookup = new Lookup(node.getList(), node.getMajorType(), node.getMinorType(), node.getLanguage());
		}
		if (addEntryFeature) {
			if (gazNode.getFeatureMap() == null) {
				Map<String, Object> gazFeat = new HashMap<>();
				gazNode.setFeatureMap(gazFeat);
			}
			gazNode.getFeatureMap().put(Constants.LOOKUP_ENTRY, gazNode.getEntry());
		}
		if (gazNode.getFeatureMap() != null) {
			lookup.features = gazNode.getFeatureMap();
		}
		return lookup;
	}

	/**
	 * Add the Lookup annotations to the document
	 *
	 * @param lookup the Lookup to be added.
	 * @param startPos the start of the matched text region.
	 * @param endPos the end of the matched text region.
	 * @param annotSet the annotation set where the new annotations should be
	 * added.
	 */
	protected void addLookupsToDoc(Lookup lookup, long startPos, long endPos, AnnotationSet annotSet) {
		FeatureMap fm = Factory.newFeatureMap();
		fm.put(Constants.LOOKUP_MAJOR, lookup.majorType);
		if (null != lookup.minorType) {
			fm.put(Constants.LOOKUP_MINOR, lookup.minorType);
		}
		if (null != lookup.languages) {
			fm.put(Constants.LOOKUP_LANG, lookup.languages);
		}
		if (null != lookup.features) {
			fm.putAll(lookup.features);
		}
		if (addStringFeature) {
			fm.put(Constants.LOOKUP_STRING, stringFor(document, startPos, endPos));
		}
		addAnn(annotSet, startPos, endPos, lookup.annotationType, fm);
	}

	/**
	 * Use a {@link SharedDefaultGazetteer} to duplicate this gazetteer by
	 * sharing the internal FSM rather than re-loading the lists.
	 */
	@Override
	public Resource duplicate(Factory.DuplicationContext ctx)
			throws ResourceInstantiationException {
		return Factory.createResource(SharedDefaultGazetteer.class.getName(),
				Utils.featureMap(SharedDefaultGazetteer.BOOTSTRAP_GAZ_NAME,
						this),
				Factory.duplicate(this.getFeatures(), ctx),
				this.getName());
	}

	/**
	 * class implementing the map using binary search by char as key to retrieve
	 * the corresponding object.
	 */
	public static class CharMap implements Serializable {

		private static final long serialVersionUID = 4192829422957074447L;

		char[] itemsKeys = null;
		Object[] itemsObjs = null;

		/**
		 * resize the containers by one, leaving empty element at position
		 * 'index'
		 */
		void resize(int index) {
			int newsz = itemsKeys.length + 1;
			char[] tempKeys = new char[newsz];
			Object[] tempObjs = new Object[newsz];
			System.arraycopy(itemsKeys, 0, tempKeys, 0, index);
			System.arraycopy(itemsObjs, 0, tempObjs, 0, index);
			System.arraycopy(itemsKeys, index, tempKeys, index + 1, newsz - index - 1);
			System.arraycopy(itemsObjs, index, tempObjs, index + 1, newsz - index - 1);

			itemsKeys = tempKeys;
			itemsObjs = tempObjs;
		}

		Object get(char key) {
			if (itemsKeys == null) {
				return null;
			}
			int index = Arrays.binarySearch(itemsKeys, key);
			if (index < 0) {
				return null;
			}
			return itemsObjs[index];
		}

		Object put(char key, Object value) {
			if (itemsKeys == null) {
				itemsKeys = new char[1];
				itemsKeys[0] = key;
				itemsObjs = new Object[1];
				itemsObjs[0] = value;
				return value;
			}// if first time
			int index = Arrays.binarySearch(itemsKeys, key);
			if (index < 0) {
				index = ~index;
				resize(index);
				itemsKeys[index] = key;
				itemsObjs[index] = value;
			}
			return itemsObjs[index];
		}
	}// class CharMap

	public String getGazetteerFeatureSeparator() {
		return gazetteerFeatureSeparator;
	}

	@Optional
	@CreoleParameter(defaultValue = ":")
	public void setGazetteerFeatureSeparator(String gazetteerFeatureSeparator) {
		this.gazetteerFeatureSeparator = gazetteerFeatureSeparator;
	}
}
