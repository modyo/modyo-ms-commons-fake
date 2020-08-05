package com.modyo.ms.commons.core.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.modyo.ms.commons.core.exceptions.CustomValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class JwtUtilsTest {

  @Test
  public void getClaims_validToken() throws IOException {
    String jwtToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxaXFqYWphMGI1eUZWQUhpWnI1WHowajVIbUEzZkVXdWNHcnE4QkdCVl9BIn0.eyJqdGkiOiIwOTY0ZWE1Yi04ODQ5LTRiZjUtYjA2MS1mMjUzZDAyM2QwN2UiLCJleHAiOjE1OTU1MjYwNjcsIm5iZiI6MCwiaWF0IjoxNTk1NTIyNDY3LCJpc3MiOiJodHRwczovL2JjaW1pYW1pLXNzby5tb2R5by5iZS9hdXRoL3JlYWxtcy9CY2ktTWlhbWkiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzViN2ZmMzktNWE4OC00ZmY2LTk3NmYtN2VmNDIzNDliZTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW9keW8iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMDJkMjc3ZS04YmY5LTQyZWYtOWI4ZC00OTNiNzExYzFiYjgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYmNpbWlhbWkubW9keW8uYmUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImJjaV9taWFtaV9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1hcmt1cyBCcmVja25lciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1icmVja25lckBtb2R5by5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJNYXJrdXMiLCJmYW1pbHlfbmFtZSI6IkJyZWNrbmVyIiwiZW1haWwiOiJtYnJlY2tuZXJAbW9keW8uY29tIn0.gwk2LibEFP_TUGw1v-5XFPjey_L6Rm9zJ7OpbDUnCSuxalwiqGVboxhtvnyGm04iRorfB8jZ7OlG3Cz2W5Wt20S4RHApMuGOZTw7978OmAzAaOAKUC00FCjEj36FFTD2r2AQRHAu513L2ysCKY9JrPed11Ma57GHoCNr-py2ycftHlo8-JII5nxHDQRvAKK4VH_aRvsY4X4-UqzHjN9DTLg1NV97t6gnshes9eV96RzQTYXNDB4vG83fstojuJMUoVAu6qh74Cn83o0SzSBYXOEjzLFzREAKb6YQtZB9ThifVfFeU973FXFTp9D9exc8kkpqcbirEm4IRaAJnjFslw";
    Map<String,Object> claims = JwtUtils.getClaims(jwtToken);

    assertNotNull(claims);
    assertThat(claims.keySet().size(), is(23));
  }

  @Test(expected = CustomValidationException.class)
  public void getClaims_givenInvalidTokenWithoutBearer_ThrowCustomValidationException() throws IOException {
    String tokenWithoutBearer = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxaXFqYWphMGI1eUZWQUhpWnI1WHowajVIbUEzZkVXdWNHcnE4QkdCVl9BIn0.eyJqdGkiOiIwOTY0ZWE1Yi04ODQ5LTRiZjUtYjA2MS1mMjUzZDAyM2QwN2UiLCJleHAiOjE1OTU1MjYwNjcsIm5iZiI6MCwiaWF0IjoxNTk1NTIyNDY3LCJpc3MiOiJodHRwczovL2JjaW1pYW1pLXNzby5tb2R5by5iZS9hdXRoL3JlYWxtcy9CY2ktTWlhbWkiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzViN2ZmMzktNWE4OC00ZmY2LTk3NmYtN2VmNDIzNDliZTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW9keW8iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMDJkMjc3ZS04YmY5LTQyZWYtOWI4ZC00OTNiNzExYzFiYjgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYmNpbWlhbWkubW9keW8uYmUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImJjaV9taWFtaV9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1hcmt1cyBCcmVja25lciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1icmVja25lckBtb2R5by5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJNYXJrdXMiLCJmYW1pbHlfbmFtZSI6IkJyZWNrbmVyIiwiZW1haWwiOiJtYnJlY2tuZXJAbW9keW8uY29tIn0.gwk2LibEFP_TUGw1v-5XFPjey_L6Rm9zJ7OpbDUnCSuxalwiqGVboxhtvnyGm04iRorfB8jZ7OlG3Cz2W5Wt20S4RHApMuGOZTw7978OmAzAaOAKUC00FCjEj36FFTD2r2AQRHAu513L2ysCKY9JrPed11Ma57GHoCNr-py2ycftHlo8-JII5nxHDQRvAKK4VH_aRvsY4X4-UqzHjN9DTLg1NV97t6gnshes9eV96RzQTYXNDB4vG83fstojuJMUoVAu6qh74Cn83o0SzSBYXOEjzLFzREAKb6YQtZB9ThifVfFeU973FXFTp9D9exc8kkpqcbirEm4IRaAJnjFslw";
    JwtUtils.getClaims(tokenWithoutBearer);
  }

  @Test(expected = CustomValidationException.class)
  public void getClaims_givenInvalidToken_ThrowCustomValidationException() throws IOException {
    String tokenWithoutBearer = "BearereyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxaXFqYWphMGI1eUZWQUhpWnI1WHowajVIbUEzZkVXdWNHcnE4QkdCVl9BIn0.eyJqdGkiOiIwOTY0ZWE1Yi04ODQ5LTRiZjUtYjA2MS1mMjUzZDAyM2QwN2UiLCJleHAiOjE1OTU1MjYwNjcsIm5iZiI6MCwiaWF0IjoxNTk1NTIyNDY3LCJpc3MiOiJodHRwczovL2JjaW1pYW1pLXNzby5tb2R5by5iZS9hdXRoL3JlYWxtcy9CY2ktTWlhbWkiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzViN2ZmMzktNWE4OC00ZmY2LTk3NmYtN2VmNDIzNDliZTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW9keW8iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMDJkMjc3ZS04YmY5LTQyZWYtOWI4ZC00OTNiNzExYzFiYjgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYmNpbWlhbWkubW9keW8uYmUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImJjaV9taWFtaV9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1hcmt1cyBCcmVja25lciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1icmVja25lckBtb2R5by5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJNYXJrdXMiLCJmYW1pbHlfbmFtZSI6IkJyZWNrbmVyIiwiZW1haWwiOiJtYnJlY2tuZXJAbW9keW8uY29tIn0.gwk2LibEFP_TUGw1v-5XFPjey_L6Rm9zJ7OpbDUnCSuxalwiqGVboxhtvnyGm04iRorfB8jZ7OlG3Cz2W5Wt20S4RHApMuGOZTw7978OmAzAaOAKUC00FCjEj36FFTD2r2AQRHAu513L2ysCKY9JrPed11Ma57GHoCNr-py2ycftHlo8-JII5nxHDQRvAKK4VH_aRvsY4X4-UqzHjN9DTLg1NV97t6gnshes9eV96RzQTYXNDB4vG83fstojuJMUoVAu6qh74Cn83o0SzSBYXOEjzLFzREAKb6YQtZB9ThifVfFeU973FXFTp9D9exc8kkpqcbirEm4IRaAJnjFslw";
    JwtUtils.getClaims(tokenWithoutBearer);
  }

  @Test(expected = CustomValidationException.class)
  public void getClaims_givenNullToken_ThrowCustomValidationException() throws IOException {
    JwtUtils.getClaims(null);
  }

  @Test
  public void getClaimFromAccessToken_givenClaimExist_ThenReturnValue() throws IOException {
    String jwtToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxaXFqYWphMGI1eUZWQUhpWnI1WHowajVIbUEzZkVXdWNHcnE4QkdCVl9BIn0.eyJqdGkiOiIwOTY0ZWE1Yi04ODQ5LTRiZjUtYjA2MS1mMjUzZDAyM2QwN2UiLCJleHAiOjE1OTU1MjYwNjcsIm5iZiI6MCwiaWF0IjoxNTk1NTIyNDY3LCJpc3MiOiJodHRwczovL2JjaW1pYW1pLXNzby5tb2R5by5iZS9hdXRoL3JlYWxtcy9CY2ktTWlhbWkiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzViN2ZmMzktNWE4OC00ZmY2LTk3NmYtN2VmNDIzNDliZTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW9keW8iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMDJkMjc3ZS04YmY5LTQyZWYtOWI4ZC00OTNiNzExYzFiYjgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYmNpbWlhbWkubW9keW8uYmUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImJjaV9taWFtaV9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1hcmt1cyBCcmVja25lciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1icmVja25lckBtb2R5by5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJNYXJrdXMiLCJmYW1pbHlfbmFtZSI6IkJyZWNrbmVyIiwiZW1haWwiOiJtYnJlY2tuZXJAbW9keW8uY29tIn0.gwk2LibEFP_TUGw1v-5XFPjey_L6Rm9zJ7OpbDUnCSuxalwiqGVboxhtvnyGm04iRorfB8jZ7OlG3Cz2W5Wt20S4RHApMuGOZTw7978OmAzAaOAKUC00FCjEj36FFTD2r2AQRHAu513L2ysCKY9JrPed11Ma57GHoCNr-py2ycftHlo8-JII5nxHDQRvAKK4VH_aRvsY4X4-UqzHjN9DTLg1NV97t6gnshes9eV96RzQTYXNDB4vG83fstojuJMUoVAu6qh74Cn83o0SzSBYXOEjzLFzREAKb6YQtZB9ThifVfFeU973FXFTp9D9exc8kkpqcbirEm4IRaAJnjFslw";
    String response = JwtUtils.getClaimFromAccessToken(jwtToken, "given_name");

    assertThat(response, is("Markus"));
  }

  @Test(expected = CustomValidationException.class)
  public void getClaimFromAccessToken_givenClaimDoesNotExist_ThenReturnValue() throws IOException {
    String jwtToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxaXFqYWphMGI1eUZWQUhpWnI1WHowajVIbUEzZkVXdWNHcnE4QkdCVl9BIn0.eyJqdGkiOiIwOTY0ZWE1Yi04ODQ5LTRiZjUtYjA2MS1mMjUzZDAyM2QwN2UiLCJleHAiOjE1OTU1MjYwNjcsIm5iZiI6MCwiaWF0IjoxNTk1NTIyNDY3LCJpc3MiOiJodHRwczovL2JjaW1pYW1pLXNzby5tb2R5by5iZS9hdXRoL3JlYWxtcy9CY2ktTWlhbWkiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzViN2ZmMzktNWE4OC00ZmY2LTk3NmYtN2VmNDIzNDliZTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW9keW8iLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyMDJkMjc3ZS04YmY5LTQyZWYtOWI4ZC00OTNiNzExYzFiYjgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYmNpbWlhbWkubW9keW8uYmUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImJjaV9taWFtaV9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ik1hcmt1cyBCcmVja25lciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1icmVja25lckBtb2R5by5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJNYXJrdXMiLCJmYW1pbHlfbmFtZSI6IkJyZWNrbmVyIiwiZW1haWwiOiJtYnJlY2tuZXJAbW9keW8uY29tIn0.gwk2LibEFP_TUGw1v-5XFPjey_L6Rm9zJ7OpbDUnCSuxalwiqGVboxhtvnyGm04iRorfB8jZ7OlG3Cz2W5Wt20S4RHApMuGOZTw7978OmAzAaOAKUC00FCjEj36FFTD2r2AQRHAu513L2ysCKY9JrPed11Ma57GHoCNr-py2ycftHlo8-JII5nxHDQRvAKK4VH_aRvsY4X4-UqzHjN9DTLg1NV97t6gnshes9eV96RzQTYXNDB4vG83fstojuJMUoVAu6qh74Cn83o0SzSBYXOEjzLFzREAKb6YQtZB9ThifVfFeU973FXFTp9D9exc8kkpqcbirEm4IRaAJnjFslw";
    JwtUtils.getClaimFromAccessToken(jwtToken, "not_existing");
  }

  @Test
  public void createJWT() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("keyA", "a");
    claims.put("keyB", "b");

    String tokenResponse = JwtUtils.createJWT(claims);

    assertNotNull(tokenResponse);
  }

}