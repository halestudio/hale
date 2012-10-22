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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.security.extender;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;

/**
 * Post-processes all beans and replaces all {@link RequestMatcher} instances by
 * {@link DelegatingContextPathUrlMatcher}s. Only works if the current
 * ApplicationContext is a WebApplicationContext, because
 * {@link DelegatingContextPathUrlMatcher} needs that.
 * 
 * @author Michel Kraemer
 */
public class OverrideRequestMatcherPostProcessor implements BeanPostProcessor,
		ApplicationContextAware {

	/**
	 * The current application context (can be null if it is no
	 * WebApplicationContext)
	 */
	private WebApplicationContext _ctx;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof RequestMatcher) {
			RequestMatcher rm = (RequestMatcher) bean;
			return DelegatingContextPathUrlMatcher.wrapIfNecessary(rm, _ctx);
		}
		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof WebApplicationContext) {
			_ctx = (WebApplicationContext) applicationContext;
		}
	}
}
