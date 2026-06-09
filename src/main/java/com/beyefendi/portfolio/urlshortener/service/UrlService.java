package com.beyefendi.portfolio.urlshortener.service;

import com.beyefendi.portfolio.urlshortener.dto.request.ShortenRequest;
import com.beyefendi.portfolio.urlshortener.dto.response.ShortenResponse;
import com.beyefendi.portfolio.urlshortener.entity.Url;
import com.beyefendi.portfolio.urlshortener.mapper.UrlMapper;
import com.beyefendi.portfolio.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final long CACHE_TTL = 24;
    private static final String SHORTEN_PREFIX = "shorten:";
    private static final String REDIRECT_PREFIX = "redirect:";

    public ShortenResponse shorten(ShortenRequest request) {
        String cachedShortCode = redisTemplate.opsForValue().get(SHORTEN_PREFIX + request.getOriginalUrl());
        if (cachedShortCode != null) {
            return new ShortenResponse(request.getOriginalUrl(), baseUrl + "/api/" + cachedShortCode, cachedShortCode);
        }

        return urlRepository.findByOriginalUrl(request.getOriginalUrl())
                .map(existingUrl -> {
                    redisTemplate.opsForValue().set(SHORTEN_PREFIX + existingUrl.getOriginalUrl(), existingUrl.getShortCode(), CACHE_TTL, TimeUnit.HOURS);
                    ShortenResponse response = urlMapper.toResponse(existingUrl);
                    response.setShortUrl(baseUrl + "/api/" + existingUrl.getShortCode());
                    return response;
                })
                .orElseGet(() -> {
                    String shortCode = generateShortCode();
                    Url url = new Url();
                    url.setOriginalUrl(request.getOriginalUrl());
                    url.setShortCode(shortCode);
                    urlRepository.save(url);

                    redisTemplate.opsForValue().set(SHORTEN_PREFIX + request.getOriginalUrl(), shortCode, CACHE_TTL, TimeUnit.HOURS);
                    redisTemplate.opsForValue().set(REDIRECT_PREFIX + shortCode, request.getOriginalUrl(), CACHE_TTL, TimeUnit.HOURS);

                    ShortenResponse response = urlMapper.toResponse(url);
                    response.setShortUrl(baseUrl + "/api/" + shortCode);
                    return response;
                });
    }

    public String redirect(String shortCode) {
        String cached = redisTemplate.opsForValue().get(REDIRECT_PREFIX + shortCode);
        if (cached != null) return cached;

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found: " + shortCode));

        redisTemplate.opsForValue().set(REDIRECT_PREFIX + shortCode, url.getOriginalUrl(), CACHE_TTL, TimeUnit.HOURS);
        return url.getOriginalUrl();
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        while (true) {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                code.append(chars.charAt((int) (Math.random() * chars.length())));
            }
            if (!urlRepository.existsByShortCode(code.toString())) {
                return code.toString();
            }
        }
    }
}