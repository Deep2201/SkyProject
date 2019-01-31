package com.sky.service;

import com.sky.exception.ParentalControlException;

public interface ParentalControlService {
    boolean canWatchMovie(String customerParentalControlLevel, String movieId) throws ParentalControlException;
}
