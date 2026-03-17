package com.insurance.nexusclaim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	public List<String> findMissing(Map<String, Object> data) {
		List<String> missing = new ArrayList<>();

		Map<String, Object> policy = getMap(data, "policy_information");
		if (policy.get("policy_number") == null) {
			missing.add("policy_number");
		}

		Map<String, Object> incidentInfo = getMap(data, "incident_information");
		if (incidentInfo.get("description") == null) {
			missing.add("description");
		}

		return missing;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(Map<String, Object> parent, String key) {
		Object obj = parent.get(key);
		if (obj instanceof Map) {
			return (Map<String, Object>) obj;
		} else {
			Map<String, Object> map = new HashMap<>();
			parent.put(key, map);
			return map;
		}
	}

	public List<String> validate(Map<String, Object> data) {
		List<String> issues = new ArrayList<>();
		// Add rules if needed
		return issues;
	}
}