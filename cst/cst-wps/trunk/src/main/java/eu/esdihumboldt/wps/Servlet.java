package eu.esdihumboldt.wps;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.CstServiceCapabilitiesImpl;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

public class Servlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");

		CstServiceCapabilitiesImpl service = new CstServiceCapabilitiesImpl();
		for (Iterator<FunctionDescription> i = service
				.getFunctionDescriptions().iterator(); i.hasNext();) {

			FunctionDescription fd = i.next();
			out.println(fd.getFunctionId());

			for (Iterator<String> j = fd.getParameterConfiguration().keySet()
					.iterator(); j.hasNext();) {
				String name = j.next();

				fd.getParameterConfiguration().get(name);
				System.out.println(name + " --- "
						+ fd.getParameterConfiguration().get(name));
			}

		}
	}
}
