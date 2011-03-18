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
		File Eap = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//Humboldt.eap"); //$NON-NLS-1$
		File model = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.uml"); //$NON-NLS-1$
		String pkg = "INSPIRE Consolidated UML Model/Themes/Cadastral Parcels/Feature types"; //$NON-NLS-1$
		boolean result = EapToUml2.Convert(Eap, model, pkg);
		assertTrue(result);
		
		File xmlfile = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.xmi"); //$NON-NLS-1$
		model.renameTo(xmlfile);
		result = Importer.load(xmlfile);
		assertTrue(result);
	}
}
