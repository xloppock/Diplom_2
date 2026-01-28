package tests;

import org.junit.BeforeClass;
import static io.restassured.RestAssured.baseURI;

public class BaseTest {

    @BeforeClass
    public static void setUp() {
        baseURI = "https://stellarburgers.education-services.ru";
    }
}