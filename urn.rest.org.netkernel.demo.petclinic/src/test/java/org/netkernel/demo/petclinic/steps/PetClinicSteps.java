package org.netkernel.demo.petclinic.steps;

import net.thucydides.core.annotations.Step;
import org.netkernel.demo.petclinic.model.Owner;

import static net.serenitybdd.rest.SerenityRest.rest;

public class PetClinicSteps {
    private Owner owner;

    @Step
    public void when_i_retrieve_the_owner_from_the_clinic(Owner owner) {
        this.owner = owner;

        rest().given().contentType("application/json")
                .get("http://localhost:8080/petclinic/api/owners/10")
                .then().statusCode(200);
    }
}
