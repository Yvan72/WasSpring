package com.ybl.was.service;

import com.ybl.was.model.WASScriptRequest;
import com.ybl.was.util.WASCommandBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

@Service
public class WASService {
	
	private static final Logger logger = LoggerFactory.getLogger(WASService.class);

	public String startServer(String profile, String server) {
		String path = "C:/IBM/WebSphere/AppServer/bin/startServer.bat";
		logger.info("Demande de démarrage du serveur '{}' pour le profil '{}'", server, profile);
		try {
		 String result= new WASCommandBuilder(path)
					.addArg(server)
					.addOption("-profileName", profile)
					.executeAndGetOutput();
		 	logger.info("Démarrage terminé :\n{}", result);
		 	return result;
			
		} catch (IOException e) {
			 logger.error("Erreur lors du démarrage du serveur", e);
			return "Erreur : " + e.getMessage();
		}
	}

	public String stopServer(String profile, String server) {
		String path = "C:/IBM/WebSphere/AppServer/bin/stopServer.bat";
		 logger.info("Demande d'arrêt du serveur '{}' pour le profil '{}'", server, profile);
		try {
			String result= new WASCommandBuilder(path)
					.addArg(server)
					.addOption("-profileName", profile)
					.executeAndGetOutput();
				logger.info("Arrêt terminé :\n{}", result);
				return result;
		} catch (IOException e) {
			logger.error("Erreur lors de l'arrêt du serveur", e);
			return "Erreur : " + e.getMessage();
		}
	}

	public String runWsadmin(WASScriptRequest req) {
		String path = "C:/IBM/WebSphere/AppServer/profiles/" + req.profile + "/bin/wsadmin.bat";
		logger.info("Exécution de wsadmin.bat pour le profil '{}'", req.profile);
        logger.debug("Script : {}, Langage : {}, Args : {}", req.scriptPath, req.language, req.args);
		try {
			WASCommandBuilder builder = new WASCommandBuilder(path)
					.addOption("-lang", req.language != null ? req.language : "jython")
					.addOption("-f", req.scriptPath);

			if (req.args != null) {
				for (String arg : req.args) {
					builder.addArg(arg);
				}
			}

			String result = builder.executeAndGetOutput();
			logger.info("Exécution terminée. Résultat :\n{}", result);
			return result;
		} catch (IOException e) {
			logger.error("Erreur pendant l'exécution de wsadmin", e);
			return "Erreur : " + e.getMessage();
		}
	}


	public Map<String, String> listPorts(String profileName) {
		logger.info("Récupération des ports pour le profil '{}'", profileName);
	    Map<String, String> ports = new HashMap<>();

	    try {
	        String basePath = "C:/IBM/WebSphere/AppServer/profiles/" + profileName + "/config";
	        File cellsDir = new File(basePath + "/cells");

	        if (!cellsDir.exists() || !cellsDir.isDirectory()) {
	        	logger.warn("Répertoire 'cells' introuvable pour le profil '{}'", profileName);
	            ports.put("error", "Répertoire cells introuvable pour le profil : " + profileName);
	            return ports;
	        }

	        File cell = cellsDir.listFiles(File::isDirectory)[0];
	        File node = new File(cell, "nodes");
	        File firstNode = node.listFiles(File::isDirectory)[0];
	        File serverIndex = new File(firstNode, "serverindex.xml");

	        Document doc = DocumentBuilderFactory.newInstance()
	                .newDocumentBuilder().parse(serverIndex);

	        NodeList specialEndpoints = doc.getElementsByTagName("specialEndpoints");

	        for (int i = 0; i < specialEndpoints.getLength(); i++) {
	            Element sp = (Element) specialEndpoints.item(i);
	            String logicalName = sp.getAttribute("endPointName");

	            NodeList endPoints = sp.getElementsByTagName("endPoint");
	            if (endPoints.getLength() > 0) {
	                Element ep = (Element) endPoints.item(0);
	                String port = ep.getAttribute("port");
	                ports.put(logicalName, port);
	            }
	        }
	        logger.info("Ports récupérés pour le profil '{}': {}", profileName, ports);
	    } catch (Exception e) {
	    	 logger.error("Erreur lors de la lecture des ports du profil '{}'", profileName, e);
	        ports.put("error", "Exception : " + e.getMessage());
	    }

	    return ports;
	}

}
