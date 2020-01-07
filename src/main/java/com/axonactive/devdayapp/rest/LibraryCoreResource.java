package com.axonactive.devdayapp.rest;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.axonactive.devdayapp.dto.BookDto;
import com.axonactive.devdayapp.dto.CommentDto;
import com.axonactive.devdayapp.dto.RequestComment;
import com.axonactive.devdayapp.dto.SearchingCriteria;
import com.axonactive.devdayapp.service.BookDetailService;
import com.axonactive.devdayapp.service.BookService;
import com.axonactive.devdayapp.service.CommentService;
import com.axonactive.devdayapp.service.SearchingService;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/library-core/api")
//Add a Prometheus metrics enpoint to the route `/prometheus`. `/metrics` is already taken by Actuator.
//@EnablePrometheusEndpoint
//Pull all metrics from Actuator and expose them as Prometheus metrics. Need to disable security feature in properties file.
//@EnableSpringBootMetricsCollector
public class LibraryCoreResource {

	private static final Logger log = LogManager.getLogger(LibraryCoreResource.class);
	// Define a counter metric for /prometheus
		static final Counter requests = Counter.build().name("requests_total").help("Total number of requests.").register();
		// Define a histogram metric for /prometheus
		static final Histogram requestLatency = Histogram.build().name("requests_latency_seconds")
				.help("Request latency in seconds.").register();
	@Autowired
	private BookService bookService;

	@Autowired
	private SearchingService searchingService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private BookDetailService bookDetailService;

	@GetMapping("/books")
	public List<BookDto> getBooks() {
		log.info("Get all books.");
		return bookService.getAll();
	}

	@GetMapping("/books/{bookId}")
	public BookDto getBookById(@PathVariable("bookId") Long bookId) {
		log.info(String.format("Finding book with bookId=%s", bookId));
		return bookService.findById(bookId);

	}

	@GetMapping("/books/{bookId}/comments")
	public List<CommentDto> getCommentByBookId(@PathVariable("bookId") Long bookId) {
		log.info(String.format("Searching for comments of bookId=%s", bookId));
		return commentService.getCommentByBookId(bookId);
	}

	@PostMapping("/books/search")
	public List<BookDto> searchBook(@RequestBody SearchingCriteria criteria) {
		log.info(String.format("Searching for books with criteria '%s'", criteria));
		return searchingService.search(criteria);
	}

	@PostMapping("/book-details/{bookDetailId}/ratings")
	public boolean rateABook(@RequestHeader(value = "Authorization") Long userId,
			@PathVariable("bookDetailId") Long bookDetailId, @RequestBody Integer point) {
		log.info("User {} do rate {} star for the book detail with id {}", userId, point, bookDetailId);
		return bookDetailService.rateABook(userId, bookDetailId, point);
	}

	@DeleteMapping("/book-details/{bookDetailId}/ratings")
	public boolean unrateABook(@RequestHeader(value = "Authorization") Long userId,
			@PathVariable("bookDetailId") Long bookDetailId) {
		log.info("User {} do unrate the book detail with id {}", userId, bookDetailId);
		return bookDetailService.unrateABook(userId, bookDetailId);
	}

	@PostMapping("/book-details/{bookDetailId}/comment")
	public CommentDto addComment(@RequestHeader(value = "Authorization") Long userId,
			@PathVariable("bookDetailId") Long bookDetailId, @RequestBody RequestComment comment)
			throws ParseException {
		log.info("User {} comments {} for the book detail with id {}", userId, comment, bookDetailId);
		return bookDetailService.addComment(userId, bookDetailId, comment.getParentId(), comment.getComment());
	}

	@PostMapping("/book-details/comment/{commentId}")
	public CommentDto editComment(@RequestHeader(value = "Authorization") Long userId,
			@PathVariable("commentId") Long commentId, @RequestBody String comment) {
		log.info("User {} comments {} for the comment_id {}", userId, comment, commentId);
		return bookDetailService.editComment(commentId, comment);
	}

	@DeleteMapping("/book-details/comment/{commentId}")
	public boolean uncommentABook(@RequestHeader(value = "Authorization") Long userId,
			@PathVariable("commentId") Long commentId) {
		log.info("User {} removes comment the book detail with comment_id {}", userId, commentId);
		return bookDetailService.removeComment(commentId);
	}

	

	@GetMapping("/")
	public String home() {
		// Increase the counter metric
		requests.inc();
		// Start the histogram timer
		Histogram.Timer requestTimer = requestLatency.startTimer();
		try {
			return "Hello World!";
		} finally {
			// Stop the histogram timer
			requestTimer.observeDuration();
		}
	}
}
