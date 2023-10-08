package ga.rugal.torrency.jws.core.service

import java.util.Optional
import javax.servlet.http.HttpServletRequest
import config.Constant
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.MalformedClaimException
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumer
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver

/**
 * Service for decoding JWS.
 *
 * @author Rugal Bernstein
 */
object JwsDecodeService {

  private val LOG = KotlinLogging.logger {}

  var jwksUrl: String = Constant.JWKS_URL
    set(value) {
      this.resolver = HttpsJwksVerificationKeyResolver(HttpsJwks(value))
    }

  private var resolver: HttpsJwksVerificationKeyResolver

  init {
    this.resolver = HttpsJwksVerificationKeyResolver(HttpsJwks(this.jwksUrl))
  }

  private val jwtConsumer: JwtConsumer
    get() = JwtConsumerBuilder()
      .setRequireExpirationTime() // the JWT must have an expiration time
      // allow some leeway in validating time based claims to account for clock skew
      .setAllowedClockSkewInSeconds(30)
      .setRequireSubject() // the JWT must have a subject claim
      .setExpectedIssuer(Constant.ISSUER) // whom the JWT needs to have been issued by
      .setExpectedAudience(Constant.AUDIENCE) // to whom the JWT is intended for
      .setVerificationKeyResolver(resolver) // only allow the expected signature algorithm(s) in the given context
      .setJwsAlgorithmConstraints( // which is only RS256 here
        AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256
      )
      .build() // create the JwtConsumer instance

  /**
   * Get the actual JWS from authorization header.
   */
  @Throws(InvalidJwtException::class)
  private fun getTokenFromHeader(header: String?): String {
    if (header.isNullOrBlank() || !header.startsWith(Constant.BEARER)) {
      LOG.trace { "Header not found or has no Bearer keyword" }
      throw InvalidJwtException("Header not found or has no Bearer keyword", null, null)
    }

    return header.split(" ").let {
      if (it.size < 2) {
        LOG.trace { "Header has invalid structure" }
        throw InvalidJwtException("Header has invalid structure", null, null)
      }
      it[1]
    }
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
  @Throws(InvalidJwtException::class)
  fun decode(input: String?, isHeader: Boolean): JwtClaims = try {
    //  Validate the JWT and process it to the Claims
    LOG.trace { "Try to decode JWS" }
    jwtConsumer.processToClaims(if (isHeader) getTokenFromHeader(input) else input)
  } catch (e: InvalidJwtException) {
    LOG.error { "Invalid JWT [${e.message}]" }
    throw e
  }

  /**
   * Parse JWS to get the body part.
   *
   * @param request HTTP request that might contain Authorization header
   * @return valid JWT claim object
   * @throws InvalidJwtException JWT in valid or not found
   */
  @Throws(InvalidJwtException::class)
  fun decode(request: HttpServletRequest): JwtClaims =
    this.decode(request.getHeader(Constant.AUTHORIZATION), true)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Validation">
  /**
   * Validate input JWT or header.
   *
   * @param input    a token or header string depending on second parameter
   * @param isHeader indicating the first parameter is token or header
   * @return true iff token exists and valid
   */
  fun isValid(input: String?, isHeader: Boolean): Boolean = try {
    this.decode(input, isHeader)
    true
  } catch (e: InvalidJwtException) {
    false
  }

  /**
   * Validate input JWT or header.
   *
   * @param request HTTP request that might contain Authorization header
   * @return true iff token exists and valid
   */
  fun isValid(request: HttpServletRequest): Boolean =
    this.isValid(request.getHeader(Constant.AUTHORIZATION), true)
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
  fun getUserId(input: String?, isHeader: Boolean): Optional<Int> = try {
    Optional.of(this.decode(input, isHeader).getClaimValue(Constant.ID, Long::class.java).toInt())
  } catch (ex: InvalidJwtException) {
    LOG.error(ex) { "Unable to get user id" }
    Optional.empty()
  } catch (ex: MalformedClaimException) {
    LOG.error(ex) { "Unable to get user id" }
    Optional.empty()
  }

  /**
   * An utility method to help to get user id(in our claim it is [id]) out of the HTTP Authorization
   * header.
   *
   * @param request HTTP request that might contain Authorization header
   * @return true iff token exists and valid
   */
  fun getUserId(request: HttpServletRequest): Optional<Int> =
    this.getUserId(request.getHeader(Constant.AUTHORIZATION), true)
  //</editor-fold>
}