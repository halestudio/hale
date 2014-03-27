/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.wadl.controller;

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.util.Map.Entry

import javax.servlet.http.HttpServletRequest
import javax.xml.bind.Binder
import javax.xml.bind.JAXBContext
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.w3c.dom.Document

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.server.api.base.APIUtil
import eu.esdihumboldt.hale.server.api.wadl.doc.DocScope
import eu.esdihumboldt.hale.server.api.wadl.doc.WDoc
import eu.esdihumboldt.hale.server.api.wadl.doc.WDocUtil
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlApplication
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlDoc
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlMethod
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlParam
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlParamStyle
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlRepresentation
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlRequest
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlResource
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlResources
import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlResponse
import groovy.transform.CompileStatic

/**
 * Controller generating WADL and documentation.
 * 
 * @author Simon Templer
 */
@Controller
@RequestMapping
class WADL extends WADLBase {

	private static final ALogger log = ALoggerFactory.getLogger(WADL)

	// autowired
	private final RequestMappingHandlerMapping handlerMapping

	private String baseURI;

	/**
	 * Constructor for initializing the WADL Controller.
	 * 
	 * @param handlerMapping the handler mapping
	 */
	@Autowired
	WADL(RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping
	}

	/**
	 * Generate the API documentation based on the WADL.
	 * 
	 * @param request the HTTP servlet request
	 * @return the documentation view and XML to transform using XSLT
	 * @throws Exception if generating the documentation fails
	 */
	@WDoc(
	title = 'API documentation',
	content = { 'The API documentation as HTML, specifying resources, methods and representations.' },
	scope = DocScope.RESOURCE
	)
	@RequestMapping(value = '/', method = RequestMethod.GET, produces = 'application/xhtml+xml')
	public ModelAndView getDocumentation(HttpServletRequest request) throws Exception {
		WadlApplication wadl = generateWadl(request)

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
		final DocumentBuilder builder = factory.newDocumentBuilder()
		final Document doc = builder.getDOMImplementation().createDocument(null, null, null)

		JAXBContext jaxbContext = JAXBContext
				.newInstance('eu.esdihumboldt.hale.server.api.wadl.internal.generated', WadlDoc.class.classLoader)
		final Binder<Node> binder = jaxbContext.createBinder()
		binder.marshal(wadl, doc)

		new ModelAndView('doc', 'xml', doc)
	}

	/**
	 * Generate WADL based on the available controllers.
	 * 
	 * @param request the HTTP servlet request
	 * @return the WADL object representation that will be converted to XML
	 */
	@WDoc(
	title = 'WADL API specification',
	content = { baseURI -> WDocUtil.xhtml('''
		Specification of the service API through a
		<a href="http://www.w3.org/Submission/wadl/" target="_blank">WADL</a> document.
		''') },
	scope = DocScope.RESOURCE
	)
	// 'application/vnd.sun.wadl+xml'
	@RequestMapping(value = '/application.wadl', method = RequestMethod.GET, produces = 'application/xml')
	public @ResponseBody
	WadlApplication generateWadl(HttpServletRequest request) {
		// initialize baseURI
		synchronized (this) {
			if (baseURI == null) {
				baseURI = APIUtil.getBaseUrl(request)
			}
		}

		// build the WADL application
		WadlApplication result = new WadlApplication();
		WadlDoc doc = new WadlDoc();
		doc.title = 'REST Service API'
		result.doc << doc
		WadlResources wadResources = new WadlResources();
		wadResources.base = APIUtil.getBaseUrl(request) + '/';

		// organize handler mappings by patterns
		Multimap resourceHandlers = HashMultimap.create()
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.handlerMethods
		for (Entry<RequestMappingInfo, HandlerMethod> entry in handlerMethods.entrySet()) {
			def patterns = entry.key.patternsCondition.patterns
			patterns.each { String pattern ->
				pattern = normalizePattern(pattern) { String pathVar, boolean isEnd ->
					def matcher = pathVar =~ /^([^:]+):.*$/
					if (matcher.find()) {
						def name = matcher.group(1)
						if (isEnd && name == 'ext') {
							// special case: don't include extension path variable in pattern
							return null
						}
						return name
					}
					else {
						return pathVar
					}
				}
				resourceHandlers.put(pattern, entry)
			}
		}

		// create resources from handler mappings
		for (String pattern in resourceHandlers.keySet().sort()) {
			WadlResource wadlResource = new WadlResource();

			// resource path
			wadlResource.path = pattern.substring(1)

			// all handler java methods
			def resourceMethods = resourceHandlers.get(pattern).collect{ it.value.method }

			// resource documentation
			wadlResource.doc.addAll(WDocUtil.getWadlDocs(resourceMethods, DocScope.RESOURCE, null, baseURI))

			// template parameters (PathVariable)
			// must be the same for all handler methods for that resource

			// find method with parameter documentation
			Method paramMethod = null
			paramMethod = resourceMethods.find{
				// has param documentation
				WDocUtil.getWadlDocs([it], DocScope.PARAM, null, baseURI)
			}
			if (paramMethod == null) {
				// use any method if none is documented with parameters
				paramMethod = resourceMethods.find()
			}

			wadlResource.param.addAll(generateTemplateParams(paramMethod))

			// organize handler methods by HTTP method
			Multimap methods = HashMultimap.create()
			for (Entry<RequestMappingInfo, HandlerMethod> entry in resourceHandlers.get(pattern)) {
				entry.key.methodsCondition.methods.each {
					methods.put(it, entry)
				}
			}

			for (RequestMethod httpMethod in methods.keySet()) {
				WadlMethod wadlMethod = new WadlMethod();
				wadlMethod.name = httpMethod.name()

				Collection<Entry<RequestMappingInfo, HandlerMethod>> mappedMethods = methods.get(httpMethod)

				// collect handler java methods
				def methodMethods = mappedMethods.collect { it.value.method }

				// method documentation
				wadlMethod.doc.addAll(WDocUtil.getWadlDocs(methodMethods, DocScope.METHOD, null, baseURI))

				// method request
				WadlRequest wr = generateRequest(mappedMethods)
				if (wr) {
					wadlMethod.request = wr
				}

				// method responses
				for (Entry<RequestMappingInfo, HandlerMethod> entry in mappedMethods) {
					/*
					 * XXX what about if there are multiple handler methods w/
					 * only different representations?
					 */
					generateResponse(entry.key, entry.value).each { wadlMethod.response << it }
				}

				wadlResource.methodOrResource << wadlMethod
			}

			wadResources.resource << wadlResource
		}

		result.resources << wadResources
		return result;
	}

	/**
	 * Generate the template parameters from an annotated method.
	 * 
	 * @param method the java handler method
	 * @return the template parameters or an empty list
	 */
	@CompileStatic
	private def generateTemplateParams(Method method) {
		def result = []

		Annotation[][] annotations = method.parameterAnnotations
		Class<?>[] paramTypes = method.parameterTypes
		int parameterCounter = 0;

		// final all PathVariable annotations
		for (Annotation[] annotationArr in annotations) {
			for (Annotation annotation : annotationArr) {
				if (annotation instanceof PathVariable) {
					PathVariable param = (PathVariable) annotation;

					WadlParam wadlParam = new WadlParam();

					wadlParam.name = param.value()
					wadlParam.style = WadlParamStyle.TEMPLATE
					wadlParam.required = true

					parameterCounter = skipDefaultParams(paramTypes, parameterCounter)
					if (paramTypes.length > parameterCounter) {
						Class paramType = paramTypes[parameterCounter]
						//XXX do something with param type?
					}

					if (wadlParam.name) {
						wadlParam.doc.addAll(WDocUtil.getWadlDocs([method], DocScope.PARAM, wadlParam.name, baseURI))
					}

					result << wadlParam
				}

				parameterCounter++;
			}
		}

		result
	}

	@CompileStatic
	private WadlRequest generateRequest(Collection<Entry<RequestMappingInfo, HandlerMethod>> methods) {
		WadlRequest wadlRequest = new WadlRequest()

		//XXX for now assume the same request for all methods
		Entry firstEntry = methods.find{ true }
		RequestMappingInfo mappingInfo = firstEntry.key
		HandlerMethod handlerMethod = firstEntry.value

		Set<MediaType> mediaTypes = mappingInfo.consumesCondition.consumableMediaTypes
		// request representations
		for (MediaType mediaType in mediaTypes) {
			WadlRepresentation wadlRepresentation = new WadlRepresentation()
			wadlRepresentation.mediaType = mediaType.toString()

			wadlRequest.representation << wadlRepresentation
		}

		Method javaMethod = handlerMethod.method

		// request documentation
		wadlRequest.doc.addAll(WDocUtil.getWadlDocs([javaMethod], DocScope.REQUEST, null, baseURI))

		Annotation[][] annotations = javaMethod.parameterAnnotations
		Class<?>[] paramTypes = javaMethod.parameterTypes
		int parameterCounter = 0;

		for (Annotation[] annotationArr in annotations) {
			for (Annotation annotation : annotationArr) {
				if (annotation instanceof RequestParam) {
					// translate request parameter to WADL query parameter
					RequestParam param = (RequestParam) annotation;

					WadlParam wadlParam = new WadlParam();

					wadlParam.name = param.value()
					wadlParam.style = WadlParamStyle.QUERY
					wadlParam.required = param.required()

					parameterCounter = skipDefaultParams(paramTypes, parameterCounter)
					if (paramTypes.length > parameterCounter) {
						Class paramType = paramTypes[parameterCounter]
						//XXX do something with param type?
					}

					if (wadlParam.name) {
						wadlParam.doc.addAll(WDocUtil.getWadlDocs([javaMethod], DocScope.PARAM, wadlParam.name, baseURI))
					}

					String defaultValue = cleanDefault(param.defaultValue())
					if (defaultValue) {
						wadlParam.setDefault(defaultValue)
					}
					wadlRequest.param << wadlParam
				}
				else if (annotation instanceof RequestPart) {
					// translate request part to WADL query parameter
					RequestPart param = (RequestPart) annotation;

					WadlParam wadlParam = new WadlParam();

					wadlParam.name = param.value()
					wadlParam.style = WadlParamStyle.QUERY
					wadlParam.required = param.required()

					parameterCounter = skipDefaultParams(paramTypes, parameterCounter)
					if (paramTypes.length > parameterCounter) {
						Class paramType = paramTypes[parameterCounter]
						//XXX do something with param type?
					}

					if (wadlParam.name) {
						wadlParam.doc.addAll(WDocUtil.getWadlDocs([javaMethod], DocScope.PARAM, wadlParam.name, baseURI))
					}

					wadlRequest.param << wadlParam
				}

				parameterCounter++;
			}
		}
		if (wadlRequest.param || wadlRequest.doc) {
			return wadlRequest
		}

		null
	}

	@SuppressWarnings("rawtypes")
	@CompileStatic
	private int skipDefaultParams(Class[] paramTypes, int currentIndex) {
		if (paramTypes && paramTypes.length > currentIndex) {
			if (paramTypes.length > currentIndex
			&& (paramTypes[currentIndex] == javax.servlet.http.HttpServletRequest.class || paramTypes[currentIndex] == javax.servlet.http.HttpServletResponse.class))
				currentIndex++;
			if (paramTypes.length > currentIndex
			&& (paramTypes[currentIndex] == javax.servlet.http.HttpServletRequest.class || paramTypes[currentIndex] == javax.servlet.http.HttpServletResponse.class))
				currentIndex++;
		}

		currentIndex
	}

	/**
	 * Generate WADL responses.
	 * 
	 * @param mappingInfo the mapping information of a handler method
	 * @param method the handler method
	 * @return the {@link WadlResponse} or an empty list
	 */
	@CompileStatic
	private def generateResponse(RequestMappingInfo mappingInfo, HandlerMethod method) {
		Set<MediaType> mediaTypes = mappingInfo.producesCondition.producibleMediaTypes

		if (mediaTypes) {
			WadlResponse wadlResponse = new WadlResponse()

			//TODO response status codes
			// wadlResponse.status << 200l;

			// response documentation from handlerMethod
			wadlResponse.doc.addAll(WDocUtil.getWadlDocs([method.method], DocScope.RESPONSE, null, baseURI))

			for (MediaType mediaType in mediaTypes) {
				WadlRepresentation wadlRepresentation = new WadlRepresentation()
				wadlRepresentation.mediaType = mediaType.toString()

				wadlResponse.representation << wadlRepresentation
			}

			return wadlResponse
		}

		return []
	}

	@CompileStatic
	private String cleanDefault(String value) {
		if (value == ValueConstants.DEFAULT_NONE) return null

		value = value.replaceAll("\t", "");
		value = value.replaceAll("\n", "");
		//		value = value.replaceAll("?", "");
		//		value = value.replaceAll("?", "");
		//		value = value.replaceAll("?", "");
		return value;
	}

	/**
	 * Determine the XML Schema type name representing a Java class.
	 * 
	 * @param classType the Java class
	 * @return the corresponding schema type or <code>null</code> if none was
	 *         recognized
	 */
	@CompileStatic
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
