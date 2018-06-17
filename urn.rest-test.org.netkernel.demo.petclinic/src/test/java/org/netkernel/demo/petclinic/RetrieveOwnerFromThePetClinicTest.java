package org.netkernel.demo.petclinic;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netkernel.demo.petclinic.model.Owner;
import org.netkernel.demo.petclinic.steps.PetClinicSteps;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class RetrieveOwnerFromThePetClinicTest extends RestApiBase {
    @Steps
    PetClinicSteps petClinic;

    @Test
    public void shouldBeAbleToRetrieveOwnersFromAPetClinic() {
        Owner owner = new Owner(10);
        petClinic.when_i_retrieve_the_owner_from_the_clinic(owner);
    }

    @Test
    public void shouldFindSingleOwnerWithPet() {
        rest().get("/api/owners/{ownerId}", 3)
                .then().statusCode(200)
                .and().body("firstName", equalTo("Carlos"))
                .and().body("lastName", equalTo("Estaban"));
    }

    @Test
    public void validateOwnerSchema() {
        rest().get("/api/owners/10")
                .then().assertThat().body(matchesJsonSchemaInClasspath("schema/owner-schema.json"));
    }
}
