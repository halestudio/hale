package eu.esdihumboldt.hale.eap2uml.system;

import java.io.File;
import static org.junit.Assert.*;

import org.junit.Test;

import eu.esdihumboldt.hale.eap2uml.eapconverter.EapToUml2;
import eu.esdihumboldt.hale.eap2uml.uml2importer.Importer;

public class SystemTest {

	@Test
	public void TestSystem()
	{
		File Eap = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//Humboldt.eap");
		File model = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.uml");
		String pkg = "INSPIRE Consolidated UML Model/Themes/Cadastral Parcels/Feature types";
		boolean result = EapToUml2.Convert(Eap, model, pkg);
		assertTrue(result);
		
		File xmlfile = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.xmi");
		model.renameTo(xmlfile);
		result = Importer.load(xmlfile);
		assertTrue(result);
	}
}
