package com.beyefendi.portfolio.urlshortener.repository;

import com.beyefendi.portfolio.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    Optional<Url> findByOriginalUrl(String originalUrl);
}