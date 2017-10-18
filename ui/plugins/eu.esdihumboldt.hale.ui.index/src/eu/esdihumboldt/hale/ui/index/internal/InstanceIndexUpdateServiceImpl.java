package eu.esdihumboldt.hale.ui.index.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndex;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.ui.index.InstanceIndexUpdateService;

/**
 * Service implementation to update the configuration of the {@link InstanceIndex}
 * according to changes in the alignment.
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
		getIndexService().clear();
	}

	@Override
	public void cellsAdded(Iterable<Cell> cells) {
		for (Cell cell : cells) {
			if (MergeFunction.ID.equals(cell.getTransformationIdentifier()) || JoinFunction.ID.equals(cell.getTransformationIdentifier())) {
				for (Entity sourceEntity : cell.getSource().values()) {
					getIndexService().addPropertyMappings(getChildrenWithoutContexts(sourceEntity));
				}
			}
		}
	}

	@Override
	public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
		cellsRemoved(cells.keySet().stream().map(c -> (Cell)c).collect(Collectors.toList()));
		cellsAdded(cells.entrySet().stream().map(c -> (Cell)c).collect(Collectors.toList()));
	}

	@Override
	public void cellsRemoved(Iterable<Cell> cells) {
		for (Cell cell : cells) { 
			for (Entity sourceEntity : cell.getSource().values()) {
				getIndexService().removePropertyMappings(getChildrenWithoutContexts(sourceEntity));
			}
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

	private List<PropertyEntityDefinition> getChildrenWithoutContexts(Entity sourceEntity) {
		TypeEntityDefinition ted = (TypeEntityDefinition)sourceEntity.getDefinition();
		List<PropertyEntityDefinition> childProperties = new ArrayList<>();
		for (EntityDefinition child : AlignmentUtil.getChildrenWithoutContexts(ted)) {
			if (child instanceof PropertyEntityDefinition) {
				childProperties.add((PropertyEntityDefinition) child);
			}
		}
		return childProperties;
	}
}


