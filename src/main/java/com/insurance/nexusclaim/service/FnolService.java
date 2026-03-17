package com.insurance.nexusclaim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insurance.nexusclaim.model.FnolResponse;
import com.insurance.nexusclaim.model.RoutingResult;

@Service
public class FnolService {

	@Autowired
	private ExtractionService extractionService;
	@Autowired
	private ValidationService validationService;
	@Autowired
	private ClassificationService classificationService;
	@Autowired
	private RoutingService routingService;

	public FnolResponse process(String text) {

		Map<String, Object> extracted = extractionService.extract(text);

		List<String> missing = validationService.findMissing(extracted);
		List<String> issues = validationService.validate(extracted);

		String claimType = classificationService.classify(text, extracted);
		Map<String, Object> otherDetails = getMap(extracted, "other_details");
		otherDetails.put("claim_type", claimType);
		RoutingResult routing = routingService.route(extracted, missing, issues);

		FnolResponse response = new FnolResponse();
		response.setExtractedFields(extracted);
		response.setMissingFields(missing);
		response.setRecommendedRoute(routing.getRoute());
		response.setReasoning(routing.getReason());

		return response;
	}

	// Inside FnolService class
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
}