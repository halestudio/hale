/*
 * Copyright (c) 2024 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.internal.simpletype.converters;

import java.math.BigInteger;

import org.apache.xmlbeans.XmlInteger;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert xs:BigInteger to {@link XmlInteger}
 */
public class BigIntegerToXmlInteger implements Converter<BigInteger, XmlInteger> {

	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public XmlInteger convert(BigInteger value) {
		if (value == null) {
			return null;
		}
		return XmlInteger.Factory.newValue(value);
	}

}
