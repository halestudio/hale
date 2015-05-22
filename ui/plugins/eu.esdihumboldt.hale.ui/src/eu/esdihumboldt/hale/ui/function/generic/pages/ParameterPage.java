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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;

/**
 * Interface for a parameter configuration page of a function.
 * 
 * @author Kai Schwierczek
 */
public interface ParameterPage extends IWizardPage {

	/**
	 * Sets the parameters this page is responsible for and their initial
	 * values. This method is called before creating the page content.<br>
	 * It should only handle the parameters in the given set, even if it could
	 * handle more.
	 * 
	 * @param params the parameters this page is responsible for
	 * @param initialValues initial values of those parameters, may be
	 *            <code>null</code>, should not be changed
	 */
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues);

	/**
	 * Returns the configuration of the parameters this page is responsible for. <br>
	 * It should only contain key value pairs, where key is the name of one of
	 * the parameters this page is responsible for.
	 * 
	 * @return the configuration of the parameters
	 */
	public ListMultimap<String, ParameterValue> getConfiguration();
}
