package org.netkernel.demo.petclinic;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class RestApiBase {
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/petclinic";
    }
}
