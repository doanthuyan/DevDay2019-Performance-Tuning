Feature: Book functionalities

	Scenario: Get all books
		Given the book service is ready 
		When I get all book
		Then the result should contain some books
		
	Scenario: Get existed single book
		Given the book service is ready 
		When I get book with id 1
		Then the result should contain only 1 books
		
	Scenario: Get non-existed single book
		Given the book service is ready 
		When I get book with id 100
		Then the result has no book