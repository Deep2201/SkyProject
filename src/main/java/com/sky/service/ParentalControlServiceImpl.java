package com.sky.service;

import com.sky.exception.ParentalControlException;
import com.sky.thirdparty.MovieService;
import com.sky.thirdparty.TechnicalFailureException;
import com.sky.thirdparty.TitleNotFoundException;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Deepesh on 27/01/2019.
 *
 * The service accepts the customer's "parental control level" and *movie id* as input.
 * If the customer is able to watch the movie the *ParentalControlService* returns true to the
 * calling client.
 */
public class ParentalControlServiceImpl implements ParentalControlService {

	private final MovieService movieService;
	private final List<String> controlLevels;
	private final String systemError = "Customer can not watch this movie.";

	public ParentalControlServiceImpl(MovieService movieService, List<String> controlLevels) {
		this.movieService = movieService;
		this.controlLevels = controlLevels;
	}

	public boolean canWatchMovie(String customerParentalControlLevel, String movieId) throws ParentalControlException{
		try {
			final String movieParentalControlLevel = movieService.getParentalControlLevel(movieId);

			if(null == movieParentalControlLevel){
				throw new TechnicalFailureException(systemError);
			}
			return compareCutomerMovieControlLevel(customerParentalControlLevel, movieParentalControlLevel);
		} catch (TitleNotFoundException | TechnicalFailureException e) {
			throw new ParentalControlException(e.getMessage());
		}
	}

	private boolean compareCutomerMovieControlLevel(String customerParentalControlLevel, String movieParentalControlLevel) {
		Comparator<String> controlLevelComparator = (controlLevel1, controlLevel2) -> controlLevels.indexOf(controlLevel1) - controlLevels.indexOf(controlLevel2);
		return controlLevelComparator.compare(movieParentalControlLevel, customerParentalControlLevel) <= 0;
}
}
