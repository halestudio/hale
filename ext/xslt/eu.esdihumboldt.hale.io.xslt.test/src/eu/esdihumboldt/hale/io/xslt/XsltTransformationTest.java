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
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Transformation tests based on {@link XsltExport}.
 * 
 * @author Simon Templer
 */
public class XsltTransformationTest extends DefaultTransformationTest {

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testSimpleRename() throws Exception {
		super.testSimpleRename();
	}

	@Ignore
	// XXX XSLT not yet in a state for this to work
	@Override
	@Test
	public void testCardinalityRename() throws Exception {
		super.testCardinalityRename();
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
	public void testSimpleComplex() throws Exception {
		super.testSimpleComplex();
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
				example.getTargetContainerNamespace());
		export.setParameter(XsltExport.PARAM_ROOT_ELEMENT_NAME, example.getTargetContainerName());

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
			source = new StreamSource(sourceDataInput.getInput(), sourceDataInput.getLocation()
					.toString());
		}
		else {
			source = new StreamSource(sourceDataInput.getInput());
		}
		StreamSource transformation = new StreamSource(xsltFile);
		StreamResult out = new StreamResult(targetFile);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer t = factory.newTransformer(transformation);
		t.transform(source, out);
	}

}
