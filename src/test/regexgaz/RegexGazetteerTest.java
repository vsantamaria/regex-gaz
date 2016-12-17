package test.regexgaz;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import static gate.Utils.inDocumentOrder;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import regexgaz.RegexGazetteer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import static org.junit.Assert.*;
import static gate.Utils.stringFor;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author vsantamaria
 *
 */
public class RegexGazetteerTest {

	@Rule
	public TestName testName = new TestName();

	RegexGazetteer regexGaz;
	String LISTS_PATH = "resources/lists.def";

	
	@Before
	public void setUp() throws GateException, MalformedURLException {
		
		System.out.println("\n===============================\n"
				+ testName.getMethodName()
				+"\n===============================\n");
		Gate.init();
		regexGaz = new RegexGazetteer();
		URL url = getClass().getResource(LISTS_PATH);
		regexGaz.setListsURL(url);
		regexGaz.setGazetteerFeatureSeparator("@");
		regexGaz.init();

	}

	@Test
	public void testRegexEntries() throws ExecutionException, ResourceInstantiationException {
		String text = "HOLA! Buen día Buenos días";
		Document doc = Factory.newDocument(text);
		regexGaz.setLongestMatchOnly(false);
		regexGaz.setAddEntryFeature(Boolean.TRUE);
		regexGaz.setDocument(doc);
		regexGaz.execute();
		List<Annotation> as = inDocumentOrder(doc.getAnnotations());

		//test that the matched string is not equal to the corresponding entry in the gazetteer
		System.out.println("Text : " + text + "\n");
		for (Annotation a : as) {
			String gazEntry = (String) a.getFeatures().get("gazEntry");
			String matchedString = stringFor(doc, a);
			System.out.println(matchedString + " ===== Matched with entry : " + gazEntry);
			assertNotEquals(matchedString, gazEntry);
		}
	}

	@Test
	public void testLongestMatchOnly() throws ResourceInstantiationException, ExecutionException {
		String text = "Buenos días amigo hola Buenos días amiga";
		System.out.println("Text : " + text);
		Document doc = Factory.newDocument(text);
		regexGaz.setDocument(doc);
		regexGaz.setLongestMatchOnly(true);
		regexGaz.execute();
		AnnotationSet as1 = doc.getAnnotations();
		System.out.println("\nMATCHES WITH LongestMatchOnly SET TO TRUE");
		for (Annotation a : as1) {
			System.out.println(stringFor(doc, a));
		}
		assertEquals(as1.size(),3);
		doc.getAnnotations().clear();
		regexGaz.cleanup();
		regexGaz.setLongestMatchOnly(false);
		regexGaz.execute();
		
		AnnotationSet as2 = doc.getAnnotations();
		System.out.println("\nMATCHES WITH LongestMatchOnly SET TO FALSE");
		for (Annotation a : inDocumentOrder(as2)) {
			System.out.println(stringFor(doc, a));
		}
		assertEquals(as2.size(),5);
	}
	
	@Test
	public void testAddStringFeature() throws ResourceInstantiationException, ExecutionException {
		String text = "madrid";
		Document doc = Factory.newDocument(text);
		regexGaz.setDocument(doc);
		System.out.println("Text : " + text);
		changeAddStringFeat(doc);
		changeAddStringFeat(doc);
	}

	@Test
	public void testAddEntryFeature() throws ResourceInstantiationException, ExecutionException {
		String text = "madrid";
		Document doc = Factory.newDocument(text);
		regexGaz.setDocument(doc);
		System.out.println("Text : " + text);
		changeAddEntryFeat(doc);
		changeAddEntryFeat(doc);
	}

	@Test
	public void testAnnotationType() throws ResourceInstantiationException, ExecutionException {
		String text = "madrid hola";
		Document doc = Factory.newDocument(text);
		regexGaz.setDocument(doc);
		regexGaz.execute();
		AnnotationSet as = doc.getAnnotations();
		for (Annotation a : as){
			System.out.println("Annotation Type for '"+ stringFor(doc,a) +"' : " + a.getType());
		}
		assertEquals(as.getAllTypes(), new HashSet<String>(Arrays.asList("Lookup", "City")));
		
	}

	private void changeAddStringFeat(Document doc) throws ExecutionException {
		doc.getAnnotations().clear();
		regexGaz.cleanup();
		regexGaz.setAddStringFeature(true);
		regexGaz.execute();
		System.out.println("\nAddStringFeature set to TRUE");
		AnnotationSet as = doc.getAnnotations();
		for (Annotation a : as ){
			System.out.println("Features " + a.getFeatures());
			assertNotNull(a.getFeatures().get("string"));
		}
		doc.getAnnotations().clear();
		regexGaz.cleanup();
		regexGaz.setAddStringFeature(false);
		regexGaz.execute();
		
		AnnotationSet as2 = doc.getAnnotations();
		System.out.println("\nAddStringFeature set to FALSE");
		for (Annotation a : as2 ){
			System.out.println("Features " + a.getFeatures());
			assertNull(a.getFeatures().get("string"));
		}
	}
	
	private void changeAddEntryFeat(Document doc) throws ExecutionException {
		doc.getAnnotations().clear();
		regexGaz.cleanup();
		regexGaz.setAddEntryFeature(true);
		regexGaz.execute();
		System.out.println("\nAddEntryFeature set to TRUE");
		AnnotationSet as = doc.getAnnotations();
		for (Annotation a : as ){
			System.out.println("Features " + a.getFeatures());
			assertNotNull(a.getFeatures().get("gazEntry"));
		}
		doc.getAnnotations().clear();
		regexGaz.cleanup();
		regexGaz.setAddEntryFeature(false);
		regexGaz.execute();
		
		AnnotationSet as2 = doc.getAnnotations();
		System.out.println("\nAddEntryFeature set to FALSE");
		for (Annotation a : as2 ){
			System.out.println("Features " + a.getFeatures());
			assertNull(a.getFeatures().get("gazEntry"));
		}
	}
}
