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

package eu.esdihumboldt.hale.server.webapp.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;

/**
 * Changes the CSS class of a form component depending on if it is valid and
 * updates an additional component (e.g. a {@link FieldMessage}). The CSS class
 * can be customized by overriding {@link #getCssClass(boolean)}
 * 
 * @author Simon Templer
 */
public class FieldValidatingBehavior extends AjaxFormComponentUpdatingBehavior {

	private static final long serialVersionUID = -3378402422614960278L;

	private final Component component;

	/**
	 * Constructor
	 * 
	 * @param event the update event
	 * @param component an additional component that must be updated
	 */
	public FieldValidatingBehavior(String event, Component component) {
		super(event);

		this.component = component;
	}

	/**
	 * @see AjaxFormComponentUpdatingBehavior#onUpdate(AjaxRequestTarget)
	 */
	@Override
	protected void onUpdate(AjaxRequestTarget target) {
		update(target, true);
	}

	/**
	 * @see AjaxFormComponentUpdatingBehavior#onError(AjaxRequestTarget,
	 *      RuntimeException)
	 */
	@Override
	protected void onError(AjaxRequestTarget target, RuntimeException e) {
		update(target, false);
	}

	/**
	 * Update the form component
	 * 
	 * @param target the AJAX request target
	 * @param valid if the field is valid
	 */
	private void update(AjaxRequestTarget target, boolean valid) {
		FormComponent<?> formComponent = getFormComponent();

		if (formComponent.isValid() == valid) {
			formComponent
					.add(new AttributeModifier("class", new Model<String>(getCssClass(valid))));
			target.add(formComponent);
		}

		if (component != null) {
			target.add(component);
		}
	}

	/**
	 * Get the CSS class name for the field
	 * 
	 * @param valid if the field is valid
	 * 
	 * @return the CSS class name
	 */
	private String getCssClass(boolean valid) {
		if (valid) {
			return "field-valid";
		}
		else {
			return "field-invalid";
		}
	}

}
