/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.validator;

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlIdUnique;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.XLinkReference;

/**
 * Validator for {@link XLinkReference}. Checks if local references are
 * resolvable.
 * 
 * @author Simon Templer
 */
public class XLinkReferenceValidator implements PropertyConstraintValidator {

	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property, InstanceValidationContext context)
					throws ValidationException {
		if (values == null) {
			return;
		}

		Object contextObj = context.getContext(XLinkReferenceValidator.class);

		XLinkReferenceContext ctx;
		if (contextObj instanceof XLinkReferenceContext) {
			ctx = (XLinkReferenceContext) contextObj;
		}
		else {
			ctx = new XLinkReferenceContext();
			context.putContext(XLinkReferenceValidator.class, ctx);
		}

		// collect local references
		Reference ref = property.getConstraint(Reference.class);
		if (ref instanceof XLinkReference && ref.isReference()) {
			for (Object value : values) {
				if (value != null) {
					String id = value.toString();
					if (id != null && id.startsWith("#")) {
						ctx.addLocalReference(id.substring(1));
					}
				}
			}
		}

		// collect XML IDs
		Unique unique = property.getConstraint(Unique.class);
		if (unique instanceof XmlIdUnique && unique.isEnabled()) {
			for (Object value : values) {
				addIdentifier(value, ctx);
			}
		}

	}

	private void addIdentifier(Object value, XLinkReferenceContext context) {
		if (value != null) {
			String id = value.toString();
			context.addIdentifier(id);
		}
	}

	@Override
	public void validateContext(InstanceValidationContext context) throws ValidationException {
		Object contextObj = context.getContext(XLinkReferenceValidator.class);

		if (contextObj instanceof XLinkReferenceContext) {
			((XLinkReferenceContext) contextObj).validate();
		}
	}

}