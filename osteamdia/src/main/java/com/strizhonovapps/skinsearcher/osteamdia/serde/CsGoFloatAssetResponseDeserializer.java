package com.strizhonovapps.skinsearcher.osteamdia.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.strizhonovapps.skinsearcher.osteamdia.model.AppliedStickerDto;
import com.strizhonovapps.skinsearcher.osteamdia.model.CsGoFloatAssetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsGoFloatAssetResponseDeserializer extends JsonDeserializer<CsGoFloatAssetResponse> {

    @Override
    public CsGoFloatAssetResponse deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode iteminfo = parser.getCodec().<JsonNode>readTree(parser).get("iteminfo");
        JsonNode stickersNode = iteminfo.get("stickers");
        JsonNode floatValue = iteminfo.get("floatvalue");

        List<AppliedStickerDto> stickers = new ArrayList<>();
        for (JsonNode stickerNode : stickersNode) {
            AppliedStickerDto appliedSticker = getAppliedSticker(stickerNode);
            stickers.add(appliedSticker);
        }

        return CsGoFloatAssetResponse.builder()
                .floatValue(floatValue.asDouble())
                .appliedStickers(stickers)
                .build();
    }

    private AppliedStickerDto getAppliedSticker(JsonNode stickerNode) {
        String name = getName(stickerNode);
        int wearPercentage = getWearPercentage(stickerNode);
        return AppliedStickerDto.builder()
                .name(name)
                .wearPercentage(wearPercentage)
                .build();
    }

    private int getWearPercentage(JsonNode stickerNode) {
        JsonNode wearNode = getWearNode(stickerNode);
        return wearNode == null
                ? 0
                : getIntegerWearPercentage(wearNode);
    }

    private int getIntegerWearPercentage(JsonNode wearNode) {
        return (int) Math.round(wearNode.asDouble() * 100);
    }

    private JsonNode getWearNode(JsonNode stickerNode) {
        return stickerNode.get("wear");
    }

    private String getName(JsonNode stickerNode) {
        return stickerNode.get("name").asText();
    }

}