package org.netkernel.demo.petclinic;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.BeforeClass;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

public class RestApiBase {
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/petclinic";

        JsonSchemaValidator.settings = settings().with().jsonSchemaFactory(
                JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze()).
                and().with().checkedValidation(true);
    }
}
