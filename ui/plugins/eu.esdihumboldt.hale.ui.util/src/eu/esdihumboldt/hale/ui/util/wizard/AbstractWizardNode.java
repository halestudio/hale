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

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.graphics.Point;

/**
 * Abstract wizard node implementation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractWizardNode implements ExtendedWizardNode {

	private IWizard wizard;

	private final IWizardContainer container;

	/**
	 * @param container the wizard container
	 */
	public AbstractWizardNode(IWizardContainer container) {
		super();
		this.container = container;
	}

	/**
	 * @see IWizardNode#dispose()
	 */
	@Override
	public void dispose() {
		if (wizard != null) {
			wizard.dispose();
			wizard = null;
		}
	}

	/**
	 * @see IWizardNode#getExtent()
	 */
	@Override
	public Point getExtent() {
		return new Point(-1, -1);
	}

	/**
	 * @see IWizardNode#getWizard()
	 */
	@Override
	public IWizard getWizard() {
		if (wizard == null) {
			wizard = createWizard();

			// first time setup
			// XXX provide wrapped container? - IPageChangeProvider,
			// IWizardContainer
			wizard.setContainer(container);
		}

		return wizard;
	}

	/**
	 * Create the wizard represented by the node
	 * 
	 * @return the wizard
	 */
	protected abstract IWizard createWizard();

	/**
	 * @see IWizardNode#isContentCreated()
	 */
	@Override
	public boolean isContentCreated() {
		return wizard != null;
	}

}
