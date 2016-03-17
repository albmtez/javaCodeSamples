package javaCodeSamples.WebAppServletsTemplate;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.MediaType;

@WebServlet(name = "HelloWorld", urlPatterns = {"/HelloWorld"})
public class HelloWorld extends HttpServlet {

	/** Serial Version UID. */
	private static final long serialVersionUID = 2397308550828692024L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(MediaType.HTML_UTF_8.toString());
		PrintWriter out = response.getWriter();
		out.println("Hello World!!");
		out.close();
	}
}
