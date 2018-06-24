package org.netkernel.demo.petclinic;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.WithTag;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netkernel.demo.petclinic.model.Owner;
import org.netkernel.demo.petclinic.steps.PetClinicSteps;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class OwnerManagementTest extends RestApiBase {

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

    @Test
    public void shouldFindOwnersByLastName() {
        rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/*/lastname/{lastName}", "Rodriquez")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("id", hasItem(3))
                .and().body("size", equalTo(1));

        rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/*/lastname/{lastName}", "Ronaldo")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    public void shouldFindAllOwners() {
        rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("id", hasItem(3))
                .and().body("size", equalTo(10))
                .and().body("firstName", hasItems("Jeff", "George", "Maria", "Jean"));
    }

    @Ignore
    @Test
    public void shouldDeleteOwner() {
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/owners/{ownerId}", 6)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/{ownerId}", 6)
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    public void shouldInsertOwner() {
        String owner = "{\n" +
                "  \"id\": 0,\n" +
                "  \"firstName\": \"Cristiano\",\n" +
                "  \"lastName\": \"Ronaldo\",\n" +
                "  \"address\": \"2333 Independence La.\",\n" +
                "  \"city\": \"Madrid\",\n" +
                "  \"telephone\": \"6085555488\"\n" +
                "}";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/*/lastname/{lastName}", "Ronaldo");
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("firstName");
        }

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(owner)
                .post(" /api/owners")
                .then()
                .log().all()
                .statusCode(201);

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/*/lastname/{lastName}", "Ronaldo");
        List<String> foundAfter = response.body().jsonPath().getList("firstName");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/owners/{ownerId}", response.body().jsonPath().getList("id").get(0));
    }

    @Test
    @WithTag("run:this")
    public void shouldUpdateOwner() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/{ownerId}", 1);
        String firstName = response.body().jsonPath().get("firstName");
        String lastName = response.body().jsonPath().get("lastName");
        String address = response.body().jsonPath().get("address");
        String city = response.body().jsonPath().get("city");
        String telephone = response.body().jsonPath().get("telephone");

        String newLastName = lastName + "Z";
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("id", 1);
        jsonAsMap.put("firstName", firstName);
        jsonAsMap.put("lastName", newLastName);
        jsonAsMap.put("address", address);
        jsonAsMap.put("city", city);
        jsonAsMap.put("telephone", telephone);

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(jsonAsMap)
                .put(" /api/owners/{ownerId}", 1)
                .then()
                //.log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/owners/{ownerId}", 1);
        lastName = response.body().jsonPath().get("lastName");

        assertThat(lastName, equalTo(newLastName));
    }

}
