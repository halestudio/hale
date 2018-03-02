/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.common.graph.figures.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.common.graph.figures.CellFigure;
import eu.esdihumboldt.hale.ui.common.graph.figures.CellFigureContribution;

/**
 * Utilities for the {@link CellFigure} extension point
 * 
 * @author Florian Esser
 */
public class CellFigureExtension
		extends AbstractExtension<CellFigureContribution, CellFigureContributionFactory> {

	private static class CellFigureContributionFactoryImpl
			extends AbstractConfigurationFactory<CellFigureContribution>
			implements CellFigureContributionFactory {

		/**
		 * Create the factory for the given configuration element
		 * 
		 * @param conf the configuration element
		 */
		public CellFigureContributionFactoryImpl(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(CellFigureContribution instance) {
			// do nothing
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.common.graph.figures.cell"; //$NON-NLS-1$

	/**
	 * Default extension constructor
	 */
	public CellFigureExtension() {
		super(ID);
	}

	@Override
	protected CellFigureContributionFactory createFactory(IConfigurationElement conf)
			throws Exception {
		return new CellFigureContributionFactoryImpl(conf);
	}

	/**
	 * Get the defined task provider factories
	 * 
	 * @return the task provider factories
	 */
	public static List<CellFigureContributionFactory> getCellFigureContributionFactories() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ID);

		List<CellFigureContributionFactory> result = new ArrayList<>();

		for (IConfigurationElement conf : confArray) {
			result.add(new CellFigureContributionFactoryImpl(conf));
		}

		return result;
	}

}
