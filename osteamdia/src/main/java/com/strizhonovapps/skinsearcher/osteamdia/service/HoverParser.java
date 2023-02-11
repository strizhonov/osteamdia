package com.strizhonovapps.skinsearcher.osteamdia.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HoverParser {

    private final Pattern hoverPattern = Pattern.compile(".+'(history_row_\\d+_\\d+)_\\w+'.+730, '2', '(\\d+)'.+");

    public Map<String, String> parseHovers(String hovers) {
        String[] lines = hovers.split("\r\n");
        return Arrays.stream(lines)
                .filter(line -> !line.isBlank())
                .map(hoverPattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> Map.entry(matcher.group(1), matcher.group(2)))
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
