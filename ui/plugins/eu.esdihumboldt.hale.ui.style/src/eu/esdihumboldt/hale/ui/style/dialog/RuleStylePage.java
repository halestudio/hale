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
package eu.esdihumboldt.hale.ui.style.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.Filter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.editors.Editor;
import eu.esdihumboldt.hale.ui.style.editors.EditorFactory;
import eu.esdihumboldt.hale.ui.style.editors.LineSymbolizerEditor;
import eu.esdihumboldt.hale.ui.style.editors.PointSymbolizerEditor;
import eu.esdihumboldt.hale.ui.style.editors.PolygonSymbolizerEditor;
import eu.esdihumboldt.hale.ui.style.editors.RuleEditor;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Rule based style editor page
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class RuleStylePage extends FeatureStylePage {

	/**
	 * {@link Rule} list item
	 */
	private class RuleItem {

		private Rule rule;

		/**
		 * Creates a new rule item
		 * 
		 * @param rule the rule
		 */
		public RuleItem(Rule rule) {
			this.rule = rule;
		}

		/**
		 * Get the item's rule
		 * 
		 * @return the rule
		 */
		public Rule getRule() {
			return rule;
		}

		/**
		 * Set the item's rule
		 * 
		 * @param rule the rule to set
		 */
		public void setRule(Rule rule) {
			this.rule = rule;
		}

		/**
		 * @see Object#toString()
		 */
		@Override
		public String toString() {
			String name = getRule().getName();
			if (name == null || name.isEmpty()) {
				return Messages.RuleStylePage_Rule + rules.indexOf(this);
			}
			else {
				return name;
			}
		}

	}

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private static Image addImage = null;

	private static Image removeImage = null;

	private static Image renameImage = null;

	private static Image upImage = null;

	private static Image downImage = null;

	private Button addButton;

	private Button removeButton;

	private Button renameButton;

	private Button upButton;

	private Button downButton;

	private Composite editorArea;

	private ListViewer listViewer;

	private int currentIndex = -1;

	private Editor<Rule> currentEditor = null;

	private List<RuleItem> rules;

	private boolean changed = false;

	private static final ALogger log = ALoggerFactory.getLogger(RuleStylePage.class);

	/**
	 * Creates a new editor page
	 * 
	 * @param parent the parent dialog
	 */
	public RuleStylePage(FeatureStyleDialog parent) {
		super(parent, Messages.RuleStylePage_SuperTitle);

		if (addImage == null) {
			addImage = InstanceStylePlugin.getImageDescriptor("/icons/add.gif").createImage(); //$NON-NLS-1$
		}

		if (removeImage == null) {
			removeImage = InstanceStylePlugin.getImageDescriptor("/icons/remove.gif").createImage(); //$NON-NLS-1$
		}

		if (renameImage == null) {
			renameImage = InstanceStylePlugin.getImageDescriptor("/icons/rename.gif").createImage(); //$NON-NLS-1$
		}

		if (upImage == null) {
			upImage = InstanceStylePlugin.getImageDescriptor("/icons/arrow_up.gif").createImage(); //$NON-NLS-1$
		}

		if (downImage == null) {
			downImage = InstanceStylePlugin
					.getImageDescriptor("/icons/arrow_down.gif").createImage(); //$NON-NLS-1$
		}
	}

	/**
	 * @see FeatureStylePage#getStyle(boolean)
	 */
	@Override
	public Style getStyle(boolean force) throws Exception {
		updateCurrentRule();

		if (force || changed) {
			Rule[] ruleArray = new Rule[rules.size()];
			for (int i = 0; i < rules.size(); i++) {
				Rule rule = rules.get(i).getRule();

				// set else filter
				rule.setElseFilter(rule.getFilter() == null);

				// TODO other rule manipulation?

				ruleArray[i] = rule;
			}

			// create style
			FeatureTypeStyle fts = styleBuilder.createFeatureTypeStyle("Feature", ruleArray); //$NON-NLS-1$
			Style style = styleBuilder.createStyle();
			style.featureTypeStyles().add(fts);
			return style;
		}
		else {
			return null;
		}
	}

	/**
	 * Update the {@link Rule} whose editor is currently open
	 * 
	 * @throws Exception if an error occurs during the update
	 */
	private void updateCurrentRule() throws Exception {
		if (currentEditor != null && currentEditor.isChanged()) {
			Rule rule = currentEditor.getValue();

			if (rule != null) {
				rules.get(currentIndex).setRule(rule);

				changed = true;
			}
		}
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		changed = false;

		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		// DISABLED - this method seems to change the rule order - Rule[]
		// ruleArray = SLD.rules(getParent().getStyle());
		// use list instead:
		List<Rule> ruleList;
		try {
			ruleList = getParent().getStyle().featureTypeStyles().get(0).rules();
		} catch (Exception e) {
			ruleList = new ArrayList<Rule>();
		}

		// init index
		if (ruleList.size() > 0) {
			currentIndex = 0;
		}
		else {
			currentIndex = -1;
		}

		currentEditor = null;

		// populate rule map
		rules = new ArrayList<RuleItem>(ruleList.size() + 5);
		for (int i = 0; i < ruleList.size(); i++) {
			Rule rule = ruleList.get(i);
			if (rule != null) {
				rules.add(new RuleItem(rule));
			}
		}

		// rule list
		Composite ruleArea = new Composite(page, SWT.NONE);
		ruleArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		GridLayout leftLayout = new GridLayout(5, true);
		leftLayout.horizontalSpacing = 1;
		leftLayout.verticalSpacing = 1;
		ruleArea.setLayout(leftLayout);

		// label
		Label rulesLabel = new Label(ruleArea, SWT.NONE);
		rulesLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 5, 1));
		rulesLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		rulesLabel.setText(Messages.RuleStylePage_RuleLabelText);

		// viewer
		listViewer = new ListViewer(ruleArea);
		listViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 5, 1));
		listViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// ignore
			}

			@Override
			public void dispose() {
				// ignore
			}

			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object inputElement) {
				try {
					List<RuleItem> rules = (List<RuleItem>) inputElement;
					return rules.toArray();
				} catch (Exception e) {
					return null;
				}
			}
		});
		listViewer.setInput(rules);

		if (currentIndex >= 0 && currentIndex < rules.size()) {
			listViewer.setSelection(new StructuredSelection(rules.get(currentIndex)));
		}

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				RuleItem item = (RuleItem) ((IStructuredSelection) event.getSelection())
						.getFirstElement();

				int newIndex = rules.indexOf(item);

				if (currentIndex != newIndex) {
					try {
						updateCurrentRule();
					} catch (Exception e) {
						log.userError("Invalid editor state, could not update rule.", e);
						return;
					}
					currentIndex = newIndex;
					updateEditor();
				}
			}
		});

		// buttons
		addButton = new Button(ruleArea, SWT.PUSH);
		addButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		addButton.setImage(addImage);
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addRule();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		addButton.setToolTipText(Messages.RuleStylePage_AddRuleButtonToolTippText);

		removeButton = new Button(ruleArea, SWT.PUSH);
		removeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		removeButton.setImage(removeImage);
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeCurrentRule();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		removeButton.setToolTipText(Messages.RuleStylePage_RemoveRuleButtonToolTippText);

		upButton = new Button(ruleArea, SWT.PUSH);
		upButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		upButton.setImage(upImage);
		upButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveCurrentRuleUp();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		upButton.setToolTipText(Messages.RuleStylePage_UpRuleButtonToolTippText);

		downButton = new Button(ruleArea, SWT.PUSH);
		downButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		downButton.setImage(downImage);
		downButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveCurrentRuleDown();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		downButton.setToolTipText(Messages.RuleStylePage_DownRuleButtonToolTippText);

		renameButton = new Button(ruleArea, SWT.PUSH);
		renameButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		renameButton.setImage(renameImage);
		renameButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				renameCurrentRule();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		renameButton.setToolTipText(Messages.RuleStylePage_RenameRuleButtonToolTippText);

		// editor area
		editorArea = new Composite(page, SWT.NONE);
		editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorArea.setLayout(new FillLayout());

		setControl(page);

		updateEditor();
	}

	/**
	 * Move the current rule up
	 */
	protected void moveCurrentRuleUp() {
		if (currentIndex >= 1 && currentIndex < rules.size()) {
			try {
				updateCurrentRule();
			} catch (Exception e) {
				log.userError("Invalid editor state, could not update rule.", e);
				return;
			}

			RuleItem item1 = rules.get(currentIndex);
			Rule temp = item1.getRule();

			RuleItem item2 = rules.get(--currentIndex);
			item1.setRule(item2.getRule());
			item2.setRule(temp);

			listViewer.refresh(true);
			listViewer.setSelection(new StructuredSelection(item2));
			updateEditor();

			changed = true;
		}
	}

	/**
	 * Move the current rule down
	 */
	protected void moveCurrentRuleDown() {
		if (currentIndex >= 0 && currentIndex < rules.size() - 1) {
			try {
				updateCurrentRule();
			} catch (Exception e) {
				log.userError("Invalid editor state, could not update rule.", e);
				return;
			}

			RuleItem item1 = rules.get(currentIndex);
			Rule temp = item1.getRule();

			RuleItem item2 = rules.get(++currentIndex);
			item1.setRule(item2.getRule());
			item2.setRule(temp);

			listViewer.refresh(true);
			listViewer.setSelection(new StructuredSelection(item2));
			updateEditor();

			changed = true;
		}
	}

	/**
	 * Rename the current rule
	 */
	protected void renameCurrentRule() {
		if (currentIndex >= 0 && currentIndex < rules.size()) {
			RuleItem item = rules.get(currentIndex);
			Rule rule = item.getRule();

			InputDialog dlg = new InputDialog(getShell(), Messages.RuleStylePage_InputDialogTitle,
					Messages.RuleStylePage_InputDialogDescription, rule.getName(), null);

			if (dlg.open() == InputDialog.OK) {
				rule.setName(dlg.getValue());
				listViewer.update(item, null);

				changed = true;
			}
		}
	}

	/**
	 * Remove the current rule
	 */
	protected void removeCurrentRule() {
		if (currentIndex >= 0 && currentIndex < rules.size()) {
			RuleItem item = rules.remove(currentIndex);
			currentIndex--;
			listViewer.remove(item);
			listViewer.refresh(true);
			updateEditor();

			changed = true;
		}
	}

	/**
	 * Add a new {@link Rule}
	 */
	protected void addRule() {
		SymbolizerDialog symDlg = new SymbolizerDialog(getShell());
		symDlg.open();
		Symbolizer symbolizer = symDlg.getSymbolizer();

		if (symbolizer != null) {
			Rule rule = styleBuilder.createRule(symbolizer);
			RuleItem item = new RuleItem(rule);
			rules.add(item);
			listViewer.add(item);

			updateButtonState();

			changed = true;
		}
	}

	/**
	 * Display the editor for the current rule in the editor area
	 */
	private void updateEditor() {
		if (currentEditor != null) {
			currentEditor.getControl().dispose();
		}

		if (currentIndex >= 0 && currentIndex < rules.size()) {
			Rule rule = rules.get(currentIndex).getRule();

			currentEditor = createEditor(rule, editorArea);
			editorArea.layout(true);
		}

		updateButtonState();
	}

	/**
	 * Update the button states
	 */
	private void updateButtonState() {
		boolean valid = currentIndex >= 0 && currentIndex < rules.size();

		removeButton.setEnabled(valid);
		renameButton.setEnabled(valid);

		upButton.setEnabled(valid && currentIndex >= 1);
		downButton.setEnabled(valid && currentIndex < rules.size() - 1);
	}

	/**
	 * Create a rule editor
	 * 
	 * @param rule the rule
	 * @param parent the parent composite
	 * 
	 * @return the {@link Rule} editor
	 */
	private Editor<Rule> createEditor(Rule rule, Composite parent) {
		TypeDefinition type = getParent().getType();
		Filter filter = rule.getFilter();

		Symbolizer symbolizer = null;
		Symbolizer[] symbolizers = rule.getSymbolizers();

		if (symbolizers != null && symbolizers.length > 0) {
			symbolizer = symbolizers[0];
		}

		if (symbolizer == null) {
			// fallback if there is no symbolizer defined
			FeatureTypeStyle fts = StyleHelper.getDefaultStyle(type, getParent().getDataSet());
			symbolizer = fts.rules().get(0).getSymbolizers()[0];
		}

		Editor<Rule> editor;

		if (symbolizer instanceof PointSymbolizer) {
			editor = createEditor(parent, type, filter, PointSymbolizer.class,
					(PointSymbolizer) symbolizer);
		}
		else if (symbolizer instanceof PolygonSymbolizer) {
			editor = createEditor(parent, type, filter, PolygonSymbolizer.class,
					(PolygonSymbolizer) symbolizer);
		}
		else { // TODO support other symbolizers
				// default: LineSymbolizer
			editor = createEditor(parent, type, filter, LineSymbolizer.class,
					(LineSymbolizer) symbolizer);
		}

		return editor;
	}

	private static <T extends Symbolizer> RuleEditor<?> createEditor(Composite parent,
			TypeDefinition ft, Filter filter, Class<T> type, T symbolizer) {

		if (PointSymbolizer.class.isAssignableFrom(type)) {
			return new RuleEditor<PointSymbolizer>(parent, ft, filter, PointSymbolizer.class,
					(PointSymbolizer) symbolizer, new EditorFactory<PointSymbolizer>() {

						@Override
						public Editor<PointSymbolizer> createEditor(Composite parent,
								PointSymbolizer value) {
							return new PointSymbolizerEditor(parent, value);
						}
					});
		}
		else if (PolygonSymbolizer.class.isAssignableFrom(type)) {
			return new RuleEditor<PolygonSymbolizer>(parent, ft, filter, PolygonSymbolizer.class,
					(PolygonSymbolizer) symbolizer, new EditorFactory<PolygonSymbolizer>() {

						@Override
						public Editor<PolygonSymbolizer> createEditor(Composite parent,
								PolygonSymbolizer value) {
							return new PolygonSymbolizerEditor(parent, value);
						}
					});
		}
		else {
			return new RuleEditor<LineSymbolizer>(parent, ft, filter, LineSymbolizer.class,
					(LineSymbolizer) symbolizer, new EditorFactory<LineSymbolizer>() {

						@Override
						public Editor<LineSymbolizer> createEditor(Composite parent,
								LineSymbolizer value) {
							return new LineSymbolizerEditor(parent, value);
						}
					});
		}
		// FIXME Does not work properly
	}

}
