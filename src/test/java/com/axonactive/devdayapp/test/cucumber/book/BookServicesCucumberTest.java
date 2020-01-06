package com.axonactive.devdayapp.test.cucumber.book;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/single_book")
public class BookServicesCucumberTest {

}
