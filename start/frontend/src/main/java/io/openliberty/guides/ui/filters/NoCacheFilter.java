package io.openliberty.guides.ui.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(servletNames = { "Faces Servlet" })
public class NoCacheFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                httpServletResponse.setDateHeader("Expires", 0);
                httpServletResponse.setHeader("Pragma", "no-cache");
                chain.doFilter(request, response);
	}

}
