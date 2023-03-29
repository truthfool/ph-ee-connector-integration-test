# ph-ee-connector-integration-test

![CUCUMBER](https://img.shields.io/badge/Cucumber-3DDC84?style=for-the-badge&logo=cucumber&logoColor=white)
<br>
Cucumber is a test writing framework which is used to achieve the idea of BDD(Behaviour Driven Development). To know more about BDD and why it is considered [read this article](https://www.tutorialspoint.com/behavior_driven_development/behavior_test_driven_development.htm).

## Three main components of cucumber
1. Ghrekin feature file
    Its is a human readable domain specific language, to deffine a behaviour.
2. Step definition 
    Its the actual definition or implementation of each of the steps/ behaviour deffined in the feature file.
3. Context configuration
    Integration test can be spefcific to spring applicaiton, camel specific or any other environment. So cucmber can be configured with different context within which each of the step definition will be executed.

## Dependency
Below are the required dependency to work in the spring and camel environment.
```gradle
implementation 'io.cucumber:cucumber-java:7.8.1'
implementation 'io.cucumber:cucumber-spring:7.8.1'
testImplementation 'io.cucumber:cucumber-junit:7.8.1'
testImplementation 'org.apache.camel:camel-test:3.4.0'
testImplementation 'org.springframework.boot:spring-boot-starter-test:2.5.4'
```

## 1. Writing a feature file
Refer the [official guide](https://cucumber.io/docs/gherkin/reference/) for any help wuth writing feature file. The extention of the feature file is `.feature`. Below is one of the sample feature file.
```gherkin
Feature: SLCB integration test
  Scenario: Test the payload of the SLCB
    Given I have a batchId: "123-123-123", requestId: "3af-567-dfr", purpose: "integration test"
    And I mock transactionList with two transactions each of "1" value
    And I can start camel context
    When I call the buildPayload route
    Then the exchange should have a variable with SLCB payload
    And I can parse SLCB payload to DTO
    And total transaction amount is 2
    And total transaction count is 2, failed is 0 and completed is 0
```

## 2. Adding step definition
The step definition can be created in a simple plain java class. Inside the java class you can use all the design patterns specific to the context it is run in. So for example you want to use `@Autowire` for any bean then make sure you are using the `SpringBootTest` context and that bean is present in that context. 
Each of the step definition need to match with the phrase mentioned in the feature file with proper annotation. A sample step definition for the `Given I have a batchId: "123-123-123", requestId: "3af-567-dfr", purpose: "integration test"` expression is added below. Where {string} is the placeholder for the variable. To find more about variables data type [refer this](https://cucumber.io/docs/cucumber/step-definitions/?lang=java).
```java
@Given("I have a batchId: {string}, requestId: {string}, purpose: {string}")
public void i_have_required_data(String batchId, String requestId, String purpose){
    this.batchId = batchId;
    this.requestId = requestId;
    this.purpose = purpose;
}
```

## 3. Configuring context for cucumber tests
Cucumber can be run in any context. And configuring this part totally depends on the scenario which we are testing. For configuring the context for spring applicaiton use the below annotation on default spring test class or create a new one.
```java
@SpringBootTest
@CucumberContextConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = <Main class for spring applciation>, loader = SpringBootContextLoader.class)
```
The `@CucumberContextConfiguration` is responsible for making sure that all the stepDefinitions are executed in this particular environment.

---
Yay!! :boom: :boom:
<br>
Now we can run respective feature file directly form the intellij.
<br>
<img width="515" alt="Screenshot 2022-10-26 at 6 58 19 PM" src="https://user-images.githubusercontent.com/31315800/198042079-3964389a-df08-4c05-8951-52905c9fce04.png">

## Adding gradle configuration 
Adding gradle configuration will allow us to run all the cucumber feature file at using using a CLI.

```gradle
configurations {
    cucumberRuntime {
        extendsFrom testImplementation
    }
}

task cucumberCli() {
    dependsOn assemble, testClasses
    doLast {
        javaexec {
            main = "io.cucumber.core.cli.Main"
            classpath = configurations.cucumberRuntime + sourceSets.main.output + sourceSets.test.output
            args = [
                    '--plugin', 'pretty',
                    '--plugin', 'html:target/cucumber-report.html',
                    '--glue', 'org.mifos.connector.slcb.cucumber',
                    'src/test/java/resources']
        }
    }
}
```
Where the `--glue` arg is for defining the package which contains the step definitions. And the last arg is the path which contains all the feature file. After making this configuration run the command `./gradlew cucumberCli` in terminal to make sure the newly added tasks is working.

## FAQs
1. How to make step def reusable?
2. Order of execution of feature/steps?
3. Calling a scenario from another feature?
4. How cucumber picks feature file? How to configure the location of feature file?
