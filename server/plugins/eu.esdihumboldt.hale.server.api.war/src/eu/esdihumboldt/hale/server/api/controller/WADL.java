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

package eu.esdihumboldt.hale.server.api.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlApplication;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlDoc;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlMethod;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlParam;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlParamStyle;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlRepresentation;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlRequest;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlResource;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlResources;
import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlResponse;

/**
 * Controller generating WADL and documentation.
 * 
 * @author Simon Templer
 */
@Controller
@RequestMapping
public class WADL {

	// autowired
	private final RequestMappingHandlerMapping handlerMapping;

	/**
	 * Constructor for initializing the WADL Controller.
	 * 
	 * @param handlerMapping the handler mapping
	 */
	@Autowired
	public WADL(RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	/**
	 * Generate the API documentation based on the WADL.
	 * 
	 * @param request the HTTP servlet request
	 * @return the documentation view and XML to transform using XSLT
	 * @throws Exception if generating the documentation fails
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = { "application/xhtml+xml" })
	public ModelAndView getDocumentation(HttpServletRequest request) throws Exception {
		WadlApplication wadl = generateWadl(request);

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.getDOMImplementation().createDocument(null, null, null);

		JAXBContext jaxbContext = JAXBContext
				.newInstance("eu.esdihumboldt.hale.server.api.internal.wadl.generated");
		final Binder<Node> binder = jaxbContext.createBinder();
		binder.marshal(wadl, doc);

		return new ModelAndView("doc", "xml", doc);
	}

	/**
	 * Generate WADL based on the available controllers.
	 * 
	 * @param request the HTTP servlet request
	 * @return the WADL object representation that will be converted to XML
	 */
	@RequestMapping(value = "/wadl", method = RequestMethod.GET, produces = { "application/xml" })
	public @ResponseBody
	WadlApplication generateWadl(HttpServletRequest request) {
		WadlApplication result = new WadlApplication();
		WadlDoc doc = new WadlDoc();
		doc.setTitle("REST Service WADL");
		result.getDoc().add(doc);
		WadlResources wadResources = new WadlResources();
		wadResources.setBase(getBaseUrl(request));

		Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
			WadlResource wadlResource = new WadlResource();

			HandlerMethod handlerMethod = entry.getValue();
			RequestMappingInfo mappingInfo = entry.getKey();

			Set<String> pattern = mappingInfo.getPatternsCondition().getPatterns();
			Set<RequestMethod> httpMethods = mappingInfo.getMethodsCondition().getMethods();
			ProducesRequestCondition producesRequestCondition = mappingInfo.getProducesCondition();
			Set<MediaType> mediaTypes = producesRequestCondition.getProducibleMediaTypes();

			for (RequestMethod httpMethod : httpMethods) {
				WadlMethod wadlMethod = new WadlMethod();

				for (String uri : pattern) {
					wadlResource.setPath(uri);
				}

				wadlMethod.setName(httpMethod.name());
				Method javaMethod = handlerMethod.getMethod();
				wadlMethod.setId(javaMethod.getName());
				WadlDoc wadlDocMethod = new WadlDoc();
				wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getName() + "."
						+ javaMethod.getName());
				wadlMethod.getDoc().add(wadlDocMethod);

				// Request
				WadlRequest wadlRequest = new WadlRequest();

				Annotation[][] annotations = javaMethod.getParameterAnnotations();
				Class<?>[] paramTypes = javaMethod.getParameterTypes();
				int parameterCounter = 0;

				for (Annotation[] annotation : annotations) {
					for (Annotation annotation2 : annotation) {
						if (annotation2 instanceof RequestParam) {
							RequestParam param2 = (RequestParam) annotation2;

							WadlParam waldParam = new WadlParam();

							waldParam.setName(param2.value());

							waldParam.setStyle(WadlParamStyle.QUERY);
							waldParam.setRequired(param2.required());

							if (paramTypes != null && paramTypes.length > parameterCounter) {
								if (paramTypes.length > parameterCounter
										&& (paramTypes[parameterCounter] == javax.servlet.http.HttpServletRequest.class || paramTypes[parameterCounter] == javax.servlet.http.HttpServletResponse.class))
									parameterCounter++;
								if (paramTypes.length > parameterCounter
										&& (paramTypes[parameterCounter] == javax.servlet.http.HttpServletRequest.class || paramTypes[parameterCounter] == javax.servlet.http.HttpServletResponse.class))
									parameterCounter++;

								if (paramTypes.length > parameterCounter) {

									waldParam
											.setType(getQNameForType(paramTypes[parameterCounter]));
									parameterCounter++;
								}
							}

							String defaultValue = cleanDefault(param2.defaultValue());
							if (!defaultValue.equals("")) {
								waldParam.setDefault(defaultValue);
							}
							wadlRequest.getParam().add(waldParam);
						}
						else if (annotation2 instanceof PathVariable) {
							PathVariable param2 = (PathVariable) annotation2;

							WadlParam waldParam = new WadlParam();
							waldParam.setName(param2.value());
							waldParam.setStyle(WadlParamStyle.TEMPLATE);
							waldParam.setRequired(true);
							if (paramTypes != null && paramTypes.length > parameterCounter) {
								if (paramTypes.length > parameterCounter
										&& (paramTypes[parameterCounter] == javax.servlet.http.HttpServletRequest.class || paramTypes[parameterCounter] == javax.servlet.http.HttpServletResponse.class))
									parameterCounter++;
								if (paramTypes.length > parameterCounter
										&& (paramTypes[parameterCounter] == javax.servlet.http.HttpServletRequest.class || paramTypes[parameterCounter] == javax.servlet.http.HttpServletResponse.class))
									parameterCounter++;

								if (paramTypes.length > parameterCounter) {

									waldParam
											.setType(getQNameForType(paramTypes[parameterCounter]));
									parameterCounter++;
								}
							}

							wadlRequest.getParam().add(waldParam);
						}
						else
							parameterCounter++;
					}
				}
				if (!wadlRequest.getParam().isEmpty()) {
					wadlMethod.setRequest(wadlRequest);
				}

				// Response
				if (!mediaTypes.isEmpty()) {
					WadlResponse wadlResponse = new WadlResponse();
					wadlResponse.getStatus().add(200l);
					for (MediaType mediaType : mediaTypes) {
						WadlRepresentation wadlRepresentation = new WadlRepresentation();
						wadlRepresentation.setMediaType(mediaType.toString());
						wadlResponse.getRepresentation().add(wadlRepresentation);
					}
					wadlMethod.getResponse().add(wadlResponse);
				}

				wadlResource.getMethodOrResource().add(wadlMethod);

			}

			wadResources.getResource().add(wadlResource);

		}
		result.getResources().add(wadResources);

		return result;
	}

	/**
	 * Get the API base URL.
	 * 
	 * @param request the HTTP servlet request
	 * @return the base URL
	 */
	private String getBaseUrl(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(request.getScheme());
		builder.append("://");
		builder.append(request.getServerName());
		builder.append(':');
		builder.append(request.getServerPort());
		builder.append(request.getContextPath());
		String servPath = request.getServletPath();
		if (servPath != null && !servPath.isEmpty()) {
			builder.append(servPath);
		}
		return builder.toString();
	}

	private String cleanDefault(String value) {
		value = value.replaceAll("\t", "");
		value = value.replaceAll("\n", "");
		value = value.replaceAll("?", "");
		value = value.replaceAll("?", "");
		value = value.replaceAll("?", "");
		return value;
	}

	/**
	 * Determine the XML Schema type name representing a Java class.
	 * 
	 * @param classType the Java class
	 * @return the corresponding schema type or <code>null</code> if none was
	 *         recognized
	 */
	private QName getQNameForType(Class<?> classType) {
		QName qName = null;

		// extract the component type from an array
		if (classType.isArray()) {
			classType = classType.getComponentType();
		}

		if (classType == java.lang.Long.class)
			qName = new QName("http://www.w3.org/2001/XMLSchema", "long");
		else if (classType == java.lang.Integer.class)
			qName = new QName("http://www.w3.org/2001/XMLSchema", "integer");
		else if (classType == java.lang.Double.class)
			qName = new QName("http://www.w3.org/2001/XMLSchema", "double");
		else if (classType == java.lang.String.class)
			qName = new QName("http://www.w3.org/2001/XMLSchema", "string");
		else if (classType == java.util.Date.class)
			qName = new QName("http://www.w3.org/2001/XMLSchema", "date");

		return qName;
	}

}
