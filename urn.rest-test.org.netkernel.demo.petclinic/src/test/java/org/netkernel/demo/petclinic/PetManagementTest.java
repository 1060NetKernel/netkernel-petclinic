package org.netkernel.demo.petclinic;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class PetManagementTest extends RestApiBase {

    @Test
    public void shouldFindPetWithCorrectId() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/pets/{petId}", 4)
                .then()
                //.log().all()
                .statusCode(200)
                .and().body("id", equalTo(4))
                .and().body("name", equalTo("Jewel"))
                .and().body("type.name", equalTo("dog"))
                .and().body("owner.id", equalTo(3));
    }

}
