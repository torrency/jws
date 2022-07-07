package ga.rugal.jws.core.service.impl;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import config.Constant;

import ga.rugal.jws.core.service.JwsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Implementation for JWS service.
 *
 * @author Rugal Bernstein
 */
@Slf4j
public class JwsServiceImpl implements JwsService {

  @Inject
  public JwsServiceImpl() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Jwt<Header, Claims> decode(final @Nonnull String jws) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Cut the signature part from JWS");
    }
    final String jwt = jws.substring(0, jws.lastIndexOf('.') + 1);
    final var jwtParser = Jwts.parserBuilder()
      .requireSubject(Constant.SUBJECT)
      .requireAudience(Constant.AUDIENCE)
      .requireIssuer(Constant.ISSUER)
      .build();
    //  Validate the JWT and process it to the Claims
    if (LOG.isTraceEnabled()) {
      LOG.trace("Try to decode JWT");
    }
    // only parse the token part
    return jwtParser.parseClaimsJwt(jwt);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Jwt<Header, Claims>> getFromHeader(final @Nullable String header) {
    if (StringUtils.hasLength(header) || !header.startsWith(Constant.BEARER)) {
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
   * {@inheritDoc}
   */
  @Override
  public Optional<Integer> getUserId(final @Nullable String header) {
    final var jwt = this.getFromHeader(header);
    return Optional.ofNullable(jwt.isEmpty()
                               ? null
                               : Integer.parseInt((String) jwt.get().getBody().get("id")));
  }
}
