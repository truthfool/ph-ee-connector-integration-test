Feature: Test API Endpoint

  Scenario: Send a POST request to the API 500 times and verify 200 responses in callbacks
    Given the API endpoint is "http://localhost:8080/channel/gsma/transaction"
    And the request headers are set
    And the request body is set
    When I send the POST request 500 times
    Then the response status code should be 200 for all requests
    And 500 callbacks return a 200 response
