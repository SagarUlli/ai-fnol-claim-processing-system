package com.insurance.nexusclaim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.insurance.nexusclaim.model.RoutingResult;

@Service
public class RoutingService {

	public RoutingResult route(Map<String, Object> data, List<String> missing, List<String> issues) {
		Map<String, Object> incidentInfo = getMap(data, "incident_information");
		String description = (incidentInfo.get("description") != null)
				? incidentInfo.get("description").toString().toLowerCase()
				: "";

		Map<String, Object> otherDetails = getMap(data, "other_details");
		String claimType = (otherDetails.get("claim_type") != null) ? otherDetails.get("claim_type").toString() : "";

		// Use the new robust parser
		Double estimate = parseAmount(otherDetails.get("initial_estimate"));

		// 1. Fraud check
		if (description.contains("fraud") || description.contains("staged")) {
			return new RoutingResult("INVESTIGATION_FLAG", "Suspicious keywords detected.");
		}

		// 2. Data Integrity check
		if (!missing.isEmpty()) {
			return new RoutingResult("MANUAL_REVIEW", "Missing mandatory fields: " + missing);
		}

		// 3. Complexity check
		if ("injury".equalsIgnoreCase(claimType)) {
			return new RoutingResult("SPECIALIST_QUEUE", "Injury claim detected.");
		}

		// 4. Value check
		if (estimate != null && estimate < 25000) {
			return new RoutingResult("FAST_TRACK", "Low value claim (< 25k) approved for fast-track.");
		}

		return new RoutingResult("GENERAL_QUEUE", "Standard processing.");
	}

	private Double parseAmount(Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return ((Number) value).doubleValue();
		try {
			String cleanValue = value.toString().replaceAll("[^\\d.]", "");
			return Double.parseDouble(cleanValue);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(Map<String, Object> parent, String key) {
		Object obj = parent.get(key);
		return (obj instanceof Map) ? (Map<String, Object>) obj : new HashMap<>();
	}
}