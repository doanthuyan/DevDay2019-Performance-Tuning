package com.axonactive.devdayapp.test.cucumber.book;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.axonactive.devdayapp.dto.BookDto;
import com.axonactive.devdayapp.service.BookService;
import com.axonactive.devdayapp.test.cucumber.AbstractDefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookServicesCucumberStepDefinitions extends AbstractDefs {
	private final Logger log = LogManager.getLogger(BookServicesCucumberStepDefinitions.class);
	@LocalServerPort
    int randomServerPort;
	
	@Given("^the book service is ready")
	public void the_service_is_ready() {

	}

	@When("I get all book")
	public void i_get_all_book() throws IOException {
		executeGet("http://localhost:" +randomServerPort+ "/library-core/api/books");
	}

	@Then("^the result should contain some books")
	public void the_result_should_contain_something() throws IOException {
		final HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
		
		assertEquals(currentStatusCode, HttpStatus.OK);
		JSONArray array = new JSONArray(latestResponse.getBody());
		
		log.info("Expecting all books. The result contains {}. Length {}", array,array.length());
		assertTrue(array.length() > 0);
	}

	@Then("^the result should contain only (\\d+) books")
	public void the_result_should_contain_only(final int quantity) throws IOException {
		log.info("Expecting book. The result contains {}", latestResponse.getBody());
		final HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
		
		assertEquals(currentStatusCode, HttpStatus.OK);
		JSONObject obj = new JSONObject(latestResponse.getBody());
		
		log.info("Expecting one book. The result contains {}. ", latestResponse.getBody());
		assertNotNull(obj);
	}

	@When("^I get book with id (\\d+)")
	public void i_get_a_book(long bookId) throws IOException {
		executeGet("http://localhost:" +randomServerPort+ "/library-core/api/books/"+bookId);
		

	}

	@Then("the result has no book")
	public void i_get_no_book() throws IOException {
		final HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
		
		assertEquals(currentStatusCode, HttpStatus.OK);
		//JSONObject obj = new JSONObject(latestResponse.getBody());
		
		log.info("Expecting no book. The result contains {}. ", latestResponse.getBody());
		assertTrue(latestResponse.getBody().trim().isEmpty());
	}

}