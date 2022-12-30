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

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Convert a {@link String} to a {@link Instant}.
 * 
 * @author Simon Templer
 */
public class StringToInstantConverter extends AbstractStringToDateTimeTypeConverter<Instant> {

	@Override
	protected Instant parse(String source) {
		Instant result;
		try {
			result = Instant.parse(source);
		} catch (DateTimeParseException e) {
			// Be lenient about missing time zone information
			try {
				result = Instant.parse(source + "Z");
			} catch (Throwable t) {
				throw e;
			}
		}

		return result;
	}
}
