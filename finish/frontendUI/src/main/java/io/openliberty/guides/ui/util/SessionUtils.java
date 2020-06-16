// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::jwt[]
package io.openliberty.guides.ui.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.security.auth.Subject;
import com.ibm.websphere.security.auth.WSSubject;
import java.util.Set;
import java.util.Map;

public class SessionUtils {

  /**
   * Gets the current session for a logged in user.
   */
  public static HttpSession getSession() {
    return (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
                                     .getSession(false);
  }

  /**
   * Gets Http servlet for user request information.
   */
  public static HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance()
                                            .getExternalContext().getRequest();
  }

  /*
  public static String getJwtToken() {
    return (String) getSession().getAttribute("jwt");
  }
  */

  public static String getJwtToken() {
      try {
        Subject subj = WSSubject.getRunAsSubject();
        Set<Object> privCredentials = subj.getPrivateCredentials();
        for (Object credentialObj : privCredentials) {
            if (credentialObj instanceof Map) {
                Object value = ((Map<?, ?>) credentialObj).get("access_token");
                if (value != null)
                    System.out.println("access_token="+value);
                    return (String) value;
            }
        }
      }catch (Exception e) {
      }

      System.out.println("access_token is null");
      return null;
  }

}
// end::jwt[]
