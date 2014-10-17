/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;

/**
 * Page to select/set the source of the auto correlation function
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationTypesPage extends HaleWizardPage<AutoCorrelationFunctionWizard> {

	private Composite pageComposite;
	private Button processEntireSchema;
	private ListMultimap<String, Type> source;
	private ListMultimap<String, Type> target;
	private Label targetType;
	private Label sourceType;

	/**
	 * @param pageName The name of the page
	 */
	protected AutoCorrelationTypesPage(String pageName) {
		super(pageName);

		setTitle(pageName);
		setDescription("Please choose/confirm your desired source types.");
	}

	/**
	 * Check if the page is valid and set the
	 * 
	 * @return true, if the page's state is valid
	 */
	private boolean isValid() {
		if (processEntireSchema.getSelection()) {
			setPageComplete(true);
			return true;
		}

		else if (source != null && !source.isEmpty() && target != null && !target.isEmpty()) {
			setPageComplete(true);
		}

		setPageComplete(false);
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// set the source and target types to the selection if firstShow
		if (firstShow) {
			SchemaSelection selection = SchemaSelectionHelper.getSchemaSelection();
			source = ArrayListMultimap.create();
			target = ArrayListMultimap.create();

			for (EntityDefinition entity : selection.getSourceItems()) {
				if (entity instanceof TypeEntityDefinition) {
					Type type = new DefaultType((TypeEntityDefinition) entity);

					source.put(null, type);
				}
			}

			for (EntityDefinition entity : selection.getTargetItems()) {
				if (entity instanceof TypeEntityDefinition) {
					Type type = new DefaultType((TypeEntityDefinition) entity);

					target.put(null, type);
				}
			}
			sourceType.setText(source.entries().iterator().next().getValue().getDefinition()
					.getType().getDisplayName());
			targetType.setText(target.entries().iterator().next().getValue().getDefinition()
					.getType().getDisplayName());

		}

		pageComposite.layout();
		pageComposite.pack();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		pageComposite = page;

		GridLayout layout = new GridLayout(1, false);
		page.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(page);

//		Composite typeSelectorSpace = new Composite(page, SWT.NONE);
//		typeSelectorSpace.setLayout(new GridLayout(2, false));
//		GridDataFactory.fillDefaults().grab(true, false).applyTo(typeSelectorSpace);
		Group typeSelectorSpace = new Group(page, SWT.NONE);
		typeSelectorSpace.setText("Types");
		typeSelectorSpace.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(false, true).applyTo(typeSelectorSpace);

		// Source Type
		Label sourceLabel = new Label(typeSelectorSpace, SWT.NONE);
		sourceLabel.setText("Source Type: ");
		sourceType = new Label(typeSelectorSpace, SWT.BEGINNING);
		sourceType.setText("");
//		StringFieldEditor mySourceType = new StringFieldEditor("sourceType", "Source Type: ",
//				typeSelectorSpace);
		// createSourceField(typeSelectorSpace,
		// "Wildcard source Type Selector");

		// Target Type
		Label targetLabel = new Label(typeSelectorSpace, SWT.NONE);
		targetLabel.setText("Target Type: ");
		targetType = new Label(typeSelectorSpace, SWT.BEGINNING);
		targetType.setText("");
//		StringFieldEditor myTargetType = new StringFieldEditor("targetType", "Target Type: ",
//				typeSelectorSpace);
		// createSourceField(typeSelectorSpace,
		// "Wildcard target Type Selector");

		// Checkbox entire Schema
		processEntireSchema = new Button(page, SWT.CHECK);
		processEntireSchema.setText("Process Entire Schema");
		processEntireSchema.setSelection(false);
		processEntireSchema.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// TODO disable type selector
				}
				else {
					// TODO enable type selector
				}
				isValid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// TODO disable type selector
				}
				else {
					// TODO enable type selector
				}
				isValid();
			}
		});
		GridDataFactory.swtDefaults().grab(true, false).applyTo(processEntireSchema);

		setPageComplete(false);
		page.layout();
		page.pack();
	}

	/**
	 * 
	 */
	private void createSourceField(Composite parent, String text) {

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);

		// Create a child composite to hold the controls
		Composite control = new Composite(sc, SWT.NONE);
		control.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
		control.setSize(300, 100);

		Label name = new Label(control, SWT.NONE);
		name.setText(text);
		name.setLayoutData(GridDataFactory.swtDefaults().create());
		/*
		 * // Set the absolute size of the child child.setSize(400, 400);
		 */
		// Set the child as the scrolled content of the ScrolledComposite
		sc.setContent(control);

		// Set the minimum size
		// sc.setMinSize(400, 400);

		// Expand both horizontally and vertically
		sc.setExpandHorizontal(false);
		sc.setExpandVertical(true);
	}
}
