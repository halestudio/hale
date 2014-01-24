package eu.esdihumboldt.hale.io.xls.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.junit.Test;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.xls.reader.XLSInstanceReader;
import eu.esdihumboldt.hale.io.xls.reader.XLSSchemaReader;
import eu.esdihumboldt.hale.io.xls.writer.XLSInstanceWriter;

public class XLSInstanceWriterTest extends TestCase{
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		TestUtil.startConversionService();
	}

	@Test
	public void test() {
		// set instances to xls instance writer
		XLSInstanceWriter writer = new XLSInstanceWriter();
		InstanceCollection instances = XLSInstanceWriterTestExamples.createInstanceCollection();
		IContentType contentType = Platform.getContentTypeManager().getContentType("eu.esdihumboldt.hale.io.xls.xls");
		File tempDir = Files.createTempDir();
		File tempFile = new File(tempDir, "data.xls");
		writer.setInstances(instances);
		try {
			// write instances to temporary xls file
			writer.setTarget(new FileIOSupplier(tempFile));
			writer.setContentType(contentType);
			
			IOReport report = writer.execute(null);
			assertTrue(report.isSuccess());
			
		} catch (IOProviderConfigurationException | IOException e) {
			fail("Execution of xls instance writer failed.");
		}
		
		XLSSchemaReader schemaReader = new XLSSchemaReader();
		schemaReader.setContentType(contentType);
		schemaReader.setSource(new FileIOSupplier(tempFile));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("ItemType"));
		try {
			IOReport report = schemaReader.execute(null);
			assertTrue(report.isSuccess());
		} catch (IOProviderConfigurationException | IOException e1) {
			fail("Execution of schema reader failed.");
		}
		Schema schema = schemaReader.getSchema();
		
		XLSInstanceReader reader = new XLSInstanceReader();
		reader.setSourceSchema(schema);
		reader.setParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE, Value.of(true));
		reader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("ItemType"));
		reader.setContentType(contentType);
		reader.setSource(new FileIOSupplier(tempFile));
		try {
			IOReport report = reader.execute(null);
			assertTrue(report.isSuccess());
		} catch (IOProviderConfigurationException | IOException e) {
			fail("Execution of xls instance reader failed.");
		}
		InstanceCollection inst = reader.getInstances();
		assertEquals(4, inst.size());
		
//		Iterator<Instance> testInstances = instances.iterator();
//		while(testInstances.hasNext()){
//			Instance instance = testInstances.next();
//			assertTrue(contains(inst.iterator(), instance));
//		}
//		
//		InstanceCollection falseInstances = XLSInstanceWriterTestExamples.createFalseTestInstanceCollection();
//		Iterator<Instance> falseTestInstances = falseInstances.iterator();
//		while(falseTestInstances.hasNext()){
//			Instance instance = falseTestInstances.next();
//			assertFalse(contains(inst.iterator(), instance));
//		}
		
		// delete file and temporary directory
		tempFile.delete();
		tempDir.delete();
	}
	
	// check if instance is contained in the iterator given by the instance collection
	private boolean contains(Iterator<Instance> instances, Instance instance){
		while(instances.hasNext()){
			if(compareInstances(instances.next(), instance))
				return true;
		}
		return false;
	}
	
	// currently only definition and properties
	private boolean compareInstances(Instance first, Instance second){
		boolean result = first.getDefinition().getName().equals(second.getDefinition().getName());
		for(QName qname : first.getPropertyNames()){
			try {
			result &= first.getProperty(qname).equals(second.getProperty(qname));
			} catch (NullPointerException e){
				return false;
			}
		}
		return result;
	}

}
