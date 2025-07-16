package com.ybl.was.controller;

import com.ybl.was.model.WASScriptRequest;
import com.ybl.was.service.WASService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.util.Map;

@RestController
@RequestMapping("/api/was")
public class WASController {

	private final WASService service;

	public WASController(WASService service) {
		this.service = service;
	}

	@Operation(summary = "Démarrer un serveur WebSphere", description = "Lance startServer.bat avec les paramètres fournis")
	@PostMapping("/start")
	public String start(
			@Parameter(description = "Nom du profil WAS", example = "ENV1_PROFILE") @RequestParam String profile,
			@Parameter(description = "Nom du serveur à démarrer", example = "server1") @RequestParam String server) {
		return service.startServer(profile, server);
	}

	@PostMapping("/stop")
	public String stop(
			@Parameter(description = "Nom du profil WAS", example = "ENV1_PROFILE") @RequestParam String profile,
			@Parameter(description = "Nom du serveur à démarrer", example = "server1") @RequestParam String server) {
		return service.stopServer(profile, server);
	}

	@Operation(summary = "Exécuter un script wsadmin.bat", description = "Lance un script Jython ou autre via wsadmin.bat")

	@PostMapping("/exec-script")
	public String execScript(
			@RequestBody(description = "Paramètres d'exécution du script", required = true, content = @Content(schema = @Schema(implementation = WASScriptRequest.class)))
			@org.springframework.web.bind.annotation.RequestBody WASScriptRequest req) {
		return service.runWsadmin(req);
	}

	@Operation(summary = "Lister les ports d'un profil WebSphere", description = "Lit serverindex.xml et retourne les ports utilisés")
	@GetMapping("/ports")
	public Map<String, String> listPorts(
			@Parameter(description = "Nom du profil", example = "ENV1_PROFILE") @RequestParam String profile) {
		return service.listPorts(profile);
	}
	@GetMapping("/profile/status/{profileName}/{serverName}")
	public ResponseEntity<ProfileStatusResponse> getProfileStatus(
	        @PathVariable String profileName,
	        @PathVariable String serverName,
	        @RequestParam(defaultValue = "wsadmin") String user,
	        @RequestParam(defaultValue = "wsadmin") String password
	) {
	    ProfileStatusResponse response = WasProfileChecker.checkProfileStatus(serverName, profileName, user, password);
	    return ResponseEntity.ok(response);
	}

}
