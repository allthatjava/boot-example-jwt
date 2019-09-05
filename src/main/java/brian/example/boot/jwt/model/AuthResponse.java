package brian.example.boot.jwt.model;

public class AuthResponse {

	private final String jwttoken;

	public AuthResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
}
