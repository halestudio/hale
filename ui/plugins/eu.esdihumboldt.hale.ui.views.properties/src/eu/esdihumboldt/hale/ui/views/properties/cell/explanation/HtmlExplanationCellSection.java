/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.cell.explanation;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellSection;

/**
 * HTML cell explanation section.
 * 
 * @author Simon Templer
 */
public class HtmlExplanationCellSection extends AbstractCellSection {

	private static final ALogger log = ALoggerFactory.getLogger(HtmlExplanationCellSection.class);

	private Browser browser;

	private Text textField;

	private static final AtomicBoolean BROWSER_ERROR_REPORTED = new AtomicBoolean();

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		page.setLayout(GridLayoutFactory.fillDefaults().margins(8, 8).create());

		try {
			browser = new Browser(page, SWT.NONE);
			browser.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		} catch (Throwable e) {
			if (BROWSER_ERROR_REPORTED.compareAndSet(false, true)) {
				log.error("Could not create embedded browser, using text field as fall-back", e);
			}

			textField = new Text(page,
					SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
			textField.setLayoutData(
					GridDataFactory.fillDefaults().hint(17, 17).grab(true, true).create());
		}
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void refresh() {
		super.refresh();

		String text = null;

		Cell cell = getCell();
		if (cell != null) {
			FunctionDefinition<?> function = FunctionUtil
					.getFunction(cell.getTransformationIdentifier(), HaleUI.getServiceProvider());
			if (function != null) {
				CellExplanation explanation = function.getExplanation();
				if (explanation != null) {
					if (browser != null) {
						text = explanation.getExplanationAsHtml(cell, HaleUI.getServiceProvider());
						if (text == null) {
							text = explanation.getExplanation(cell, HaleUI.getServiceProvider());
						}
					}
					else if (textField != null) {
						text = explanation.getExplanation(cell, HaleUI.getServiceProvider());
					}
				}
			}
		}

		if (text == null) {
			text = "Sorry, no explanation available.";
		}

		if (browser != null) {
			browser.setText(text);
		}
		else if (textField != null) {
			textField.setText(text);
		}
	}

}
