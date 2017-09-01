/*
 * Copyright (c) 2012 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.cst.test.DefaultTransformationTest;
import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.cst.test.TransformationExamples;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;
import net.sf.saxon.TransformerFactoryImpl;

/**
 * Transformation tests based on {@link XsltExport}.
 * 
 * @author Simon Templer
 */
public class XsltTransformationTest extends DefaultTransformationTest {

	/*
	 * XXX inline 1 example not yet complete, would need property types being
	 * available as mappable types.
	 */

	/**
	 * Simple test case with inline transformation of property types.
	 * 
	 * @throws Exception if an error occurs during the transformation
	 */
	@Ignore
	// XXX fails due to an issue with context matching
	@Test
	public void testInline2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.INLINE_2));
	}

	@Ignore
	// FIXME structural retype not implemented for XSLT
	@Override
	@Test
	public void testStructuralRetype1() throws Exception {
		super.testStructuralRetype1();
	}

	/**
	 * Simple test case with inline transformation of property types.
	 * 
	 * @throws Exception if an error occurs during the transformation
	 */
	@Test
	public void testInline3() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.INLINE_3));
	}

	/**
	 * Test an XSL transformation using custom XPath expressions for property
	 * transformation.
	 * 
	 * @throws Exception if an error occurs during the transformation
	 */
	@Test
	public void testXPath1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.XSL_XPATH_1));
	}

	@Ignore
	@Override
	@Test
	public void testPriority() throws Exception {
		super.testPriority();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCMMulti1() throws Exception {
		super.testCMMulti1();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCMMulti3() throws Exception {
		super.testCMMulti3();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCMUnion1() throws Exception {
		super.testCMUnion1();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCMUnion2() throws Exception {
		super.testCMUnion2();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testDupeAssign() throws Exception {
		super.testDupeAssign();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testPropertyJoin() throws Exception {
		super.testPropertyJoin();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Test
	@Override
	public void testJoinMultiCond_1() throws Exception {
		super.testJoinMultiCond_1();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testSimpleMerge() throws Exception {
		super.testSimpleMerge();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCardinalityMerge_1() throws Exception {
		super.testCardinalityMerge_1();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCardinalityMerge_2() throws Exception {
		super.testCardinalityMerge_2();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCardinalityMove() throws Exception {
		super.testCardinalityMove();
	}

	@Override
	protected List<Instance> transformData(TransformationExample example) throws Exception {
		// export alignment to XSLT
		XsltExport export = new XsltExport();
		export.setAlignment(example.getAlignment());
		export.setSourceSchema(new DefaultSchemaSpace().addSchema(example.getSourceSchema()));
		export.setTargetSchema(new DefaultSchemaSpace().addSchema(example.getTargetSchema()));

		export.setParameter(XsltExport.PARAM_ROOT_ELEMENT_NAMESPACE,
				Value.of(example.getTargetContainerNamespace()));
		export.setParameter(XsltExport.PARAM_ROOT_ELEMENT_NAME,
				Value.of(example.getTargetContainerName()));

		File tempXsltFile = File.createTempFile("xsltest", ".xsl");
		export.setTarget(new FileIOSupplier(tempXsltFile));
		IOReport res = export.execute(new LogProgressIndicator());
		assertTrue("XSLT export not successful", res.isSuccess());
		assertTrue("Errors during XSLT export", res.getErrors().isEmpty());

		// invoke XSLT on source file to produce target
		File target = File.createTempFile("xsltest", ".xml");
		executeXslt(example.getSourceDataInput(), tempXsltFile, target);

		// load target and return instances
		InstanceCollection instances = TestUtil.loadInstances(target.toURI(),
				example.getTargetSchema());
		List<Instance> list = new ArrayList<Instance>();
		ResourceIterator<Instance> it = instances.iterator();
		try {
			while (it.hasNext()) {
				list.add(it.next());
			}
		} finally {
			it.close();
		}

		// clean up
		tempXsltFile.delete();
		target.delete();

		return list;
	}

	/**
	 * Transform the data based on a XSLT file.
	 * 
	 * @param sourceDataInput the source data input to be transformed
	 * @param xsltFile the XSLT transformation
	 * @param targetFile the target file
	 * @throws IOException if an error occurs reading or writing data
	 * @throws TransformerException if an error in respect to the XSLT
	 *             transformation occurs
	 */
	private void executeXslt(LocatableInputSupplier<? extends InputStream> sourceDataInput,
			File xsltFile, File targetFile) throws IOException, TransformerException {
		StreamSource source;
		if (sourceDataInput.getLocation() != null) {
			source = new StreamSource(sourceDataInput.getInput(),
					sourceDataInput.getLocation().toString());
		}
		else {
			source = new StreamSource(sourceDataInput.getInput());
		}
		StreamSource transformation = new StreamSource(xsltFile);
		StreamResult out = new StreamResult(targetFile);

		TransformerFactory factory = new TransformerFactoryImpl(); // TransformerFactory.newInstance();
		Transformer t = factory.newTransformer(transformation);
		t.transform(source, out);
	}

}
