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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.resources.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.io.project.ProjectDescription;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Properties section for editing the basic project information.
 * 
 * @author Simon Templer
 */
public class ProjectProperties extends AbstractPropertySection implements ProjectDescription {

	private Text nameText;
	private Text authorText;
	private Text descriptionText;
	private boolean updateProject = true;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		// name

		nameText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
				SWT.SINGLE | SWT.BORDER);

		data = new FormData();
		data.width = 100;
		data.left = new FormAttachment(0, 100); // STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		nameText.setLayoutData(data);
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateProject();
			}

		});

		CLabel namelabel = getWidgetFactory().createCLabel(composite, "Name");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(nameText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(nameText, 0, SWT.TOP);
		namelabel.setLayoutData(data);

		// author

		authorText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
				SWT.SINGLE | SWT.BORDER);

		data = new FormData();
		data.width = 100;
		data.left = new FormAttachment(0, 100); // STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(nameText, ITabbedPropertyConstants.VSPACE);
		authorText.setLayoutData(data);
		authorText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateProject();
			}

		});

		CLabel authorlabel = getWidgetFactory().createCLabel(composite, "Author");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(authorText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(authorText, 0, SWT.TOP);
		authorlabel.setLayoutData(data);

		// description

		descriptionText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
				SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);

		data = new FormData();
		data.width = 100;
		data.height = 100; // height hint for multiline
		data.left = new FormAttachment(0, 100); // STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(authorText, ITabbedPropertyConstants.VSPACE);
		descriptionText.setLayoutData(data);
		descriptionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateProject();
			}

		});

		CLabel descriptionlabel = getWidgetFactory().createCLabel(composite, "Description");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(descriptionText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(descriptionText, 0, SWT.TOP);
		descriptionlabel.setLayoutData(data);
	}

	private void updateProject() {
		if (updateProject) {
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			ps.updateProjectInfo(this);
		}
	}

	@Override
	public String getName() {
		return nameText.getText();
	}

	@Override
	public String getAuthor() {
		return authorText.getText();
	}

	@Override
	public String getDescription() {
		return descriptionText.getText();
	}

	@Override
	public void refresh() {
		super.refresh();

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		ProjectInfo info = ps.getProjectInfo();

		updateProject = false;
		nameText.setText((info.getName() == null) ? ("") : (info.getName()));
		authorText.setText((info.getAuthor() == null) ? ("") : (info.getAuthor()));
		descriptionText.setText((info.getDescription() == null) ? ("") : (info.getDescription()));
		updateProject = true;
	}

}
