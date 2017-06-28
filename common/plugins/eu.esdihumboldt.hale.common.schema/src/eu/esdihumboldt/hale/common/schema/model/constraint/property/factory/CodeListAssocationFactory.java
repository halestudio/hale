/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.model.constraint.property.factory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.CodeListAssociation;

/**
 * Converts a {@link CodeListAssociation} constraint to a {@link Value} and vice
 * versa.
 * 
 * XXX only very simple representations supported right now, might need to be
 * extended later
 * 
 * @author Simon Templer
 */
public class CodeListAssocationFactory implements ValueConstraintFactory<CodeListAssociation> {

	@Override
	public Value store(CodeListAssociation constraint, TypeReferenceBuilder typeIndex) {
		if (constraint.hasAssociatedCodeLists()) {
			List<String> refs = StreamSupport.stream(constraint.getCodeLists().spliterator(), false)
					.collect(Collectors.toList());
			if (refs.isEmpty()) {
				return null;
			}
			else if (refs.size() == 1) {
				// single string reference
				return Value.simple(refs.get(0));
			}
			else {
				// multiple string references
				ValueList list = new ValueList(
						refs.stream().map(ref -> Value.simple(ref)).collect(Collectors.toList()));
				return list.toValue();
			}
		}
		else {
			return null;
		}
	}

	@Override
	public CodeListAssociation restore(Value value, Definition<?> definition,
			TypeResolver typeIndex, ClassResolver resolver) throws Exception {
		// is it a value list? (multiple String code list references)
		ValueList list = value.as(ValueList.class);
		if (list != null) {
			List<String> refList = list.stream().map(val -> val.as(String.class))
					.filter(val -> val != null).collect(Collectors.toList());
			return new CodeListAssociation(refList);
		}

		// is it a simple value? (single String code list reference)
		String single = value.as(String.class);
		if (single != null) {
			return new CodeListAssociation(Collections.singleton(single));
		}

		// fall-back
		return new CodeListAssociation();
	}

}
