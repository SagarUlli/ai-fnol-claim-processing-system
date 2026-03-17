package com.insurance.nexusclaim.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ClassificationService {

	public String classify(String text, Map<String, Object> data) {

		text = text.toLowerCase();

		if (text.contains("injury") || text.contains("hospital"))
			return "injury";
		if (text.contains("car") || text.contains("vehicle"))
			return "auto";
		if (text.contains("house"))
			return "property";
		if (text.contains("stolen"))
			return "theft";

		return "other";
	}
}