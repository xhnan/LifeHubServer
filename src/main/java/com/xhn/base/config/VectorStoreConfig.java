package com.xhn.base.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.zhipuai.autoconfigure.ZhiPuAiConnectionProperties;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VectorStoreConfig {

    @Bean
    @ConditionalOnMissingBean
    public ZhiPuAiApi zhiPuAiApi(
            ZhiPuAiConnectionProperties properties,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<ResponseErrorHandler> responseErrorHandlerProvider) {

        return ZhiPuAiApi.builder()
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .responseErrorHandler(responseErrorHandlerProvider.getIfAvailable(DefaultResponseErrorHandler::new))
                .build();
    }

    /** 鐢ㄦ埛鐢诲儚锛歟mbedding-3 1024 缁?*/
    @Bean(name = "profileEmbeddingModel")
    public EmbeddingModel profileEmbeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(
                zhiPuAiApi,
                MetadataMode.EMBED,
                ZhiPuAiEmbeddingOptions.builder()
                        .model("embedding-3")
                        .dimensions(1024)
                .build()
        );
    }

    /** 姣忔棩鎬荤粨/鍋ュ悍鐩爣锛歟mbedding-3 1024 缁?*/
    @Bean(name = "summaryGoalEmbeddingModel")
    public EmbeddingModel summaryGoalEmbeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(
                zhiPuAiApi,
                MetadataMode.EMBED,
                ZhiPuAiEmbeddingOptions.builder()
                        .model("embedding-3")
                        .dimensions(1024)
                .build()
        );
    }

    /** 杩愬姩鏄庣粏锛歟mbedding-3 512 缁?*/
    @Bean(name = "activityEmbeddingModel")
    public EmbeddingModel activityEmbeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(
                zhiPuAiApi,
                MetadataMode.EMBED,
                ZhiPuAiEmbeddingOptions.builder()
                        .model("embedding-3")
                        .dimensions(512)
                .build()
        );
    }

    /** 楗鏄庣粏锛歟mbedding-3 256 缁?*/
    @Bean(name = "dietEmbeddingModel")
    public EmbeddingModel dietEmbeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(
                zhiPuAiApi,
                MetadataMode.EMBED,
                ZhiPuAiEmbeddingOptions.builder()
                        .model("embedding-3")
                        .dimensions(256)
                .build()
        );
    }

    /** health_user_profiles -> health_profile_embedding (1024) */
    @Bean(name = "profileVectorStore")
    public PgVectorStore profileVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("profileEmbeddingModel") EmbeddingModel embeddingModel) {
        return buildStore(jdbcTemplate, embeddingModel, 1024, "health_user_profiles");
    }

    /** health_daily_summaries -> daily_context_embedding (1024) */
    @Bean(name = "summaryVectorStore")
    public PgVectorStore summaryVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("summaryGoalEmbeddingModel") EmbeddingModel embeddingModel) {
        return buildStore(jdbcTemplate, embeddingModel, 1024, "health_daily_summaries");
    }

    /** health_goals -> goal_embedding (1024) */
    @Bean(name = "goalVectorStore")
    public PgVectorStore goalVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("summaryGoalEmbeddingModel") EmbeddingModel embeddingModel) {
        return buildStore(jdbcTemplate, embeddingModel, 1024, "health_goals");
    }

    /** health_activities -> activity_embedding (512) */
    @Bean(name = "activityVectorStore")
    public PgVectorStore activityVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("activityEmbeddingModel") EmbeddingModel embeddingModel) {
        return buildStore(jdbcTemplate, embeddingModel, 512, "health_activities");
    }

    /** health_diet_logs -> food_embedding (256) */
    @Bean(name = "dietVectorStore")
    public PgVectorStore dietVectorStore(
            JdbcTemplate jdbcTemplate,
            @Qualifier("dietEmbeddingModel") EmbeddingModel embeddingModel) {
        return buildStore(jdbcTemplate, embeddingModel, 256, "health_diet_logs");
    }

    /** 缁熶竴鏋勫缓 PgVectorStore銆俰nitializeSchema(false) 琛ㄧず涓嶈嚜鍔ㄦ敼琛ㄧ粨鏋勩€?*/
    private PgVectorStore buildStore(
            JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel,
            int dimensions,
            String tableName) {

        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(dimensions)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .vectorTableName(tableName)
                .initializeSchema(false)
                .build();
    }
}

