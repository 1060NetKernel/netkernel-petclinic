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
import java.util.stream.Collectors;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class VisitManagementTest extends RestApiBase {

    @Test
    public void shouldFindVisitById() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/visits/{visitId}", 1)
                .then()
                .log().all()
                .statusCode(200)
                .and().body("description", equalTo("rabies shot"))
                .and().body("pet.name", equalTo("Samantha"))
                .and().body("pet.id", is(7))
                .and().body("pet.owner.id", is(6))
                .and().body("pet.owner.lastName", equalTo("Coleman"));
    }

    @Test
    public void shouldFindAllVisits() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/visits")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("size", is(4))
                .and().body("find {it.id == 1}.description", equalTo("rabies shot"))
                .and().body("find {it.id == 3}.description", equalTo("neutered"))
                .and().body("find {it.id == 3}.pet.id", is(8))
                .and().body("find {it.id == 3}.pet.type.name", equalTo("cat"));
    }


    @Test
    public void shouldFindVisitsByPetId() {
        int petId = 8;
        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/visits")
                .then()
                .log().all()
                .extract().response();

        List<Integer> allVisits = response.body().jsonPath().getList("pet.id");
        assertThat("all visits", allVisits.size(), is(4));

        List<Integer> visitsForPet = allVisits.stream().filter(pet -> pet == petId).collect(Collectors.toList());
        assertThat("visits for pet", visitsForPet.size(), is(2));
    }

    @Test
    public void shouldInsertVisit() {
        String visit = "{\n" +
                "    \"id\": 0,\n" +
                "    \"date\": \"2018/09/04\",\n" +
                "    \"description\": \"too quiet\",\n" +
                "    \"pet\": {\n" +
                "        \"id\": 7,\n" +
                "        \"name\": \"Samantha\",\n" +
                "        \"birthDate\": \"1995/09/04\",\n" +
                "        \"type\": {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"cat\"\n" +
                "        },\n" +
                "        \"owner\": {\n" +
                "            \"id\": 6,\n" +
                "            \"firstName\": \"Jean\",\n" +
                "            \"lastName\": \"Coleman\",\n" +
                "            \"address\": \"105 N. Lake St.\",\n" +
                "            \"city\": \"Monona\",\n" +
                "            \"telephone\": \"6085552654\"\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/visits");
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("id");
        }

        response = rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(visit)
                .post("/api/visits")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        int newVisitId = response.body().jsonPath().getInt("id");
        assertThat(newVisitId, not(is(0)));

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/visits");
        List<String> foundAfter = response.body().jsonPath().getList("id");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/visits/{visitId}", newVisitId);
    }

    @Test
    public void shouldUpdateVisit() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/visits/{visitId}", 2);
        String description = response.body().jsonPath().get("description");
        String date = response.body().jsonPath().get("date");

        Map<String, String> pet = response.jsonPath().getMap("pet");

        String newDescription = description + "X";
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("id", 2);
        jsonAsMap.put("description", newDescription);
        jsonAsMap.put("date", date);
        jsonAsMap.put("pet", pet);

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(jsonAsMap)
                .put("/api/visits/{visitId}", 2)
                .then()
                //.log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/visits/{visitId}", 2);
        description = response.body().jsonPath().get("description");

        assertThat(description, equalTo(newDescription));
    }

    @Ignore
    @Test
    public void shouldDeleteVisit() {
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/visits/{visitId}", 2)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get("/api/visits/{visitId}", 2)
                .then()
                .log().all()
                .statusCode(404);
    }

}
