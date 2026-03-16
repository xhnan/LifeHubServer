

-- ==========================================
-- 1. 健康画像扩展表 (healthy_user_profiles)
-- 维度：2048（最详细的全局画像，供 AI 深度理解用户）
-- ==========================================
CREATE TABLE healthy_user_profiles (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT UNIQUE NOT NULL,
                                       birth_date DATE,
                                       gender VARCHAR(10),
                                       height_cm NUMERIC(5, 2),
                                       baseline_weight_kg NUMERIC(5, 2),
                                       target_weight_kg NUMERIC(5, 2),
                                       health_profile_embedding VECTOR(1024),
                                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE healthy_user_profiles IS '健康助手特化的用户画像扩展表';
COMMENT ON COLUMN healthy_user_profiles.id IS '画像记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_user_profiles.user_id IS '关联主用户表的唯一标识';
COMMENT ON COLUMN healthy_user_profiles.birth_date IS '出生日期，用于动态计算年龄和基础代谢';
COMMENT ON COLUMN healthy_user_profiles.gender IS '性别生理特征，影响卡路里计算公式';
COMMENT ON COLUMN healthy_user_profiles.height_cm IS '身高（厘米）';
COMMENT ON COLUMN healthy_user_profiles.baseline_weight_kg IS '初始体重（千克）';
COMMENT ON COLUMN healthy_user_profiles.target_weight_kg IS '目标体重（千克）';
COMMENT ON COLUMN healthy_user_profiles.health_profile_embedding IS '用户健康特征与偏好的极高精度向量化表示，用于AI深度个性化响应 (1024)';
COMMENT ON COLUMN healthy_user_profiles.created_at IS '创建时间';
COMMENT ON COLUMN healthy_user_profiles.updated_at IS '更新时间';


-- ==========================================
-- 2. 每日活动汇总表 (healthy_daily_summaries)
-- 维度：1024（标准的段落级文本总结）
-- ==========================================
CREATE TABLE healthy_daily_summaries (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         record_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                         total_steps INTEGER NOT NULL DEFAULT 0,
                                         active_calories_kcal NUMERIC(7, 2) DEFAULT 0.00,
                                         resting_calories_kcal NUMERIC(7, 2) DEFAULT 0.00,
                                         total_distance_meters NUMERIC(8, 2) DEFAULT 0.00,
                                         active_minutes INTEGER DEFAULT 0,
                                         daily_context_embedding VECTOR(1024),
                                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                         UNIQUE(user_id, record_date)
);

COMMENT ON TABLE healthy_daily_summaries IS '每日整体活动与消耗汇总表';
COMMENT ON COLUMN healthy_daily_summaries.id IS '汇总记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_daily_summaries.user_id IS '关联的用户ID';
COMMENT ON COLUMN healthy_daily_summaries.record_date IS '记录日期';
COMMENT ON COLUMN healthy_daily_summaries.total_steps IS '当日累计总步数';
COMMENT ON COLUMN healthy_daily_summaries.active_calories_kcal IS '活动消耗卡路里（千卡）';
COMMENT ON COLUMN healthy_daily_summaries.resting_calories_kcal IS '静息/基础代谢消耗卡路里（千卡）';
COMMENT ON COLUMN healthy_daily_summaries.total_distance_meters IS '当日累计估算距离（米）';
COMMENT ON COLUMN healthy_daily_summaries.active_minutes IS '当日活跃/运动总时长（分钟）';
COMMENT ON COLUMN healthy_daily_summaries.daily_context_embedding IS '当日活动情况的自然语言总结向量，供AI分析纪律性 (1024维)';
COMMENT ON COLUMN healthy_daily_summaries.created_at IS '创建时间';
COMMENT ON COLUMN healthy_daily_summaries.updated_at IS '更新时间';


-- ==========================================
-- 3. 体重相关表 (healthy_weight_logs)
-- 维度：无（纯数值结构化数据，无需向量化）
-- ==========================================
CREATE TABLE healthy_weight_logs (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     record_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                     weight_kg NUMERIC(5, 2) NOT NULL,
                                     body_fat_percentage NUMERIC(5, 2),
                                     bmi NUMERIC(5, 2),
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE healthy_weight_logs IS '用户体重与体脂记录明细表';
COMMENT ON COLUMN healthy_weight_logs.id IS '体重记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_weight_logs.user_id IS '关联的用户ID';
COMMENT ON COLUMN healthy_weight_logs.record_date IS '记录日期';
COMMENT ON COLUMN healthy_weight_logs.weight_kg IS '体重（千克）';
COMMENT ON COLUMN healthy_weight_logs.body_fat_percentage IS '体脂率（百分比）';
COMMENT ON COLUMN healthy_weight_logs.bmi IS '身体质量指数 (BMI)';
COMMENT ON COLUMN healthy_weight_logs.created_at IS '数据写入时间';


-- ==========================================
-- 4. 运动与活动明细表 (healthy_activities)
-- 维度：512（较短的单次运动感受描述）
-- ==========================================
CREATE TABLE healthy_activities (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    activity_type VARCHAR(50) NOT NULL,
                                    start_time TIMESTAMP WITH TIME ZONE,
                                    duration_minutes INTEGER NOT NULL,
                                    calories_burned NUMERIC(7, 2),
                                    description TEXT,
                                    activity_embedding VECTOR(512),
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE healthy_activities IS '单次主动运动与锻炼记录明细表';
COMMENT ON COLUMN healthy_activities.id IS '运动记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_activities.user_id IS '关联的用户ID';
COMMENT ON COLUMN healthy_activities.activity_type IS '运动类型 (如: running, swimming, weightlifting)';
COMMENT ON COLUMN healthy_activities.start_time IS '运动开始时间';
COMMENT ON COLUMN healthy_activities.duration_minutes IS '运动时长（分钟）';
COMMENT ON COLUMN healthy_activities.calories_burned IS '消耗卡路里评估（千卡）';
COMMENT ON COLUMN healthy_activities.description IS '运动详情文本描述，如用户的感受或具体内容';
COMMENT ON COLUMN healthy_activities.activity_embedding IS '运动详情的短文本向量表示，支持自然语言检索该次运动 (512维)';
COMMENT ON COLUMN healthy_activities.created_at IS '数据写入时间';


-- ==========================================
-- 5. 饮食相关明细表 (healthy_diet_logs)
-- 维度：256（超短文本，食物名词罗列）
-- ==========================================
CREATE TABLE healthy_diet_logs (
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   meal_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                   meal_type VARCHAR(20) NOT NULL,
                                   food_items TEXT NOT NULL,
                                   total_calories NUMERIC(7, 2),
                                   protein_g NUMERIC(6, 2),
                                   carbs_g NUMERIC(6, 2),
                                   fat_g NUMERIC(6, 2),
                                   food_embedding VECTOR(256),
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE healthy_diet_logs IS '饮食记录与营养摄入明细表';
COMMENT ON COLUMN healthy_diet_logs.id IS '饮食记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_diet_logs.user_id IS '关联的用户ID';
COMMENT ON COLUMN healthy_diet_logs.meal_time IS '进餐具体时间';
COMMENT ON COLUMN healthy_diet_logs.meal_type IS '餐别 (如: breakfast, lunch, dinner, snack)';
COMMENT ON COLUMN healthy_diet_logs.food_items IS '食物明细文本';
COMMENT ON COLUMN healthy_diet_logs.total_calories IS '总摄入热量（千卡）';
COMMENT ON COLUMN healthy_diet_logs.protein_g IS '蛋白质摄入量（克）';
COMMENT ON COLUMN healthy_diet_logs.carbs_g IS '碳水化合物摄入量（克）';
COMMENT ON COLUMN healthy_diet_logs.fat_g IS '脂肪摄入量（克）';
COMMENT ON COLUMN healthy_diet_logs.food_embedding IS '食物明细的超短文本向量表示，用于AI判断膳食结构相似度 (256维)';
COMMENT ON COLUMN healthy_diet_logs.created_at IS '数据写入时间';


-- ==========================================
-- 6. 健康目标表 (healthy_goals)
-- 维度：1024（标准的段落级目标描述与策略）
-- ==========================================
CREATE TABLE healthy_goals (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               goal_type VARCHAR(50) NOT NULL,
                               target_value NUMERIC(10, 2),
                               deadline DATE,
                               status VARCHAR(20) DEFAULT 'active',
                               goal_embedding VECTOR(1024),
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE healthy_goals IS '用户健康目标与里程碑表';
COMMENT ON COLUMN healthy_goals.id IS '目标记录独立主键，自增BIGINT';
COMMENT ON COLUMN healthy_goals.user_id IS '关联的用户ID';
COMMENT ON COLUMN healthy_goals.goal_type IS '目标类型 (如: weight_loss, step_goal, muscle_gain)';
COMMENT ON COLUMN healthy_goals.target_value IS '目标数值';
COMMENT ON COLUMN healthy_goals.deadline IS '期望达成日期';
COMMENT ON COLUMN healthy_goals.status IS '状态 (active, achieved, abandoned)';
COMMENT ON COLUMN healthy_goals.goal_embedding IS '自然语言目标的向量表示，供AI持续监控并给出鼓励策略 (1024维)';
COMMENT ON COLUMN healthy_goals.created_at IS '数据写入时间';


-- 1. 健康画像表 (2048维)
CREATE INDEX healthy_user_profiles_embedding_idx
    ON healthy_user_profiles
        USING hnsw (health_profile_embedding vector_cosine_ops);

-- 2. 每日活动汇总表 (1024维)
CREATE INDEX healthy_daily_summaries_embedding_idx
    ON healthy_daily_summaries
        USING hnsw (daily_context_embedding vector_cosine_ops);

-- 4. 运动与活动明细表 (512维)
CREATE INDEX healthy_activities_embedding_idx
    ON healthy_activities
        USING hnsw (activity_embedding vector_cosine_ops);

-- 5. 饮食相关明细表 (256维)
CREATE INDEX healthy_diet_logs_embedding_idx
    ON healthy_diet_logs
        USING hnsw (food_embedding vector_cosine_ops);

-- 6. 健康目标表 (1024维)
CREATE INDEX healthy_goals_embedding_idx
    ON healthy_goals
        USING hnsw (goal_embedding vector_cosine_ops);

