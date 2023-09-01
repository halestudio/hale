package eu.esdihumboldt.hale.io.xls.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.content.IContentType;
import org.junit.Test;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.xls.reader.XLSInstanceReader;
import eu.esdihumboldt.hale.io.xls.reader.XLSSchemaReader;
import eu.esdihumboldt.hale.io.xls.test.writer.XLSInstanceWriterTestUtil;
import eu.esdihumboldt.hale.io.xls.writer.XLSInstanceWriter;
import junit.framework.TestCase;

/**
 * Tests for reading and writing instances in XLS file format
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceIOTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		TestUtil.startConversionService();
	}

	/**
	 * Exports the instances created by
	 * {@link XLSInstanceWriterTestExamples#createInstanceCollection} into a
	 * temporary XLS file by executing {@link XLSInstanceWriter#execute}.
	 * Afterwards, the schema is read by {@link XLSSchemaReader} and the
	 * instances are loaded by {@link XLSInstanceReader}. Each of the imported
	 * instances are compared with the original instances. In addtion, a
	 * different set of instances is compared with the imported instances.
	 */
	@Test
	public void test() {
		// set instances to xls instance writer
		XLSInstanceWriter writer = new XLSInstanceWriter();
		InstanceCollection instances = XLSInstanceWriterTestExamples.createInstanceCollection();
		IContentType contentType = HalePlatform.getContentTypeManager()
				.getContentType("eu.esdihumboldt.hale.io.xls.xls");
		writer.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(false));
		writer.setParameter(InstanceTableIOConstants.USE_SCHEMA, Value.of(true));
		writer.setParameter(InstanceTableIOConstants.EXPORT_TYPE, Value.of("ItemType"));

		File tempDir = Files.createTempDir();
		File tempFile = new File(tempDir, "data.xls");
		writer.setInstances(instances);
		try {
			Schema schema = XLSInstanceWriterTestUtil.createExampleSchema();
			DefaultSchemaSpace ss = new DefaultSchemaSpace();
			ss.addSchema(schema);
			writer.setTargetSchema(ss);

			// write instances to a temporary XLS file
			writer.setTarget(new FileIOSupplier(tempFile));
			writer.setContentType(contentType);
			IOReport report = writer.execute(null);

			assertTrue(report.isSuccess());

		} catch (IOProviderConfigurationException | IOException e) {
			fail("Execution of xls instance writer failed.");
		}

		// read the schema from the temporary XLS file
		XLSSchemaReader schemaReader = new XLSSchemaReader();
		schemaReader.setContentType(contentType);
		schemaReader.setSource(new FileIOSupplier(tempFile));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("ItemType"));
		schemaReader.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES,
				Value.of(false));
		schemaReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(0));
		try {
			IOReport report = schemaReader.execute(null);
			assertTrue(report.isSuccess());
		} catch (IOProviderConfigurationException | IOException e1) {
			fail("Execution of schema reader failed.");
		}
		Schema schema = schemaReader.getSchema();

		// read the instances from the temporary XLS file - test SKIP_N_LINES as
		// integer
		XLSInstanceReader reader = new XLSInstanceReader();
		reader.setSourceSchema(schema);
		reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
		reader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("ItemType"));
		reader.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(false));
		// read sheet with index 0 since there is only one sheet
		reader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(0));
		reader.setContentType(contentType);
		reader.setSource(new FileIOSupplier(tempFile));
		try {
			IOReport report = reader.execute(null);
			assertTrue(report.isSuccess());
		} catch (IOProviderConfigurationException | IOException e) {
			fail("Execution of xls instance reader failed.");
		}
		// compare size of instance collection
		InstanceCollection inst = reader.getInstances();
		assertEquals(4, inst.size());

		// read the instances from the temporary XLS file - test SKIP_N_LINES as
		// boolean (backward compatibility)
		reader = new XLSInstanceReader();
		reader.setSourceSchema(schema);
		reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(true));
		reader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("ItemType"));
		reader.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES, Value.of(false));
		// read sheet with index 0 since there is only one sheet
		reader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(0));
		reader.setContentType(contentType);
		reader.setSource(new FileIOSupplier(tempFile));
		try {
			IOReport report = reader.execute(null);
			assertTrue(report.isSuccess());
		} catch (IOProviderConfigurationException | IOException e) {
			fail("Execution of xls instance reader failed.");
		}
		// compare size of instance collection
		inst = reader.getInstances();
		assertEquals(4, inst.size());

		// check if instance collection contains current instance
		Iterator<Instance> instanceIt = inst.iterator();
		while (instanceIt.hasNext()) {
			Instance instance = instanceIt.next();
			assertTrue(contains(instances.iterator(), instance));
		}

		// other instance should be contained in the imported instances
		InstanceCollection falseInstances = XLSInstanceWriterTestExamples
				.createFalseTestInstanceCollection();
		instanceIt = inst.iterator();
		while (instanceIt.hasNext()) {
			Instance instance = instanceIt.next();
			assertFalse(contains(falseInstances.iterator(), instance));
		}

		// delete file and temporary directory
		tempFile.delete();
		tempDir.delete();
	}

	// check if instance is contained in the iterator given by the instance
	// collection
	private boolean contains(Iterator<Instance> instances, Instance instance) {
		while (instances.hasNext()) {
			Instance current = instances.next();
			if (InstanceUtil.instanceEqual(current, instance, false))
				return true;
		}
		return false;
	}

}
