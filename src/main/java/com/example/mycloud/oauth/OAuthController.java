package com.example.mycloud.oauth;

import com.example.mycloud.oauth.dto.*;
import com.example.mycloud.oauth.entities.OAuthClient;
import com.example.mycloud.oauth.entities.OAuthCode;
import com.example.mycloud.utils.OperationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Operation(
            summary = "Получение страницы для аутентификации пользователя",
            description = "Страница запрашивается внешним приложением при попытке пользователя входа во внешнем приложении через облачное хранилище",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.OAUTH)
            }),
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная проверка clientId и успешное получение страницы для аутентификации", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Переданный URL для редиректа не совпадает с URL в БД или client приложению заблокирован OAuth сценарий", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/authorize")
    public String getAuthorizePage(@RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri,  @RequestParam(value = "state", required = false) String state, Model model) {
        OAuthClient clientApp = oAuthService.checkClient(clientId, redirectUri);
        model.addAttribute("clientId", clientApp.getClientId());
        model.addAttribute("clientName", clientApp.getClientName());
        model.addAttribute("redirectUri", clientApp.getRedirectUri());
        model.addAttribute("state", state);
        return "oauth-login";
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверка данных пользователя во время аутентификации",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.OAUTH)
            }),
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "303",
                    description = "Редирект на redirect-uri с кодом авторизации",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "URL для редиректа с параметрами code и state",
                                    schema = @Schema(
                                            type = "string",
                                            example = "https://client.com/callback?code=abc123&state=xyz"
                                    )
                            )
                    }
            )
    })
    @PostMapping("/authorize")
    @ResponseBody
    public ResponseEntity<?> getCode(@ModelAttribute OAuthForm form) {
        OAuthCode code = oAuthService.getCode(form);

        String targetUrl = String.format("%s?code=%s&state=%s", form.redirectUri(), code.getCode(), form.state() != null ? form.state() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, targetUrl);

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @Operation(
            summary = "Обмен кода на токен",
            description = "Происходит обмен кода авторизации, полученного после аутентификации пользователя, на токен доступа к облачному хранилищу от имени аутентифицированного пользователя",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.OAUTH)
            }),
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный обмен кода на токен"),
            @ApiResponse(responseCode = "400", description = "Неверный grant_type или Неверный пароль или Срок действия кода истек", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь для которого был сгенерирова код не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TokenResponse> exchangeCodeForToken(@RequestBody TokenRequest tokenRequest) {
        String token = oAuthService.exchangeCodeForToken(tokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }

    @Operation(
            summary = "Регистрация внещнего приложения для осуществления OAuth сценария",
            description = "Внешнее приложение передаёт clientName и redirectUri",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.OAUTH)
            })
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент для OAuth сценария успешно создан"),
    })
    @PostMapping("/clients")
    @ResponseBody
    public ResponseEntity<ClientRegisterResponse> registerClientApp(@RequestBody ClientRegisterRequest clientData) {
        ClientRegisterResponseDto registerResponse = oAuthService.registerClient(clientData);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClientRegisterResponse(registerResponse.getClient().getClientId(), registerResponse.getClientSecret(), registerResponse.getClient().getRedirectUri()));
    }
}
