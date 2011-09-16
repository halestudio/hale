package eu.esdihumboldt.hale.eap2uml.eapconverter;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

public class EapToUml2Test {

	@Test
	public void TestEapToUml2()
	{
		File Eap = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//Humboldt.eap"); //$NON-NLS-1$
		File model = new File("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//model.uml"); //$NON-NLS-1$
		String pkg = "INSPIRE Consolidated UML Model/Themes/Cadastral Parcels/Feature types"; //$NON-NLS-1$
		boolean result = EapToUml2.Convert(Eap, model, pkg);
		assertTrue(result);
	}
}
