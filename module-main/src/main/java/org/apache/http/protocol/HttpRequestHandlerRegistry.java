/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Copyright 1999-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Maintains a map of HTTP request handlers keyed by a request URI pattern. 
 * {@link HttpRequestHandler} instances can be looked by request URI
 * using {@link HttpRequestHandlerResolver} interface.
 *
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 * @version $Revision$
 */
public class HttpRequestHandlerRegistry implements HttpRequestHandlerResolver {

    private final Map handlerMap;
    
    public HttpRequestHandlerRegistry() {
        super();
        this.handlerMap = new HashMap();
    }
    
    public void register(final String pattern, final HttpRequestHandler handler) {
        if (pattern == null) {
            throw new IllegalArgumentException("URI request pattern may not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("HTTP request handelr may not be null");
        }
        this.handlerMap.put(pattern, handler);
    }
    
    public void unregister(final String pattern) {
        if (pattern == null) {
            return;
        }
        this.handlerMap.remove(pattern);
    }
    
    public void setHandlers(final Properties props) {
        if (props == null) {
            throw new IllegalArgumentException("Properties may not be null");
        }
        this.handlerMap.clear();
        this.handlerMap.putAll(props);
    }
    
    public HttpRequestHandler lookup(final String requestURI) {
        // direct match?
        Object handler = this.handlerMap.get(requestURI);
        if (handler == null) {
            // pattern match?
            String bestMatch = null;
            for (Iterator it = this.handlerMap.keySet().iterator(); it.hasNext();) {
                String pattern = (String) it.next();
                if (matchUriRequestPattern(pattern, requestURI)) {
                    // we have a match. is it any better?
                    if (bestMatch == null || bestMatch.length() <= pattern.length()) {
                        handler = this.handlerMap.get(pattern);
                        bestMatch = pattern;
                    }
                }
            }
        }
        return (HttpRequestHandler) handler;
    }

    protected boolean matchUriRequestPattern(final String pattern, final String requestUri) {
        if (pattern.equals("*")) {
            return true;
        } else {
            return 
            (pattern.endsWith("*") && requestUri.startsWith(pattern.substring(0, pattern.length() - 1))) ||
            (pattern.startsWith("*") && requestUri.endsWith(pattern.substring(1, pattern.length())));
        }
    }
    
}
