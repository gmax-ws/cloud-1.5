package scalable.solutions.rest.exceptions;

public class OrderNotFoundException extends RuntimeException {

	/**
	 * Serialization id.
	 */
	private static final long serialVersionUID = -5634417769205451678L;

	/**
	 * Constructor.
	 *
	 * @param orderId
	 *            Order ID.
	 */
	public OrderNotFoundException(long orderId) {
		super(String.format("Cannot find order [ID: %d]", orderId));
	}

	/**
	 * Constructor.
	 *
	 * @param throwable
	 *            Throwable object.
	 */
	public OrderNotFoundException(Throwable throwable) {
		super(throwable);
	}
}
