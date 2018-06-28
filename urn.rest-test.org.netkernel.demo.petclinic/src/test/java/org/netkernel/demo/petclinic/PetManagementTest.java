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

    @Test
    public void shouldFindAllPets() {
        rest().filter(validationFilter)
                .log().all()
                .get("/api/pets")
                .then()
                .log().all()
                .statusCode(200)
                .and().body("size", equalTo(13))
                .and().body("name", hasItems("Leo", "Basil", "Rosy", "Jewel", "Iggy", "George", "Samantha", "Max", "Lucky", "Mulligan", "Lucky", "Sly"));
    }

    @Test
    public void shouldInsertPetIntoDatabaseAndGenerateId() {
        String pet = "{\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"Riff\",\n" +
                "    \"birthDate\": \"1990/01/05\",\n" +
                "    \"type\": {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"dog\"\n" +
                "    },\n" +
                "    \"owner\": {\n" +
                "        \"id\": 6,\n" +
                "        \"firstName\": \"Jean\",\n" +
                "        \"lastName\": \"Coleman\",\n" +
                "        \"address\": \"105 N. Lake St.\",\n" +
                "        \"city\": \"Monona\",\n" +
                "        \"telephone\": \"6085552654\"\n" +
                "    }\n" +
                "},\n";

        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/owners/{ownerId}", 6);
        List<String> foundBefore = Collections.emptyList();
        if (!response.body().asString().isEmpty()) {
            foundBefore = response.body().jsonPath().getList("pets.id");
        }

        response = rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(pet)
                .post("/api/pets")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        int newPetId = response.body().jsonPath().getInt("id");

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/owners/{ownerId}", 6);
        List<String> foundAfter = response.body().jsonPath().getList("pets.id");

        assertThat(foundAfter.size(), equalTo(foundBefore.size() + 1));

        //teardown
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/pets/{newPetId}", newPetId);
    }

    @Test
    public void shouldUpdatePetName() {
        Response response = rest().filter(validationFilter)
                .log().all()
                .get("/api/pets/{petId}", 11);
        String name = response.body().jsonPath().get("name");
        String birthDate = response.body().jsonPath().get("birthDate");

        int typeId = response.body().jsonPath().getInt("type.id");
        String typeName = response.body().jsonPath().get("type.name");

        int ownerId = response.body().jsonPath().getInt("owner.id");
        String firstName = response.body().jsonPath().get("owner.firstName");
        String lastName = response.body().jsonPath().get("owner.lastName");
        String address = response.body().jsonPath().get("owner.address");
        String city = response.body().jsonPath().get("owner.city");
        String telephone = response.body().jsonPath().get("owner.telephone");

        String newName = name + "X";
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("id", 11);
        jsonAsMap.put("name", newName);
        jsonAsMap.put("birthDate", birthDate);

        Map<String, Object> petTypeJonAsMap = new HashMap<>();
        petTypeJonAsMap.put("id", typeId);
        petTypeJonAsMap.put("name", typeName);
        jsonAsMap.put("type", petTypeJonAsMap);

        Map<String, Object> ownerJonAsMap = new HashMap<>();
        ownerJonAsMap.put("id", ownerId);
        ownerJonAsMap.put("firstName", firstName);
        ownerJonAsMap.put("lastName", lastName);
        ownerJonAsMap.put("address", address);
        ownerJonAsMap.put("city", city);
        ownerJonAsMap.put("telephone", telephone);
        jsonAsMap.put("owner", ownerJonAsMap);

        rest().filter(validationFilter)
                .log().all()
                .given()
                .contentType("application/json")
                .body(jsonAsMap)
                .put("/api/pets/{petId}", 11)
                .then()
                //.log().all()
                .statusCode(204);

        response = rest().filter(validationFilter)
                .log().all()
                .get("/api/pets/{petId}", 11);
        name = response.body().jsonPath().get("name");

        assertThat(name, equalTo(newName));
    }

    @Ignore
    @Test
    public void shouldDeletePet() {
        rest().filter(validationFilter)
                .log().all()
                .delete("/api/pets/{petId}", 13)
                .then()
                .log().all()
                .statusCode(204);

        rest().filter(validationFilter)
                .log().all()
                .get("/api/pets/{petId}", 13)
                .then()
                .log().all()
                .statusCode(404);
    }

}
