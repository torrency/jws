package ga.rugal.jws.core.service;

import java.util.Optional;
import javax.annotation.Nullable;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;

/**
 * This service class provisions basic ability to decode and validate JWS.<BR>
 * This is designed for those services that need to get user id information out of request header,
 * while not knowing signing key.<BR>
 * The signing key is a private property of our A&A service.<BR>
 * In our term, the project [Raizekusu].
 *
 * @author Rugal Bernstein
 */
public interface JwsService {

  /**
   * Parse JWS to get the body part, regardless of the signature validity.<BR>
   * This would get body even if signing key not provided. Will not handle exception here because we
   * trust that the A&A service has done its job properly already.
   *
   * @param jws the JWS from header after the Bearer keyword
   * @return valid JWT object
   */
  Jwt<Header, Claims> decode(String jws);

  /**
   * An utility method to help get the JWT from HTTP request Authorization header, so you do not
   * have to manually write the parse logic.
   *
   * @param header just toss the entire Authorization header into it
   * @return empty of unable to find legit token out of Authorization header, otherwise would decode
   *         JWS and get the JWT object out of it
   */
  Optional<Jwt<Header, Claims>> getFromHeader(@Nullable String header);

  /**
   * An utility method to help to get user id(in our claim it is [id]) out of the HTTP Authorization
   * header.
   *
   * @param header just toss the entire Authorization header into it
   * @return the user id if everything look good, or an empty object
   */
  Optional<Integer> getUserId(@Nullable String header);
}
