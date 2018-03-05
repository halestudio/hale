/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import javax.xml.namespace.QName;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;

/**
 * Abstract Property Transformation Handler
 * 
 * @author zahnen
 */
abstract class AbstractPropertyTransformationHandler implements PropertyTransformationHandler {

	private final static String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

	protected final TransformationContext transformationContext;

	protected AbstractPropertyTransformationHandler(
			final TransformationContext transformationContext) {
		this.transformationContext = transformationContext;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.reader.handler.TransformationHandler#handle(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public MutableCell handle(final MappingValue mappingValue, final String tableName) {
		// ignore xsi:nil
		QName lastPathElement = mappingValue.getQualifiedTargetPath()
				.get(mappingValue.getQualifiedTargetPath().size() - 1);
		if (lastPathElement.getNamespaceURI().equals(XSI_NS)
				&& lastPathElement.getLocalPart().equals("@nil")) {
			return null;
		}

		final String transformationIdentifier = doHandle(mappingValue, tableName);

		final MutableCell propertyCell = new DefaultCell();

		propertyCell.setTransformationIdentifier(transformationIdentifier);

		if (transformationContext.hasCurrentSourceProperty()) {
			propertyCell.setSource(transformationContext.getCurrentSourcePropertyEntities());
		}
		propertyCell.setTarget(transformationContext.getCurrentTargetPropertyEntities());

		propertyCell
				.setTransformationParameters(transformationContext.getCurrentPropertyParameters());

		return propertyCell;
	}

	protected abstract String doHandle(final MappingValue mappingValue, final String tableName);

}
