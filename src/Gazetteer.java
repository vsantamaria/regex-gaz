
/*
 * Verónica Santamaría
 * modified version of GATE's Gazetteer.java
 * Dec 2016


 * Gazetteer.java
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
import gate.LanguageAnalyser;
import gate.ProcessingResource;
import java.net.URL;

public interface Gazetteer extends LanguageAnalyser, ProcessingResource {

	
	public void setAnnotationSetName(String newAnnotationSetName);
	
	public String getAnnotationSetName();

	public void setEncoding(String newEncoding);

	public String getEncoding();

	public URL getListsURL();
	
	public void setListsURL(URL newListsURL);

	public LinearDefinition getLinearDefinition();

	public void fireGazetteerEvent(GazetteerEvent ge);

	public void addGazetteerListener(GazetteerListener gl);

}
