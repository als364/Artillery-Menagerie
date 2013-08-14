package edu.cornell.artillerymenagerie;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class ArtilleryMenagerieServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
