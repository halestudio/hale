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

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import eu.esdihumboldt.cst.functions.geometric.extent.ExtentTransformation;
import eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction;
import eu.esdihumboldt.cst.functions.string.DateExtractionFunction;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;

/**
 * Instantiates the property transformation handler capable of handling the
 * specified transformation function.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class PropertyTransformationHandlerFactory {

	private static PropertyTransformationHandlerFactory instance;

	private PropertyTransformationHandlerFactory() {

	}

	/**
	 * Return the singleton factory instance.
	 * 
	 * @return the factory instance
	 */
	public static PropertyTransformationHandlerFactory getInstance() {
		if (instance == null) {
			instance = new PropertyTransformationHandlerFactory();
		}

		return instance;
	}

	/**
	 * Creates a new property transformation handler instance to handle the
	 * transformation function specified by the provided identifier.
	 * 
	 * @param propertyTransformationIdentifier the property transformation
	 *            function identifier
	 * @return the property transformation handler instance
	 * @throws UnsupportedTransformationException if the specified
	 *             transformation is not supported
	 */
	public PropertyTransformationHandler createPropertyTransformationHandler(
			String propertyTransformationIdentifier) throws UnsupportedTransformationException {
		if (propertyTransformationIdentifier == null
				|| propertyTransformationIdentifier.trim().isEmpty()) {
			throw new IllegalArgumentException("propertyTransformationIdentifier must be set");
		}

		if (propertyTransformationIdentifier.equals(RenameFunction.ID)) {
			return new RenameHandler();
		}
		else if (propertyTransformationIdentifier.equals(AssignFunction.ID)
				|| propertyTransformationIdentifier.equals(AssignFunction.ID_BOUND)) {
			return new AssignHandler();
		}
		else if (propertyTransformationIdentifier.equals(FormattedStringFunction.ID)) {
			return new FormattedStringHandler();
		}
		else if (propertyTransformationIdentifier.equals(MathematicalExpressionFunction.ID)) {
			return new MathematicalExpressionHandler();
		}
		else if (propertyTransformationIdentifier.equals(DateExtractionFunction.ID)) {
			return new DateExtractionHandler();
		}
		else if (propertyTransformationIdentifier.equals(ClassificationMappingFunction.ID)) {
			return new ClassificationHandler();
		}
		else if (propertyTransformationIdentifier.equals(ExtentTransformation.ID)) {
			return new ExtentHandler();
		}
		else {
			String errMsg = String.format("Unsupported property transformation %s",
					propertyTransformationIdentifier);
			throw new UnsupportedTransformationException(errMsg, propertyTransformationIdentifier);
		}
	}
}
