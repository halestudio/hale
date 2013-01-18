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

package eu.esdihumboldt.hale.io.gml.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.gml.writer.XmlWriterBase;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for setting an XML root element
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class RootElementPage extends
		AbstractConfigurationPage<XmlWriterBase, IOWizard<XmlWriterBase>> {

	private ListViewer list;
	private Text filterText;

	/**
	 * Default constructor
	 */
	public RootElementPage() {
		super("xml.rootElement");

		setTitle("XML root element");
		setDescription("Please select the root element to use in the XML file");
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(XmlWriterBase provider) {
		ISelection sel = list.getSelection();

		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) sel).getFirstElement();

			if (selected instanceof XmlElement) {
				QName name = ((XmlElement) selected).getName();

				provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE,
						Value.of(name.getNamespaceURI()));
				provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME,
						Value.of(name.getLocalPart()));
				return true;
			}
		}

		provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE, null);
		provider.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME, null);

		return false;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));

		// add filter text
		filterText = new Text(page, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		filterText.setText(""); //$NON-NLS-1$

		// add filtered list
		list = new ListViewer(page, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		list.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof XmlElement) {
					QName name = ((XmlElement) element).getName();

					return name.getLocalPart() + " (" + name.getNamespaceURI() + ")";
				}
				if (element instanceof Definition) {
					return ((Definition<?>) element).getDisplayName();
				}
				return super.getText(element);
			}

		});
		list.setContentProvider(ArrayContentProvider.getInstance());
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 8 * list.getList().getItemHeight();
		list.getControl().setLayoutData(layoutData);

		// page status update
		list.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
			}
		});

		list.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (canFlipToNextPage()) {
					getContainer().showPage(getNextPage());
					return;
				}
			}
		});

		// search filter & update
		list.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				String filter = filterText.getText();
				// handle empty filter
				if (filter == null || filter.isEmpty()) {
					return true;
				}

				if (element instanceof Definition) {
					Definition<?> def = (Definition<?>) element;
					filter = filter.toLowerCase();

					if (def.getDisplayName().toLowerCase().contains(filter)) {
						return true;
					}
				}

				return false;
			}
		});
		list.setComparator(new ViewerComparator());
		filterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// refilter
				list.refresh();
			}
		});

		updateList();
	}

	/**
	 * @see DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		filterText.setFocus();
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		updateList();
	}

	private void updateList() {
		if (list != null // during enable if content not yet created
				&& getWizard().getProvider() != null) {
			// TODO instead of showing all elements allow filtering for elements
			// that can hold the type in some form?
			SchemaSpace schemas = getWizard().getProvider().getTargetSchema();
			XmlIndex index = StreamGmlWriter.getXMLIndex(schemas);
			// FIXME use filtered table for selection?
			list.setInput(index.getElements().values());
			setPageComplete(!list.getSelection().isEmpty());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#loadPreSelection(eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration)
	 */
	@Override
	public void loadPreSelection(IOConfiguration conf) {

		String name = conf.getProviderConfiguration().get(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME)
				.getStringRepresentation();
		String namespace = conf.getProviderConfiguration()
				.get(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE).getStringRepresentation();
		String elementName = namespace + "/" + name;

		SchemaSpace schemas = getWizard().getProvider().getTargetSchema();
		XmlIndex index = StreamGmlWriter.getXMLIndex(schemas);

		for (XmlElement element : index.getElements().values()) {
			if (element.getIdentifier().equals(elementName)) {
				list.setSelection(new StructuredSelection(element), true);
			}
		}
	}
}
