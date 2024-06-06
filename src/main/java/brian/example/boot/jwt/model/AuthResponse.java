package brian.example.boot.jwt.model;

public class AuthResponse {

	private final String accessToken;
	private final String refreshToken;

	public AuthResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return this.accessToken;
	}
	public String getRefreshToken(){ return this.refreshToken; }
}
