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

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.ui.common.graph.figures.CellFigureContribution;

/**
 * Factory interface for {@link CellFigureContribution}s
 * 
 * @author Florian Esser
 */
public interface CellFigureContributionFactory
		extends ExtensionObjectFactory<CellFigureContribution> {
	// typed interface
}
