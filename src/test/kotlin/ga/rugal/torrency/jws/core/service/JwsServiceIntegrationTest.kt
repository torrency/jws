package ga.rugal.torrency.jws.core.service

import ga.rugal.torrency.jws.Constant
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class JwsServiceIntegrationTest {

  private val JWS =
    "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJyYWl6ZWt1c3UiLCJhdWQiOiJ1c2VyIiwiZXhwIjoxNjYyNTM1NjQ3LCJqdGkiOiJ6bl9HbFhkX1pmc2tLdjZPdF9sLUtBIiwiaWF0IjoxNjYyNDQ5MjQ3LCJuYmYiOjE2NjI0NDkxMjcsInN1YiI6ImF1dGhlbnRpY2F0aW9uIiwiaWQiOjF9.x9exf51Tf6x-PTDRQ5ehaEDgwxvoCuB10P8i0iCavAPUgH4o0gB5s_EOMt_6g0OdcrGZaiWasUHt7TVY0eKTQNSwDZMZqnQaYnXHN3UXdzpxpNOzXnCkIpx4OHA2hnaxFW_5kLSi15r6tDApCEHiVqqfC9beC-_yynhKp9tL19Vzcb4zd9i4720No2LmFz4nXLTXoHNzzvzDp1XkQRZrBwg4rDccl-dByzV7SIPftRtPo3YjxXsMWgpXJN_0vnddH00PlcXjmTZisPsw5Fz_hf86LYi0g2jIh--ICIloUMGLJLrAxwx8HrHKXbJEqMMCmptIpEIo6Y0ALZvR4zf2mg"

  @Test
  fun decode() {
    val get = JwsDecodeService.decode(JWS, false)
    Assertions.assertEquals(Constant.SUBJECT, get.subject)
  }

  @Test
  fun userId() {
    val claim = JwsDecodeService.getUserId(JWS, false)
    Assertions.assertNotNull(claim)
    Assertions.assertEquals(1, claim)
  }
}
