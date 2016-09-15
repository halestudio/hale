/*
 * Copyright (c) 2013 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt.compatibility;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.xslt.extension.XslPropertyTransformationExtension;
import eu.esdihumboldt.hale.io.xslt.extension.XslTypeTransformationExtension;

/**
 * Compatibility Mode for Xslt
 * 
 * @author Sebastian Reinhardt
 */
public class XsltCompatibilityMode implements CompatibilityMode {

	private static ALogger _log = ALoggerFactory.getLogger(XsltCompatibilityMode.class);

	@Override
	public boolean supportsFunction(String id, ServiceProvider serviceProvider) {
		return (checkPropertyFunc(id) || checkTypeFunc(id));
	}

	/**
	 * checks the functions id of compatibility with property functions
	 * 
	 * @param id the functions id
	 * @return true if compatible, else false
	 */
	private boolean checkPropertyFunc(String id) {
		try {
			XslPropertyTransformationExtension.getInstance().getTransformation(id);
		} catch (IllegalStateException e) {
			return false;
		} catch (Exception e) {
			_log.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * checks the functions id of compatibility with type functions
	 * 
	 * @param id the functions id
	 * @return true if compatible, else false
	 */
	private boolean checkTypeFunc(String id) {
		try {
			XslTypeTransformationExtension.getInstance().getTransformation(id);
		} catch (IllegalStateException e) {
			return false;
		} catch (Exception e) {
			_log.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean supportsCell(Cell cell) {
		// only accept cells with supported filters
		// XXX currently all filters supported for XSLT

		// contexts not supported yet
		return true;
	}

}
