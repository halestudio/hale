/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.schematron;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.io.schematron.validator.SchematronInstanceValidator;

/**
 * Tests for the {@link SchematronInstanceValidator}.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class SchematronInstanceValidatorTest {

	@Test
	public void testValid() throws Exception {
		testValidate("GML321-schematron.xml", "inspire-hy-p.gml", true);
	}

	@Test
	public void testDimensionRule() throws Exception {
		testValidate("GML321-schematron.xml", "inspire-hy-p-dimension.gml", false);
	}

	@Test
	public void testDuplicateRule() throws Exception {
		// no rule regarding duplicate IDs -> so expect success
		testValidate("GML321-schematron.xml", "inspire-hy-p-ids.gml", true);
	}

	private void testValidate(String schematronResource, String xmlResource, boolean expectSuccess)
			throws Exception {
		SchematronInstanceValidator validator = new SchematronInstanceValidator();

		validator.setSource(new ResourceInputSupplier(getClass(), xmlResource));
		validator.setSchematronLocation(getClass().getResource(schematronResource).toURI());
		IOReport report = validator.execute(null);

		assertEquals("Unexpected report result", expectSuccess, report.isSuccess());
	}

}
