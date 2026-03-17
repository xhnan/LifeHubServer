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
    content_vector VECTOR(1536),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_psy_knowledge_base (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    content TEXT NOT NULL,
    content_vector VECTOR(1536),
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
