package test.eu.esdihumboldt.hale.schemaprovider.provider.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import eu.esdihumboldt.hale.schemaprovider.LogProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.provider.ShapeSchemaProvider;

public class ShapeSchemaProviderTest {
	
	private static final Logger log = Logger.getLogger(ShapeSchemaProviderTest.class);

	@Test
	public void testLoadSchema() {
		
		log.setLevel(Level.INFO);
		
		ShapeSchemaProvider ssp = new ShapeSchemaProvider();
		Schema result = null;
		try {
			result = ssp.loadSchema(new URI(
					"file:///C:/workspaces/demodata/departements-shp/DEPARTEMENT.SHP"), 
					new LogProgressIndicator());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
		
		assertTrue(result.getElements().size() == 1);
		
		for (SchemaElement se : result.getElements().values()) {
			log.info(se.getDisplayName());
			for (AttributeDefinition ad : se.getType().getAttributes()) {
				log.info(ad.getDisplayName() + ": " 
						+ ad.getAttributeType().getType().getBinding().getSimpleName()
						+ " (" + ad.getAttributeType().getType().getRestrictions() + ")");
			}
		}
		
	}

}
