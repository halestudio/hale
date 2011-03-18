package eu.esdihumboldt.hale.eap2uml.uml2importer;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

public class ImporterTest {

	@Test
	public void TestImporter()
	{
		File xmlfile = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.xmi"); //$NON-NLS-1$
		//File xmlfile = new File("adress.xmi");
		boolean result = Importer.load(xmlfile);
		assertTrue(result);
	}
}
