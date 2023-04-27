package de.flyndre.fleventsbackend.security.payload.request;


import jakarta.validation.constraints.NotBlank;
/**
 * This is the DTO of the Login which the Frontend sends to the Backend.
 * @author Ruben Kraft
 * @version $I$
 */
public class LoginRequest {
	@NotBlank
	private String username;

	@NotBlank
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
