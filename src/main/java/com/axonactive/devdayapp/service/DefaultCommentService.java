package com.axonactive.devdayapp.service;

import com.axonactive.devdayapp.Constants;
import com.axonactive.devdayapp.domain.Book;
import com.axonactive.devdayapp.dto.CommentDto;
import com.axonactive.devdayapp.repo.BookRepository;
import com.axonactive.devdayapp.util.BookDetailUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultCommentService implements CommentService {
    private static final Logger log = LogManager.getLogger(DefaultCommentService.class);
    
    @Autowired
    private BookRepository bookRepo;

	@Override
	public List<CommentDto> getCommentByBookId(long bookId) {
        long startTime = System.currentTimeMillis();
		Optional<Book> result = bookRepo.findById(bookId);
		List<CommentDto> comments = Collections.emptyList();
		if(result.isPresent()) {
			comments = StreamSupport.stream(result.get().getDetails().spliterator(),false)
				.flatMap(bookDetail -> Optional.ofNullable(BookDetailUtil.toBookDetailDto(bookDetail).getComments()).orElse( Collections.emptyList()).stream())
				.collect(Collectors.toList());
		}
		log.info(Constants.INFO_LOG_MSG, getClass().getName(),
                "getCommentByBookId", 
                System.currentTimeMillis() - startTime,
                String.format("bookId=%s", bookId));
        return comments;
	}
}

