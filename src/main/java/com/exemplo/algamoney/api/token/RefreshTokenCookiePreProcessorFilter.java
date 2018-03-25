package com.exemplo.algamoney.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		System.out.println("RefreshTokenCookiePreProcessorFilter.doFilter()");

		HttpServletRequest req = (HttpServletRequest) request;
		
		System.out.println("req.getRequestURI(): "+ req.getRequestURI());
		System.out.println("eq.getParameter: "+req.getParameter("grant_type"));

		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type")) 
				&& req.getCookies() != null) {

			System.out.println("yes");
			
			for (Cookie cookie : req.getCookies()) {

				System.out.println("cookie.getName(): " +cookie.getName());
				
				if (cookie.getName().equals("refreshToken")) {
					String refreshCookie = cookie.getValue();
					System.out.println("refreshCookie: " +refreshCookie);
					req = new MyServletRequestWrapper(req, refreshCookie);
				}

			}
		}
		
		chain.doFilter(req, response);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	static class MyServletRequestWrapper extends HttpServletRequestWrapper {

		String refreshtoken;

		public MyServletRequestWrapper(HttpServletRequest request, String refreshtoken) {
			super(request);
			this.refreshtoken = refreshtoken;
			System.out.println("MyServletRequestWrapper");
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());

			map.put("refresh_token", new String[] { refreshtoken });
			map.setLocked(true);
			
			return map;
		}

	}
}
