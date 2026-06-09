package com.beyefendi.portfolio.urlshortener.mapper;

import com.beyefendi.portfolio.urlshortener.dto.response.ShortenResponse;
import com.beyefendi.portfolio.urlshortener.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {

    @Mapping(target = "shortUrl", ignore = true)
    ShortenResponse toResponse(Url url);
}