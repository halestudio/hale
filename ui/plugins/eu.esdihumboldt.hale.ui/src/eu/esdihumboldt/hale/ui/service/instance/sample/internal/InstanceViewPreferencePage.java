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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Preference page for instance view settings.
 * 
 * @author Simon Templer
 */
public class InstanceViewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * The preference page ID as registered in the extension point.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.preferences.instanceview";

	private final Map<String, Value> samplerSettings = new HashMap<>();

	private Button enabled;

	private AttributeEditor<Value> currentEditor;

	private Sampler currentSampler;

	private ComboViewer samplers;

	private Group samplerGroup;

	private Button occurringValuesComplete;

	private boolean ov_changed = false;

	private boolean changed = false;

	private final IPropertyChangeListener editorListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (AttributeEditor.VALUE.equals(event.getProperty())) {
				changed = true;
			}
		}
	};

	@Override
	protected Control createContents(Composite parent) {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(page);

		// current sampler settings
		samplerSettings.clear();
		for (Entry<String, Sampler> entry : InstanceViewPreferences.SAMPLERS.entrySet()) {
			Value settings = ps.getConfigurationService()
					.getProperty(InstanceViewPreferences.KEY_SETTINGS_PREFIX + entry.getKey());
			if (settings.isEmpty()) {
				settings = entry.getValue().getDefaultSettings();
			}
			samplerSettings.put(entry.getKey(), settings);
		}

		// sampler group
		samplerGroup = new Group(page, SWT.NONE);
		samplerGroup.setText("Instance sampling");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(samplerGroup);
		GridLayoutFactory.swtDefaults().applyTo(samplerGroup);

		// enabled button
		enabled = new Button(samplerGroup, SWT.CHECK);
		enabled.setText("Use a sub-set of the imported source data as specified below:");
		enabled.setSelection(ps.getConfigurationService().getBoolean(
				InstanceViewPreferences.KEY_ENABLED, InstanceViewPreferences.ENABLED_DEFAULT));
		enabled.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
			}
		});

		// sampler selector
		samplers = new ComboViewer(samplerGroup);
		samplers.setContentProvider(ArrayContentProvider.getInstance());
		samplers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Sampler) {
					return ((Sampler) element).getDisplayName(Value.NULL);
				}
				return super.getText(element);
			}

		});
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false);

		samplers.setInput(InstanceViewPreferences.SAMPLERS.values());

		samplers.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection.isEmpty()) {
					updateEditor(null);
				}
				else {
					if (selection instanceof IStructuredSelection) {
						updateEditor(
								(Sampler) ((IStructuredSelection) selection).getFirstElement());
					}
				}
				changed = true;
			}

		});

		// restore the selected sampler
		String samplerId = ps.getConfigurationService().get(InstanceViewPreferences.KEY_SAMPLER,
				InstanceViewPreferences.SAMPLER_FIRST);
		Sampler selectedSampler = InstanceViewPreferences.SAMPLERS.get(samplerId);
		if (selectedSampler != null) {
			samplers.setSelection(new StructuredSelection(selectedSampler));
			changed = false;
		}

		// occurring values group
		Group ovGroup = new Group(page, SWT.NONE);
		ovGroup.setText("Occurring values");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(ovGroup);
		GridLayoutFactory.swtDefaults().applyTo(ovGroup);

		// occurring values button
		occurringValuesComplete = new Button(ovGroup, SWT.CHECK);
		occurringValuesComplete.setText(
				"Always use complete source data to determine occurring values (ignore sampling)");
		occurringValuesComplete.setSelection(ps.getConfigurationService().getBoolean(
				InstanceViewPreferences.KEY_OCCURRING_VALUES_USE_EXTERNAL,
				InstanceViewPreferences.OCCURRING_VALUES_EXTERNAL_DEFAULT));
		occurringValuesComplete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ov_changed = true;
			}
		});

		return page;
	}

	/**
	 * Update the sampler editor.
	 * 
	 * @param newSampler the new selected sampler
	 */
	protected void updateEditor(Sampler newSampler) {
		if (currentEditor != null) {
			// store value for current sampler
			Value setting = currentEditor.getValue();
			samplerSettings.put(InstanceViewPreferences.SAMPLERS.inverse().get(currentSampler),
					setting);

			// dispose current editor
			currentEditor.setPropertyChangeListener(null);
			currentEditor.getControl().dispose();
		}

		if (newSampler != null) {
			// create the editor
			currentEditor = newSampler.createEditor(samplerGroup);
			if (currentEditor != null) {
				// set the editor value
				currentEditor.setValue(samplerSettings
						.get(InstanceViewPreferences.SAMPLERS.inverse().get(newSampler)));
				currentEditor.setPropertyChangeListener(editorListener);
			}
		}
		else {
			currentEditor = null;
		}

		currentSampler = newSampler;

		// updated and re-layout controls
		samplerGroup.layout(true);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// update the enabled button with the default
		enabled.setSelection(InstanceViewPreferences.ENABLED_DEFAULT);

		// update the settings map with default values
		for (Entry<String, Sampler> entry : InstanceViewPreferences.SAMPLERS.entrySet()) {
			samplerSettings.put(entry.getKey(), entry.getValue().getDefaultSettings());
		}

		// update the current editor
		if (currentEditor != null) {
			// set the editor value
			currentEditor.setValue(samplerSettings
					.get(InstanceViewPreferences.SAMPLERS.inverse().get(currentSampler)));
		}

		// select the default sampler
		Sampler selectedSampler = InstanceViewPreferences.SAMPLERS
				.get(InstanceViewPreferences.SAMPLER_FIRST);
		if (selectedSampler != null) {
			samplers.setSelection(new StructuredSelection(selectedSampler));
		}
		else {
			samplers.setSelection(StructuredSelection.EMPTY);
		}

		changed = true;

		// update the occurring values button with the default
		occurringValuesComplete
				.setSelection(InstanceViewPreferences.OCCURRING_VALUES_EXTERNAL_DEFAULT);

		ov_changed = true;
	}

	@Override
	public boolean performOk() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

		if (changed) {
			if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					"Reload source data",
					"Applying the new settings will result in the source data being reloaded.")) {
				return false;
			}

			// save the enabled state
			ps.getConfigurationService().setBoolean(InstanceViewPreferences.KEY_ENABLED,
					enabled.getSelection());

			// store the current editor value in the map
			if (currentEditor != null) {
				// store value for current sampler
				Value setting = currentEditor.getValue();
				samplerSettings.put(InstanceViewPreferences.SAMPLERS.inverse().get(currentSampler),
						setting);
			}

			// store the map in the configuration
			for (Entry<String, Value> entry : samplerSettings.entrySet()) {
				ps.getConfigurationService().setProperty(
						InstanceViewPreferences.KEY_SETTINGS_PREFIX + entry.getKey(),
						entry.getValue());
			}

			// store the selected sampler
			Sampler selectedSampler = null;
			if (!samplers.getSelection().isEmpty()) {
				selectedSampler = (Sampler) ((IStructuredSelection) samplers.getSelection())
						.getFirstElement();
			}
			if (selectedSampler != null) {
				ps.getConfigurationService().set(InstanceViewPreferences.KEY_SAMPLER,
						InstanceViewPreferences.SAMPLERS.inverse().get(selectedSampler));
			}

			// reload the data
			ps.reloadSourceData();

			changed = false;
		}

		if (ov_changed) {
			ps.getConfigurationService().setBoolean(
					InstanceViewPreferences.KEY_OCCURRING_VALUES_USE_EXTERNAL,
					occurringValuesComplete.getSelection());

			ov_changed = false;
		}

		return true;
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
