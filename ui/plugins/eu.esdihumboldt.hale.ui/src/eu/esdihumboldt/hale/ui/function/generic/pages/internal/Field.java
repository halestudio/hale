/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.generic.pages.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.function.common.EntitySelector;

/**
 * Represents named entities in a function
 * @param <S> the entity selector type
 * @param <F> the field type
 * @author Simon Templer
 */
public abstract class Field<F extends AbstractParameter, S extends EntitySelector<F>> extends Observable {

	private final F definition;
	private final SchemaSpaceID ssid;
	
	private final List<S> selectors = new ArrayList<S>();
	private final Composite selectorContainer;
	private final ISelectionChangedListener selectionChangedListener;
	
	private boolean valid = false;

	/**
	 * Create a field
	 * @param definition the field definition
	 * @param ssid the schema space
	 * @param parent the parent composite
	 * @param candidates the entity candidates
	 * @param initialCell the initial cell
	 */
	public Field(F definition, SchemaSpaceID ssid, 
			Composite parent, Set<EntityDefinition> candidates,
			Cell initialCell) {
		super();
		
		this.definition = definition;
		this.ssid = ssid;
		
		ControlDecoration descriptionDecoration = null;
		
		// field name
		if (!definition.getDisplayName().isEmpty()) {
			Label name = new Label(parent, SWT.NONE);
			name.setText(definition.getDisplayName());
			name.setLayoutData(GridDataFactory.swtDefaults().create());
			
			if (definition.getDescription() != null) {
				// add decoration
				descriptionDecoration = new ControlDecoration(name,
						SWT.RIGHT | SWT.TOP);
			}
		}
		
		selectorContainer = new Composite(parent, SWT.NONE);
		selectorContainer.setLayoutData(GridDataFactory.fillDefaults().
				grab(true, false).create());
		selectorContainer.setLayout(GridLayoutFactory.fillDefaults().create());
		
		selectionChangedListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState();
			}
		};

		// determine number of contained fields and the corresponding values
		int minCount = definition.getMinOccurrence();
		
		//TODO determine filters from definition
		
		List<EntityDefinition> fieldValues = new ArrayList<EntityDefinition>();
		if (initialCell != null) {
			// entities from cell
			List<? extends Entity> entities;
			switch (ssid) {
			case SOURCE:
				entities = initialCell.getSource().get(definition.getName());
				break;
			case TARGET:
				entities= initialCell.getTarget().get(definition.getName());
				break;
			default:
				throw new IllegalStateException("Illegal schema space");
			}
			for (Entity entity : entities) {
				fieldValues.add(entity.getDefinition()); //FIXME what about the information in the entity?!
			}
			// adapt minCount if needed (and possible)
			if (fieldValues.size() > minCount) {
				if (definition.getMaxOccurrence() == F.UNBOUNDED) {
					minCount = fieldValues.size();
				}
				else {
					minCount = Math.min(fieldValues.size(), definition.getMaxOccurrence());
				}
			}
		}
		else {
			// populate from candidates
			if (candidates != null && !candidates.isEmpty()) {
				LinkedHashSet<EntityDefinition> rotatingCandidates = new LinkedHashSet<EntityDefinition>(candidates);

				// try to add candidates for each required entity
				for (int i = 0; i < definition.getMinOccurrence(); i++) {
					boolean found = false;
					for (EntityDefinition candidate : candidates) {
						if (true) { //TODO check against filters
							fieldValues.add(candidate);
							rotatingCandidates.remove(candidate);
							rotatingCandidates.add(candidate);
							found = true;
							break;
						}
					}
					if (!found) {
						fieldValues.add(null);
					}
				}
			}
		}
		
		// add a field w/o value if additional values are supported
		if (definition.getMaxOccurrence() == F.UNBOUNDED ||
				definition.getMaxOccurrence() > minCount) {
			minCount++;
		}
		
		// add fields
		for (int num = 0; num < minCount; num++) {
			// create entity selector
			S selector = createEntitySelector(ssid, definition, selectorContainer);
			selector.getControl().setLayoutData(GridDataFactory.swtDefaults().
					align(SWT.FILL, SWT.CENTER).grab(true, false).create());
			addSelector(selector);
			
			// do initial selection
			EntityDefinition value = (num < fieldValues.size())?(fieldValues.get(num)):(null);
			if (value == null) {
				selector.setSelection(new StructuredSelection());
			}
			else {
				selector.setSelection(new StructuredSelection(value));
			}
			
			if (descriptionDecoration == null && definition.getDescription() != null) {
				descriptionDecoration = new ControlDecoration(selector.getControl(),
						SWT.LEFT | SWT.TOP);
			}
		}
		
		// setup description decoration
		if (descriptionDecoration != null) {
			descriptionDecoration.setDescriptionText(definition.getDescription());
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			descriptionDecoration.setImage(fieldDecoration.getImage());
			descriptionDecoration.setMarginWidth(2);
		}
		
		//TODO conditions/filters etc. ?!
		//XXX -> create a wrapper to EntitySelector for this?
		
		//TODO optional fields (+)
		
		//TODO "required" decorations for required fields
		
		updateState();
	}
	
	/**
	 * Create an entity selector
	 * @param ssid the schema space
	 * @param field the field definition
	 * @param parent the parent composite
	 * @return the entity selector
	 */
	protected abstract S createEntitySelector(SchemaSpaceID ssid,
			F field, Composite parent);
	
	/**
	 * Get the selectors associated with the field
	 * @return the selectors
	 */
	protected List<S> getSelectors() {
		return Collections.unmodifiableList(selectors);
	}

	/**
	 * Add a selector
	 * @param selector the entity selector to add
	 */
	protected void addSelector(S selector) {
		selectors.add(selector);
		selector.addSelectionChangedListener(selectionChangedListener);
	}
	
	/**
	 * Remove a selector
	 * @param selector the entity selector to remove
	 */
	protected void removeSelector(S selector) {
		//TODO remove listener
		//TODO remove from set
		//TODO remove from composite (dispose)
		//TODO relayout
	}

	/**
	 * Get the schema space
	 * @return the schema space
	 */
	public SchemaSpaceID getSchemaSpace() {
		return ssid;
	}
	
	/**
	 * Updates the valid state
	 */
	private void updateState() {
		boolean newValid = false;
		
		// valid if minimum occurrence is met
		int validCount = 0;
		for (EntitySelector<F> selector : selectors) {
			if (!selector.getSelection().isEmpty()) { //TODO improve condition
				validCount++;
			}
			if (validCount >= selector.getField().getMinOccurrence()) {
				newValid = true;
				break;
			}
		}
		
		boolean change = newValid != valid;
		valid = newValid;
		
		if (change) {
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Determines if the field is valid in its current configuration
	 * @return if the field is valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Fill the given map with the field's entities
	 * @param target the map to add the entities to
	 */
	public void fillEntities(ListMultimap<String, Entity> target) {
		for (S selector : selectors) {
			Entity entity = selector.getEntity();
			if (entity != null) {
				target.put(definition.getName(), entity);
			}
		}
	}

}
