package com.beyefendi.portfolio.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenResponse {

    private String originalUrl;
    private String shortUrl;
    private String shortCode;
}
