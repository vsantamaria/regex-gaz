/*
 * Verónica Santamaría
 * modified version of GATE's AbstractGazetteer.java
 * Dec 2016
 *
 *
 * AbstractGazetteer.java
 *
 * Copyright (c) 2002, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 * borislav popov 02/2002
 *
 */
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import java.net.URL;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * AbstractGazetteer This class implements the common-for-all methods of the
 * Gazetteer interface
 */
public abstract class AbstractGazetteer extends AbstractLanguageAnalyser implements Gazetteer {

	private static final long serialVersionUID = 223125105523762358L;

	protected Set<GazetteerListener> listeners = new HashSet<>();

	protected String annotationSetName;

	protected String encoding = "UTF-8";

	/**
	 * The value of this property is the URL that will be used for reading the
	 * lists that define this Gazetteer
	 */
	protected URL listsURL;

	/**
	 * Should this gazetteer only match the longest entry in a .lst file. 
	 * The default behavior (when this parameter is set to <tt>true</tt>) 
	 * is to only match the longest entry. Setting this parameter 
	 * to <tt>false</tt> will cause the gazetteer to match all possible entries.
	 */
	protected Boolean longestMatchOnly = true;

	/**
	 * The [lists.def] file
	 */
	protected LinearDefinition definition;

	protected Boolean addStringFeature = true;

	protected Boolean addEntryFeature = true;
	
	/**
	 * Sets the AnnotationSet that will be used at the next run for the newly
	 * produced annotations.
	 */
	@Override
	@RunTime
	@Optional
	@CreoleParameter(comment = "The annotation set to be used for the generated annotations")
	public void setAnnotationSetName(String newAnnotationSetName) {
		annotationSetName = newAnnotationSetName;
	}

	/**
	 * Gets the AnnotationSet that will be used at the next run for the newly
	 * produced annotations.
	 */
	@Override
	public String getAnnotationSetName() {
		return annotationSetName;
	}

	@Override
	@CreoleParameter(comment = "The encoding used for reading the definitions", defaultValue = "UTF-8")
	public void setEncoding(String newEncoding) {
		encoding = newEncoding;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	@CreoleParameter(comment = "The URL to the file with list of lists", suffixes = "def", defaultValue = "resources/lists.def")
	public void setListsURL(java.net.URL newListsURL) {
		listsURL = newListsURL;
	}

	@Override
	public URL getListsURL() {
		return listsURL;
	}

	@RunTime
	@CreoleParameter(comment = "Should this gazetteer only match the longest string starting from any offset?", defaultValue = "true")
	public void setLongestMatchOnly(Boolean longestMatchOnly) {
		this.longestMatchOnly = longestMatchOnly;
	}

	public Boolean getLongestMatchOnly() {
		return longestMatchOnly;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "Should the text matched by this gazetteer be added as a feature ('string') to the Lookup annotation", defaultValue = "true")
	public void setAddStringFeature(Boolean addStringFeature) {
		this.addStringFeature = addStringFeature;
	}

	public Boolean getAddStringFeature() {
		return addStringFeature;
	}
	
	@RunTime
	@Optional
	@CreoleParameter(comment = "Should the gazetteer entry be added as a feature ('gazEntry') to the Lookup annotation", defaultValue = "true")
	public void setAddEntryFeature(Boolean addStringFeature) {
		this.addEntryFeature = addStringFeature;
	}
	
	public Boolean getAddEntryFeature() {
		return addEntryFeature;
	}

	@Override
	public LinearDefinition getLinearDefinition() {
		return definition;
	}

	@Override
	public void reInit() throws ResourceInstantiationException {
		super.reInit();
		fireGazetteerEvent(new GazetteerEvent(this, GazetteerEvent.REINIT));
	}

	@Override
	public void fireGazetteerEvent(GazetteerEvent ge) {
		Iterator<GazetteerListener> li = listeners.iterator();
		while (li.hasNext()) {
			GazetteerListener gl = li.next();
			gl.processGazetteerEvent(ge);
		}
	}

	@Override
	public void addGazetteerListener(GazetteerListener gl) {
		if (null != gl) {
			listeners.add(gl);
		}
	}
}
