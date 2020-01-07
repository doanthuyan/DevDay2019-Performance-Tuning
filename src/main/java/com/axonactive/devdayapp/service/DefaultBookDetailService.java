package com.axonactive.devdayapp.service;

import com.axonactive.devdayapp.Constants;
import com.axonactive.devdayapp.domain.BookDetail;
import com.axonactive.devdayapp.domain.Comment;
import com.axonactive.devdayapp.domain.Rating;
import com.axonactive.devdayapp.domain.User;
import com.axonactive.devdayapp.dto.CommentDto;
import com.axonactive.devdayapp.repo.BookDetailRepository;
import com.axonactive.devdayapp.repo.CommentRepository;
import com.axonactive.devdayapp.repo.RatingRepository;
import com.axonactive.devdayapp.repo.UserRepository;
import com.axonactive.devdayapp.util.CommentUtil;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class DefaultBookDetailService implements BookDetailService {
	private static final Logger log = LogManager.getLogger(DefaultBookDetailService.class);
	@Autowired
	private BookDetailRepository bookDetailRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RatingRepository ratingRepo;

	@Autowired
	private CommentRepository commentRepository;

	@Override
	public boolean rateABook(Long userId, Long bookDetailId, Integer point) {
		long startTime = System.currentTimeMillis();
		if (point == null || userId == null || bookDetailId == null) {
			return false;
		}
		if (point < 1 || point > 5) {
			return false;
		}
		Rating oldRating = ratingRepo.findRatingByUserIdAndBookDetailId(userId, bookDetailId);
		if (oldRating != null) {
			oldRating.setValue(point);
			ratingRepo.save(oldRating);
			return true;
		}
		Optional<User> user = userRepo.findById(userId);
		if (!user.isPresent()) {
			return false;
		}
		Optional<BookDetail> book = bookDetailRepo.findById(bookDetailId);
		if (!book.isPresent()) {
			return false;
		}
		Rating rating = new Rating();
		rating.setUser(user.get());
		rating.setBookDetail(book.get());
		rating.setValue(point);
		ratingRepo.save(rating);
		log.info(Constants.INFO_LOG_MSG, getClass().getName(), "rateABook", System.currentTimeMillis() - startTime,
				String.format("userId=%s, bookDetailId=%s, point=%s", userId, bookDetailId, point));
		return true;
	}

	@Override
	public boolean unrateABook(Long userId, Long bookDetailId) {
		long startTime = System.currentTimeMillis();
		Rating oldRating = ratingRepo.findRatingByUserIdAndBookDetailId(userId, bookDetailId);
		if (oldRating == null) {
			return false;
		}
		ratingRepo.delete(oldRating);
		log.info(Constants.INFO_LOG_MSG, getClass().getName(), "rateABook", System.currentTimeMillis() - startTime,
				String.format("userId=%s, bookDetailId=%s", userId, bookDetailId));
		return true;
	}

	@Override
	public CommentDto addComment(Long userId, Long bookDetailId, Long parentCommentId, String comment) {
		long startTime = System.currentTimeMillis();
		if (StringUtils.isEmpty(comment) || userId == null || bookDetailId == null) {
			return null;
		}
		Optional<User> user = userRepo.findById(userId);
		if (!user.isPresent()) {
			return null;
		}
		Optional<BookDetail> book = bookDetailRepo.findById(bookDetailId);
		if (!book.isPresent()) {
			return null;
		}
		Optional<Comment> parentComment = Optional.empty();
		if (parentCommentId != null) {
			parentComment = commentRepository.findById(parentCommentId);
		}
		Comment newComment = new Comment();
		newComment.setUser(user.get());
		newComment.setBookDetail(book.get());
		newComment.setContent(comment);
		if (parentComment.isPresent()) {
		
			newComment.setParent(parentComment.get());
		}
		commentRepository.save(newComment);
		log.info(Constants.INFO_LOG_MSG, getClass().getName(), "commentABook", System.currentTimeMillis() - startTime,
				String.format("userId=%s, bookDetailId=%s, comment=%s", userId, bookDetailId, comment));
		return CommentUtil.toCommentDto(newComment);
	}

	@Override
	public boolean removeComment(Long commentId) {
		long startTime = System.currentTimeMillis();
		Optional<Comment> oldComment = commentRepository.findById(commentId);
		if (!oldComment.isPresent()) {
			return true;
		}
		Comment comment = oldComment.get();
		if (comment.getParent() == null)
		{
			List<Comment> findChildComments = commentRepository.findChildComments(commentId);
			if (!CollectionUtils.isEmpty(findChildComments))
			{
				for (Comment childComment : findChildComments)
				{
					commentRepository.delete(childComment);
				}
			}
		}

		commentRepository.delete(comment);
		log.info(Constants.INFO_LOG_MSG, getClass().getName(), "uncomment", System.currentTimeMillis() - startTime,
				String.format("commentId=%s", commentId));
		return true;
	}

	@Override
	public CommentDto editComment(Long commentId, String comment) {
		Optional<Comment> oldComment = commentRepository.findById(commentId);
		if (!oldComment.isPresent()) {
			return null;
		}
		Comment updateComment = oldComment.get();
		updateComment.setContent(comment);
		Comment save = commentRepository.save(updateComment);
		return CommentUtil.toCommentDto(save);
	}

}
