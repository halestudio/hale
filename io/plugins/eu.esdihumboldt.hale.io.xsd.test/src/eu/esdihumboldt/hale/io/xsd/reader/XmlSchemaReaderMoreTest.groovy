/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.xsd.reader

import static eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReaderTest.readSchema
import static org.junit.Assert.*

import javax.xml.namespace.QName

import org.junit.Ignore
import org.junit.Test

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAppInfo
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex

/**
 * More XmlSchemaReader tests (using Groovy).
 * 
 * @author Simon Templer
 */
class XmlSchemaReaderMoreTest {

	/**
	 * Test if app info can be retrieved from constraint. 
	 */
	@Test
	void testAppInfo() throws Exception {
		def location = URI.create('http://inspire.ec.europa.eu/schemas/ad/4.0/Addresses.xsd')
		def input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://inspire.ec.europa.eu/schemas/ad/4.0";
		assertEquals(ns, schema.getNamespace());

		// test if references are defined

		// address to parcel
		def address = schema.getType(new QName(ns, 'AddressType'))
		assertNotNull(address)

		def parcel = address.accessor().parcel.toDefinition()
		assertNotNull(parcel)

		XmlAppInfo appInfo = parcel.getConstraint(XmlAppInfo)
		def appInfos = appInfo.appInfos
		assertNotNull(appInfos)
		assertFalse(appInfos.empty)

		//TODO check app info content?
	}

	/**
	 * Test if target type for inspire reference is correctly identified. 
	 */
	@Ignore
	@Test
	void testInspireReferenceAppInfo() throws Exception {
		def location = URI.create('http://inspire.ec.europa.eu/schemas/ad/4.0/Addresses.xsd')
		def input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://inspire.ec.europa.eu/schemas/ad/4.0";
		assertEquals(ns, schema.getNamespace());

		// test if references are defined

		// address to parcel
		def address = schema.getType(new QName(ns, 'AddressType'))
		assertNotNull(address)

		def parcel = address.accessor().parcel.href.toDefinition()
		assertNotNull(parcel)

		Reference parcelRef = parcel.getConstraint(Reference)
		assertTrue(parcelRef.isReference())

		assertNotNull(parcelRef.referencedTypes)
		assertFalse(parcelRef.referencedTypes.empty)
		assertEquals(1, parcelRef.referencedTypes.size())
		assertEquals('CadastralParcelType', (parcelRef.referencedTypes as List)[0].name.localPart)

		// address to component
		def component = address.accessor().component.href.toDefinition()
		assertNotNull(component)

		Reference componentRef = component.getConstraint(Reference)
		assertTrue(componentRef.isReference())

		assertNotNull(componentRef.referencedTypes)
		assertFalse(componentRef.referencedTypes.empty)
		assertEquals(1, componentRef.referencedTypes.size())
		assertEquals('AddressComponentType', (componentRef.referencedTypes as List)[0].name.localPart)

		//TODO also test for case w/ substitutions?
	}

	//TODO add test for reference type where embedded or ref is allowed?
}
