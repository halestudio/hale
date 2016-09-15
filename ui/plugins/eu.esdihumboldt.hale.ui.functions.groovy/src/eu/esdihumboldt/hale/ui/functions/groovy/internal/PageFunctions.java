/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Argument;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Tools for helper function dialog page
 * 
 * @author Sameer Sheikh
 */
public class PageFunctions extends DialogTray implements GroovyConstants {

	private static final AtomicBoolean BROWSER_ERROR_REPORTED = new AtomicBoolean();
	private static final ALogger log = ALoggerFactory.getLogger(PageFunctions.class);
	private static final String TAB_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;";

	private Text textField;
	private Browser browser = null;

	/**
	 * Creates a tool item for a helper function dialog page
	 * 
	 * @param toolbar the tool bar
	 * @param page the dialog page
	 */
	public static void createToolItem(ToolBar toolbar, final HaleWizardPage<?> page) {
		ToolItem item = new ToolItem(toolbar, SWT.PUSH);
		item.setToolTipText("Show functions");
		item.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_FUNCTION));
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog tray = (TrayDialog) page.getContainer();
					if (tray.getTray() != null) {
						tray.closeTray();
					}
					tray.openTray(new PageFunctions());
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing

			}
		});

	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogTray#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(comp);
		Label label = new Label(comp, SWT.NONE);
		label.setText("Functions Overview");
		label.setFont(JFaceResources.getHeaderFont());

		// tree viwever
		PatternFilter patternFilter = new TreePathPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(comp,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);

		TreeViewer tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		HelperFunctionLabelProvider labelProvider = new HelperFunctionLabelProvider();
		tree.setLabelProvider(labelProvider);
		IContentProvider contentProvider;

		HelperFunctionsService functions = HaleUI.getServiceProvider()
				.getService(HelperFunctionsService.class);

		contentProvider = new TreePathProviderAdapter(new HelperFunctionContentProvider(functions));

		tree.setContentProvider(contentProvider);
		GridDataFactory.fillDefaults().grab(true, true).hint(280, 400).applyTo(filteredTree);

		tree.setComparator(new HelperFunctionComparator());

		tree.setInput(Category.ROOT);

		// Examples
		Label example = new Label(comp, SWT.NONE);
		example.setText("Function documentation");

		try {
			browser = new Browser(comp, SWT.WRAP | SWT.BORDER);
			browser.setLayoutData(
					GridDataFactory.fillDefaults().grab(true, true).hint(300, 250).create());
		} catch (Throwable e) {

			if (BROWSER_ERROR_REPORTED.compareAndSet(false, true)) {
				log.error("Could not create embedded browser, using text field as fall-back", e);
			}

			textField = new Text(comp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			textField.setLayoutData(
					GridDataFactory.fillDefaults().grab(true, true).hint(300, 250).create());

		}

		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				String eg = null;
				HelperFunctionSpecification hfs = null;

				if (!event.getSelection().isEmpty()) {
					TreeSelection treesel = (TreeSelection) event.getSelection();
					TreePath[] paths = treesel.getPaths();
					if (paths != null) {
						TreePath path = paths[0];
						for (int i = 0; i < path.getSegmentCount(); i++) {
							if (path.getSegment(i) instanceof Category) {
								eg = "Select a function to see its documentation.";
								if (browser != null) {
									browser.setText(eg);
								}
								else if (textField != null) {
									textField.setText(eg);
								}
							}
							else if (path.getSegment(i) instanceof HelperFunctionOrCategory) {

								HelperFunctionOrCategory hfoc = ((HelperFunctionOrCategory) path
										.getSegment(i));

								try {
									hfs = (HelperFunctionSpecification) hfoc.asFunction()
											.getSpec(hfoc.getName());
								} catch (Exception e) {
									log.error(
											"There is a problem in retrieving the specification for a helper function.",
											e);
								}

								// displaying the specification
								if (browser != null) {
									eg = getFunctionSpecHTML(hfs);
									browser.setText(eg);
								}
								else if (textField != null) {
									eg = getFunctionSpecText(hfs);
									textField.setText(eg);
								}

							}
						}
					}
				}

			}

		});
		return comp;

	}

	/**
	 * get the specification of a given function
	 * 
	 * @param spec helper function specification
	 * @return the specification text display for a helper function.
	 * 
	 */
	private String getFunctionSpecText(HelperFunctionSpecification spec) {

		StringBuilder example = new StringBuilder();

//		example.append("Description:");
//		example.append("\n\t");
		example.append(spec.getDescription());
		example.append(" \n\nParameters: ");
		for (Argument arg : spec.getArguments()) {
			example.append("\n \t");
			example.append(arg.getName());
			example.append(": ");
			example.append(arg.getDescription());
			if (getDefaultValueToDisplay(arg) != null) {
				example.append(" - ");
				example.append("default value: ");
				example.append(getDefaultValueToDisplay(arg) + " ("
						+ arg.getDefaultValue().getClass().getSimpleName() + ")");
			}
		}
		example.append("\n\nReturns: \n");
		example.append("\t");
		example.append(spec.getResultDescription());

		return example.toString();
	}

	/**
	 * Returns a HTML string representing the additional information of a
	 * function.
	 * 
	 * @param hfs Helper function specification for a function
	 * @return additional message in HTML string
	 */
	public static String getFunctionSpecHTML(HelperFunctionSpecification hfs) {

		StringBuilder example = new StringBuilder("<div>");
//		example.append(" <H3>Description:</H3> ");
//		example.append(TAB_SPACE);
		example.append(hfs.getDescription());

		example.append("<br><br><h3>Parameters:</h3>");

		for (Argument arg : hfs.getArguments()) {

			example.append("<b>");
			example.append(TAB_SPACE + arg.getName());
			example.append("</b>");
			example.append(": ");
			example.append(arg.getDescription());
			if (getDefaultValueToDisplay(arg) != null)
				example.append(" - default value: " + getDefaultValueToDisplay(arg) + " ("
						+ arg.getDefaultValue().getClass().getSimpleName() + ")");
			example.append("<br>");

		}
		example.append("<br><h3>Returns: </h3>");

		example.append(TAB_SPACE + hfs.getResultDescription());
		example.append("</div>");

		return example.toString();
	}

	@Nullable
	private static String getDefaultValueToDisplay(Argument arg) {
		Object defaultValue = arg.getDefaultValue();
		if (defaultValue instanceof Number || defaultValue instanceof Boolean) {
			return String.valueOf(defaultValue);
		}
		else if (defaultValue instanceof String) {
			return "'" + defaultValue + "'";
		}
		return null;
	}

	/**
	 * 
	 * Gets formatted parameters of a function as a String with parameters
	 * separated by comma
	 * 
	 * @param hfs Helper function specification
	 * @return the formated parameters as string separated by comma
	 */
	public static StyledString getStyledParameters(HelperFunctionSpecification hfs) {
		StyledString s = new StyledString();
		int argSize = hfs.getArguments().size();
		s.append("(", StyledString.DECORATIONS_STYLER);

		Iterator<Argument> x = hfs.getArguments().iterator();
		if (x.hasNext()) {
			Argument arg = x.next();

			s.append(arg.getName(), StyledString.DECORATIONS_STYLER);

			if (argSize > 1) {
				s.append(": ", StyledString.DECORATIONS_STYLER);
				if (getDefaultValueToDisplay(arg) != null) {
					s.append(getDefaultValueToDisplay(arg), StyledString.COUNTER_STYLER);
				}
				else {
					s.append("?", StyledString.COUNTER_STYLER);
				}
			}
			while (x.hasNext()) {
				Argument arg1 = x.next();
				s.append(", " + arg1.getName(), StyledString.DECORATIONS_STYLER);
				s.append(": ", StyledString.DECORATIONS_STYLER);
				if (getDefaultValueToDisplay(arg1) != null) {
					s.append(getDefaultValueToDisplay(arg1), StyledString.COUNTER_STYLER);
				}
				else {
					s.append("?", StyledString.COUNTER_STYLER);
				}
			}
		}
		s.append(")", StyledString.DECORATIONS_STYLER);

		return s;

	}
}
