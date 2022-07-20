/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.filter.cql;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoECqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionDialog;
import eu.esdihumboldt.hale.ui.filter.TypeFilterField;
import eu.esdihumboldt.hale.ui.filter.internal.Messages;

/**
 * Field for editing a CQL type filter.
 * 
 * @author Kai Schwierczek
 */
public class CQLFilterField extends TypeFilterField {

	private TypeDefinition type;
	private final SchemaSpaceID ssid;

	/**
	 * Creates a CQL filter field for the given type and schema space.
	 * 
	 * @param type the type definition
	 * @param parent the parent composite
	 * @param style the composite style
	 * @param ssid the schema space, may be <code>null</code>
	 */
	public CQLFilterField(TypeDefinition type, Composite parent, int style, SchemaSpaceID ssid) {
		super(parent, style);
		this.type = type;
		this.ssid = ssid;
	}

	/**
	 * Creates a CQL filter field for the given type's type and schema space,
	 * with a initial value for the filter field according to the type's filter.
	 * 
	 * @param type the type entity definition
	 * @param parent the parent composite
	 * @param style the composite style
	 */
	public CQLFilterField(TypeEntityDefinition type, Composite parent, int style) {
		this(type.getDefinition(), parent, style, type.getSchemaSpace());
		// XXX check filter type for CQL?
		if (type.getFilter() != null)
			setFilterExpression(AlignmentUtil.getFilterText(type.getFilter()));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterField#createFilter(java.lang.String)
	 */
	@Override
	protected Filter createFilter(String filterString) throws CQLException {
		return new FilterGeoECqlImpl(filterString);

//		switch (filterType) {
//		case CQL:
//			return new FilterGeoCqlImpl(filterString);
//		case ECQL:
//			return new FilterGeoECqlImpl(filterString);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterField#selectVariable()
	 */
	@Override
	protected String selectVariable() {
		PropertyDefinitionDialog dialog = new PropertyDefinitionDialog(
				Display.getCurrent().getActiveShell(), ssid, type, Messages.FeatureFilterField_7,
				null);

		String stringVar = null;
		if (dialog.open() == PropertyDefinitionDialog.OK && dialog.getObject() != null
				&& dialog.getObject().getType().getName().toString().length() >= 1) {
			StringBuilder var = new StringBuilder();
			for (int i = 0; i < dialog.getObject().getPropertyPath().size(); i++) {
				ChildContext child = dialog.getObject().getPropertyPath().get(i);

				if (child.getChild().asGroup() == null) {
					if (i != 0 && !stringVar.isEmpty())
						var.append(".");
					var.append(dialog.getObject().getPropertyPath().get(i).getChild().getName()
							.getLocalPart().toString());

				}

				stringVar = var.toString();

			}

			return stringVar;
		}
		else
			return null;
	}

	/**
	 * Set the type definition
	 * 
	 * @param type the type definition
	 */
	public void setType(TypeDefinition type) {
		this.type = type;

		setVariableSelectEnabled(type != null);
	}

}
