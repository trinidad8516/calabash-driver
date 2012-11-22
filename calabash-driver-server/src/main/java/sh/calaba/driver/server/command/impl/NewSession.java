/*
 * Copyright 2012 calabash-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package sh.calaba.driver.server.command.impl;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sh.calaba.driver.CalabashCapabilities;
import sh.calaba.driver.exceptions.SessionNotCreatedException;
import sh.calaba.driver.net.FailedWebDriverLikeResponse;
import sh.calaba.driver.net.WebDriverLikeRequest;
import sh.calaba.driver.net.WebDriverLikeResponse;
import sh.calaba.driver.server.CalabashProxy;
import sh.calaba.driver.server.command.BaseCommandHandler;

/**
 * Command handler for a new calabash test session.
 * 
 * @author ddary
 */
public class NewSession extends BaseCommandHandler {
  final static Logger logger = LoggerFactory.getLogger(NewSession.class);

  public NewSession(CalabashProxy proxy, WebDriverLikeRequest request) {
    super(proxy, request);
  }

  public WebDriverLikeResponse handle() throws Exception {
    JSONObject payload = getRequest().getPayload();
    // Retrieving the capabilities for the session
    JSONObject desiredCapabilities = payload.getJSONObject("desiredCapabilities");

    CalabashCapabilities capabilities = CalabashCapabilities.fromJSON(desiredCapabilities);


    String sessionID = null;
    try {
      sessionID = getCalabashProxy().initializeSessionForCapabilities(capabilities);
    } catch (SessionNotCreatedException e) {
      return new FailedWebDriverLikeResponse("", e, 33);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("New Session Class; session iD: " + sessionID);
    }
    JSONObject json = new JSONObject();
    json.put("sessionId", sessionID);
    json.put("status", 0);
    json.put("value", new JSONObject());
    WebDriverLikeResponse response = new WebDriverLikeResponse(json);

    if (logger.isDebugEnabled()) {
      logger.debug("Created session ID in response: " + response.getSessionId());
    }
    return response;
  }
}
