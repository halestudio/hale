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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.first;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.ibm.icu.text.MessageFormat;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.ext.impl.PerTypeInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.editors.IntegerEditor;
import eu.esdihumboldt.hale.ui.common.editors.value.IntegerValueEditor;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;

/**
 * Sampler that returns the first n instances.
 * 
 * @author Simon Templer
 */
public class FirstSampler implements Sampler {

	private static final String DISPLAY_NAME = "First {0} instances per type";

	private static final int DEFAULT_MAX = 500;

	@Override
	public InstanceCollection sample(InstanceCollection instances, Value settings) {
		final int max = settings.as(Integer.class, DEFAULT_MAX);

		if (max >= 1) {
			if (instances instanceof InstanceCollection2) {
				InstanceCollection2 fic = (InstanceCollection2) instances;
				if (fic.supportsFanout()) {
					/*
					 * The instance collection supports fan-out by type -> so we
					 * apply the sampling on each instance collection
					 * individually
					 */
					Map<TypeDefinition, InstanceCollection> collections = Maps.transformValues(
							fic.fanout(), new Function<InstanceCollection, InstanceCollection>() {

								@Override
								public InstanceCollection apply(InstanceCollection org) {
									return new FirstOverallInstances(org, max);
								}

							});
					return new PerTypeInstanceCollection(collections);
				}
			}

			return new FirstInstancesPerType(instances, max);
		}
		return instances;
	}

	@Override
	public String getDisplayName(Value settings) {
		Integer n = settings.as(Integer.class);

		if (n != null) {
			return MessageFormat.format(DISPLAY_NAME, n);
		}
		else {
			return MessageFormat.format(DISPLAY_NAME, 'n');
		}
	}

	@Override
	public AttributeEditor<Value> createEditor(Composite parent) {
		return new IntegerValueEditor(new IntegerEditor(parent, 100000, 1, 10, 100));
	}

	@Override
	public Value getDefaultSettings() {
		return Value.of(DEFAULT_MAX);
	}

}
