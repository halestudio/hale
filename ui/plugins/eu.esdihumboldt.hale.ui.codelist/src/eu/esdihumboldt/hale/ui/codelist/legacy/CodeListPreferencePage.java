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

package eu.esdihumboldt.hale.ui.codelist.legacy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.codelist.internal.Messages;
import eu.esdihumboldt.hale.ui.util.tree.CollectionTreeNodeContentProvider;

/**
 * Code list preference page
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final ALogger log = ALoggerFactory.getLogger(CodeListPreferencePage.class);

	private TreeViewer listViewer;

	private List<SearchPathNode> searchPath;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		page.setLayout(new GridLayout(2, true));

		Label label = new Label(page, SWT.NONE);
		label.setText(Messages.CodeListPreferencePage_0); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));

		// search path list
		listViewer = new TreeViewer(page);
		listViewer.setContentProvider(new CollectionTreeNodeContentProvider());
		listViewer.setLabelProvider(new SearchPathLabelProvider()); // new
																	// MultiColumnTreeNodeLabelProvider(0));
		listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		List<String> paths = CodeListPreferenceInitializer.getSearchPath();
		searchPath = new ArrayList<SearchPathNode>(paths.size());
		for (String path : paths) {
			searchPath.add(new SearchPathNode(path));
		}
		listViewer.setInput(searchPath);

		// add button (using a directory dialog)
		Button add = new Button(page, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		add.setText(Messages.CodeListPreferencePage_1); //$NON-NLS-1$
		add.setToolTipText(Messages.CodeListPreferencePage_2); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				DirectoryDialog dialog = new DirectoryDialog(display.getActiveShell());
				dialog.setText(Messages.CodeListPreferencePage_3); //$NON-NLS-1$
				dialog.setMessage(Messages.CodeListPreferencePage_4); //$NON-NLS-1$
				String path = dialog.open();
				if (path != null) {
					SearchPathNode node = new SearchPathNode(path);
					searchPath.add(node);
					listViewer.refresh(false);
					if (!node.hasChildren()) {
						log.userWarn(MessageFormat.format(Messages.CodeListPreferencePage_5, path));
					}
				}
			}
		});

		// remove button
		Button remove = new Button(page, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		remove.setText(Messages.CodeListPreferencePage_6); //$NON-NLS-1$
		remove.setToolTipText(Messages.CodeListPreferencePage_7); //$NON-NLS-1$
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = listViewer.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					TreeNode selected = (TreeNode) ((IStructuredSelection) selection)
							.getFirstElement();
					while (selected.getParent() != null) {
						selected = selected.getParent();
					}
					searchPath.remove(selected);
					listViewer.refresh(false);
				}
			}

		});

		return page;
	}

//	/**
//	 * @see PreferencePage#performOk()
//	 */
//	@Override
//	public boolean performOk() {
//		// save preferences
//		List<String> paths = new ArrayList<String>(searchPath.size());
//		for (SearchPathNode sp : searchPath) {
//			paths.add(sp.getSearchPath());
//		}
//		CodeListPreferenceInitializer.setSearchPath(paths);
//		
//		// update service
//		CodeListService codeListService = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
//		codeListService.searchPathChanged();
//		
//		return true;
//	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// ignore
	}

}
