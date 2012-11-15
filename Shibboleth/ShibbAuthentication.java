package edu.rochester.cascade.shibb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hannonhill.cascade.model.security.authentication.AuthenticationPhase;
import com.hannonhill.cascade.model.security.authentication.Authenticator;

/**
 * This is based on the work of Andre Daniels (andre777 [at] ucsc [dot] edu) from UC Santa Cruz.
 * 
 * @author Bradley Wagner
 * @author Andre Daniels
 * 
 * @description This is an implementation of the Cascade Server Authentication module that requires the user
 *              login via Shibboleth.
 */
public class ShibbAuthentication implements Authenticator
{
    private static Logger logger = Logger.getLogger("shib-log");
    
    /**
     * Logs a message as an ERROR. Useful for debugging as ERRORS show up in the log by default.
     */
    private void log(String message)
    {
        logger.error(message);
    }

    public boolean redirect(HttpServletRequest request, HttpServletResponse response, AuthenticationPhase authenticationPhase) throws IOException
    {
        // Allows login through the normal Cascade interface if you're allowing
        // traffic to flow through Apache on a different port or virtual host
        // and not go through Shibboleth
        if (request.getAttribute("uid") == null)
            return false;

        switch (authenticationPhase)
        {
        case LOGIN:
            // Redirect to the custom authentication servlet so that it can call authenticate()
            response.sendRedirect(Authenticator.AUTHENTICATION_URI);
            return true;
        case LOGOUT:
            // Shibboleth doesn't support Single Sign-Out but this will ensure that 
            // the local Shibboleth session is destroyed and that the system will have
            // to go back to the Shibboleth IdP to ensure the user is still logged in.
            response.sendRedirect("/Shibboleth.sso/Logout");
            return true;
        default:
            // should never get here
            return false;
        }
    }

    /**
     * Allows the module to report back whether or not the user has been authenticated.
     * 
     * @param request
     * @param response
     * @return the username if the user successfully authenticated
     */
    public String authenticate(HttpServletRequest request, HttpServletResponse response)
    {
        // Pull the appropriate attribute passed to it by Apache/AJP and return it
        
        log("Attribute: uid, Value: " + request.getAttribute("uid"));
        String username = (String) request.getAttribute("uid");
        log("Authenticating with username: " + username);

        return username;
    }
}