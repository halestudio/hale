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

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Feedback message label for a form component. CSS style classes can be
 * customized by overriding {@link #getValidCssClass()},
 * {@link #getCssClass(String)} and {@link #getDefaultCssClass()}
 * 
 * @author Simon Templer
 */
public class FieldMessage extends Label {

	private static final long serialVersionUID = 5960168238690311429L;

	private final IModel<String> text;

	private final FormComponent<?> formComponent;

//	private final FeedbackCollector collector;

	private final boolean specialValidStyle;

	/**
	 * Constructor
	 * 
	 * @param id the field message id
	 * @param text the field message text
	 * @param formComponent the form component
	 */
	public FieldMessage(String id, IModel<String> text, FormComponent<?> formComponent) {
		this(id, text, formComponent, true);
	}

	/**
	 * Constructor
	 * 
	 * @param id the field message id
	 * @param text the field message text
	 * @param formComponent the form component
	 * @param specialValidStyle use the special valid style for a valid
	 *            component that has no messages
	 */
	public FieldMessage(String id, IModel<String> text, FormComponent<?> formComponent,
			boolean specialValidStyle) {
		super(id, text);

		this.text = text;
		this.formComponent = formComponent;
		this.specialValidStyle = specialValidStyle;
//		this.collector = new FeedbackCollector(formComponent);
//		collector.setIncludeSession(false);

		setOutputMarkupId(true);
	}

	/**
	 * @see Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();

		List<FeedbackMessage> msgs = formComponent.getFeedbackMessages().toList(); // collector.collect();
		// only collect the last message (XXX correct like this?)
		FeedbackMessage msg = (msgs.isEmpty()) ? (null) : (msgs.get(msgs.size() - 1));
		if (msg != null) {
			setDefaultModel(new Model<Serializable>(msg.getMessage()));

			add(new AttributeModifier("class", new Model<String>(
					getCssClass(msg.getLevelAsString()))));
		}
		else {
			if (specialValidStyle && formComponent.isValid() && formComponent.checkRequired()) {
				setDefaultModel(new Model<String>(""));

				add(new AttributeModifier("class", new Model<String>(getValidCssClass())));
			}
			else {
				setDefaultModel(text);

				add(new AttributeModifier("class", new Model<String>(getDefaultCssClass())));
			}
		}
	}

	/**
	 * Get the CSS class for valid fields without message
	 * 
	 * @return the CSS class name
	 */
	protected String getValidCssClass() {
		return "field-msg-valid";
	}

	/**
	 * Get the CSS class name for the field message
	 * 
	 * @param level the feedback level name (ERROR, WARNING, INFO, ...)
	 * 
	 * @return the CSS class name
	 */
	protected String getCssClass(String level) {
		return "field-msg-" + level;
	}

	/**
	 * Get the default CSS class
	 * 
	 * @return the default CSS class
	 */
	protected String getDefaultCssClass() {
		return "field-msg";
	}

}
