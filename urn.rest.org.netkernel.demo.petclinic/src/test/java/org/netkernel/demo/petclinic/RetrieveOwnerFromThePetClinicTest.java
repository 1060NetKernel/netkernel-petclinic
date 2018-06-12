package org.netkernel.demo.petclinic;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netkernel.demo.petclinic.model.Owner;
import org.netkernel.demo.petclinic.steps.PetClinicSteps;

@RunWith(SerenityRunner.class)
public class RetrieveOwnerFromThePetClinicTest {
    @Steps
    PetClinicSteps petClinic;

    @Test
    public void shouldBeAbleToRetrieveOwnersFromAPetClinic() {
        Owner owner = new Owner(10);
        petClinic.when_i_retrieve_the_owner_from_the_clinic(owner);
    }
}
