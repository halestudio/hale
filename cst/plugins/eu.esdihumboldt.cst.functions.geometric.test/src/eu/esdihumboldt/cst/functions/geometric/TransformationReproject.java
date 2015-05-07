package eu.esdihumboldt.cst.functions.geometric;

import java.net.URI;
import java.net.URISyntaxException;

import eu.esdihumboldt.cst.test.TransformationExampleImpl;

public class TransformationReproject extends TransformationExampleImpl {

	public TransformationReproject(TestDataConfiguration configuration) throws URISyntaxException {
		super(toLocalURI(configuration.getSourceSchema()),
				toLocalURI(configuration.getTargetSchema()),
				toLocalURI(configuration.getAlignment()),
				toLocalURI(configuration.getSourceData()), null, null, null);
	}

	private static URI toLocalURI(String location) throws URISyntaxException {
		return TransformationReproject.class.getResource(location).toURI();
	}

}
