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

package eu.esdihumboldt.hale.common.align.extension.function.validator;

import java.util.List;

import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.Validator;

/**
 * Validator that checks whether the value's length is in given bounds.<br>
 * 
 * Expects parameters "min" (natural number) and "max" (natural number or -1 for
 * unbounded).
 * 
 * @author Kai Schwierczek
 */
public class LengthValidator implements Validator {

	private static final ALogger log = ALoggerFactory.getLogger(LengthValidator.class);

	private int min = 0;
	private int max = -1;

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		if (value.length() < min)
			return "parameter must have at least " + min + " character" + (min != 1 ? "s" : "");
		if (max != -1 && value.length() > max)
			return "parameter must have at most " + max + " character" + (max != 1 ? "s" : "");
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		List<String> minList = parameters.get("min");
		if (!minList.isEmpty()) {
			int minConf = 0;
			try {
				minConf = Integer.parseInt(minList.get(0));
			} catch (NumberFormatException nfe) {
				log.error("Specified min parameter is no integer.", nfe);
			}
			if (minConf >= 0)
				min = minConf;
			else
				log.error("Specified min parameter is smaller than zero.");
		}

		List<String> maxList = parameters.get("max");
		if (!maxList.isEmpty()) {
			int maxConf = 0;
			try {
				maxConf = Integer.parseInt(maxList.get(0));
			} catch (NumberFormatException nfe) {
				log.error("Specified max parameter is no integer.", nfe);
			}
			if (maxConf >= -1)
				max = maxConf;
			else
				log.error("Specified max parameter is smaller than -1.");
		}
	}
}
