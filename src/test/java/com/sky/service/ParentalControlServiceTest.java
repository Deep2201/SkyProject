package com.sky.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.sky.exception.ParentalControlException;
import com.sky.thirdparty.MovieService;
import com.sky.thirdparty.TechnicalFailureException;
import com.sky.thirdparty.TitleNotFoundException;

	@RunWith(MockitoJUnitRunner.class)
	public class ParentalControlServiceTest {

		@InjectMocks
		private ParentalControlServiceImpl parentalControlService;

		@Mock
		private MovieService movieService;

		private List<String> controlLevels = Arrays.asList("U", "PG", "12", "15", "18");

		@Before
		public void setUp() throws Exception {
			MockitoAnnotations.initMocks(this);
			parentalControlService = new ParentalControlServiceImpl(movieService, controlLevels);
		}

		@Test
		public void returnErrorMessageWhenMovieIdNotPresent() throws Exception {
			// Given
			String expectedErrorMessage = "The movie service could not find the given movie";
			String movieId = "MOV123";
			String customerParentalControlLevel = "PG";
			when(movieService.getParentalControlLevel(movieId)).thenThrow(new TitleNotFoundException(expectedErrorMessage));

			// Call service and catch the exception raised for movie id not present.
			try{
			parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);
			} catch(ParentalControlException pce){
				assertEquals(pce.getMessage(), expectedErrorMessage);
			}
		}

		@Test
		public void returnErrorMessageWhenTechnicalFailure() throws Exception {
			// Given
			String expectedErrorMessage = "System error";
			String movieId = "MOV123";
			String customerParentalControlLevel = "PG";
			when(movieService.getParentalControlLevel(movieId))
					.thenThrow(new TechnicalFailureException(expectedErrorMessage));

			// Call service and catch the exception raised for System Error.
			try{
				parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);
				} catch(ParentalControlException pce){
					assertEquals(pce.getMessage(), expectedErrorMessage);
				}
		}

		@Test
		public void returnErrorMessageWhenMovieServiceReturnsNull() throws Exception {
			// Given
			String expectedErrorMessage = "Customer can not watch this movie.";
			String movieId = "MOV123";
			String customerParentalControlLevel = "PG";
			when(movieService.getParentalControlLevel(movieId)).thenReturn(null);

			// Call service and catch the exception raised for System Error.
			try{
				parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);
				} catch(ParentalControlException pce){
					assertEquals(pce.getMessage(), expectedErrorMessage);
				}
		}
		
		@Test
		public void canWatchWhenMovieLevelLessThanCustomerLevel() throws Exception {
			// Given
			String customerParentalControlLevel = "PG";
			String movieId = "movieId1";

			when(movieService.getParentalControlLevel(movieId)).thenReturn("U");

			// Call the service
			final boolean actualResult = parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);

			// assert the result
			assertThat(actualResult, is(true));
		}

		@Test
		public void canWatchWhenMovieLevelEqualToCustomerLevel() throws Exception {
			// Given
			String customerParentalControlLevel = "12";
			String movieId = "MOV123";

			when(movieService.getParentalControlLevel(movieId)).thenReturn("12");

			// Call the service
			final boolean actualResult = parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);

			// Assert the result.
			assertThat(actualResult, is(true));
		}

		@Test
		public void canNotWatchWhenMovieLevelHigherThanCustomerLevel() throws Exception {
			// Given
			String customerParentalControlLevel = "12";
			String movieId = "MOV123";

			when(movieService.getParentalControlLevel(movieId)).thenReturn("15");

			// Call the service
			final boolean actualResult = parentalControlService.canWatchMovie(customerParentalControlLevel, movieId);

			// Assert the result
			assertThat(actualResult, is(false));
		}
	}
