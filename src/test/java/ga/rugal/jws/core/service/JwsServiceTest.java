package ga.rugal.jws.core.service;

import config.Constant;
import config.DaggerJwsLibrary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class JwsServiceTest {

  private JwsService service;

  private final String JWS = "eyJraWQiOiJrMSIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJyYWl6ZWt1c3UiLCJhdWQiOiJ1c2VyIiwiZXhwIjoxNjQ1MDk3NzYzLCJqdGkiOiIxeC1ZREJVX0VGSWt2QUdPTzhnakJ3IiwiaWF0IjoxNjQ1MDExMzYzLCJuYmYiOjE2NDUwMTEyNDMsInN1YiI6ImF1dGhlbnRpY2F0aW9uIiwiaWQiOiIxIn0.R04tTQ3YruFI3EHngA09VyMAF3RpFtuZQhYsoEHVtD4Mh13n4oW41MLLaPmoA1Fnyeava0gU5pZOJbZk-rT8ZsDGO_xi_tFgmNhwiJJBk5dYZlJJI0H2Sz6k3xzFz2WimFejuv72QHEqFeBJo8l1Hfackkh59ByCJf-tmVmEQR64uUGxg3Qu0RWpzalzGOpaHwkN6luT-0ZcrbjG26_JPhYKI_6ssU1-gdV-w3k8PRz1Yvgw7MY_BP6Oh3YSaGiQtWTTP6y9-f3Q0zwR4T05zf5zHy6QgR55IWKGiDEfXEzydu6PlfMH9kNA3ZduciKsR5rXqkaTJYSEcv6UmAFo9g";

  @BeforeEach
  public void setup() {
    final var build = DaggerJwsLibrary.builder().build();
    this.service = build.jwsService();
  }

  @Test
  public void decode() {
    var get = this.service.decode(JWS);
    Assertions.assertEquals(Constant.SUBJECT, get.getBody().getSubject());
  }

  @Test
  public void getFromHeader() {
    var get = this.service.getFromHeader(String.format("Bearer %s", JWS));
    Assertions.assertTrue(get.isPresent());
    Assertions.assertEquals(Constant.SUBJECT, get.get().getBody().getSubject());
  }

  @Test
  public void getUserId() {
    var get = this.service.getUserId(String.format("Bearer %s", JWS));
    Assertions.assertTrue(get.isPresent());
    Assertions.assertEquals(1, get.get().intValue());
  }
}
