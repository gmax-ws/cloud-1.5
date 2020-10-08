package scalable.solutions.oborder.exceptions;

public class UnauthorizedException extends RuntimeException {
	
	/**
	 * Serialization id.
	 */
	private static final long serialVersionUID = 7358504912774576391L;

	/**
	 * Constructor.
	 *
	 * @param message
	 *            Error message.
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param throwable
	 *            Throwable object.
	 */
	public UnauthorizedException(Throwable throwable) {
		super(throwable);
	}
}
