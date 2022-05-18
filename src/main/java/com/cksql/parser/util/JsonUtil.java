package com.cksql.parser.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/** json utils. */
@Slf4j
public class JsonUtil {

    public static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T toBean(File jsonFile, Class<T> clazz) {
        if (jsonFile == null) {
            return null;
        }

        try {
            return MAPPER.readValue(jsonFile, clazz);
        } catch (IOException e) {
            log.error("Failed to serial file: {} to {}", jsonFile, clazz, e);
            return null;
        }
    }
}
