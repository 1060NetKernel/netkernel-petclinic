package org.netkernel.demo.petclinic;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class SpecialtyManagementTest extends RestApiBase {

    @Test
    public void shouldFindSpecialtyById() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties/{specialtyId}", 1)
                .then()
                .log().all()
                .statusCode(200)
                .and().body("name", equalTo("radiology"));
    }

    @Test
    public void shouldFindAllSpecialties() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("size", equalTo(3))
                .and().body("find {it.id == 1}.name", equalTo("radiology"))
                .and().body("find {it.id == 2}.name", containsString("surgery"))
                .and().body("find {it.id == 3}.name", equalTo("dentistry"));
    }

    @Test
    public void shouldInsertSpecialty() {
        //TODO Request representation does not require id. This design is asymmetric as /pets and /owners require id key.
        //More than that, when id is provided in request representation, existing record with same id is overwritten.
        //New id should be generated and returned in response representation.
        String specialty = "{\n" +
                "    \"name\": \"neurology\"\n" +
                "}\n";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties");
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("id");
        }

        response = rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(specialty)
                .post("/api/specialties")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        int newSpecialtyId = response.body().jsonPath().getInt("id");
        assertThat(newSpecialtyId, not(is(0)));

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties");
        List<String> foundAfter = response.body().jsonPath().getList("id");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/specialties/{specialtyId}", newSpecialtyId);
    }

    @Test
    public void shouldUpdateSpecialty() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties/{specialtyId}", 2);
        String name = response.body().jsonPath().get("name");

        String newName = name + "Y";
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("id", 2);
        jsonAsMap.put("name", newName);

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(jsonAsMap)
                .put("/api/specialties/{specialtyId}", 2)
                .then()
                .log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties/{specialtyId}", 2);
        name = response.body().jsonPath().get("name");

        assertThat(name, equalTo(newName));
    }

    @Ignore
    @Test
    public void shouldDeleteSpecialty() {
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/specialties/{specialtyId}", 3)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get("/api/specialties/{specialtyId}", 3)
                .then()
                .log().all()
                .statusCode(404);
    }

}
