package hood.rest;

import hood.IntegrationTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TokenControllerTest extends IntegrationTestBase {
    private final static Cookie SOME_COOKIE = new Cookie("SomeName", "SomeValue");

    private final MockMvc mockMvc;

    @Autowired
    public TokenControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getToken_WrongCredentials() throws Exception {
        token("wrong_pass")
                .andExpect(status().isNotFound());
    }

    @Test
    void getToken_Ok() throws Exception {
        token("admin")
                .andExpect(status().isOk());
    }

    @Test
    void getRefresh_WrongToken() throws Exception {
        refresh("wrong_token", "wrong_fingerprint", SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JWT token is expired or invalid"));
    }

    @Test
    void getRefresh_WrongTokenType() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String accessToken = data.getString("access_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();
        refresh(accessToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token audience"));
    }

    @Test
    void getRefresh_WrongFingerprint() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        refresh(refreshToken, "wrong_fingerprint", SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid fingerprint"));
    }

    @Test
    void getRefresh_TokenExpired() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();

        TimeUnit.SECONDS.sleep(2);
        refresh(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JWT token is expired or invalid"));
    }

    @Test
    void getRefresh_TokenRevoked() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();

        revoke(refreshToken, fingerprint, SOME_COOKIE);
        refresh(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Fingerprint has been revoked"));
    }

    @Test
    void getRefresh_OkCookie() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        refresh(refreshToken, "", response.getCookie("__Secure-Fgp"))
                .andExpect(status().isOk());
    }

    @Test
    void getRefresh_OkPayload() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();
        refresh(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isOk());
    }

    @Test
    void getRevoke_WrongToken() throws Exception {
        revoke("wrong_token", "wrong_fingerprint", SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JWT token is expired or invalid"));
    }

    @Test
    void getRevoke_WrongTokenType() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String accessToken = data.getString("access_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();
        revoke(accessToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token audience"));
    }

    @Test
    void getRevoke_WrongFingerprint() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        revoke(refreshToken, "wrong_fingerprint", SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid fingerprint"));
    }

    @Test
    void getRevoke_TokenExpired() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();

        TimeUnit.SECONDS.sleep(2);
        revoke(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JWT token is expired or invalid"));
    }

    @Test
    void getRevoke_TokenRevoked() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();

        revoke(refreshToken, fingerprint, SOME_COOKIE);
        revoke(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Fingerprint has been revoked"));
    }

    @Test
    void getRevoke_OkCookie() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        revoke(refreshToken, "", response.getCookie("__Secure-Fgp"))
                .andExpect(status().isOk());
    }

    @Test
    void getRevoke_OkPayload() throws Exception {
        MockHttpServletResponse response = token("admin").andReturn().getResponse();
        JSONObject root = new JSONObject(response.getContentAsString());
        JSONObject data = root.getJSONObject("data");

        String refreshToken = data.getString("refresh_token");
        String fingerprint = Objects.requireNonNull(response.getCookie("__Secure-Fgp")).getValue();
        revoke(refreshToken, fingerprint, SOME_COOKIE)
                .andExpect(status().isOk());
    }

    private ResultActions token(String password) throws Exception {
        return this.mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "admin")
                        .param("password", password));
    }

    private ResultActions refresh(String refreshToken, String fingerprint, Cookie cookie) throws Exception {
        return this.mockMvc.perform(post("/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(cookie)
                    .param("refresh_token", refreshToken)
                    .param("fingerprint", fingerprint));
    }

    private ResultActions revoke(String refreshToken, String fingerprint, Cookie cookie) throws Exception {
        return this.mockMvc.perform(post("/revoke")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .param("refresh_token", refreshToken)
                .param("fingerprint", fingerprint));
    }

}