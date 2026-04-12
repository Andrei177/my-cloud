package com.example.mycloud.oauth;

import com.example.mycloud.oauth.dto.ClientRegisterDto;
import com.example.mycloud.oauth.dto.OAuthForm;
import com.example.mycloud.oauth.dto.TokenRequest;
import com.example.mycloud.oauth.entities.OAuthClient;
import com.example.mycloud.oauth.entities.OAuthCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/authorize")
    public String getAuthorizePage(@RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri,  @RequestParam(value = "state", required = false) String state, Model model) {
        OAuthClient clientApp = oAuthService.checkClient(clientId, redirectUri);
        model.addAttribute("clientId", clientApp.getClientId());
        model.addAttribute("clientName", clientApp.getClientName());
        model.addAttribute("redirectUri", clientApp.getRedirectUri());
        model.addAttribute("state", state);
        return "oauth-login";
    }

    @PostMapping("/authorize")
    @ResponseBody
    public ResponseEntity<?> getCode(@ModelAttribute OAuthForm form) {
        OAuthCode code = oAuthService.getCode(form);

        String targetUrl = String.format("%s?code=%s&state=%s", form.redirectUri(), code.getCode(), form.state() != null ? form.state() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, targetUrl);

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseEntity<Map<String, String>> exchangeCodeForToken(@RequestBody TokenRequest tokenRequest) {
        String token = oAuthService.exchangeCodeForToken(tokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", token));
    }

    @PostMapping("/clients")
    @ResponseBody
    public ResponseEntity<Map<String, String>> registerClientApp(@RequestBody ClientRegisterDto clientData) {
        oAuthService.registerClient(clientData);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("clientId", clientData.getClientId(), "redirectUri", clientData.getRedirectUri()));
    }
}
