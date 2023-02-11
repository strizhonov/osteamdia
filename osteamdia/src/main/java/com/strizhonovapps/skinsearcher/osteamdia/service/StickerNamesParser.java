package com.strizhonovapps.skinsearcher.osteamdia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StickerNamesParser {

    @Value("#{'${steam.sticker-names-with-comma}'.split('\\n')}")
    private final Set<String> withCommaStickerNames;

    public Optional<List<String>> parseStickerNames(String nodeValue) {
        if (nodeValue != null && nodeValue.contains("<br>Sticker: ")) {
            List<String> stickerNames = getStickerNames(nodeValue);
            return Optional.of(stickerNames);
        }
        return Optional.empty();
    }

    private List<String> getStickerNames(String value) {
        String stickersRawString = value
                .split("<br>Sticker: ")[1]
                .split("</center>")[0];
        return getStickerNamesFromRawString(stickersRawString);
    }

    private List<String> getStickerNamesFromRawString(String raw) {
        List<String> result = new ArrayList<>();
        StringBuilder rawBuilder = new StringBuilder(raw);
        extractStickersWithCommaAndRemoveThemFromSourceStringBuilder(result, rawBuilder);
        extractStickers(result, rawBuilder);
        return result;
    }

    private void extractStickersWithCommaAndRemoveThemFromSourceStringBuilder(List<String> result, StringBuilder rawBuilder) {
        for (String name : withCommaStickerNames) {
            if (name == null || name.isEmpty()) {
                continue;
            }
            int nameIndex;
            while ((nameIndex = rawBuilder.indexOf(name)) > -1) {
                result.add(name);
                rawBuilder.delete(nameIndex, nameIndex + name.length());
            }
        }
    }

    private void extractStickers(List<String> result, StringBuilder rawBuilder) {
        String[] stickerNamesCandidates = rawBuilder.toString().split(",");
        Arrays.stream(stickerNamesCandidates)
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .forEach(result::add);
    }

}
