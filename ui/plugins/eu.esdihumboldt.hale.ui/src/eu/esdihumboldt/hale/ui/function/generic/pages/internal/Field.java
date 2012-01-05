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
import eu.esdihumboldt.hale.ui.HaleSharedImages;
import eu.esdihumboldt.hale.ui.function.common.EntitySelector;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

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
			final Composite parent, Set<EntityDefinition> candidates,
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
						SWT.RIGHT, parent);
			}
		}
		
		selectorContainer = new Composite(parent, SWT.NONE);
		selectorContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		// left margin 6 pixels for ControlDecorations to have place within this component
		// so they're not drawn outside of the ScrolledComposite in case it's present.
		selectorContainer.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(6, 0, 0, 0).create());

		selectionChangedListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				int changedIndex = selectors.indexOf(event.getSelectionProvider());
				S changedSelector = selectors.get(changedIndex);

				// add/remove selector
				// check whether all selectors are valid (so must the changed one be)
				if (countValidEntities() == selectors.size()) {
					// maybe last invalid entity was set, check whether to add another one
					if (Field.this.definition.getMaxOccurrence() != selectors.size()) {
						S newSelector = createEntitySelector(Field.this.ssid, Field.this.definition, selectorContainer);
						newSelector.getControl().setLayoutData(
								GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
						addSelector(newSelector);

						// layout new selector in scrolled pane 
						selectorContainer.getParent().getParent().layout();
					}
				} else {
					// check whether a field was set to None and remove the field if it isn't the last one and minOccurrence is still met
					if (event.getSelection().isEmpty() && changedIndex != selectors.size() - 1
							&& Field.this.definition.getMinOccurrence() < selectors.size()) {
						// check whether first selector will be removed and it had the fields description
						boolean createDescriptionDecoration = changedIndex == 0
								&& Field.this.definition.getDisplayName().isEmpty()
								&& !Field.this.definition.getDescription().isEmpty();
						removeSelector(changedSelector);

						// add new description decoration if necessary
						if (createDescriptionDecoration) {
							ControlDecoration descriptionDecoration = new ControlDecoration(selectors.get(0)
									.getControl(), SWT.RIGHT | SWT.TOP, parent);
							descriptionDecoration.setDescriptionText(Field.this.definition.getDescription());
							FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
									FieldDecorationRegistry.DEC_INFORMATION);
							descriptionDecoration.setImage(fieldDecoration.getImage());
							descriptionDecoration.setMarginWidth(2);
						}

						// necessary layout call for control decoration to appear at the correct place
						selectorContainer.getParent().getParent().layout();

						// add mandatory decoration to next selector if needed
						if (changedIndex < Field.this.definition.getMinOccurrence()) {
							S newMandatorySelector = selectors.get(Field.this.definition.getMinOccurrence() - 1);

							ControlDecoration mandatory = new ControlDecoration(newMandatorySelector.getControl(),
									SWT.LEFT | SWT.TOP, parent);
							FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
									FieldDecorationRegistry.DEC_REQUIRED);
							mandatory.setImage(HALEUIPlugin.getDefault().getImageRegistry()
									.get(HaleSharedImages.IMG_DECORATION_MANDATORY));
							mandatory.setDescriptionText(fieldDecoration.getDescription());
						}
					}
				}

				// update state
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
		} else if (candidates != null && !candidates.isEmpty()) {
			// populate from candidates
			LinkedHashSet<EntityDefinition> rotatingCandidates = new LinkedHashSet<EntityDefinition>(candidates);

			// try to add candidates for each required entity
			for (int i = 0; i < minCount; i++) {
				boolean found = false;
				for (EntityDefinition candidate : candidates) {
					//XXX checked against filters later, because here filters aren't present yet.
					//if (true) {
					fieldValues.add(candidate);
					rotatingCandidates.remove(candidate);
					rotatingCandidates.add(candidate);
					found = true;
					break;
					//}
				}
				if (!found) {
					fieldValues.add(null);
				}
			}
		}

		// add a field w/o value if additional values are supported and all minCount fields are filled
		if ((minCount == 0 || minCount <= fieldValues.size())
				&& (definition.getMaxOccurrence() == F.UNBOUNDED || definition.getMaxOccurrence() > minCount)) {
			minCount++;
		}

		// add fields
		for (int num = 0; num < minCount; num++) {
			// create entity selector
			S selector = createEntitySelector(ssid, definition, selectorContainer);
			selector.getControl().setLayoutData(GridDataFactory.swtDefaults().
					align(SWT.FILL, SWT.CENTER).grab(true, false).create());
			addSelector(selector);

			// add mandatory decoration
			if (num < definition.getMinOccurrence()) {
				ControlDecoration mandatory = new ControlDecoration(
						selector.getControl(), SWT.LEFT | SWT.TOP, parent);
				FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
						.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
				mandatory.setImage(HALEUIPlugin.getDefault().getImageRegistry()
						.get(HaleSharedImages.IMG_DECORATION_MANDATORY));
				mandatory.setDescriptionText(fieldDecoration.getDescription());
			}

			// do initial selection
			EntityDefinition value = (num < fieldValues.size()) ? (fieldValues.get(num)) : (null);
			if (value == null || !selector.accepts(value))
				selector.setSelection(new StructuredSelection());
			else
				selector.setSelection(new StructuredSelection(value));

			if (descriptionDecoration == null && definition.getDescription() != null) {
				descriptionDecoration = new ControlDecoration(selector.getControl(), SWT.RIGHT | SWT.TOP, parent);
			}
		}

		// setup description decoration
		if (descriptionDecoration != null) {
			descriptionDecoration.setDescriptionText(definition.getDescription());
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_INFORMATION);
			descriptionDecoration.setImage(fieldDecoration.getImage());
			descriptionDecoration.setMarginWidth(2);
		}

		//TODO conditions/filters etc. ?!
		//XXX -> create a wrapper to EntitySelector for this?

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
		selector.removeSelectionChangedListener(selectionChangedListener);
		selectors.remove(selector);
		selector.getControl().dispose();
	}

	/**
	 * Get the schema space
	 * @return the schema space
	 */
	public SchemaSpaceID getSchemaSpace() {
		return ssid;
	}
	
	/**
	 * Counts valid entities.
	 * 
	 * @return number of valid entities
	 */
	private int countValidEntities() {
		int validCount = 0;
		for (EntitySelector<F> selector : selectors) {
			if (!selector.getSelection().isEmpty()) //TODO improve condition
				validCount++;
		}
		return validCount;
	}
	
	/**
	 * Updates the valid state
	 */
	private void updateState() {
		boolean newValid = countValidEntities() >= definition.getMinOccurrence();		
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
