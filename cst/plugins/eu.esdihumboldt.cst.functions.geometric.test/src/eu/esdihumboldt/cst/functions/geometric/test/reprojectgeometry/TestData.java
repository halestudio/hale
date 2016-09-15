package eu.esdihumboldt.cst.functions.geometric.test.reprojectgeometry;

import java.net.URI;
import java.net.URISyntaxException;

import eu.esdihumboldt.cst.test.TransformationExampleImpl;

@SuppressWarnings("javadoc")
public class TestData extends TransformationExampleImpl {

	public TestData(TestDataConfiguration configuration) throws URISyntaxException {
		super(toLocalURI(configuration.getSourceSchema()), toLocalURI(configuration
				.getTargetSchema()), toLocalURI(configuration.getAlignment()),
				toLocalURI(configuration.getSourceData()), null, null, null);
	}

	private static URI toLocalURI(String location) throws URISyntaxException {
		return TestData.class.getResource(location).toURI();
	}

}
