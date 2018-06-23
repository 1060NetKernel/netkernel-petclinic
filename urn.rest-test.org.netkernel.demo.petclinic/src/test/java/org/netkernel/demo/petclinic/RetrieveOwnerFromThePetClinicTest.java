package org.netkernel.demo.petclinic;

import com.atlassian.oai.validator.restassured.SwaggerValidationFilter;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netkernel.demo.petclinic.model.Owner;
import org.netkernel.demo.petclinic.steps.PetClinicSteps;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.Matchers.*;


@RunWith(SerenityRunner.class)
public class RetrieveOwnerFromThePetClinicTest extends RestApiBase {
    private static final String SCHEMA_ROOT_PATH = "C:/data/nk-workspace/netkernel-petclinic";
    private static final String SWAGGER_JSON_URL = "file:///" + SCHEMA_ROOT_PATH + "/urn.rest-test.org.netkernel.demo.petclinic/src/test/resources/schema/model-schema.json";
    private final SwaggerValidationFilter validationFilter = new SwaggerValidationFilter(SWAGGER_JSON_URL);

    @Steps
    PetClinicSteps petClinic;

    @Test
    public void shouldBeAbleToRetrieveOwnersFromAPetClinic() {
        Owner owner = new Owner(10);
        petClinic.when_i_retrieve_the_owner_from_the_clinic(owner);
    }

    @Test
    public void shouldFindSingleOwnerWithPet() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/owners/{ownerId}", 10)
                .then()
                //.log().all()
                .statusCode(200)
                .and().body("id", equalTo(10))
                .and().body("firstName", equalTo("Carlos"))
                .and().body("lastName", equalTo("Estaban"))
                .and().body("pets.size", equalTo(2))
                .and().body("pets.findAll {it.type.name == 'cat'}.name", hasSize(1))
                .and().body("pets.findAll {it.type.name == 'cat'}.name", hasItem("Sly"));
    }

}
