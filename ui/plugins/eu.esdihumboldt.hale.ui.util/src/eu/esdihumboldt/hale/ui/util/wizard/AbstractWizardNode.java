/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
