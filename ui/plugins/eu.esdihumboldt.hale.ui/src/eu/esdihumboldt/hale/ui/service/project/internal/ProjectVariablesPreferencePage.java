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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Preference page for project variables.
 * 
 * @author Simon Templer
 */
public class ProjectVariablesPreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {

	private static final ALogger log = ALoggerFactory
			.getLogger(ProjectVariablesPreferencePage.class);

	/**
	 * The preference page ID as registered in the extension point.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.preferences.projectvars";

	private ValueProperties variables;

	private boolean changed = false;

	private Composite varList;

	private final Map<String, List<Control>> varControls = new HashMap<>();

	private Composite page;

	private DynamicScrolledComposite sc;

	/**
	 * Default constructor.
	 */
	public ProjectVariablesPreferencePage() {
		super();
		noDefaultButton();
		setDescription(
				"Specify project variables that can be used in certain transformation functions. "
						+ "Project variables may be overridden by system properties or environment variables (using a specific prefix).");
	}

	@Override
	protected Control createContents(Composite parent) {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		Value value = ps.getConfigurationService()
				.getProperty(ProjectVariables.PROJECT_PROPERTY_VARIABLES);
		variables = value.as(ValueProperties.class);
		if (variables == null) {
			variables = new ValueProperties();
			if (value.getValue() != null) {
				log.error("Unknown representation of project variables encountered");
			}
		}

		sc = new DynamicScrolledComposite(parent, SWT.V_SCROLL);
		sc.setExpandHorizontal(true);

		sc.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).create());

		page = new Composite(sc, SWT.NONE);

		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(page);

		varList = new Composite(page, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(varList);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(varList);

		Map<String, Value> sorted = new TreeMap<>(variables);
		for (String varName : sorted.keySet()) {
			addEditor(varName, false);
		}

		// add Add button
		Button add = new Button(page, SWT.PUSH);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).applyTo(add);
		add.setImage(
				CommonSharedImages.getImageRegistry().get(CommonSharedImagesConstants.IMG_ADD));
		add.setToolTipText("Add variable");
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				InputDialog dialog = new InputDialog(display.getActiveShell(), "Add new variable",
						"Please enter the name of the variable to add", "", new IInputValidator() {

					@Override
					public String isValid(String newText) {
						if (newText == null || newText.isEmpty()) {
							return "Variable name must not be empty";
						}
						else if (variables.containsKey(newText)) {
							return "Variable already exists";
						}

						return null;
					}
				});
				if (dialog.open() == InputDialog.OK) {
					String varName = dialog.getValue();
					if (varName != null) {
						variables.put(varName, Value.of(""));
						addEditor(varName, true);
						changed = true;
					}
				}
			}
		});

		sc.setContent(page);

		return page;
	}

	private void addEditor(final String varName, boolean layout) {
		List<Control> controls = varControls.get(varName);

		if (controls == null) {
			controls = new ArrayList<>();

			// create variable label
			Label label = new Label(varList, SWT.NONE);
			label.setText(varName);
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(label);
			controls.add(label);

			// create text field
			Text editor = new Text(varList, SWT.BORDER | SWT.SINGLE);
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
					.applyTo(editor);
			editor.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					changed = true;
				}
			});
			controls.add(editor);

			// create remove button
			Button remove = new Button(varList, SWT.PUSH);
			remove.setImage(CommonSharedImages.getImageRegistry()
					.get(CommonSharedImagesConstants.IMG_REMOVE));
			GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).applyTo(remove);
			remove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					List<Control> controls = varControls.get(varName);
					for (Control c : controls) {
						c.dispose();
					}
					variables.remove(varName);
					updateLayout();
					changed = true;
				}
			});
			controls.add(remove);

			varControls.put(varName, controls);

			if (layout) {
				updateLayout();
			}
		}

		// update text field
		Text textField = (Text) controls.get(1);
		textField.setText(variables.getSafe(varName).as(String.class, ""));
	}

	private void updateLayout() {
		page.layout(true, true);
		sc.layout();
	}

	@Override
	public boolean performOk() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

		if (changed) {
			for (Entry<String, List<Control>> entry : varControls.entrySet()) {
				String varName = entry.getKey();

				Text textField = (Text) entry.getValue().get(1);
				variables.put(varName, Value.of(textField.getText()));
			}

			ps.getConfigurationService().setProperty(ProjectVariables.PROJECT_PROPERTY_VARIABLES,
					Value.complex(variables));

			changed = false;
		}

		return true;
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
