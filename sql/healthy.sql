

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
                                       health_profile_embedding VECTOR(2048),
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
COMMENT ON COLUMN healthy_user_profiles.health_profile_embedding IS '用户健康特征与偏好的极高精度向量化表示，用于AI深度个性化响应 (2048维)';
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















-- 确保已经安装并启用了 pgvector 扩展
-- CREATE EXTENSION IF NOT EXISTS vector;

-- ==========================================
-- 1. 心理健康档案表 (health_psy_profiles)
-- 用于存储用户的长期心理状态基线、性格特征等
-- ==========================================
CREATE TABLE health_psy_profiles (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id BIGINT UNIQUE NOT NULL,
                                     mbti_type VARCHAR(10),
                                     enneagram_type VARCHAR(10),
                                     baseline_stress_level INT DEFAULT 0,
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_psy_profiles IS '用户心理健康档案表，记录用户的长期心理特征';
COMMENT ON COLUMN health_psy_profiles.id IS '主键ID，自增BIGINT';
COMMENT ON COLUMN health_psy_profiles.user_id IS '关联主用户表的唯一标识';
COMMENT ON COLUMN health_psy_profiles.mbti_type IS '用户的MBTI性格类型（如 INTJ, ENFP）';
COMMENT ON COLUMN health_psy_profiles.enneagram_type IS '九型人格类型（如 5w4）';
COMMENT ON COLUMN health_psy_profiles.baseline_stress_level IS '基准压力水平(0-100)，用于对比短期情绪波动';
COMMENT ON COLUMN health_psy_profiles.created_at IS '档案创建时间';
COMMENT ON COLUMN health_psy_profiles.updated_at IS '档案最后更新时间';


-- ==========================================
-- 2. 每日情绪打卡日记表 (health_psy_daily_moods)
-- 用于追踪用户的日常情绪波动，生成情绪曲线
-- ==========================================
CREATE TABLE health_psy_daily_moods (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        mood_score INT NOT NULL,
                                        primary_emotion VARCHAR(50),
                                        journal_text TEXT,
                                        record_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_psy_daily_moods IS '每日情绪打卡与日志表';
COMMENT ON COLUMN health_psy_daily_moods.id IS '主键ID，自增BIGINT';
COMMENT ON COLUMN health_psy_daily_moods.user_id IS '关联的主用户ID';
COMMENT ON COLUMN health_psy_daily_moods.mood_score IS '情绪分数(1-10分，1为极度抑郁，10为极度亢奋)';
COMMENT ON COLUMN health_psy_daily_moods.primary_emotion IS '主导情绪词（如：焦虑、平静、开心、失落）';
COMMENT ON COLUMN health_psy_daily_moods.journal_text IS '用户记录的情绪日记文本';
COMMENT ON COLUMN health_psy_daily_moods.record_date IS '记录所属的日期';
COMMENT ON COLUMN health_psy_daily_moods.created_at IS '记录创建时间';

-- 创建联合索引加速按日期查询
CREATE INDEX idx_health_psy_daily_moods_user_date ON health_psy_daily_moods(user_id, record_date);


-- ==========================================
-- 3. 心理量表测评记录表 (health_psy_assessments)
-- 用于记录标准的心理测评结果
-- ==========================================
CREATE TABLE health_psy_assessments (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        scale_name VARCHAR(100) NOT NULL,
                                        total_score INT NOT NULL,
                                        severity_level VARCHAR(50),
                                        result_analysis TEXT,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_psy_assessments IS '标准心理量表测评结果记录表';
COMMENT ON COLUMN health_psy_assessments.id IS '主键ID，自增BIGINT';
COMMENT ON COLUMN health_psy_assessments.user_id IS '关联的主用户ID';
COMMENT ON COLUMN health_psy_assessments.scale_name IS '量表名称（如 PHQ-9抑郁筛查, GAD-7焦虑筛查）';
COMMENT ON COLUMN health_psy_assessments.total_score IS '测评总分';
COMMENT ON COLUMN health_psy_assessments.severity_level IS '严重程度级别（如：轻度、中度、重度）';
COMMENT ON COLUMN health_psy_assessments.result_analysis IS 'Agent生成的详细结果分析和建议';
COMMENT ON COLUMN health_psy_assessments.created_at IS '测评完成时间';


-- ==========================================
-- 4. 心理对话记忆表 (health_psy_chat_memories)
-- ==========================================
CREATE TABLE health_psy_chat_memories (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(20) NOT NULL,
                                          content TEXT NOT NULL,
                                          emotion_tags VARCHAR(100),
                                          content_vector VECTOR(1024),
                                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_psy_chat_memories IS '心理陪伴对话历史与记忆表（含向量检索）';
COMMENT ON COLUMN health_psy_chat_memories.id IS '主键ID，自增BIGINT';
COMMENT ON COLUMN health_psy_chat_memories.user_id IS '关联的主用户ID';
COMMENT ON COLUMN health_psy_chat_memories.role IS '角色（如 user, agent）';
COMMENT ON COLUMN health_psy_chat_memories.content IS '对话具体内容或提炼后的记忆摘要';
COMMENT ON COLUMN health_psy_chat_memories.emotion_tags IS '从该段对话中提取的情绪标签';
COMMENT ON COLUMN health_psy_chat_memories.content_vector IS '对话内容的文本向量表示（1024维），用于相似度召回';
COMMENT ON COLUMN health_psy_chat_memories.created_at IS '对话发生或记忆生成的具体时间';

-- 为向量字段创建 HNSW 索引，加速相似度检索 (使用余弦相似度)
CREATE INDEX idx_health_psy_chat_vector ON health_psy_chat_memories USING hnsw (content_vector vector_cosine_ops);


-- ==========================================
-- 5. 心理学知识库表 (health_psy_knowledge_base)
-- ==========================================
CREATE TABLE health_psy_knowledge_base (
                                           id BIGSERIAL PRIMARY KEY,
                                           title VARCHAR(255) NOT NULL,
                                           category VARCHAR(50) NOT NULL,
                                           content TEXT NOT NULL,
                                           content_vector VECTOR(1024),
                                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_psy_knowledge_base IS '心理学专业知识与干预策略库（用于RAG）';
COMMENT ON COLUMN health_psy_knowledge_base.id IS '主键ID，自增BIGINT';
COMMENT ON COLUMN health_psy_knowledge_base.title IS '知识条目或干预策略的标题';
COMMENT ON COLUMN health_psy_knowledge_base.category IS '分类（如：CBT认知行为疗法、正念冥想）';
COMMENT ON COLUMN health_psy_knowledge_base.content IS '详细的干预步骤、文章内容或话术指导';
COMMENT ON COLUMN health_psy_knowledge_base.content_vector IS '知识内容的文本向量表示（1024维），用于匹配用户痛点';
COMMENT ON COLUMN health_psy_knowledge_base.created_at IS '入库时间';
COMMENT ON COLUMN health_psy_knowledge_base.updated_at IS '更新时间';

-- 为知识库向量字段创建 HNSW 索引
CREATE INDEX idx_health_psy_kb_vector ON health_psy_knowledge_base USING hnsw (content_vector vector_cosine_ops);



CREATE TABLE IF NOT EXISTS health_psy_profiles (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   user_id BIGINT UNIQUE NOT NULL,
                                                   mbti_type VARCHAR(20),
                                                   enneagram_type VARCHAR(20),
                                                   baseline_stress_level INTEGER,
                                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_psy_daily_moods (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      user_id BIGINT NOT NULL,
                                                      mood_score INTEGER,
                                                      primary_emotion VARCHAR(50),
                                                      journal_text TEXT,
                                                      record_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_psy_assessments (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      user_id BIGINT NOT NULL,
                                                      scale_name VARCHAR(100) NOT NULL,
                                                      total_score INTEGER,
                                                      severity_level VARCHAR(50),
                                                      result_analysis TEXT,
                                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_psy_chat_memories (
                                                        id BIGSERIAL PRIMARY KEY,
                                                        user_id BIGINT NOT NULL,
                                                        role VARCHAR(20) NOT NULL,
                                                        content TEXT NOT NULL,
                                                        emotion_tags TEXT,
                                                        content_vector VECTOR(1024),
                                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_psy_knowledge_base (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         title VARCHAR(255) NOT NULL,
                                                         category VARCHAR(100),
                                                         content TEXT NOT NULL,
                                                         content_vector VECTOR(1024),
                                                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_risk_flags (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 user_id BIGINT NOT NULL,
                                                 risk_type VARCHAR(100) NOT NULL,
                                                 level VARCHAR(50) NOT NULL,
                                                 reason TEXT NOT NULL,
                                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS health_psy_chat_memories_vector_idx
    ON health_psy_chat_memories USING hnsw (content_vector vector_cosine_ops);

CREATE INDEX IF NOT EXISTS health_psy_knowledge_base_vector_idx
    ON health_psy_knowledge_base USING hnsw (content_vector vector_cosine_ops);




CREATE TABLE public.health_agent_advice_records (
                                                    id BIGSERIAL PRIMARY KEY,
                                                    user_id BIGINT NOT NULL,
                                                    agent_type VARCHAR(64) NOT NULL,
                                                    advice_type VARCHAR(64) NOT NULL,
                                                    title VARCHAR(255),
                                                    content TEXT NOT NULL,
                                                    source_summary TEXT,
                                                    source_snapshot JSONB,
                                                    priority_level VARCHAR(32),
                                                    status VARCHAR(32) DEFAULT 'active',
                                                    valid_until DATE,
                                                    advice_vector vector(1024),
                                                    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.health_agent_advice_records IS '健康Agent建议记录表，保存各类Agent生成的正式建议内容。';
COMMENT ON COLUMN public.health_agent_advice_records.id IS '主键ID';
COMMENT ON COLUMN public.health_agent_advice_records.user_id IS '用户ID';
COMMENT ON COLUMN public.health_agent_advice_records.agent_type IS '生成建议的Agent类型，如health_manager、diet_agent、weight_trend_agent';
COMMENT ON COLUMN public.health_agent_advice_records.advice_type IS '建议类型，如diet、weight、mood、activity';
COMMENT ON COLUMN public.health_agent_advice_records.title IS '建议标题';
COMMENT ON COLUMN public.health_agent_advice_records.content IS '建议正文';
COMMENT ON COLUMN public.health_agent_advice_records.source_summary IS '建议生成依据摘要';
COMMENT ON COLUMN public.health_agent_advice_records.source_snapshot IS '生成建议时的上下文快照，JSON结构';
COMMENT ON COLUMN public.health_agent_advice_records.priority_level IS '建议优先级，如low、medium、high';
COMMENT ON COLUMN public.health_agent_advice_records.status IS '建议状态，如active、completed、expired、cancelled';
COMMENT ON COLUMN public.health_agent_advice_records.valid_until IS '建议有效截止日期';
COMMENT ON COLUMN public.health_agent_advice_records.advice_vector IS '建议内容的向量表示，用于语义检索';
COMMENT ON COLUMN public.health_agent_advice_records.created_at IS '创建时间';
COMMENT ON COLUMN public.health_agent_advice_records.updated_at IS '更新时间';


CREATE TABLE public.health_agent_followup_plans (
                                                    id BIGSERIAL PRIMARY KEY,
                                                    user_id BIGINT NOT NULL,
                                                    plan_type VARCHAR(64) NOT NULL,
                                                    title VARCHAR(255) NOT NULL,
                                                    plan_json JSONB NOT NULL,
                                                    goal_summary TEXT,
                                                    related_advice_id BIGINT,
                                                    status VARCHAR(32) DEFAULT 'active',
                                                    start_date DATE,
                                                    end_date DATE,
                                                    followup_vector vector(1024),
                                                    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    CONSTRAINT fk_health_agent_followup_advice
                                                        FOREIGN KEY (related_advice_id)
                                                            REFERENCES public.health_agent_advice_records(id)
                                                            ON DELETE SET NULL
);

COMMENT ON TABLE public.health_agent_followup_plans IS '健康Agent跟踪计划表，保存7天、14天、30天等结构化执行计划。';
COMMENT ON COLUMN public.health_agent_followup_plans.id IS '主键ID';
COMMENT ON COLUMN public.health_agent_followup_plans.user_id IS '用户ID';
COMMENT ON COLUMN public.health_agent_followup_plans.plan_type IS '计划类型，如weekly_adjustment、fat_loss_plan、mood_support_plan';
COMMENT ON COLUMN public.health_agent_followup_plans.title IS '计划标题';
COMMENT ON COLUMN public.health_agent_followup_plans.plan_json IS '结构化计划内容，JSON格式';
COMMENT ON COLUMN public.health_agent_followup_plans.goal_summary IS '计划目标摘要';
COMMENT ON COLUMN public.health_agent_followup_plans.related_advice_id IS '关联的建议记录ID';
COMMENT ON COLUMN public.health_agent_followup_plans.status IS '计划状态，如active、completed、cancelled、expired';
COMMENT ON COLUMN public.health_agent_followup_plans.start_date IS '计划开始日期';
COMMENT ON COLUMN public.health_agent_followup_plans.end_date IS '计划结束日期';
COMMENT ON COLUMN public.health_agent_followup_plans.followup_vector IS '计划内容的向量表示，用于语义检索';
COMMENT ON COLUMN public.health_agent_followup_plans.created_at IS '创建时间';
COMMENT ON COLUMN public.health_agent_followup_plans.updated_at IS '更新时间';


CREATE TABLE public.health_agent_checkins (
                                              id BIGSERIAL PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              advice_record_id BIGINT,
                                              followup_plan_id BIGINT,
                                              checkin_date DATE NOT NULL,
                                              completion_status VARCHAR(32) DEFAULT 'pending',
                                              adherence_score INTEGER,
                                              effect_score INTEGER,
                                              user_feedback TEXT,
                                              blocker_reason TEXT,
                                              checkin_vector vector(1024),
                                              created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                              CONSTRAINT fk_health_agent_checkins_advice
                                                  FOREIGN KEY (advice_record_id)
                                                      REFERENCES public.health_agent_advice_records(id)
                                                      ON DELETE SET NULL,
                                              CONSTRAINT fk_health_agent_checkins_plan
                                                  FOREIGN KEY (followup_plan_id)
                                                      REFERENCES public.health_agent_followup_plans(id)
                                                      ON DELETE SET NULL
);

COMMENT ON TABLE public.health_agent_checkins IS '健康Agent执行反馈表，记录用户对建议和计划的执行情况与主观反馈。';
COMMENT ON COLUMN public.health_agent_checkins.id IS '主键ID';
COMMENT ON COLUMN public.health_agent_checkins.user_id IS '用户ID';
COMMENT ON COLUMN public.health_agent_checkins.advice_record_id IS '关联的建议记录ID';
COMMENT ON COLUMN public.health_agent_checkins.followup_plan_id IS '关联的跟踪计划ID';
COMMENT ON COLUMN public.health_agent_checkins.checkin_date IS '打卡日期';
COMMENT ON COLUMN public.health_agent_checkins.completion_status IS '执行状态，如pending、done、partial、missed';
COMMENT ON COLUMN public.health_agent_checkins.adherence_score IS '执行度评分，建议1到5';
COMMENT ON COLUMN public.health_agent_checkins.effect_score IS '主观效果评分，建议1到5';
COMMENT ON COLUMN public.health_agent_checkins.user_feedback IS '用户反馈内容';
COMMENT ON COLUMN public.health_agent_checkins.blocker_reason IS '未完成或执行困难的原因';
COMMENT ON COLUMN public.health_agent_checkins.checkin_vector IS '反馈内容的向量表示，用于语义检索';
COMMENT ON COLUMN public.health_agent_checkins.created_at IS '创建时间';
COMMENT ON COLUMN public.health_agent_checkins.updated_at IS '更新时间';


CREATE TABLE public.health_agent_user_preferences (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      user_id BIGINT NOT NULL UNIQUE,
                                                      preferred_diet_style VARCHAR(64),
                                                      disliked_foods TEXT,
                                                      preferred_exercise_types TEXT,
                                                      preferred_support_style VARCHAR(64),
                                                      routine_pattern TEXT,
                                                      motivation_tags TEXT,
                                                      habit_profile JSONB,
                                                      preference_vector vector(1024),
                                                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                      updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.health_agent_user_preferences IS '健康Agent用户长期偏好表，记录饮食、运动、支持方式和习惯画像。';
COMMENT ON COLUMN public.health_agent_user_preferences.id IS '主键ID';
COMMENT ON COLUMN public.health_agent_user_preferences.user_id IS '用户ID，唯一';
COMMENT ON COLUMN public.health_agent_user_preferences.preferred_diet_style IS '偏好的饮食风格，如高蛋白、清淡、低碳';
COMMENT ON COLUMN public.health_agent_user_preferences.disliked_foods IS '不喜欢或忌口食物描述';
COMMENT ON COLUMN public.health_agent_user_preferences.preferred_exercise_types IS '偏好的运动类型描述';
COMMENT ON COLUMN public.health_agent_user_preferences.preferred_support_style IS '偏好的支持风格，如鼓励型、直接型、简洁型';
COMMENT ON COLUMN public.health_agent_user_preferences.routine_pattern IS '作息规律和生活节奏描述';
COMMENT ON COLUMN public.health_agent_user_preferences.motivation_tags IS '用户动机标签，如减脂、改善睡眠、减压';
COMMENT ON COLUMN public.health_agent_user_preferences.habit_profile IS '习惯画像，JSON结构';
COMMENT ON COLUMN public.health_agent_user_preferences.preference_vector IS '偏好画像的向量表示，用于语义检索';
COMMENT ON COLUMN public.health_agent_user_preferences.created_at IS '创建时间';
COMMENT ON COLUMN public.health_agent_user_preferences.updated_at IS '更新时间';


CREATE INDEX idx_health_agent_advice_user_id
    ON public.health_agent_advice_records(user_id);

CREATE INDEX idx_health_agent_advice_agent_type
    ON public.health_agent_advice_records(agent_type);

CREATE INDEX idx_health_agent_advice_status
    ON public.health_agent_advice_records(status);

CREATE INDEX idx_health_agent_followup_user_id
    ON public.health_agent_followup_plans(user_id);

CREATE INDEX idx_health_agent_followup_status
    ON public.health_agent_followup_plans(status);

CREATE INDEX idx_health_agent_checkins_user_id
    ON public.health_agent_checkins(user_id);

CREATE INDEX idx_health_agent_checkins_plan_id
    ON public.health_agent_checkins(followup_plan_id);

