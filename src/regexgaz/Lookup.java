package regexgaz;

/*
 * Verónica Santamaría
 * modified version of GATE's Lookup.java
 * Dec 2016
 

 * Lookup.java
 * 
 * Copyright (c) 1995-2012, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Valentin Tablan, 11/07/2000 borislav popov, 05/02/2002
 * 
 * $Id: Lookup.java 17593 2014-03-08 10:03:19Z markagreenwood $
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Used to describe a type of lookup annotations. A lookup is described by a
 * major type a minor type and a list of languages. Added members are :
 * ontologyClass and list. All these values are strings (the list of languages
 * is a string and it is intended to represent a comma separated list). An
 * optional features field stores arbitrary features as part of the lookup
 * annotation. This can be used to set meta-data for a gazetteer entry.
 */
public class Lookup implements Serializable {

	private static final long serialVersionUID = 4107354748136747541L;

	public Map<String, Object> features = null;

	/**
	 * Creates a new Lookup value with the given major and minor types and
	 * languages.
	 *
	 * @param major major type
	 * @param minor minor type
	 * @param theLanguages the languages
	 * @param annotationType the annotation type to use for annotating this
	 * particular lookup.
	 */
	public Lookup(String theList, String major, String minor,
			String theLanguages, String annotationType) {
		majorType = major;
		minorType = minor;
		languages = theLanguages;
		list = theList;
		this.annotationType = annotationType;
	}

	/**
	 * Creates a new Lookup value with the given major and minor types and
	 * languages.
	 *
	 * @param major major type
	 * @param minor minor type
	 * @param theLanguages the languages
	 */
	public Lookup(String theList, String major, String minor, String theLanguages) {
		this(theList, major, minor, theLanguages, Constants.LOOKUP);
	}

	public String majorType;

	public String minorType;

	/**
	 * The languages for this lookup, e.g. "English, French"
	 */
	public String languages;

	/**
	 * the lst represented by this lookup
	 */
	public String list;

	/**
	 * annotation type that should be used to create a lookup
	 */
	public String annotationType;

	/**
	 * Returns a string representation of this lookup in the format This method
	 * is used in equals().
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		boolean hasArbitaryFeatures = false;
		if (null != features) {
			hasArbitaryFeatures = true;
		}
		b.append(majorType);
		b.append(".");
		if (null != minorType) {
			b.append(minorType);
			if (null != languages) {
				b.append(".");
				b.append(languages);
			}
		}
		if (hasArbitaryFeatures) {
			// as the ordering of the featureMap is undefined, create a new list of
			// keys and sort it to ensure the string returned is always the same
			List<String> sortedKeys = new ArrayList<>(features.keySet());
			Collections.sort(sortedKeys);
			for (String key : sortedKeys) {
				b.append("|");
				b.append(key);
				b.append(":");
				b.append(features.get(key));
			}
		}
		return b.toString();
	}

	/**
	 * Two lookups are equal if they have the same string representation (major
	 * type and minor type).
	 *
	 * @param obj
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lookup) {
			return obj.toString().equals(toString());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}