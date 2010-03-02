package eu.esdihumboldt.wps;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List<FileItem> items = upload.parseRequest(req);
			Iterator iter = items.iterator();
			File gmlFile = null;
			File omlFile = null;
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (!item.isFormField()) {
					if (item.getFieldName().equals("gml")) {
						Date d = new Date();
						gmlFile = new File(this.getServletContext()
								.getRealPath("./tmp")
								+ "/" + d.getTime() + "." + item.getFieldName());
						item.write(gmlFile);
					} else if (item.getFieldName().equals("oml")) {
						Date d = new Date();
						omlFile = new File(this.getServletContext()
								.getRealPath("./tmp")
								+ "/" + d.getTime() + "." + item.getFieldName());
						item.write(omlFile);
					}

				} else {
					//throw new IOException("unexpected field "
					//		+ item.getFieldName());
				}
			}

                        resp.setContentType("text/html");
			String r = "{success:true, oml:\"files/tmp/"+omlFile.getName()+"\", gml:\"files/tmp/"+gmlFile.getName()+"\"}";
			
			resp.getWriter().write(r);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

}
