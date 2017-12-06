/*
 * Copyright (c) 2017 wetransform GmbH
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
package eu.esdihumboldt.hale.ui.index.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.merge.MergeUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndex;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstance;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.ui.index.InstanceIndexUpdateService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Service implementation to update the configuration of the
 * {@link InstanceIndex} according to changes in the alignment.
 * 
 * @author Florian Esser
 */
public class InstanceIndexUpdateServiceImpl implements InstanceIndexUpdateService {

	private final ServiceProvider serviceProvider;

	/**
	 * Create the instance index update service
	 * 
	 * @param serviceProvider Service provider instance
	 */
	public InstanceIndexUpdateServiceImpl(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public void alignmentCleared() {
		getIndexService().clearAll();
	}

	@Override
	public void cellsAdded(Iterable<Cell> cells) {
		boolean reindex = false;

		for (Cell cell : cells) {
			List<PropertyEntityDefinition> indexedProperties = new ArrayList<>();
			switch (cell.getTransformationIdentifier()) {
			case MergeFunction.ID:
				indexedProperties.addAll(MergeUtil.getKeyPropertyDefinitions(cell));
				break;
			case JoinFunction.ID:
				// TODO
				break;
			}

			if (!indexedProperties.isEmpty()) {
				getIndexService().addPropertyMapping(indexedProperties);
				reindex = true;
			}
		}

		if (reindex) {
			reindex();
		}
	}

	private void reindex() {
		getIndexService().clearIndexedValues();
		InstanceService is = serviceProvider.getService(InstanceService.class);
		InstanceCollection source = is.getInstances(DataSet.SOURCE);
		try (ResourceIterator<Instance> it = source.iterator()) {
			while (it.hasNext()) {
				Instance i = it.next();
				InstanceReference ref = source.getReference(i);
				if (ref instanceof IdentifiableInstance) {
					ref = new IdentifiableInstanceReference(ref,
							((IdentifiableInstance) ref).getId());
				}
				ResolvableInstanceReference rir = new ResolvableInstanceReference(ref, source);

				getIndexService().add(i, rir);
			}
		}
	}

	@Override
	public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
		cellsRemoved(cells.keySet().stream().map(c -> (Cell) c).collect(Collectors.toList()));
		cellsAdded(cells.entrySet().stream().map(c -> (Cell) c).collect(Collectors.toList()));
	}

	@Override
	public void cellsRemoved(Iterable<Cell> cells) {
		boolean reindex = false;

		for (Cell cell : cells) {
			switch (cell.getTransformationIdentifier()) {
			case MergeFunction.ID:
			case JoinFunction.ID:
				reindex = true;
				break;
			}
		}

		// TODO Reindexing will not remove stale mappings, therefore index may
		// be too large until project is reloaded
		if (reindex) {
			reindex();
		}
	}

	@Override
	public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
		// TODO Action required?
	}

	@Override
	public void customFunctionsChanged() {
		// TODO Action required?
	}

	@Override
	public void alignmentChanged() {
		// TODO Action required?
	}

	private InstanceIndexService getIndexService() {
		return serviceProvider.getService(InstanceIndexService.class);
	}

}
