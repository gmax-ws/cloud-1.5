package scalable.solutions.zuul.utils;

import org.springframework.http.ResponseEntity;
import scalable.solutions.zuul.errors.UnauthorizedException;
import scalable.solutions.zuul.oauth.OAuth2Controller;

import javax.servlet.http.HttpSession;

/**
 * Utilities
 *
 * @author Marius
 */
public class RestUtility {

    /**
     * Clone ResponseEntity without headers
     *
     * @param response ResponseEntity source
     * @return ResponseEntity Clone
     */
    public static <T> ResponseEntity<T> clone(final ResponseEntity<T> response) {
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    /**
     * Get access token from http session. Throw an exception if doesn't exists.
     *
     * @param session Http session
     * @return access token or raise exception if not found
     */
    public static String getAccessTokenFromSession(final HttpSession session) {
        checkHttpSession(session);
        final Object token = session.getAttribute(OAuth2Controller.AUTHORIZATION);
        if (token == null) {
            throw new UnauthorizedException("Access token is missing");
        }
        return token.toString();
    }

    /**
     * Get refresh token from http session. Throw an exception if doesn't
     * exists.
     *
     * @param session Http session
     * @return refresh token or raise exception if not found
     */
    public static String getRefershTokenFromSession(final HttpSession session) {
        checkHttpSession(session);
        final Object token = session.getAttribute(OAuth2Controller.REFRESH_TOKEN);
        if (token == null) {
            throw new UnauthorizedException("Refresh token is missing");
        }
        return token.toString();
    }

    private static void checkHttpSession(final HttpSession session) {
        if (session == null) {
            throw new UnauthorizedException("Unauthenticated");
        }
    }
}
