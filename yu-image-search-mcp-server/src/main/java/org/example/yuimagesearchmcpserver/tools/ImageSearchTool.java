package org.example.yuimagesearchmcpserver.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    private String API_KEY = "Fknny0VDalzdJqhp8LnUugCpbzeeJQ8M3rMdBtAXMViN3e1lxbxGRD6P";

    private String SEARCH_API = "https://api.pexels.com/v1/search";

    @Tool(description = "Search image from web")
    public String searchImage(@ToolParam(description = "search query keyword") String keyword) {
        try {
            return String.join(",", searchMediumImage(keyword));
        } catch (Exception e) {
            return "Error search image: " + e.getMessage();
        }
    }

    public List<String> searchMediumImage(String keyword) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", API_KEY);

        Map<String, Object> params = new HashMap<>();
        params.put("query", keyword);

        String response = HttpUtil.createGet(SEARCH_API)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();

        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject)photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

}
