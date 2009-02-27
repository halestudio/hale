package eu.esdihumboldt.hale.models.impl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class SchemaServiceTest {

	@Test
	public void testLoadSourceSchema() throws URISyntaxException {
		SchemaServiceImpl service = new SchemaServiceImpl();
		URI file = new URI("");
		service.loadSourceSchema(file);
	}

	@Test
	public void testLoadTargetSchema() {
	}

}
