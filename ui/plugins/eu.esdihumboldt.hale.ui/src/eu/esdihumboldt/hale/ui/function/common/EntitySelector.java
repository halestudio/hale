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

package eu.esdihumboldt.hale.ui.function.common;

import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Entity selector
 * @param <F> the field type
 * @author Simon Templer
 */
public abstract class EntitySelector<F extends AbstractParameter> {
	
	private final ComboViewer combo;
	
	private final Composite main;
	
	private final F field;

	/**
	 * Create an entity selector
	 * @param ssid the schema space
	 * @param candidates the entity candidates
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 */
	public EntitySelector(final SchemaSpaceID ssid, final Set<EntityDefinition> candidates,
			F field, Composite parent) {
		this.field = field;
		
		main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).
				equalWidth(false).margins(0, 0).create());
		
		// entity selection combo
		combo = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.getControl().setLayoutData(GridDataFactory.swtDefaults().
				align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setLabelProvider(new DefinitionLabelProvider());

		combo.setInput(candidates);
		
		ViewerFilter[] filters = createFilters(field);
		if (filters != null) {
			combo.setFilters(filters);
		}
		
		// browse button
		Button browse = new Button(main, SWT.PUSH);
		browse.setText("...");
		browse.setLayoutData(GridDataFactory.swtDefaults().create());
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EntityDialog dialog = createEntityDialog(
						Display.getCurrent().getActiveShell(), ssid, 
						EntitySelector.this.field);
				dialog.setFilters(combo.getFilters());
				if (dialog.open() == EntityDialog.OK) {
					EntityDefinition entity = dialog.getEntity();
					if (entity != null) {
						candidates.add(entity);
						combo.refresh();
						combo.setSelection(new StructuredSelection(entity));
					}
				}
			}
		});
	}
	
	/**
	 * Create filters for the combo viewer and the dialog. The default 
	 * implementation creates no filters.
	 * @param field the field definition, may be <code>null</code>
	 * @return the array of filters or <code>null</code>
	 */
	protected ViewerFilter[] createFilters(F field) {
		// override me
		return null;
	}

	/**
	 * Create the dialog for selecting an entity.
	 * @param parentShell the parent shell for the dialog
	 * @param ssid the schema space
	 * @param field the field definition
	 * @return the entity dialog
	 */
	protected abstract EntityDialog createEntityDialog(Shell parentShell, 
			SchemaSpaceID ssid, F field);

	/**
	 * Get the main selector control
	 * @return the main control
	 */
	public Control getControl() {
		return main;
	}

	/**
	 * Get the viewer
	 * @return the viewer
	 */
	public ComboViewer getViewer() {
		return combo;
	}

	/**
	 * Get the selected entity definition
	 * @return the selected entity definition or <code>null</code>
	 */
	public EntityDefinition getEntityDefinition() {
		ISelection selection = combo.getSelection();
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element instanceof EntityDefinition) {
			return (EntityDefinition) element;
		}
		
		return null;
	}
	
	/**
	 * Get the selected entity
	 * @return the selected entity or <code>null</code>
	 */
	public Entity getEntity() {
		EntityDefinition def = getEntityDefinition();
		
		if (def != null) {
			return createEntity(def);
		}
		
		return null;
	}

	/**
	 * Create an entity for the given entity definition
	 * @param element the entity definition
	 * @return the entity
	 */
	protected abstract Entity createEntity(EntityDefinition element);

}
