package com.study.attach;

/**
 * Exception thrown when {@link IFlightRecorderService} operations fail.
 */
public class FlightRecorderException extends Exception {
	private static final long serialVersionUID = -6818343566212416982L;

	public FlightRecorderException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlightRecorderException(String message) {
		super(message);
	}
}
