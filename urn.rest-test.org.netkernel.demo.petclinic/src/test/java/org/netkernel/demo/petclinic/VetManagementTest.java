package org.netkernel.demo.petclinic;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(SerenityRunner.class)
public class VetManagementTest extends RestApiBase {

    @Test
    public void shouldFindVets() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/vets")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("id.size", equalTo(6))
                .and().body("find {it.id == 3}.lastName", equalTo("Douglas"))
                .and().body("find {it.id == 3}.specialties.size", is(2))
                .and().body("find {it.id == 3}.specialties[0].name", equalTo("dentistry"))
                .and().body("find {it.id == 3}.specialties[1].name", equalTo("surgery"));
    }

    @Test
    public void shouldFindVetById() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/vets/{vetId}", 4)
                .then()
                .log().all()
                .statusCode(200)
                .and().body("id", equalTo(4))
                .and().body("firstName", equalTo("Rafael"))
                .and().body("lastName", equalTo("Ortega"))
                .and().body("specialties.size", is(1))
                .and().body("specialties[0].id", is(2))
                .and().body("specialties[0].name", equalTo("surgery"));
    }

    @Test
    public void shouldInsertVet() {
        //TODO Request representation must not specify id.
        //If specified, if there is existing record with same id then that record would be replaced.
        //Same id as in request is echoed back in response.
        //New id should be generated and returned in response representation.
        String owner = "  {\n" +
                "    \"firstName\": \"Ionel\",\n" +
                "    \"lastName\": \"Dima\",\n" +
                "    \"specialties\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"radiology\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/vets");
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("id");
        }

        response = rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(owner)
                .post(" /api/vets")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        int newVetId = response.body().jsonPath().getInt("id");
        assertThat(newVetId, not(Matchers.is(0)));

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/vets");
        List<String> foundAfter = response.body().jsonPath().getList("id");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/vets/{vetId}", newVetId);
    }

    @Test
    public void shouldUpdateVet() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/vets/{vetId}", 5);
        String firstName = response.body().jsonPath().get("firstName");
        String lastName = response.body().jsonPath().get("lastName");
        List<Map<String, String>> specialties = response.body().jsonPath().getList("specialties");

        String newLastName = lastName + "X";
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("id", 5);
        jsonAsMap.put("firstName", firstName);
        jsonAsMap.put("lastName", newLastName);
        jsonAsMap.put("specialties", specialties);

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(jsonAsMap)
                .put(" /api/vets/{vetId}", 5)
                .then()
                //.log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/vets/{vetId}", 5);
        lastName = response.body().jsonPath().get("lastName");
        specialties = response.body().jsonPath().getList("specialties");

        assertThat(specialties.get(0).get("name"), equalTo("radiology"));
        assertThat(lastName, equalTo(newLastName));
    }

    @Ignore
    @Test
    public void shouldDeleteVet() {
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/vets/{vetId}", 2)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get(" /api/vets/{vetId}", 2)
                .then()
                .log().all()
                .statusCode(404);
    }

}
