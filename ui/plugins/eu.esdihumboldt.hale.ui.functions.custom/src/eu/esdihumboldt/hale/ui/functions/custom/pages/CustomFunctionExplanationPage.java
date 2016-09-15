/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.custom.pages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomFunctionExplanation;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.functions.custom.CustomPropertyFunctionWizard;

/**
 * Page for editing custom function explanations.
 * 
 * @author Simon Templer
 */
public class CustomFunctionExplanationPage extends HaleWizardPage<CustomPropertyFunctionWizard>
		implements CustomFunctionWizardPage {

	private static class TabGroup {

		private final TabItem tab;
		private final Text editor;

		public TabGroup(TabItem tab, Text editor) {
			super();
			this.tab = tab;
			this.editor = editor;
		}

		public TabItem getTab() {
			return tab;
		}

		public Text getEditor() {
			return editor;
		}

	}

	private static final AtomicBoolean BROWSER_ERROR_REPORTED = new AtomicBoolean();

	private static final ALogger log = ALoggerFactory
			.getLogger(CustomFunctionExplanationPage.class);

	private Text previewText;

	private Browser previewBrowser;

	private TabFolder tabFolder;

	private final Map<Locale, TabGroup> tabs = new HashMap<>();

	/**
	 * Default constructor.
	 */
	public CustomFunctionExplanationPage() {
		super("explanation");

		// no explanation required
		setPageComplete(true);

		setTitle("Function explanation");
		setDescription("Markdown templates for generating mapping cell explanations");
	}

	@Override
	public void apply() {
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		if (cf == null)
			return;

		Map<Locale, Value> templates = new HashMap<>();
		tabs.forEach((locale, group) -> {
			String text = group.getEditor().getText();
			if (text != null && !text.isEmpty()) {
				templates.put(locale, Value.of(new eu.esdihumboldt.hale.common.core.io.Text(text)));
			}
		});
		DefaultCustomFunctionExplanation explanation = new DefaultCustomFunctionExplanation(
				templates);
		cf.setExplanation(explanation);
	}

	@Override
	protected void createContent(Composite page) {
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		DefaultCustomFunctionExplanation expl = cf.getExplanation();
		Map<Locale, Value> initialContent = null;
		if (expl != null) {
			initialContent = expl.getTemplates();
		}

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(page);

		// top part - editor and locale controls
		createMainPart(page, initialContent);

		// bottom part - explanation preview
//		createPreviewPart(page);
	}

	private void createMainPart(Composite page, @Nullable Map<Locale, Value> initialContent) {
		Composite main = new Composite(page, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(main);

		// left part - editor tabs
		tabFolder = new TabFolder(main, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFolder);
		if (initialContent != null && !initialContent.isEmpty()) {
			for (Entry<Locale, Value> entry : initialContent.entrySet()) {
				addTab(entry.getKey(),
						entry.getValue().as(eu.esdihumboldt.hale.common.core.io.Text.class));
			}
		}
		else {
			// per default, add an empty explanation for the root locale
			addTab(Locale.ROOT, null);
		}

		// right part - controls
		Composite actions = new Composite(main, SWT.NONE);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(actions);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(actions);

		// TODO add a language
//		Button addLanguage = new Button(actions, SWT.PUSH | SWT.FLAT);
//		addLanguage.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
//		addLanguage.setToolTipText("Add another explanation language");

		// TODO remove a language (prevent removing default)

	}

	private void addTab(Locale locale, eu.esdihumboldt.hale.common.core.io.Text content) {
		String name = locale.getDisplayLanguage();
		if (name == null || name.isEmpty()) {
			// root locale
			name = "Default";
		}

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(name);

		// created editor
		// TODO use different component for editing - source viewer?
		Text editor = new Text(tabFolder, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		if (content != null && content.getText() != null) {
			editor.setText(content.getText());
		}

		// associate to tab
		item.setControl(editor);

		// add to map
		TabGroup group = new TabGroup(item, editor);
		tabs.put(locale, group);
	}

	private void createPreviewPart(Composite page) {
		GridDataFactory df = GridDataFactory.fillDefaults().grab(true, false).hint(10, 200);
		try {
			previewBrowser = new Browser(page, SWT.NONE);
			df.applyTo(previewBrowser);
		} catch (Throwable e) {
			if (BROWSER_ERROR_REPORTED.compareAndSet(false, true)) {
				log.error("Could not create embedded browser, using text field as fall-back", e);
			}

			previewText = new Text(page,
					SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
			df.applyTo(previewText);
		}
	}

}
