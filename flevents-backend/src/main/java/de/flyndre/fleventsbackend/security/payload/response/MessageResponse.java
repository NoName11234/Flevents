package de.flyndre.fleventsbackend.security.payload.response;
/**
 * This is the DTO of the Answer of the Message-Response which is sent to the Frontend.
 * @author Ruben Kraft
 * @version $I$
 */
public class MessageResponse {
	private String message;

	public MessageResponse(String message) {
	    this.message = message;
	  }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
