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

package eu.esdihumboldt.hale.common.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.supplier.LookupStreamResource;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.InputSupplier;
import eu.esdihumboldt.util.resource.Resources;

/**
 * Hale I/O utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class HaleIO {

	private static final ALogger log = ALoggerFactory.getLogger(HaleIO.class);

	/**
	 * Namespace for HALE core complex value type elements.
	 */
	public static final String NS_HALE_CORE = "http://www.esdi-humboldt.eu/hale/core";

	/**
	 * Tests whether a InputStream to the given URI can be opened. <br>
	 * In case of a file it instead tests File.isFile and File.canRead() because
	 * it is a lot faster.
	 * 
	 * @param uri the URI to test
	 * @param allowResource allow resolving through {@link Resources}
	 * @return true, if a InputStream to the URI could be opened.
	 */
	public static boolean testStream(URI uri, boolean allowResource) {
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			File file = new File(uri);
			if (file.isFile() && file.canRead())
				return true;
			return false;
		}

		// try resolving through local resources
		if (allowResource && Resources.tryResolve(uri, null) != null) {
			return true;
		}

		// could be further enhanced to check for example for http response
		// codes like 404.
		try {
			uri.toURL().openConnection().getInputStream().close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Filter I/O provider factories by content type
	 * 
	 * @param factories the I/O provider factories
	 * @param contentType the content type factories must support
	 * @return provider factories that support the given content type
	 */
	public static List<IOProviderDescriptor> filterFactories(
			Collection<IOProviderDescriptor> factories, IContentType contentType) {
		List<IOProviderDescriptor> result = new ArrayList<IOProviderDescriptor>();

		for (IOProviderDescriptor factory : factories) {
			Set<IContentType> supportedTypes = factory.getSupportedTypes();

			// check if contentType is supported
			for (IContentType test : supportedTypes) {
				if (isCompatibleContentType(test, contentType)) {
					result.add(factory);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Filter I/O provider factories by configuration content type
	 * 
	 * @param factories the I/O provider factories
	 * @param configurationContentType the configuration content type the
	 *            factories must support
	 * @return provider factories that support the given configuration content
	 *         type
	 */
	public static List<IOProviderDescriptor> filterFactoriesByConfigurationType(
			Collection<IOProviderDescriptor> factories, IContentType configurationContentType) {
		List<IOProviderDescriptor> result = new ArrayList<IOProviderDescriptor>();

		for (IOProviderDescriptor factory : factories) {
			Set<IContentType> supportedTypes = factory.getConfigurationTypes();

			// check if contentType is supported
			for (IContentType test : supportedTypes) {
				if (isCompatibleContentType(test, configurationContentType)) {
					result.add(factory);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Find all content types that are of the same kind as the given content
	 * type.
	 * 
	 * @param candidates Content types to search
	 * @param kind Parent content type
	 * @return List of matches, including at least <code>kind</code>
	 */
	public static List<IContentType> findContentTypesOfKind(IContentType[] candidates,
			IContentType kind) {

		return findContentTypesOfKind(Arrays.asList(candidates), kind);
	}

	/**
	 * Find all content types that are of the same kind as the given content
	 * type.
	 * 
	 * @param candidates Content types to search
	 * @param kind Parent content type
	 * @return List of matches, including at least <code>kind</code>
	 */
	public static List<IContentType> findContentTypesOfKind(Collection<IContentType> candidates,
			IContentType kind) {
		List<IContentType> result = new ArrayList<>();
		if (candidates == null || kind == null) {
			return result;
		}

		for (IContentType candidate : candidates) {
			if (candidate.isKindOf(kind)) {
				result.add(candidate);
			}
		}

		return result;
	}

	/**
	 * Find the content types that match the given file name and/or input.
	 * 
	 * NOTE: The implementation should try to restrict the result to one content
	 * type and only use the input supplier if absolutely needed.
	 * 
	 * @param types the types to match
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *            if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *            supplier is not <code>null</code>
	 * @return the matched content types
	 */
	public static List<IContentType> findContentTypesFor(Collection<IContentType> types,
			InputSupplier<? extends InputStream> in, String filename) {
		Preconditions.checkArgument(filename != null || in != null,
				"At least one of input supplier and file name must not be null");

		List<IContentType> results = new ArrayList<IContentType>();

		if (filename != null && !filename.isEmpty()) {
			// test file extension
			String lowerFile = filename.toLowerCase();
			for (IContentType type : types) {
				String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				boolean match = false;
				for (int i = 0; i < extensions.length && !match; i++) {
					if (lowerFile.endsWith("." + extensions[i].toLowerCase())) {
						match = true;
					}
				}
				if (match) {
					results.add(type);
				}
			}
		}

		if ((results.isEmpty() || results.size() > 1) && in != null) {
			// remember previous results
			List<IContentType> extensionResults = null;
			if (!results.isEmpty()) {
				extensionResults = new ArrayList<>(results);
			}
			// clear results because only an ambiguous result was found
			results.clear();
			// use input stream to make a better test
			IContentTypeManager ctm = HalePlatform.getContentTypeManager();
			try {
				InputStream is = in.getInput();

				/*
				 * IContentTypeManager.findContentTypes seems to return all kind
				 * of content types that match in any way, but ordered by
				 * relevance - so if all but the allowed types are removed, the
				 * remaining types may be very irrelevant and not a match that
				 * actually was determined based on the input stream.
				 * 
				 * Thus findContentTypesFor should not be used or only relied
				 * upon the single best match that is returned. It is now only
				 * used as fall-back if there is no result for
				 * findContentTypeFor.
				 */
				// instead use findContentTypeFor
				IContentType candidate = ctm.findContentTypeFor(is, null);
				if (types.contains(candidate)) {
					results.add(candidate);
				}

				is.close();
			} catch (IOException e) {
				log.warn("Could not read input to determine content type", e);
			}

			if (results.isEmpty()) {
				/*
				 * Fall-back to findContentTypesFor if there was no valid result
				 * for findContentTypeFor.
				 */
				try (InputStream is = in.getInput()) {
					IContentType[] candidates = ctm.findContentTypesFor(is, filename);

					for (IContentType candidate : candidates) {
						if (types.contains(candidate)) {
							results.add(candidate);
						}
					}
				} catch (IOException e) {
					log.warn("Could not read input to determine content type", e);
				}
			}

			if (results.isEmpty() && extensionResults != null) {
				/*
				 * If there was no valid result for the stream check, but for
				 * the extension checks, use those results. This may happen for
				 * instance for generic XML files, that have no specific root
				 * element.
				 */
				results.addAll(extensionResults);
			}
		}

		return results;
	}

	/**
	 * Get the I/O provider factories of a certain type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @return the factories currently registered in the system
	 */
	public static <P extends IOProvider> Collection<IOProviderDescriptor> getProviderFactories(
			final Class<P> providerType) {
		return IOProviderExtension.getInstance()
				.getFactories(new FactoryFilter<IOProvider, IOProviderDescriptor>() {

					@Override
					public boolean acceptFactory(IOProviderDescriptor descriptor) {
						return descriptor != null && descriptor.getProviderType() != null
								&& providerType.isAssignableFrom(descriptor.getProviderType());
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<IOProvider, IOProviderDescriptor> collection) {
						return true;
					}
				});
	}

	/**
	 * Find an I/O provider factory
	 * 
	 * @param
	 * 			<P>
	 *            the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider factory or <code>null</code> if no matching I/O
	 *         provider factory is found
	 */
	public static <P extends IOProvider> IOProviderDescriptor findIOProviderFactory(
			Class<P> providerType, IContentType contentType, String providerId) {
		Preconditions.checkArgument(contentType != null || providerId != null);

		Collection<IOProviderDescriptor> factories = getProviderFactories(providerType);
		if (contentType != null) {
			factories = filterFactories(factories, contentType);
		}

		IOProviderDescriptor result = null;

		if (providerId != null) {
			for (IOProviderDescriptor factory : factories) {
				if (factory.getIdentifier().equals(providerId)) {
					result = factory;
					break;
				}
			}
		}
		else {
			// TODO choose priority based?
			if (!factories.isEmpty()) {
				result = factories.iterator().next();
			}
		}

		return result;
	}

	/**
	 * Creates an I/O provider instance
	 * 
	 * @param
	 * 			<P>
	 *            the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was
	 *         given or <code>null</code> if no matching I/O provider is found
	 */
	@SuppressWarnings("unchecked")
	public static <P extends IOProvider> P createIOProvider(Class<P> providerType,
			IContentType contentType, String providerId) {
		IOProviderDescriptor factory = findIOProviderFactory(providerType, contentType, providerId);
		P result;
		try {
			result = (P) ((factory == null) ? (null) : (factory.createExtensionObject()));
		} catch (Exception e) {
			throw new RuntimeException("Could not create I/O provider", e);
		}

		if (result != null && contentType != null) {
			result.setContentType(contentType);
		}

		return result;
	}

	/**
	 * Find the content type for the given input
	 * 
	 * @param
	 * 			<P>
	 *            the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *            if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *            supplier is not <code>null</code>
	 * @return the content type or <code>null</code> if no matching content type
	 *         is found
	 */
	public static <P extends IOProvider> IContentType findContentType(Class<P> providerType,
			InputSupplier<? extends InputStream> in, String filename) {
		Collection<IOProviderDescriptor> providers = getProviderFactories(providerType);

		// collect supported content types
		Set<IContentType> supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : providers) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}

		// find matching content type
		List<IContentType> types = findContentTypesFor(supportedTypes, in, filename);

		if (types == null || types.isEmpty()) {
			return null;
		}

		// TODO choose?
		return types.iterator().next();
	}

	/**
	 * Find an I/O provider instance for the given input
	 * 
	 * @param
	 * 			<P>
	 *            the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *            if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *            supplier is not <code>null</code>
	 * @return the I/O provider or <code>null</code> if no matching I/O provider
	 *         is found
	 */
	public static <P extends IOProvider> P findIOProvider(Class<P> providerType,
			InputSupplier<? extends InputStream> in, String filename) {
		IContentType contentType = findContentType(providerType, in, filename);
		if (contentType == null) {
			return null;
		}

		return HaleIO.createIOProvider(providerType, contentType, null);
	}

	/**
	 * Find an I/O provider instance for the given input
	 * 
	 * @param <T> the provider interface type
	 * 
	 * @param providerType the provider type, usually an interface
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *            if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *            supplier is not <code>null</code>
	 * @return a pair with the I/O provider and the corresponding identifier,
	 *         both are <code>null</code> if no matching I/O provider was found
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IOProvider> Pair<T, String> findIOProviderAndId(Class<T> providerType,
			InputSupplier<? extends InputStream> in, String filename) {
		T reader = null;
		String providerId = null;
		IContentType contentType = HaleIO.findContentType(providerType, in, filename);
		if (contentType != null) {
			IOProviderDescriptor factory = HaleIO.findIOProviderFactory(providerType, contentType,
					null);
			try {
				reader = (T) factory.createExtensionObject();
				providerId = factory.getIdentifier();
			} catch (Exception e) {
				throw new RuntimeException("Could not create I/O provider", e);
			}

			if (reader != null) {
				reader.setContentType(contentType);
			}
		}
		return new Pair<T, String>(reader, providerId);
	}

	/**
	 * Automatically find an import provider to load a resource that is
	 * available through an input stream that can only be read once.
	 * 
	 * @param type the import provider type
	 * @param in the input stream
	 * @return the import provider or <code>null</code> if none was found
	 */
	public static <T extends ImportProvider> T findImportProvider(Class<T> type, InputStream in) {
		LookupStreamResource res = new LookupStreamResource(in, null, 64 * 1024);
		T provider = HaleIO.findIOProvider(type, res.getLookupSupplier(), null);
		if (provider != null) {
			provider.setSource(res.getInputSupplier());
			return provider;
		}
		return null;
	}

	/**
	 * Test if the given value content type is compatible with the given parent
	 * content type
	 * 
	 * @param parentType the parent content type
	 * @param valueType the value content type
	 * @return if the value content type is compatible with the parent content
	 *         type
	 */
	public static boolean isCompatibleContentType(IContentType parentType, IContentType valueType) {
		return valueType.isKindOf(parentType);
	}

	/**
	 * Get the value of a complex property represented as a DOM element.
	 * 
	 * @param element the DOM element
	 * @param context the context object, may be <code>null</code>
	 * @return the complex value converted through the
	 *         {@link ComplexValueExtension}, or the original element
	 */
	public static Object getComplexValue(Element element, Object context) {
		QName name;
		if (element.getNamespaceURI() != null && !element.getNamespaceURI().isEmpty()) {
			name = new QName(element.getNamespaceURI(), element.getLocalName());
		}
		else {
			name = new QName(element.getTagName()); // .getLocalName());
		}
		ComplexValueDefinition cvt = ComplexValueExtension.getInstance().getDefinition(name);
		if (cvt != null) {
			// create and return the complex parameter value
			return cvt.fromDOM(element, cvt.getContextType().isInstance(context) ? context : null);
		}

		// the element itself is the complex value
		return element;
	}

	/**
	 * Get the value of a complex property represented as a DOM element.
	 * 
	 * @param element the DOM element
	 * @param expectedType the expected parameter type, this must be either
	 *            {@link String}, DOM {@link Element} or a complex value type
	 *            defined in the {@link ComplexValueExtension}
	 * @param context the context object, may be <code>null</code>
	 * @return the complex value or <code>null</code> if it could not be created
	 *         from the element
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getComplexValue(Element element, Class<T> expectedType, Object context) {
		if (element == null) {
			return null;
		}

		QName name;
		if (element.getNamespaceURI() != null && !element.getNamespaceURI().isEmpty()) {
			name = new QName(element.getNamespaceURI(), element.getLocalName());
		}
		else {
			String ln = element.getTagName(); // .getLocalName();
			name = new QName(ln);
		}
		ComplexValueDefinition cvt = ComplexValueExtension.getInstance().getDefinition(name);
		Object value = null;
		if (cvt != null) {
			try {
				value = cvt.fromDOM(element,
						cvt.getContextType().isInstance(context) ? context : null);
			} catch (Exception e) {
				throw new IllegalStateException("Failed to load complex value from DOM", e);
			}
		}

		if (value != null && expectedType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}

		// maybe the element itself is OK
		if (expectedType.isAssignableFrom(element.getClass())) {
			return (T) element;
		}

		if (expectedType.isAssignableFrom(String.class)) {
			// FIXME use legacy complex value if possible
		}

		return null;
	}

	/**
	 * Get the representation of a complex value as a DOM element. Uses the
	 * {@link ComplexValueExtension}.
	 * 
	 * @param value the complex value
	 * @return the DOM representation
	 * @throws IllegalStateException if the value is neither a DOM element nor
	 *             can be converted to one using the
	 *             {@link ComplexValueExtension}
	 */
	public static Element getComplexElement(Object value) {
		if (value instanceof Element) {
			// as is
			return (Element) value;
		}

		ComplexValueDefinition cvd = ComplexValueExtension.getInstance()
				.getDefinition(value.getClass());
		if (cvd != null) {
			return cvd.toDOM(value);
		}

		throw new IllegalStateException("No definition for complex parameter value found");
	}

//	/**
//	 * Get the file extensions for the given content type
//	 * 
//	 * @param contentType the content type
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(ContentType contentType,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = HalePlatform.getService(ContentTypeService.class);
//		String[] typeExts = cts.getFileExtensions(contentType);
//		if (typeExts != null) {
//			for (String typeExt : typeExts) {
//				if (prefix == null) {
//					exts.add(typeExt);
//				}
//				else {
//					exts.add(prefix + typeExt);
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}

//	/**
//	 * Get all file extensions for the given content types
//	 * 
//	 * @param contentTypes the content types
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(Iterable<ContentType> contentTypes,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = HalePlatform.getService(ContentTypeService.class);
//		for (ContentType contentType : contentTypes) {
//			String[] typeExts = cts.getFileExtensions(contentType);
//			if (typeExts != null) {
//				for (String typeExt : typeExts) {
//					if (prefix == null) {
//						exts.add(typeExt);
//					}
//					else {
//						exts.add(prefix + typeExt);
//					}
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}

}
