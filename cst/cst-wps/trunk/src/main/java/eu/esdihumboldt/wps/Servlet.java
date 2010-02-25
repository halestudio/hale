package eu.esdihumboldt.wps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.esdihumboldt.cst.iobridge.IoBridgeFactory;
import eu.esdihumboldt.cst.iobridge.IoBridgeFactory.BridgeType;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

public class Servlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");

		
	}
}
