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

package eu.esdihumboldt.hale.common.convert.core;

import java.time.LocalDate;

/**
 * Convert a {@link String} to a {@link LocalDate}.
 * 
 * @author Simon Templer
 */
public class StringToLocalDateConverter extends AbstractStringToDateTimeTypeConverter<LocalDate> {

	/**
	 * @see eu.esdihumboldt.hale.common.convert.core.AbstractStringToDateTimeTypeConverter#parse(java.lang.String)
	 */
	@Override
	protected LocalDate parse(String source) {
		return LocalDate.parse(source);
	}

}
