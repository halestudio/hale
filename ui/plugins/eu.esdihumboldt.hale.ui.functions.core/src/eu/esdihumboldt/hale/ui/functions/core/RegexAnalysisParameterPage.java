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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.string.RegexAnalysis;
import eu.esdihumboldt.cst.functions.string.RegexAnalysisFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page for Regex Analysis function.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RegexAnalysisParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage, RegexAnalysisFunction, ModifyListener {

	private ParameterValue initialRegexValue;
	private ParameterValue initialOutformatValue;
	private Composite page;
	private PropertyDefinition target = null;
	private Text _regexText;
	private Text _outformatText;

	/**
	 * Constructor.
	 */
	public RegexAnalysisParameterPage() {
		super("regex", "Please enter a Regular Expression and the desired Output Format", null);
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// should never be null here, but better be safe than sorry
		if (getWizard().getUnfinishedCell().getTarget() != null) {
			PropertyDefinition propDef = (PropertyDefinition) getWizard().getUnfinishedCell()
					.getTarget().values().iterator().next().getDefinition().getDefinition();
			if (!propDef.equals(target)) {
				// target property definition changed, rebuild editor
				target = propDef;
				createContent(page);
				page.layout();
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		// this page is only for parameter value, ignore params
		if (initialValues == null)
			return;
		List<ParameterValue> regexValues = initialValues.get(PARAMETER_REGEX_PATTERN);
		List<ParameterValue> formatValues = initialValues.get(PARAMETER_OUTPUT_FORMAT);
		if (!regexValues.isEmpty() && !formatValues.isEmpty()) {
			initialRegexValue = regexValues.get(0);
			initialOutformatValue = formatValues.get(0);
			setPageComplete(true);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create(1, 1);
		if (_regexText != null && !_regexText.isDisposed())
			configuration.put(PARAMETER_REGEX_PATTERN, new ParameterValue(
					ParameterValue.DEFAULT_TYPE, Value.of(_regexText.getText())));
		if (_outformatText != null && !_outformatText.isDisposed())
			configuration.put(PARAMETER_OUTPUT_FORMAT, new ParameterValue(
					ParameterValue.DEFAULT_TYPE, Value.of(_outformatText.getText())));
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		if (getWizard().getUnfinishedCell().getTarget() != null) {
			PropertyDefinition propDef = (PropertyDefinition) getWizard().getUnfinishedCell()
					.getTarget().values().iterator().next().getDefinition().getDefinition();
			if (!propDef.equals(target)) {

				String regexTooltip = "A regular expression containing groups (see http://www.javamex.com/tutorials/regular_expressions/capturing_groups.shtml).";
				Group regexGroup = new Group(page, SWT.NONE);
				regexGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				regexGroup.setLayout(new GridLayout(1, false));
				regexGroup.setText("Regular Expression");
				regexGroup.setToolTipText(regexTooltip);
				_regexText = new Text(regexGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				_regexText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				_regexText.setText("");
				_regexText.setToolTipText("");
				_regexText.addModifyListener(this);

				String formatTooltip = "The output format to apply, containing curly brackets delimited group definitions. Ex. {1} represents the result of group 1 from the regex analysis.";

				Group outformatGroup = new Group(page, SWT.NONE);
				outformatGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				outformatGroup.setLayout(new GridLayout(1, false));
				outformatGroup.setText("Output format");
				outformatGroup.setToolTipText(formatTooltip);
				_outformatText = new Text(outformatGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				_outformatText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				_outformatText.setText("");
				_outformatText.setToolTipText(formatTooltip);
				_outformatText.addModifyListener(this);

				Group exampleGroup = new Group(page, SWT.NONE);
				exampleGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				exampleGroup.setLayout(new GridLayout(3, false));
				exampleGroup.setText("Example");

				Label inputLabel = new Label(exampleGroup, SWT.NONE);
				inputLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				inputLabel.setText("Sample text");
				final Text inputText = new Text(exampleGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				inputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				inputText.setText("");

				Button testButton = new Button(exampleGroup, SWT.PUSH);
				GridData testButtonGD = new GridData(SWT.FILL, SWT.FILL, false, false);
				testButtonGD.verticalSpan = 2;
				testButton.setLayoutData(testButtonGD);
				testButton.setText("Test");

				Label outputLabel = new Label(exampleGroup, SWT.NONE);
				outputLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				outputLabel.setText("Sample result");
				final Text outputText = new Text(exampleGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				outputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				outputText.setText("");

				testButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						String convertedString = "No match found.";
						try {
							convertedString = RegexAnalysis.analize(_regexText.getText(),
									_outformatText.getText(), inputText.getText());
							outputText.setText(convertedString);
						} catch (Exception e1) {
							outputText.setText(e1.getLocalizedMessage());
						}
					}
				});
			}
		}
		if (_regexText != null && initialRegexValue != null) {
			_regexText.setText(initialRegexValue.as(String.class));
		}
		if (_outformatText != null && initialOutformatValue != null) {
			_outformatText.setText(initialOutformatValue.as(String.class));
		}

	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		if (_regexText != null && !_regexText.isDisposed() && _outformatText != null
				&& !_outformatText.isDisposed() && _regexText.getText().length() > 0
				&& _outformatText.getText().length() > 0) {
			setPageComplete(true);
		}
		else {
			setPageComplete(false);
		}

	}
}
