package scalable.solutions.zuul.filters;

import com.google.common.net.HttpHeaders;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scalable.solutions.zuul.oauth.OAuth2Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Zuul Filter used to inject Authorization header from user session. The header
 * is injected only if the Authorization header is missing in the request and an
 * authorization header is found inside user session.
 * <p>Zuul filter types:</p>
 * <ul>
 * 	<li>pre      filters are executed before the request is routed</li>
 * 	<li>routing  filters can handle the actual routing of the request</li>
 * 	<li>post     filters are executed after the request has been routed</li>
 * 	<li>error    filters execute if an error occurs in the course of handling the request</li>
 * </ul>
 *
 * @author Marius
 */
public class AccessTokenFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        addAuthHeader(context);
        return null;
    }

    /**
     * Add authorization header if not exists in the request and if exists in
     * the user session.
     *
     * @param context Request context
     */
    private void addAuthHeader(RequestContext context) {
        HttpServletRequest request = context.getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            // No authorization header
            HttpSession session = request.getSession(false);
            if (session != null) {
                // have session
                Object authorization = session.getAttribute(OAuth2Controller.AUTHORIZATION);
                if (authorization != null) {
                    // have access token stored in session
                    HeaderMapRequestWrapper wrappedRequest = new HeaderMapRequestWrapper(request);
                    wrappedRequest.addHeader(HttpHeaders.AUTHORIZATION, authorization.toString());
                    context.setRequest(wrappedRequest);
                    LOG.info(String.format("[Method: %s][URL: %s]", request.getMethod(), request.getRequestURL().toString()));
                }
            }
        }
    }

    /**
     * Http Request wrapper class.
     *
     * @author Marius
     */
    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

        /**
         * Added headers
         */
        private final Map<String, String> headerMap = new HashMap<>();

        /**
         * Construct a wrapper for this request
         *
         * @param request Wrapped request
         */
        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * Add a header with given name and value
         *
         * @param name  Header name
         * @param value Header value
         */
        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
			names.addAll(headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            }
            return Collections.enumeration(values);
        }
    }
}
