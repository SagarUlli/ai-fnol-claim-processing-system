package com.insurance.nexusclaim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExtractionService {

	@Autowired
	private OllamaClient ollamaClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, Object> extract(String text) {

		String prompt = buildPrompt(text);

		String rawResponse = ollamaClient.generate(prompt);

		return parseResponse(rawResponse, text);
	}

	private String buildPrompt(String text) {
		return """
				You are an insurance FNOL parser.

				Extract the following fields into STRICT JSON:

				{
				  "policy_information": {
				    "policy_number": "",
				    "policyholder_name": "",
				    "effective_start_date": "",
				    "effective_end_date": ""
				  },
				  "incident_information": {
				    "date": "",
				    "time": "",
				    "location": "",
				    "description": ""
				  },
				  "involved_parties": {
				    "claimant": "",
				    "third_parties": [],
				    "contact_details": ""
				  },
				  "asset_details": {
				    "asset_type": "",
				    "asset_id": "",
				    "estimated_damage": ""
				  },
				  "other_details": {
				    "claim_type": "",
				    "attachments": [],
				    "initial_estimate": ""
				  }
				}

				Rules:
				- Return ONLY JSON
				- No explanation
				- Use null if missing
				- Normalize dates to YYYY-MM-DD
				- Extract numbers without currency symbols

				FNOL TEXT:
				""" + text;
	}

	private Map<String, Object> parseResponse(String rawResponse, String text) {
		try {
			// For Ollama 0.18.0, the response is plain JSON string
			String cleanJson = cleanJson(rawResponse);
			return objectMapper.readValue(cleanJson, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			return fallbackExtraction(text);
		}
	}

	private Map<String, Object> fallbackExtraction(String text) {
		Map<String, Object> data = new HashMap<>();

		// Minimal fallback structure matching your FNOL JSON schema
		Map<String, Object> policy = new HashMap<>();
		policy.put("policy_number", null);
		policy.put("policyholder_name", null);
		policy.put("effective_start_date", null);
		policy.put("effective_end_date", null);

		Map<String, Object> incident = new HashMap<>();
		incident.put("date", null);
		incident.put("time", null);
		incident.put("location", null);
		incident.put("description", text); // fallback to full text

		Map<String, Object> involvedParties = new HashMap<>();
		involvedParties.put("claimant", null);
		involvedParties.put("third_parties", new ArrayList<>());
		involvedParties.put("contact_details", null);

		Map<String, Object> assetDetails = new HashMap<>();
		assetDetails.put("asset_type", null);
		assetDetails.put("asset_id", null);
		assetDetails.put("estimated_damage", null);

		Map<String, Object> otherDetails = new HashMap<>();
		otherDetails.put("claim_type", null);
		otherDetails.put("attachments", new ArrayList<>());
		otherDetails.put("initial_estimate", null);

		data.put("policy_information", policy);
		data.put("incident_information", incident);
		data.put("involved_parties", involvedParties);
		data.put("asset_details", assetDetails);
		data.put("other_details", otherDetails);

		return data;
	}

	int start = 0;
	int end = 0;

	private String cleanJson(String text) {
		start = text.indexOf("{");
		end = text.lastIndexOf("}");
		return text.trim();
	}
}