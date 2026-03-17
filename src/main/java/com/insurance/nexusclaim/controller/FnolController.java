package com.insurance.nexusclaim.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.insurance.nexusclaim.model.FnolResponse;
import com.insurance.nexusclaim.service.FnolService;

@RestController
@RequestMapping("/fnol")
public class FnolController {

	private static final String FILE = "file";
	@Autowired
	private FnolService fnolService;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FnolResponse uploadFnol(@RequestParam(FILE) MultipartFile file) {

		String text = extractTextFromFile(file);

		return fnolService.process(text);
	}

	private String extractTextFromFile(MultipartFile file) {
		try {
			if (file.getOriginalFilename().endsWith(".pdf")) {
				return extractFromPDF(file);
			} else {
				return new String(file.getBytes(), StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			throw new RuntimeException("File processing failed", e);
		}
	}

	private String extractFromPDF(MultipartFile file) throws IOException {
		PDDocument document = PDDocument.load(file.getInputStream());
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(document);
		document.close();
		return text;
	}
}