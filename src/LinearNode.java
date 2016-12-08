/*
 * LinearNode.java
 * 
 * Copyright (c) 2002, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June1991.
 * 
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 * 
 * borislav popov 02/2002
 */


import gate.creole.gazetteer.InvalidFormatException;

/**
 * Linear node specifies an entry of the type :
 list:majorType:minorType:language:annotationType
 */
public class LinearNode {

	/**
	 * the gazetteer list from the node
	 */
	private String list;

	private String minorType;

	private String majorType;

	private String language;

	private String annotationType;

	/**
	 * Constructor
	 *
	 * @param list name of the list
	 * @param minor minorType type
	 * @param major majorType type
	 * @param language language feature
	 * @param annotationType the annotation type that should be used for
	 * annotating mentions of entries from the list
	 */
	public LinearNode(String list, String minor, String major, String language,
			String annotationType) {
		this.list = list;
		this.minorType = minor;
		this.majorType = major;
		this.language = language;
		this.annotationType = annotationType;
	}

	/**
	 * Constructs a linear node given its elements
	 *
	 * @param aList the gazetteer list file name
	 * @param aMajor the majorType type
	 * @param aMinor the minorType type
	 * @param aLanguage the language(s)
	 */
	public LinearNode(String aList, String aMajor, String aMinor, String aLanguage) {
		this(aLanguage, aMajor, aMinor, aLanguage, null);
	} // LinearNode construct

	/**
	 * Parses and create a linear node from a string
	 *
	 * @param node the linear node to be parsed
	 * @throws InvalidFormatException
	 */
	public LinearNode(String node) throws InvalidFormatException {
		int firstColon = node.indexOf(':');
		int secondColon = node.indexOf(':', firstColon + 1);
		int thirdColon = node.indexOf(':', secondColon + 1);
		int fourthColon = node.indexOf(':', thirdColon + 1);
		annotationType = Constants.LOOKUP; // default value
		// must be lookup
		// for backword
		// compatibility
		if (firstColon == -1) {
			throw new InvalidFormatException("", "Line: " + node);
		}
		list = node.substring(0, firstColon);
		if (secondColon == -1) {
			majorType = node.substring(firstColon + 1);
			minorType = null;
			language = null;
		} else {
			majorType = node.substring(firstColon + 1, secondColon);
			if (thirdColon == -1) {
				minorType = node.substring(secondColon + 1);
				language = null;
			} else {
				minorType = node.substring(secondColon + 1, thirdColon);
				if (fourthColon == -1) {
					language = node.substring(thirdColon + 1);
					annotationType = Constants.LOOKUP;
				} else {
					language = node.substring(thirdColon + 1, fourthColon);
					annotationType = node.substring(fourthColon + 1);
				}
			}
		}
	}

	/**
	 * Get the gazetteer list filename from the node
	 * @return the gazetteer list filename
	 */
	public String getList() {
		return list;
	}

	/**
	 * Sets the gazetteer list filename for the node
	 * @param aList the gazetteer list filename
	 */
	public void setList(String aList) {
		list = aList;
	}

	/**
	 * Gets the language of the node (the language is optional)
	 * @return the language of the node
	 */
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String aLanguage) {
		language = aLanguage;
	}

	public String getMinorType() {
		return minorType;
	}

	public void setMinorType(String minorType) {
		this.minorType = minorType;
	}

	public String getMajorType() {
		return majorType;
	}

	public void setMajorType(String majorType) {
		this.majorType = majorType;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public String toString() {
		String result = list + ':' + majorType;
		if ((null != minorType) && (0 != minorType.length())) {
			result += ':' + minorType;
		}
		if ((null != language) && (0 != language.length())) {
			if ((null == minorType) || (0 == minorType.length())) {
				result += ':';
			}
			result += ':' + language;
		}
		// if the annotation type is Lookup we don't really need to add
		// it to the definition file
		if ((null != annotationType) && (0 != annotationType.length())
				&& !annotationType.equals(Constants.LOOKUP)) {
			if ((null == minorType) || (0 == minorType.length())) {
				result += ':';
			}
			if (language == null || (0 == language.length())) {
				result += ':';
			}
			result += ':' + annotationType;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result
				= prime * result
				+ ((annotationType == null) ? 0 : annotationType.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((majorType == null) ? 0 : majorType.hashCode());
		result = prime * result + ((minorType == null) ? 0 : minorType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LinearNode other = (LinearNode) obj;
		if (annotationType == null) {
			if (other.annotationType != null) {
				return false;
			}
		} else if (!annotationType.equals(other.annotationType)) {
			return false;
		}
		if (language == null) {
			if (other.language != null) {
				return false;
			}
		} else if (!language.equals(other.language)) {
			return false;
		}
		if (list == null) {
			if (other.list != null) {
				return false;
			}
		} else if (!list.equals(other.list)) {
			return false;
		}
		if (majorType == null) {
			if (other.majorType != null) {
				return false;
			}
		} else if (!majorType.equals(other.majorType)) {
			return false;
		}
		if (minorType == null) {
			if (other.minorType != null) {
				return false;
			}
		} else if (!minorType.equals(other.minorType)) {
			return false;
		}
		return true;
	}
}