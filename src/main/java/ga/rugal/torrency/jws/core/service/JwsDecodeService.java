package ga.rugal.torrency.jws.core.service;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import config.Constant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.springframework.util.StringUtils;

/**
 * Service for decoding JWS.
 *
 * @author Rugal Bernstein
 */
@Slf4j
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class JwsDecodeService {

  private final HttpsJwksVerificationKeyResolver resolver;

  @Inject
  public JwsDecodeService(final HttpsJwksVerificationKeyResolver resolver) {
    this.resolver = resolver;
  }

  private JwtConsumer getJwtConsumer() {
    return new JwtConsumerBuilder()
      .setRequireExpirationTime() // the JWT must have an expiration time
      // allow some leeway in validating time based claims to account for clock skew
      .setAllowedClockSkewInSeconds(30)
      .setRequireSubject() // the JWT must have a subject claim
      .setExpectedIssuer(Constant.ISSUER) // whom the JWT needs to have been issued by
      .setExpectedAudience(Constant.AUDIENCE) // to whom the JWT is intended for
      .setVerificationKeyResolver(this.resolver)
      // only allow the expected signature algorithm(s) in the given context
      .setJwsAlgorithmConstraints(
        // which is only RS256 here
        AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
      .build(); // create the JwtConsumer instance
  }

  private String getTokenFromHeader(final @Nullable String header) throws InvalidJwtException {
    if (null == header
        || !StringUtils.hasText(header)
        || !header.startsWith(Constant.BEARER)) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Header not found or has no Bearer keyword");
      }
      throw new InvalidJwtException("Header not found or has no Bearer keyword", null, null);
    }

    final var split = header.split(" ");
    if (split.length < 2) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Header has invalid structure");
      }
      throw new InvalidJwtException("Header has invalid structure", null, null);
    }
    return split[1];
  }

  //<editor-fold defaultstate="collapsed" desc="Decode">
  /**
   * Parse JWS to get the body part.
   *
   * @param input    a token or header string depending on second parameter
   * @param isHeader indicating the first parameter is token or header
   * @return valid JWT claim object
   * @throws InvalidJwtException JWT in valid or not found
   */
  public JwtClaims decode(final @Nullable String input, final boolean isHeader)
    throws InvalidJwtException {
    String token = input;
    if (isHeader) {
      token = this.getTokenFromHeader(input);
    }
    final var jwtConsumer = this.getJwtConsumer();

    try {
      //  Validate the JWT and process it to the Claims
      if (LOG.isTraceEnabled()) {
        LOG.trace("Try to decode JWS");
      }
      return jwtConsumer.processToClaims(token);
    } catch (final InvalidJwtException e) {
      LOG.error("Invalid JWT [{}]", e.getMessage());
      throw e;
    }
  }

  /**
   * Parse JWS to get the body part.
   *
   * @param request HTTP request that might contain Authorization header
   * @return valid JWT claim object
   * @throws InvalidJwtException JWT in valid or not found
   */
  public JwtClaims decode(final @Nonnull HttpServletRequest request) throws InvalidJwtException {
    return this.decode(request.getHeader(Constant.AUTHORIZATION), true);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Validation">
  /**
   * Validate input JWT or header.
   *
   * @param input    a token or header string depending on second parameter
   * @param isHeader indicating the first parameter is token or header
   * @return true iff token exists and valid
   */
  public boolean isValid(final @Nullable String input, final boolean isHeader) {
    try {
      this.decode(input, isHeader);
      return true;
    } catch (final InvalidJwtException e) {
      return false;
    }
  }

  /**
   * Validate input JWT or header.
   *
   * @param request HTTP request that might contain Authorization header
   * @return true iff token exists and valid
   */
  public boolean isValid(final @Nonnull HttpServletRequest request) {
    return this.isValid(request.getHeader(Constant.AUTHORIZATION), true);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Get User ID">
  /**
   * An utility method to help to get user id(in our claim it is [id]) out of the HTTP Authorization
   * header.
   *
   * @param input    a token or header string depending on second parameter
   * @param isHeader indicating the first parameter is token or header
   * @return true iff token exists and valid
   */
  public Optional<Integer> getUserId(final @Nullable String input, final boolean isHeader) {
    try {
      final var claims = this.decode(input, isHeader);
      return Optional.of(claims.getClaimValue(Constant.ID, Long.class).intValue());
    } catch (final InvalidJwtException | MalformedClaimException ex) {
      LOG.error("Unable to get user id", ex);
      return Optional.empty();
    }
  }

  /**
   * An utility method to help to get user id(in our claim it is [id]) out of the HTTP Authorization
   * header.
   *
   * @param request HTTP request that might contain Authorization header
   * @return true iff token exists and valid
   */
  public Optional<Integer> getUserId(final @Nonnull HttpServletRequest request) {
    return this.getUserId(request.getHeader(Constant.AUTHORIZATION), true);
  }
  //</editor-fold>
}
