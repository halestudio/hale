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

package eu.esdihumboldt.hale.ui.service.groovy.internal;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.common.align.custom.CustomPropertyFunctionType;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.scripting.scripts.groovy.GroovyScript;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyRestrictionException;
import groovy.lang.Script;

/**
 * Groovy service utilizing preferences to save project restriction exceptions.
 * 
 * @author Kai Schwierczek
 */
public class PreferencesGroovyService extends DefaultGroovyService {

	private static final String PREFERENCE_NAME = "groovy.restriction-exceptions";

	private final ProjectService projectService;
	private final AlignmentService alignmentService;

	// for caching the value for the current project
	private volatile boolean restrictionActive = true;
	private URI restrictionActiveURI = null;
	/**
	 * scriptHash must not be accessed directly, but through
	 * {@link #getScriptHash()}.
	 */
	private String scriptHash = null;
	private boolean askedForAllowance = false;

	/**
	 * Constructs the service.
	 * 
	 * @param projectService the project service, needed for project URI and
	 *            clean information
	 * @param alignmentService the alignment service, needed to get Groovy
	 *            scripts from cells
	 */
	public PreferencesGroovyService(ProjectService projectService,
			AlignmentService alignmentService) {
		this.projectService = projectService;
		this.alignmentService = alignmentService;
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				if (restrictionActive == false) {
					restrictionActive = true;
					notifyRestrictionChanged(true);
				}
				restrictionActiveURI = null;
				scriptHash = null;
				askedForAllowance = false;
			}

			@Override
			public void afterSave(ProjectService projectService) {
				projectSaved();
			}

			@Override
			public void afterLoad(ProjectService projectService) {
				projectLoaded();
			}
		});
		alignmentService.addListener(new AlignmentServiceListener() {

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void alignmentCleared() {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void customFunctionsChanged() {
				PreferencesGroovyService.this.alignmentChanged();
			}

			@Override
			public void alignmentChanged() {
				PreferencesGroovyService.this.alignmentChanged();
			}
		});
	}

	@Override
	public <T> T evaluate(Script script, ResultProcessor<T> processor) throws Exception {
		try {
			return super.evaluate(script, processor);
		} catch (GroovyRestrictionException e) {
			if (!askedForAllowance && askForAllowance()) {
				return super.evaluate(script, processor);
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Ask for allowance to run script with full permissions.
	 * 
	 * @return true, if allowance was given.
	 */
	private synchronized boolean askForAllowance() {
		// synchronization...
		if (!askedForAllowance) {
			final AtomicBoolean disableRestriction = new AtomicBoolean(false);
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					// check if it was enabled previously
					String message = "A Groovy script tries using a restricted method, do you want to lift all restrictions?";
					if (restrictionActiveURI != null) {
						String hash = loadPreferences().get(restrictionActiveURI);
						if (hash != null) {
							message += "\nA previous version of the current project had these additional rights, but was modified since.";
						}
					}
					message += "\n\nWARNING: The Groovy script can then do \"anything\", so be sure to trust your source!";
					boolean result = MessageDialog.openQuestion(
							Display.getCurrent().getActiveShell(), "Groovy script restriction",
							message);
					disableRestriction.set(result);
				}
			});

			// careful: setRestrictionActive has to stay in this thread (not
			// Display => deadlock)!
			if (disableRestriction.get()) {
				setRestrictionActive(false);
			}
			askedForAllowance = true;
		}

		return !restrictionActive;
	}

	@Override
	public void setRestrictionActive(final boolean active) {
		if (restrictionActive != active) {
			restrictionActive = active;
			if (restrictionActiveURI != null) {
				Map<URI, String> preferences = loadPreferences();
				if (!restrictionActive)
					preferences.put(restrictionActiveURI, getScriptHash());
				else
					preferences.remove(restrictionActiveURI);
				savePreferences(preferences);
			}

			// notification must happen asynchronously
			// (otherwise loading a project may fail)
			// XXX use another possibility, not the display thread?
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					notifyRestrictionChanged(active);
				}
			});
		}
	}

	@Override
	public boolean isRestrictionActive() {
		return restrictionActive;
	}

	/**
	 * Calculates the current alignments script hash.
	 * 
	 * @return the current alignments script hash
	 */
	private synchronized String getScriptHash() {
		if (scriptHash == null) {
			List<String> scripts = new ArrayList<>();
			// get all Groovy scripts
			for (Cell cell : alignmentService.getAlignment().getCells()) {
				ListMultimap<String, ParameterValue> parameters = cell
						.getTransformationParameters();
				if (parameters == null)
					continue;
				// Groovy transformations
				if (cell.getTransformationIdentifier().contains("groovy")) {
					List<ParameterValue> val = parameters.get(GroovyConstants.PARAMETER_SCRIPT);
					if (!val.isEmpty()) {
						String script = getScriptString(val.get(0));
						if (script != null) {
							scripts.add(script);
						}
					}
				}
				// GroovyScript function parameters
				for (ParameterValue value : parameters.values()) {
					if (GroovyScript.GROOVY_SCRIPT_ID.equals(value.getType())) {
						String script = getScriptString(value);
						if (script != null) {
							scripts.add(script);
						}
					}
				}
			}

			// Groovy scripts of custom property functions
			for (CustomPropertyFunction customFunction : alignmentService.getAlignment()
					.getAllCustomPropertyFunctions().values()) {
				if (customFunction instanceof DefaultCustomPropertyFunction) {
					DefaultCustomPropertyFunction cf = (DefaultCustomPropertyFunction) customFunction;
					if (CustomPropertyFunctionType.GROOVY.equals(cf.getFunctionType())) {
						Value functionDef = cf.getFunctionDefinition();
						if (functionDef != null && !functionDef.isEmpty()) {
							String script = getScriptString(functionDef);
							if (script != null) {
								scripts.add(script);
							}
						}
					}
				}
			}

			// order scripts (for consistent hash)
			Collections.sort(scripts);

			// compute hash
			// not simply using hashCode, because it would be far to easy to
			// modify the script in a undetectable way
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				for (String script : scripts)
					md.update(script.getBytes("UTF-8"));
				byte[] hash = md.digest();
				StringBuilder sb = new StringBuilder(2 * hash.length);
				for (byte b : hash) {
					sb.append(String.format("%02x", b & 0xff));
				}
				scriptHash = sb.toString();
				// Both exceptions cannot happen in a valid Java platform.
				// Anyways, if they happen, execution should stop here!
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("No MD5 MessageDigest!");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("No UTF-8 Charset!");
			}
		}
		return scriptHash;
	}

	/**
	 * Call when alignment changed.
	 */
	private void alignmentChanged() {
		// Reset script hash, only recompute when necessary!
		scriptHash = null;
	}

	/**
	 * Call when the project was saved.
	 */
	private void projectSaved() {
		// update preferences if restriction disabled
		if (!restrictionActive) {
			restrictionActiveURI = projectService.getLoadLocation();
			Map<URI, String> preferences = loadPreferences();
			preferences.put(restrictionActiveURI, getScriptHash());
			savePreferences(preferences);
		}
	}

	/**
	 * Call when a project was loaded
	 */
	protected void projectLoaded() {
		URI location = projectService.getLoadLocation();
		if (!Objects.equal(location, restrictionActiveURI)) {
			restrictionActiveURI = location;
			if (location == null) {
				restrictionActive = true;
			}
			else {
				String hash = loadPreferences().get(location);
				boolean hashChecked = hash != null && getScriptHash().equals(hash);
				restrictionActive = !hashChecked;
				// XXX inform user directly if the hash was invalid?
				if (!restrictionActive) {
					notifyRestrictionChanged(restrictionActive);
				}
			}
		}
	}

	private String getScriptString(Value value) {
		Text text = value.as(Text.class);
		if (text != null) {
			return text.getText();
		}
		return value.as(String.class);
	}

	private Map<URI, String> loadPreferences() {
		String preferenceString = HALEUIPlugin.getDefault().getPreferenceStore()
				.getString(PREFERENCE_NAME);
		Map<URI, String> preferences = new HashMap<>();
		if (preferenceString.isEmpty())
			return preferences;
		String[] entries = preferenceString.split(" ");
		for (int i = 0; i < entries.length; i += 2) {
			preferences.put(URI.create(entries[i]), entries[i + 1]);
		}
		return preferences;
	}

	private void savePreferences(Map<URI, String> preferences) {
		StringBuilder sb = new StringBuilder();
		for (Entry<URI, String> entry : preferences.entrySet()) {
			sb.append(entry.getKey().toString()).append(' ');
			sb.append(entry.getValue()).append(' ');
		}
		HALEUIPlugin.getDefault().getPreferenceStore().setValue(PREFERENCE_NAME, sb.toString());
	}
}
