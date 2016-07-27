/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.io.util.GeoInstanceWriterDecorator;
import eu.esdihumboldt.hale.io.gml.writer.XmlWrapper;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.util.http.ProxyUtil;
import eu.esdihumboldt.util.http.client.fluent.FluentProxyUtil;

/**
 * Base class for WFS writers that directly write to the WFS-T.
 * 
 * @param <T> the XML/GML writer type
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class AbstractWFSWriter<T extends StreamGmlWriter>
		extends GeoInstanceWriterDecorator<T>implements WFSWriter, WFSConstants {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractWFSWriter.class);

	private LocatableOutputSupplier<? extends OutputStream> targetWfs;

	private OutputStream currentExecuteStream;

	private final LocatableOutputSupplier<? extends OutputStream> decorateeTarget = new LocatableOutputSupplier<OutputStream>() {

		@Override
		public OutputStream getOutput() throws IOException {
			return currentExecuteStream;
		}

		@Override
		public URI getLocation() {
			return targetWfs.getLocation();
		}
	};

	/**
	 * @param internalProvider the internal provider producing GML
	 */
	public AbstractWFSWriter(T internalProvider) {
		super(internalProvider);
	}

	@Override
	public void setWFSVersion(WFSVersion version) {
		setParameter(PARAM_WFS_VERSION, Value.of(version.versionString));
	}

	@Override
	public WFSVersion getWFSVersion() {
		String versionString = getParameter(PARAM_WFS_VERSION).as(String.class);
		if (versionString == null) {
			return null;
		}
		else
			return WFSVersion.fromString(versionString, null);
	}

	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		targetWfs = target;
		if (targetWfs == null) {
			super.setTarget(null);
		}
		else {
			super.setTarget(decorateeTarget);
		}
	}

	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
		return targetWfs;
	}

	@Override
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		progress.begin("WFS Transaction", ProgressIndicator.UNKNOWN);

		// configure internal provider
		internalProvider.setDocumentWrapper(createTransaction());

		final PipedInputStream pIn = new PipedInputStream();
		PipedOutputStream pOut = new PipedOutputStream(pIn);
		currentExecuteStream = pOut;

		Future<Response> futureResponse = null;
		IOReporter reporter = createReporter();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			// read the stream (in another thread)
			futureResponse = executor.submit(new Callable<Response>() {

				@Override
				public Response call() throws Exception {

					Proxy proxy = ProxyUtil.findProxy(targetWfs.getLocation());
					Request request = Request.Post(targetWfs.getLocation()).bodyStream(pIn,
							ContentType.APPLICATION_XML);
					Executor executor = FluentProxyUtil.setProxy(request, proxy);

					try {
						return executor.execute(request);
					} finally {
						pIn.close();
					}
				}
			});

			// write the stream
			SubtaskProgressIndicator subprogress = new SubtaskProgressIndicator(progress);
			reporter = (IOReporter) super.execute(subprogress);
		} finally {
			executor.shutdown();
		}

		try {
			Response response = futureResponse.get();
			HttpResponse res = response.returnResponse();
			int statusCode = res.getStatusLine().getStatusCode();
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			if (statusCode >= 200 && statusCode < 300) {
				// success
				reporter.setSuccess(reporter.isSuccess());

				// construct summary from response
				try {
					Document responseDoc = parseResponse(res.getEntity());

					// totalInserted
					String inserted = xpath.compile("//TransactionSummary/totalInserted")
							.evaluate(responseDoc);
					// XXX totalUpdated
					// XXX totalReplaced
					// XXX totalDeleted
					reporter.setSummary("Inserted " + inserted + " features.");
				} catch (XPathExpressionException e) {
					log.error("Error in XPath used to evaluate service response");
				} catch (ParserConfigurationException | SAXException e) {
					reporter.error(new IOMessageImpl(MessageFormat.format(
							"Server returned status code {0}, but could not parse server response",
							statusCode), e));
					reporter.setSuccess(false);
				}
			}
			else {
				// failure
				reporter.error(new IOMessageImpl(
						"Server reported failure with code " + res.getStatusLine().getStatusCode()
								+ ": " + res.getStatusLine().getReasonPhrase(),
						null));
				reporter.setSuccess(false);

				try {
					Document responseDoc = parseResponse(res.getEntity());
					String errorText = xpath.compile("//ExceptionText/text()")
							.evaluate(responseDoc);
					reporter.setSummary("Request failed: " + errorText);
				} catch (XPathExpressionException e) {
					log.error("Error in XPath used to evaluate service response");
				} catch (ParserConfigurationException | SAXException e) {
					reporter.error(new IOMessageImpl("Could not parse server response", e));
					reporter.setSuccess(false);
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			reporter.error(new IOMessageImpl("Failed to execute WFS-T request", e));
			reporter.setSuccess(false);
		}

		progress.end();

		return reporter;
	}

	private Document parseResponse(HttpEntity entity)
			throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		byte[] data = EntityUtils.toByteArray(entity);
		try (InputStream response = new ByteArrayInputStream(data)) {
			return builder.parse(response);
		} catch (SAXException e) {
			String response = new String(data, StandardCharsets.UTF_8);
			throw new SAXException("Invalid XML response: " + response, e);
		}
	}

	/**
	 * @return the transaction wrapper
	 */
	protected abstract XmlWrapper createTransaction();

}
