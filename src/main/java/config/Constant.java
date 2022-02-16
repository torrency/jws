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

  String ISSUER = SystemDefaultProperty.SCHEMA;

  String ID = "id";

  String AUDIENCE = "user";
}
