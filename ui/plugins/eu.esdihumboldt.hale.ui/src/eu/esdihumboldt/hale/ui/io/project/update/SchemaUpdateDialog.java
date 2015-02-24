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

package eu.esdihumboldt.hale.ui.io.project.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;

/**
 * Dialog for updating schemas prior to loading the project.
 * 
 * @author Simon Templer
 */
public class SchemaUpdateDialog extends TrayDialog {

	/**
	 * List of I/O configurations. Will be updated by the individual components.
	 */
	private final List<IOConfiguration> configurations;

	/**
	 * Constructor.
	 * 
	 * @param shell the parent shell
	 * @param configs the project's I/O configurations
	 */
	public SchemaUpdateDialog(Shell shell, Collection<IOConfiguration> configs) {
		super(shell);
		this.configurations = new ArrayList<>(configs);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText("Update schemas");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create main composite
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 500;
		data.heightHint = 300;
		page.setLayoutData(data);

		// configure main composite
		GridLayoutFactory.fillDefaults().applyTo(page);

		// tab folder for different update tasks
		final TabFolder tabFolder = new TabFolder(page, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFolder);

		// target schema
		TabItem targetSchemaTab = new TabItem(tabFolder, SWT.NONE);
		targetSchemaTab.setText("Target schemas");
		targetSchemaTab.setControl(new SchemaUpdateComponent(tabFolder,
				SchemaIO.ACTION_LOAD_TARGET_SCHEMA, configurations));

		return page;
	}

	/**
	 * @return the configurations
	 */
	public List<IOConfiguration> getConfigurations() {
		return configurations;
	}

}
