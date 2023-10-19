package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class callBackStepDef {

    private String apiEndpoint;
    private Response[] responses = new Response[500]; // Array to store responses
    private int callback200Count = 0; // Count of 200 responses in callbacks
    private String callbackUrl = "http://localhost:8080/callback"; // Callback URL

    @Given("^the API endpoint is \"(.*)\"$")
    public void setApiEndpoint(String endpoint) {
        apiEndpoint = endpoint;
    }

    @Given("the request headers are set")
    public void setRequestHeaders() {
        RestAssured.given()
                .header("X-CorrelationID", "123456789")
                .header("accountHoldingInstitutionId", "gorilla")
                .header("amsName", "mifos")
                .header("Platform-TenantId", "gorilla")
                .header("Content-Type", "application/json");
    }

    @Given("the request body is set")
    public void setRequestBody() {
        // Define the request body as a JSON object
        RestAssured.given()
                .body("{" +
                        "\"requestingOrganisationTransactionReference\":\"string\"," +
                        "\"subType\":\"inbound\"," +
                        "\"type\":\"transfer\"," +
                        "\"amount\":\"11\"," +
                        "\"currency\":\"USD\"," +
                        "\"descriptionText\":\"string\"," +
                        "\"requestDate\":\"2022-09-28T12:51:19.260+00:00\"," +
                        "\"customData\":[{\"key\":\"string\",\"value\":\"string\"}]," +
                        "\"payer\":[{\"partyIdType\":\"MSISDN\",\"partyIdIdentifier\":\"+44999911\"}]," +
                        "\"payee\":[{\"partyIdType\":\"accountId\",\"partyIdIdentifier\":\"000000001\"}]" +
                        "}");
    }

    @When("I send the POST request (\\d+) times")
    public void sendPostRequestMultipleTimes(int requestCount) {
        for (int i = 0; i < requestCount; i++) {
            responses[i] = RestAssured.when().post(apiEndpoint);
        }
    }

    @Then("the response status code should be (\\d+) for all requests")
    public void verifyStatusCodes(int expectedStatusCode) {
        for (Response response : responses) {
            response.then().statusCode(expectedStatusCode);
        }
    }

    @Then("^(\\d+) callbacks return a (\\d+) response")
    public void verifyCallbackResponses(int expectedCallbackCount, int expectedResponseCode) {
        // You can count the number of callbacks with the expected response code
        // and verify if it matches the expected count
        for (Response response : responses) {
            // Implement code to listen for and verify callbacks
            // Ensure that the response code matches expectedResponseCode
            if (response.getStatusCode() == expectedResponseCode) {
                callback200Count++;
            }
        }
        if (callback200Count != expectedCallbackCount) {
            throw new AssertionError("Expected " + expectedCallbackCount + " callbacks with a response code of " + expectedResponseCode + ", but received " + callback200Count);
        }
    }
}
