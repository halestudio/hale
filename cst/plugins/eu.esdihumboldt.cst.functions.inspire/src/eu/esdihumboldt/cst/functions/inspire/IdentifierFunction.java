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

package eu.esdihumboldt.cst.functions.inspire;

/**
 * Constants for the identifier function
 * 
 * @author Kevin Mais
 * 
 */
@SuppressWarnings("javadoc")
public interface IdentifierFunction {

	public static final String ID = "eu.esdihumboldt.cst.functions.inspire.identifier";

	public static final String COUNTRY_PARAMETER_NAME = "countryName"; //$NON-NLS-1$
	public static final String DATA_PROVIDER_PARAMETER_NAME = "providerName"; //$NON-NLS-1$
	public static final String PRODUCT_PARAMETER_NAME = "productName"; //$NON-NLS-1$
	public static final String VERSION = "version"; //$NON-NLS-1$
	public static final String VERSION_NIL_REASON = "versionNilReason"; //$NON-NLS-1$

	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id"; //$NON-NLS-1$

}
