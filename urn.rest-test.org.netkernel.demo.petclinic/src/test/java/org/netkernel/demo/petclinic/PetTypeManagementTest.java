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
public class PetTypeManagementTest extends RestApiBase {

    @Test
    public void shouldFindPetTypeById() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/pettypes/{petTypeId}", 1)
                .then()
                .log().all()
                .statusCode(200)
                .and().body("name", equalTo("cat"));
    }

    @Test
    public void shouldFindAllPetTypes() {
        rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("size", equalTo(6))
                .and().body("find {it.id == 1}.name", equalTo("cat"))
                .and().body("find {it.id == 3}.name", equalTo("lizard"));
    }

    @Test
    public void shouldInsertPetType() {
        //TODO Request representation does not require id. This design is asymmetric as /pets and /owners require id key.
        //More than that, when id is provided in request representation, same id is echoed back in response which is wrong.
        //Generated id should be returned in response representation.
        String owner = "{\n" +
                "    \"name\": \"tiger\"\n" +
                "}\n";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes");
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("id");
        }

        response = rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(owner)
                .post(" /api/pettypes")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        int newPetTypeId = response.body().jsonPath().getInt("id");
        assertThat(newPetTypeId, not(is(0)));

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes");
        List<String> foundAfter = response.body().jsonPath().getList("id");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/pettypes/{petTypeId}", newPetTypeId);
    }

    @Test
    public void shouldUpdatePetType() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes/{petTypeId}", 2);
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
                .put(" /api/pettypes/{petTypeId}", 2)
                .then()
                .log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes/{petTypeId}", 2);
        name = response.body().jsonPath().get("name");

        assertThat(name, equalTo(newName));
    }

    @Ignore
    @Test
    public void shouldDeletePetType() {
        rest().filter(validationFilter)
                .log().all()
                .delete(" /api/pettypes/{petTypeId}", 3)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get(" /api/pettypes/{petId}", 3)
                .then()
                .log().all()
                .statusCode(404);
    }

}
