package com.beyefendi.portfolio.urlshortener.controller;

import com.beyefendi.portfolio.urlshortener.dto.request.ShortenRequest;
import com.beyefendi.portfolio.urlshortener.dto.response.ShortenResponse;
import com.beyefendi.portfolio.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        return ResponseEntity.ok(urlService.shorten(request));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.redirect(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}