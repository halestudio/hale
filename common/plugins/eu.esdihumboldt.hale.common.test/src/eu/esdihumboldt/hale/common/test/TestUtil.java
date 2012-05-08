package eu.esdihumboldt.hale.common.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import eu.esdihumboldt.hale.common.align.io.impl.DefaultAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

@SuppressWarnings("restriction")
public class TestUtil {
	/**
	 * Loads the specified XML Schema.
	 * 
	 * @param location the URI specifying the location of the schema
	 * @return the loaded schema
	 * @throws IOProviderConfigurationException 
	 * @throws IOException 
	 */
	public static Schema loadSchema(URI location) throws IOProviderConfigurationException, IOException {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);
		
		reader.validate();
		IOReport report = reader.execute(null);
		
		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());
		
		return reader.getSchema();
	}

	/**
	 * Loads the specified alignment.
	 * 
	 * @param location the URI specifying the location of the alignment
	 * @param sourceTypes the source type index
	 * @param targetTypes the target type index
	 * @return the loaded alignment
	 * @throws IOException 
	 * @throws MappingException 
	 * @throws ValidationException 
	 * @throws MarshalException 
	 */
	public static Alignment loadAlignment(final URI location, Schema sourceTypes, Schema targetTypes) throws MarshalException, ValidationException, MappingException, IOException {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		IOReporter report = new DefaultIOReporter(new Locatable() {
			@Override
			public URI getLocation() {
				return location;
			}
		}, "Load alignment", true);
		Alignment alignment = DefaultAlignmentIO.load(input.getInput(), report, sourceTypes, targetTypes);

		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return alignment;
	}

	/**
	 * Loads an instance collection from the specified XML file with the given source types.
	 * 
	 * @param location the URI specifying the location of the xml instance file
	 * @param sourceTypes the source type index
	 * @return the loaded instance collection
	 * @throws IOException 
	 * @throws IOProviderConfigurationException 
	 */
	public static InstanceCollection loadInstances(URI location, Schema types) throws IOProviderConfigurationException, IOException {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		XmlInstanceReader instanceReader = new XmlInstanceReader();
		instanceReader.setSource(input);
		instanceReader.setSourceSchema(types);
		IOReport report = instanceReader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return instanceReader.getInstances();
	}
}
