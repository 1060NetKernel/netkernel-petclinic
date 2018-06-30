package org.netkernel.demo.petclinic;

import com.atlassian.oai.validator.restassured.SwaggerValidationFilter;
import io.restassured.RestAssured;
import org.junit.BeforeClass;

import static net.serenitybdd.rest.SerenityRest.rest;

public class RestApiBase {
    protected static final String SCHEMA_ROOT_PATH = "C:/data/nk-workspace/netkernel-petclinic";
    protected static final String SWAGGER_JSON_URL = "file:///" + SCHEMA_ROOT_PATH + "/urn.rest-test.org.netkernel.demo.petclinic/src/test/resources/schema/model-schema.json";
    private static int SPRING_REST_API_PORT = 9966;
    private static int NK_REST_API_PORT = 8080;
    protected final SwaggerValidationFilter validationFilter = new SwaggerValidationFilter(SWAGGER_JSON_URL);

    @BeforeClass
    public static void setUp() {
        resetDatabase();

        RestAssured.baseURI = "http://localhost";
        //NOTE: All REST API tests have been developed and validated against existing Spring REST API impl.
        //RestAssured.port = SPRING_REST_API_PORT;
        //NOTE: While implementing the REST API with NK we target tests to NK REST API endpoint. NK impl will pass the
        //existing REST API characterizing tests one test at a time. Once NK impl passes all the tests then we are done.
        RestAssured.port = NK_REST_API_PORT;
        RestAssured.basePath = "/petclinic";
    }

    private static void resetDatabase() {
        rest().get("http://localhost:8080/petclinic/reset-db");
    }
}
