package org.netkernel.demo.petclinic;

import com.atlassian.oai.validator.restassured.SwaggerValidationFilter;
import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class RestApiBase {
    protected static final String SCHEMA_ROOT_PATH = "C:/data/nk-workspace/netkernel-petclinic";
    protected static final String SWAGGER_JSON_URL = "file:///" + SCHEMA_ROOT_PATH + "/urn.rest-test.org.netkernel.demo.petclinic/src/test/resources/schema/model-schema.json";
    protected final SwaggerValidationFilter validationFilter = new SwaggerValidationFilter(SWAGGER_JSON_URL);

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9966;
        RestAssured.basePath = "/petclinic";
    }
}
