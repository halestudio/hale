/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import de.fhg.igd.mapviewer.server.wms.capabilities.Layer;

/**
 * Dialog for selecting and ordering {@link Layer}s
 * 
 * @author Simon Templer
 */
public class LayersDialog extends TitleAreaDialog {

	/**
	 * Check state provider for {@link Layer}s
	 */
	private static class LayerCheckStateProvider implements ICheckStateProvider {

		/**
		 * @see ICheckStateProvider#isChecked(java.lang.Object)
		 */
		@Override
		public boolean isChecked(Object element) {
			if (element instanceof Layer) {
				return ((Layer) element).isSelected();
			}
			else {
				return false;
			}
		}

		/**
		 * @see ICheckStateProvider#isGrayed(java.lang.Object)
		 */
		@Override
		public boolean isGrayed(Object element) {
			return false;
		}

	}

	/**
	 * Label provider for {@link Layer}s
	 */
	private static class LayerLabelProvider extends BaseLabelProvider
			implements ITableLabelProvider {

		/**
		 * @see ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Layer) {
				Layer layer = (Layer) element;
				switch (columnIndex) {
				case 0:
					return layer.getDisplayName();
				case 1:
					return layer.getDescription();
				default:
					return null;
				}
			}
			else {
				return element.toString();
			}
		}

	}

	private final List<Layer> layers;

	/**
	 * Constructor
	 * 
	 * @param parentShell the shell
	 * @param layers the layer list
	 */
	public LayersDialog(Shell parentShell, final List<Layer> layers) {
		super(parentShell);

		this.layers = layers;
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setMessage(Messages.LayersDialog_0);
		setTitle(Messages.LayersDialog_1);

		return control;
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);
		page.setLayout(new FillLayout());

		CheckboxTableViewer table = CheckboxTableViewer.newCheckList(page,
				SWT.V_SCROLL | SWT.BORDER);

		TableColumn names = new TableColumn(table.getTable(), SWT.NONE);
		names.setWidth(200);
		names.setText(Messages.LayersDialog_2);
		TableColumn descs = new TableColumn(table.getTable(), SWT.NONE);
		descs.setWidth(200);
		descs.setText(Messages.LayersDialog_3);

		// labels
		table.setLabelProvider(new LayerLabelProvider());
		table.setCheckStateProvider(new LayerCheckStateProvider());
		table.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				((Layer) event.getElement()).setSelected(event.getChecked());
			}

		});

		// content
		table.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// ignore
			}

			@Override
			public void dispose() {
				// ignore
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				return ((List<Layer>) inputElement).toArray();
			}
		});

		table.setInput(layers);
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		// pack columns
		names.pack();
		descs.pack();

		return page;
	}

}
