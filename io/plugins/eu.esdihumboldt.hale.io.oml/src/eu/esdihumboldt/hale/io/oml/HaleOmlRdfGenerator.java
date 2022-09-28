/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.io.oml;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;

/**
 * @author Thorsten Reitz
 */
public class HaleOmlRdfGenerator extends OmlRdfGenerator {

	private static ALogger _log = ALoggerFactory.getLogger(HaleOmlRdfGenerator.class);

	/**
	 * @param m the {@link Marshaller} to configure.
	 */
	@Override
	public void configurePrefixMapper(Marshaller m) {
		try {
			m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			_log.error("Setting the PrefixMapper failed: ", e); //$NON-NLS-1$
		}
	}

}
