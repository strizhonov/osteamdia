package com.strizhonovapps.skinsearcher.osteamdia.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.strizhonovapps.skinsearcher.osteamdia.model.AssetItem;
import com.strizhonovapps.skinsearcher.osteamdia.model.SteamHistoryResponseWrapper;
import com.strizhonovapps.skinsearcher.osteamdia.service.StickerNamesParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SteamHistoryResponseDeserializer extends JsonDeserializer<SteamHistoryResponseWrapper> {

    private static final String UNAUTHORIZED_TOKEN = "Login</a> to view your Community Market history";
    private final StickerNamesParser stickerNamesRetriever;

    @Override
    public SteamHistoryResponseWrapper deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);

        if (root.findValue("results_html").toString().contains(UNAUTHORIZED_TOKEN)) {
            throw new IllegalArgumentException("Unauthorized to Steam. Check the access token.");
        }
        JsonNode assets = root.get("assets").get("730").get("2");
        List<AssetItem> skinListings = getAssetItems(assets);
        int totalCount = root.get("total_count").asInt();
        String hovers = root.get("hovers").asText();
        String html = root.get("results_html").asText();

        return SteamHistoryResponseWrapper.builder()
                .totalCount(totalCount)
                .hovers(hovers)
                .resultHtml(html)
                .assetItems(skinListings)
                .build();
    }

    private List<AssetItem> getAssetItems(JsonNode root) {
        List<AssetItem> skinListings = new ArrayList<>();
        root.fields()
                .forEachRemaining(
                        entry -> findAssetItem(entry).ifPresent(skinListings::add)
                );
        return skinListings;
    }

    private Optional<AssetItem> findAssetItem(Map.Entry<String, JsonNode> entry) {
        List<String> stickers = getStickers(entry.getValue());
        String nameTag = getNameTag(entry.getValue());
        String link = getLink(entry);
        String name = getName(entry.getValue());
        int commodity = getCommodity(entry);

        AssetItem result = AssetItem.builder()
                .id(entry.getKey())
                .name(name)
                .commodity(commodity)
                .steamLink(link)
                .nameTag(nameTag)
                .appliedStickers(stickers)
                .build();
        return Optional.of(result);
    }

    private int getCommodity(Map.Entry<String, JsonNode> entry) {
        return entry.getValue().get("commodity").asInt();
    }

    private String getLink(Map.Entry<String, JsonNode> entry) {
        return Optional.ofNullable(
                entry.getValue().get("market_actions")
        ).map(actions ->
                actions.get(0)
                        .get("link")
                        .asText()
                        .replace("%assetid%", entry.getKey())
        ).orElse(null);
    }

    private List<String> getStickers(JsonNode assetNode) {
        JsonNode descriptionsNode = assetNode.findValue("descriptions");
        for (JsonNode descriptionNode : descriptionsNode) {
            Optional<List<String>> names = findStickerNames(descriptionNode);
            if (names.isPresent()) {
                return names.get();
            }
        }
        return new ArrayList<>();
    }

    private String getNameTag(JsonNode assetNode) {
        JsonNode nameTagNode = assetNode.findValue("fraudwarnings");
        return Optional.ofNullable(nameTagNode)
                .map(node -> node.get(0))
                .map(JsonNode::asText)
                .map(value -> value.split("Name Tag: ")[1])
                .map(value -> value.split("''")[1])
                .orElse(null);
    }

    private String getName(JsonNode root) {
        return root.findValue("market_hash_name").asText();
    }

    private Optional<List<String>> findStickerNames(JsonNode descriptionNode) {
        JsonNode valueNode = descriptionNode.findValue("value");
        if (valueNode == null) {
            return Optional.empty();
        }
        String value = valueNode.asText();
        return stickerNamesRetriever.parseStickerNames(value);
    }

}
