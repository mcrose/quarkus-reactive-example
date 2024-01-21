package py.com.icarusdb.reactiveexample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
// @formatter:off
public class CountryResourceTest {

    private static List<Country> expectedCountries;

    @BeforeAll
    static void init() {
        expectedCountries = List.of(
                new Country(1L, "Paraguay", "py"),
                new Country(2L, "Brasil", "br"),
                new Country(3L, "Argentina", "ar"));

    }

    @Test
    @Order(1)
    public void testAllCountriesEndpoint() {

        List<Country> retrievedCountries = given()
                .when()
                    .get(CountriesResource.RESOURCE_URI + "/all")
                .then()
                    .statusCode(200)
                    .extract()
                    .body().jsonPath()
                        .getList(".", Country.class);

        assertEquals(expectedCountries, retrievedCountries);
    }

    @Test
    @Order(2)
    public void testByIdEndpoint() {
        given()
            .when()
                .get(CountriesResource.RESOURCE_URI + "/1")
            .then()
                .statusCode(200)
                .assertThat()
                    .body(containsString("name"))
                        .and()
                    .body(containsString("Paraguay"));
    }

    @Test
    @Order(3)
    public void testByNameEndpoint() {
        given()
            .when()
                .get(CountriesResource.RESOURCE_URI + "/name/Brasil")
            .then()
                .statusCode(200)
                .assertThat()
                    .body(containsString("Brasil"))
                        .and()
                    .body(containsString("br"));
    }
}
// @formatter:on
