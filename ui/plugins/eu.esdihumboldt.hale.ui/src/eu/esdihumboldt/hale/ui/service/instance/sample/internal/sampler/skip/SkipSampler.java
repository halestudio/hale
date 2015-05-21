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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.skip;

import org.eclipse.swt.widgets.Composite;

import com.ibm.icu.text.MessageFormat;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.editors.IntegerEditor;
import eu.esdihumboldt.hale.ui.common.editors.value.IntegerValueEditor;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;

/**
 * Sampler that returns every n-th instance of each encountered type.
 * 
 * @author Simon Templer
 */
public class SkipSampler implements Sampler {

	private static final String DISPLAY_NAME = "Every {0} instance per type";

	private static final int DEFAULT_N = 10;

	@Override
	public InstanceCollection sample(InstanceCollection instances, Value settings) {
		int n = settings.as(Integer.class, DEFAULT_N);

		if (n > 1) {
			return new SkipSampleInstances(instances, n - 1);
		}
		return instances;
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

	@Override
	public AttributeEditor<Value> createEditor(Composite parent) {
		return new IntegerValueEditor(new IntegerEditor(parent, 1000, 2, 1, 10));
	}

	@Override
	public Value getDefaultSettings() {
		return Value.of(DEFAULT_N);
	}

}
