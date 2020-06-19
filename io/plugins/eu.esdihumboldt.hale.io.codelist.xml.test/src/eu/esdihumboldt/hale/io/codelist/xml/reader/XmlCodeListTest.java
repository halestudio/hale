/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.codelist.xml.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;

/**
 * Tests for {@link XmlCodeList}.
 * 
 * @author Simon Templer
 */
public class XmlCodeListTest {

	/**
	 * Test reading a GML Dictionary code list w/o entry names.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadNoNames() throws Exception {
		CodeList codeList = readCodeList(
				getClass().getResource("/resources/RoadFeatureSourceCode.xml").toURI());

		assertEquals(
				"Specifies the available list of enumerations for the various types source concerning road features",
				codeList.getDescription());

		Collection<CodeEntry> entries = codeList.getEntries();
		assertEquals(5, entries.size());

		Set<String> ids = entries.stream().map(e -> e.getIdentifier()).collect(Collectors.toSet());
		Set<String> expected = new HashSet<>(Arrays.asList("fixedPlateRoadSign",
				"variableMessageSign", "temporaryRoadSign", "regulation", "otherRoadFeature"));
		assertEquals(expected, ids);
	}

	// utilities

	private CodeList readCodeList(URI source) throws Exception {

		CodeListReader reader = new XmlCodeListReader();

		reader.setSource(new DefaultInputSupplier(source));

		IOReport report = reader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return reader.getCodeList();

	}

}
