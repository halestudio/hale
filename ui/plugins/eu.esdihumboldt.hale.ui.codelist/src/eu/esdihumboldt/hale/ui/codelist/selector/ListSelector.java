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

package eu.esdihumboldt.hale.ui.codelist.selector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.ui.codelist.internal.Messages;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;

/**
 * A component to select a code list from already loaded code lists.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ListSelector implements CodeListSelector {

	private final Composite page;
	private final ListViewer listViewer;
	private final List<CodeList> codeLists;

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 */
	public ListSelector(Composite parent) {
		page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		page.setLayout(gridLayout);

		CodeListService codeListService = PlatformUI.getWorkbench()
				.getService(CodeListService.class);

		codeLists = codeListService.getCodeLists();
		Collections.sort(codeLists, new Comparator<CodeList>() {

			@Override
			public int compare(CodeList o1, CodeList o2) {
				int result = o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());

				if (result == 0) {
					result = o1.getNamespace().compareToIgnoreCase(o2.getNamespace());
				}

				return result;
			}
		});

		// search field
		String tip = Messages.ListSelector_0; // $NON-NLS-1$

		Label searchLabel = new Label(page, SWT.NONE);
		searchLabel.setText(Messages.ListSelector_1); // $NON-NLS-1$
		searchLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		searchLabel.setToolTipText(tip);

		final Text searchText = new Text(page,
				SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchText.setToolTipText(tip);

		// list viewer
		listViewer = new ListViewer(page, SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL | SWT.SINGLE);
		listViewer.setContentProvider(ArrayContentProvider.getInstance());
		listViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof CodeList) {
					CodeList codeList = (CodeList) element;
					if (Objects.equals(codeList.getIdentifier(), codeList.getNamespace())) {
						return codeList.getIdentifier();
					}
					else {
						return codeList.getIdentifier() + " (" + codeList.getNamespace() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					return super.getText(element);
				}
			}
		});

		listViewer.setInput(codeLists);

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 10 * listViewer.getList().getItemHeight();
		listViewer.getControl().setLayoutData(layoutData);

		// info
		final Text info = new Text(page,
				SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);

		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 6 * listViewer.getList().getItemHeight();
		info.setLayoutData(layoutData);

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					CodeList codeList = (CodeList) ((IStructuredSelection) selection)
							.getFirstElement();
					String desc = codeList.getDescription();
					if (desc != null) {
						info.setText(desc);
					}
					else {
						info.setText(Messages.ListSelector_4); // $NON-NLS-1$
					}
				}
				else {
					info.setText(Messages.ListSelector_5); // $NON-NLS-1$
				}
			}
		});

		// search filter & update
		listViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				String filterText = searchText.getText();
				// handle empty filter
				if (filterText == null || filterText.isEmpty()) {
					return true;
				}

				if (element instanceof CodeList) {
					CodeList codeList = (CodeList) element;
					filterText = filterText.toLowerCase();

					if (codeList.getIdentifier().toLowerCase().contains(filterText))
						return true;
					if (codeList.getNamespace().toLowerCase().contains(filterText))
						return true;
					if (codeList.getDescription() != null
							&& codeList.getDescription().toLowerCase().contains(filterText))
						return true;
				}

				return false;
			}
		});
		searchText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// refilter
				listViewer.refresh();
			}
		});
	}

	/**
	 * @see CodeListSelector#getCodeList()
	 */
	@Override
	public CodeList getCodeList() {
		ISelection selection = listViewer.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			return (CodeList) ((IStructuredSelection) selection).getFirstElement();
		}

		return null;
	}

	/**
	 * @see CodeListSelector#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * Select the given code list.
	 * 
	 * @param codeList the code list
	 * @return true, if the code list was selected
	 */
	public boolean selectCodeList(CodeList codeList) {
		if (codeList != null && codeLists.contains(codeList)) {
			listViewer.setSelection(new StructuredSelection(codeList), true);
			return true;
		}
		else
			return false;
	}
}
