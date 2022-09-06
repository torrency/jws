package config;

/**
 * Record constant string.
 *
 * @author Rugal Bernstein
 */
public interface Constant {

  String BEARER = "Bearer";

  String AUTHORIZATION = "Authorization";

  String SUBJECT = "authentication";

  String ISSUER = "raizekusu";

  String ID = "id";

  String AUDIENCE = "user";

  String JWKS_URL = "http://localhost:8080/jwks.json";
}
