package com.example.elasticsearchproject.services;

import com.example.elasticsearchproject.models.Article;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class EsService {

    private final static String INDEX_NAME = "articles";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestHighLevelClient esClient;

    public EsService(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    public void updateArticle(String id, String title, String text) throws Exception {
        Article article = new Article();
        article.setTitle(title);
        article.setText(text);

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(id);
        indexRequest.source(objectMapper.writeValueAsString(article), XContentType.JSON);

        esClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @SneakyThrows
    public List<Article> search(String query) {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("text", query));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Article> articles = new ArrayList<>();
        Arrays.stream(searchResponse.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .forEach(sourceAsMap -> {
                    Article article = new Article();
                    article.setText((String) sourceAsMap.get("text"));
                    article.setTitle((String) sourceAsMap.get("title"));
                    articles.add(article);
                });

        return articles;
    }
}
