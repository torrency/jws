package config;

import dagger.Module;
import dagger.Provides;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;

/**
 * Module regarding application.
 *
 * @author Rugal Bernstein
 */
@Module
public class AssembleModule {

  private final String jwksUrl;

  public AssembleModule() {
    this(Constant.JWKS_URL);
  }

  public AssembleModule(final String jwksUrl) {
    this.jwksUrl = jwksUrl;
  }

  /**
   * The HttpsJwksVerificationKeyResolver uses JWKs obtained from the HttpsJwks and will select the
   * most appropriate one to use for verification based on the Key ID and other factors provided in
   * the header of the JWS/JWT.
   *
   * @return key resolver object
   */
  @Provides
  public HttpsJwksVerificationKeyResolver jwksKeyResolver() {
    return new HttpsJwksVerificationKeyResolver(new HttpsJwks(this.jwksUrl));
  }
}
