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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.FeatureTypeList.TypeSelectionListener;
import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class FeatureTypesPage extends AbstractTypesPage<WfsConfiguration> {

	private FeatureTypeList list;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration
	 * @param capsPage the capabilities page
	 */
	public FeatureTypesPage(WfsConfiguration configuration, CapabilitiesPage capsPage) {
		super(configuration, capsPage, Messages.FeatureTypesPage_0); //$NON-NLS-1$

		setTitle(Messages.FeatureTypesPage_1); //$NON-NLS-1$
		setMessage(Messages.FeatureTypesPage_2); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTypesPage#update(List)
	 */
	@Override
	protected void update(List<FeatureType> types) {
		list.setFeatureTypes(types);

		// XXX the update doesn't refresh the buttons when the page is shown the
		// first time
		// update();

		// XXX so try something nasty instead
		final Display display = Display.getCurrent();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						FeatureTypesPage.this.update();
					}

				});
			}
		}, 500);
	}

	/**
	 * @see AbstractWfsPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));

		list = new FeatureTypeList(page, getConfiguration().getFixedNamespace());
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		list.addTypeSelectionListener(new TypeSelectionListener() {

			@Override
			public void selectionChanged() {
				FeatureTypesPage.this.update();
			}

		});

		setControl(page);

		update();
	}

	/**
	 * @see AbstractTypesPage#getSelection()
	 */
	@Override
	protected List<FeatureType> getSelection() {
		return list.getSelection();
	}

	private void update() {
		boolean valid = true;

		// test namespace
		if (valid) {
			String ns = getConfiguration().getFixedNamespace();

			if (ns != null) {
				valid = list.getNamespace().equals(ns);
				if (!valid) {
					setErrorMessage(Messages.FeatureTypesPage_3 + ns); //$NON-NLS-1$
				}
			}
		}

		// test selection
		if (valid) {
			List<FeatureType> selection = list.getSelection();
			valid = selection != null && !selection.isEmpty();
			if (!valid) {
				setErrorMessage(Messages.FeatureTypesPage_4); //$NON-NLS-1$
			}
		}

		if (valid) {
			setErrorMessage(null);
		}

		setPageComplete(valid);
	}

}
