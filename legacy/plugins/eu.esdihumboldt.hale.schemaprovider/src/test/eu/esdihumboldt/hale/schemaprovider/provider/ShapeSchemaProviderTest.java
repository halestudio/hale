package test.eu.esdihumboldt.hale.schemaprovider.provider;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.provider.ShapeSchemaProvider;

/**
 * Test class for the {@link ShapeSchemaProvider}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @since 2.0.0.M2
 */
public class ShapeSchemaProviderTest {

	private static final Logger log = Logger
			.getLogger(ShapeSchemaProviderTest.class);

	/**
	 * test for
	 * {@link ShapeSchemaProvider#loadSchema(URI, eu.esdihumboldt.hale.schemaprovider.ProgressIndicator)}
	 */
	@Test
	public void testLoadSchema() {

		// log.setLevel(Level.INFO);

		ShapeSchemaProvider ssp = new ShapeSchemaProvider();
		Schema result = null;
		try {
			URI uri = ShapeSchemaProviderTest.class.getResource(
					"DEPARTEMENT.SHP").toURI(); //$NON-NLS-1$
			result = ssp.loadSchema(uri, new LogProgressIndicator());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(result.getElements().size() == 2);

		for (SchemaElement se : result.getElements().values()) {
			log.info(se.getDisplayName());
			for (AttributeDefinition ad : se.getType().getAttributes()) {
				log.info(ad.getDisplayName()
						+ ": " //$NON-NLS-1$
						+ ad.getAttributeType().getType(null).getBinding()
								.getSimpleName()
						+ " (" + ad.getAttributeType().getType(null).getRestrictions() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

	}

}
