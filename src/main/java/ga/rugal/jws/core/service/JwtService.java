package ga.rugal.jws.core.service;

import java.util.Optional;
import javax.annotation.Nullable;

import config.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * This service class provisions basic ability to decode and validate JWS.<BR>
 * This is designed for those services that need to get user id information out of request header,
 * while not knowing signing key.<BR>
 * The signing key is a private property of our A&A service.<BR>
 * In our term, the project [Raizekusu].
 *
 * @author rugal
 */
@Slf4j
public class JwtService {

  /**
   * Parse JWS to get the body part, regardless of the signature validity.<BR>
   * This would get body even if signing key not provided. Will not handle exception here because we
   * trust that the A&A service has done its job properly already.
   *
   * @param jws the JWS from header after the Bearer keyword
   * @return valid JWT object
   */
  public Jwt<Header, Claims> decode(final String jws) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Cut the signature part from JWS");
    }
    final String jwt = jws.substring(0, jws.lastIndexOf('.') + 1);
    final var jwtParser = Jwts.parserBuilder().build();
    //  Validate the JWT and process it to the Claims
    if (LOG.isTraceEnabled()) {
      LOG.trace("Try to decode JWT");
    }
    // only parse the token part
    return jwtParser.parseClaimsJwt(jwt);
  }

  /**
   * An utility method to help get the JWT from HTTP request Authorization header, so you do not
   * have to manually write the parse logic.
   *
   * @param header just toss the entire Authorization header into it
   * @return empty of unable to find legit token out of Authorization header, otherwise would decode
   *         JWS and get the JWT object out of it
   */
  public Optional<Jwt<Header, Claims>> getFromHeader(final @Nullable String header) {
    if (StringUtils.isEmpty(header) || !header.startsWith(Constant.BEARER)) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Header not found or has no Bearer keyword");
      }
      return Optional.empty();
    }

    final var split = header.split(" ");
    if (split.length < 2) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Header has invalid structure");
      }
      return Optional.empty();
    }
    return Optional.of(this.decode(split[1]));
  }

  /**
   * An utility method to help to get user id(in our claim it is [id]) out of the HTTP Authorization
   * header.
   *
   * @param header just toss the entire Authorization header into it
   * @return the user id if everything look good, or an empty object
   */
  public Optional<Integer> getUserId(final @Nullable String header) {
    final var jwt = this.getFromHeader(header);
    return Optional.ofNullable(jwt.isEmpty()
                               ? null
                               : Integer.parseInt((String) jwt.get().getBody().get("id")));
  }
}
