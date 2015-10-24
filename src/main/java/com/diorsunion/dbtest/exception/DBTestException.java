package com.diorsunion.dbtest.exception;


/**
 * The Class AnntestException.
 *
 * @author harley-dog
 */
public class DBTestException extends RuntimeException {

	/** The Constant serialVersionUID. */
    private final static long serialVersionUID = 3116114419992542779L;

    /** The error code. */
	private int errorCode;
    
    /** The error name. */
    private String errorName;

    /**
     * Instantiates a new anntest exception.
     */
    public DBTestException() {
	}

	/**
	 * Instantiates a new anntest exception.
	 *
	 * @param message the message
	 */
	public DBTestException(String message) {
		super(message);
		this.errorCode = 0;
	}

    /**
     * Instantiates a new anntest exception.
     *
     * @param cause the cause
     */
    public DBTestException(Throwable cause){
        super(cause);
    }

    /**
     * Instantiates a new anntest exception.
     *
     * @param cause the cause
     * @param errorName the error name
     */
    public DBTestException(Throwable cause, String errorName) {
        super(cause);
        this.errorName = errorName;
    }

    /**
     * Instantiates a new anntest exception.
     *
     * @param errorCode the error code
     * @param message the message
     */
    public DBTestException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new anntest exception.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 * @param cause the cause
	 */
	public DBTestException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

    /**
     * Instantiates a new anntest exception.
     *
     * @param msg the msg
     * @param e the e
     */
    public DBTestException(String msg, Throwable e) {
        super(msg,e);
        this.errorName = msg;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public int getErrorCode() {
		return errorCode;
	}

    /**
     * Gets the error name.
     *
     * @return the error name
     */
    public String getErrorName() {
        return errorName;
    }
}
