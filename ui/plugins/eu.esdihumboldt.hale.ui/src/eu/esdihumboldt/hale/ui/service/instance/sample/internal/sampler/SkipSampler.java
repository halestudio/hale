/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler;

import com.ibm.icu.text.MessageFormat;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class SkipSampler implements Sampler {

	private static final String DISPLAY_NAME = "Every {0} instance per type";

	@Override
	public InstanceCollection sample(InstanceCollection instances, Value settings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName(Value settings) {
		Integer n = settings.as(Integer.class);

		String arg;
		if (n != null) {
			String number = String.valueOf(n);
			switch (number.charAt(number.length() - 1)) {
			case '1':
				arg = number + "st";
				break;
			case '2':
				arg = number + "nd";
				break;
			case '3':
				arg = number + "rd";
				break;
			default:
				arg = number + "th";
			}
		}
		else {
			arg = "n-th";
		}

		return MessageFormat.format(DISPLAY_NAME, arg);
	}

}
