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

package eu.esdihumboldt.hale.server.templates.war.components;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

/**
 * Panel creating a Recaptcha control to be added to a form.
 * 
 * @author Simon Templer
 */
public class RecaptchaPanel extends Panel {

	private static final long serialVersionUID = -4790960555419918417L;

	/**
	 * Name of the system property specifying the Recaptcha public key.
	 */
	public static final String SYSTEM_PROPERTY_RECAPTCHA_PUBLIC_KEY = "recaptcha.public";

	/**
	 * Name of the system property specifying the Recaptcha private key.
	 */
	public static final String SYSTEM_PROPERTY_RECAPTCHA_PRIVATE_KEY = "recaptcha.private";

	private static String getPublicKey() {
		return System.getProperty(SYSTEM_PROPERTY_RECAPTCHA_PUBLIC_KEY, "");
	}

	private static String getPrivateKey() {
		return System.getProperty(SYSTEM_PROPERTY_RECAPTCHA_PRIVATE_KEY, "");
	}

	/**
	 * Constructor.
	 * 
	 * @param id the component ID
	 */
	public RecaptchaPanel(String id) {
		super(id);

		add(new FormComponent<String>("imagePassword", new Model<String>()) {

			private static final long serialVersionUID = 6622368671409426173L;

			@Override
			public void onComponentTagBody(final MarkupStream markupStream,
					final ComponentTag openTag) {
				String privateCaptchaKey = getPrivateKey();
				String publicCaptchaKey = getPublicKey();
				ReCaptcha recaptcha = ReCaptchaFactory.newReCaptcha(publicCaptchaKey,
						privateCaptchaKey, false);

				Properties properties = new Properties();
				properties.put("theme", "clean");
				replaceComponentTagBody(markupStream, openTag,
						recaptcha.createRecaptchaHtml(null, properties));
			}

			@Override
			public void validate() {
				WebRequest request = (WebRequest) RequestCycle.get().getRequest();
				HttpServletRequest servletRequest = (HttpServletRequest) getRequest()
						.getContainerRequest();

				// FIXME find proxied address when running behind proxy?
				String remoteAddr = servletRequest.getRemoteAddr();

				String challenge = request.getRequestParameters()
						.getParameterValue("recaptcha_challenge_field").toString();
				String response = request.getRequestParameters()
						.getParameterValue("recaptcha_response_field").toString();

				if (response == null || response.isEmpty()) {
					error("Please enter the Captcha or log in to upload a template.");
					return;
				}

				String privateCaptchaKey = getPrivateKey();
				String publicCaptchaKey = getPublicKey();
				ReCaptcha recaptcha = ReCaptchaFactory.newReCaptcha(publicCaptchaKey,
						privateCaptchaKey, false);
				ReCaptchaResponse reCaptchaResponse = recaptcha.checkAnswer(remoteAddr, challenge,
						response);

				if (!reCaptchaResponse.isValid()) {
					error("The Captcha was not entered correctly. Please enter the Captcha or log in to upload a template.");
				}
			}
		});
	}
}
