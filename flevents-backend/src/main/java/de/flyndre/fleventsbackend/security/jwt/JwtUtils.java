package de.flyndre.fleventsbackend.security.jwt;


import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This Class handles the creation and decoding of the JWT-Token.
 * @author Ruben Kraft
 * @version $I$
 */
@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  private ExpiringMap<String,String> invalidTokens = ExpiringMap.builder().variableExpiration().build();
  @Value("${application.jwtSecret}")
  private String jwtSecret;

  @Value("${application.jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * generates JWT-Token.
   * @param authentication The Authentication of the User
   * @return a String containing the Token
   */
  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder().setSubject((userPrincipal.getId())).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * get Uuid of JWT-Token.
   * @param token The JWT-Token
   * @return a String containing the Uuid
   */
  public String getUserNameFromJwtToken(String token) {
    String as = String.valueOf(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody());
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * get Uuid of JWT-Token.
   * @param authToken The JWT-Token
   * @return a boolean if the JWT-Token is valid oder not
   */
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      if(invalidTokens.containsKey(authToken)){
        logger.error("The token was invalidated");
        return false;
      }
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
  public void invalidateToken(String token){
    invalidTokens.put(token,token,jwtExpirationMs, TimeUnit.MILLISECONDS);
  }
}
