package com.insurance.nexusclaim.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaClient {

	@Value("${ollama.url}")
	private String URL;

	@Value("${ollama.model}")
	private String model;

	public String generate(String prompt) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> body = new HashMap<>();
			body.put("model", model);
			body.put("prompt", prompt);
			body.put("temperature", 0);
			body.put("max_tokens", 500);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(URL, request, String.class);

			return response.getBody();
		} catch (Exception e) {
			throw new RuntimeException("Ollama call failed", e);
		}
	}
}