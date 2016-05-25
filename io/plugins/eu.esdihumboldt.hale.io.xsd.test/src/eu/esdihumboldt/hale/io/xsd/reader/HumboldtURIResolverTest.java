/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xsd.reader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.io.xsd.reader.internal.HumboldtURIResolver;

/**
 * Tests for schema URI resolver.
 */
public class HumboldtURIResolverTest {

	@SuppressWarnings("javadoc")
	@Test
	public void testJarURI() {
		String baseUri = "jar:file:/home/simon/.gradle/caches/modules-2/files-2.1/eu.esdihumboldt.hale/eu.esdihumboldt.hale.io.xsd/2.9.5-SNAPSHOT/fe32ccb0764ec0adcfab7563b3d84e46c36bd3a0/eu.esdihumboldt.hale.io.xsd-2.9.5-SNAPSHOT.jar!/schemas/";
		String location = "xml.xsd";
		String expected = "jar:file:/home/simon/.gradle/caches/modules-2/files-2.1/eu.esdihumboldt.hale/eu.esdihumboldt.hale.io.xsd/2.9.5-SNAPSHOT/fe32ccb0764ec0adcfab7563b3d84e46c36bd3a0/eu.esdihumboldt.hale.io.xsd-2.9.5-SNAPSHOT.jar!/schemas/xml.xsd";

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHttpRelative1() {
		String baseUri = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
		String location = "feature.xsd";
		String expected = "http://schemas.opengis.net/gml/3.2.1/feature.xsd";

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testFileRelative1() {
		String baseUri = "file:///C:/Test/gml/3.2.1/feature.xsd";
		String location = "gml.xsd";
		String expected = "/C:/Test/gml/3.2.1/gml.xsd";

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testFileRelative2() {
		String baseUri = "C:/Test/gml/3.2.1/feature.xsd";
		String location = "gml.xsd";
		String expected = "C:/Test/gml/3.2.1/gml.xsd";

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHttpAbsolute1() {
		String baseUri = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
		String location = "http://schemas.opengis.net/gml/3.2.1/feature.xsd";
		String expected = location;

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHttpAbsoluteOtherJar() {
		String baseUri = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
		String location = "jar:file:/home/simon/.gradle/caches/modules-2/files-2.1/eu.esdihumboldt.hale/eu.esdihumboldt.hale.io.xsd/2.9.5-SNAPSHOT/fe32ccb0764ec0adcfab7563b3d84e46c36bd3a0/eu.esdihumboldt.hale.io.xsd-2.9.5-SNAPSHOT.jar!/schemas/xml.xsd";
		String expected = location;

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHttpAbsoluteOtherFile() {
		String baseUri = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
		String location = "file:///C:/Test/gml/3.2.1/feature.xsd";
		String expected = location;

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testHttpAbsoluteOtherHttp() {
		String baseUri = "file:///C:/Test/gml/3.2.1/feature.xsd";
		String location = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
		String expected = location;

		HumboldtURIResolver resolver = new HumboldtURIResolver();
		InputSource source = resolver.resolveEntity("ns", location, baseUri);

		assertEquals(expected, source.getSystemId());
	}

}
