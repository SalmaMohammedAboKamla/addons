/*
 * Copyright 2018 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.addon.restfulapiclient.actions.base;

import io.testproject.addon.restfulapiclient.internal.*;
import io.testproject.java.annotations.v2.Parameter;
import io.testproject.java.enums.ParameterDirection;
import io.testproject.java.sdk.v2.addons.helpers.AddonHelper;
import io.testproject.java.sdk.v2.enums.ExecutionResult;
import io.testproject.java.sdk.v2.exceptions.FailureException;

public class BaseAction {

    // Base and common fields
    @Parameter(description = "Endpoint URL")
    public String uri = "";

    @Parameter(description = "Query parameters (e.g. abc=123&efg=456)")
    public String query = "";

    @Parameter(description = "Request headers (e.g. h1=v1,h2=v2")
    public String headers = "";

    @Parameter(description = "Expected response code")
    public String expectedStatus = "";

    @Parameter(description = "Jayway JsonPath (e.g. '$.key', see docs for more info)")
    public String jsonPath = "";

    @Parameter(description = "Server response body or value found using jsonPath specified", direction = ParameterDirection.OUTPUT)
    public String response = "";

    @Parameter(description = "Server response status code", direction = ParameterDirection.OUTPUT)
    public int status = 0;

    @Parameter(description = "Ignore untrusted SSL certificate (true/false)")
    public boolean ignoreUntrustedCertificate;

    protected ExecutionResult baseExecute(AddonHelper helper, RequestMethod requestMethod, String body, String bodyFormat) throws FailureException {

        // Validate input fields. In case that one of the fields is invalid, throw FailureException
        InputValidator.validateInputsFields(uri, query, headers, expectedStatus);

        // Create a request helper, with the desired settings
        RequestHelper requestHelper = new RequestHelper(requestMethod, uri, query, headers, body, bodyFormat, jsonPath, ignoreUntrustedCertificate);

        // Send the request and receive a response
        ServerResponse serverResponse = requestHelper.sendRequest();
        status = serverResponse.responseCode;

        // If user provided jsonPath parameter then set response to be the value  found by evaluating jsonPath
        response = (jsonPath.isEmpty()) ? serverResponse.responseBody : serverResponse.jsonParseResult;

        // Examine the result of the action and report it
        return ReporterHelper.reportResult(helper.getReporter(), serverResponse, expectedStatus, jsonPath);
    }
}
