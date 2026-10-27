CREATE TABLE IF NOT EXISTS product_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    text TEXT NOT NULL,
    rating INTEGER NOT NULL,
    likes_count INTEGER NOT NULL DEFAULT 0,
    dislikes_count INTEGER NOT NULL DEFAULT 0,
    ai_summary TEXT,
    CONSTRAINT fk_product_reviews_user FOREIGN KEY (user_id) REFERENCES user_details(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_reviews_goods FOREIGN KEY (product_id) REFERENCES goods(id) ON DELETE CASCADE,
    CONSTRAINT uk_product_reviews_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_product_reviews_product_id ON product_reviews(product_id);

CREATE TABLE IF NOT EXISTS product_reviews_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    is_like BOOLEAN NOT NULL,
    CONSTRAINT fk_product_reviews_likes_review FOREIGN KEY (review_id) REFERENCES product_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_reviews_likes_goods FOREIGN KEY (product_id) REFERENCES goods(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_reviews_likes_user FOREIGN KEY (user_id) REFERENCES user_details(id) ON DELETE CASCADE,
    CONSTRAINT uk_product_reviews_likes_user_review UNIQUE (user_id, review_id)
);

CREATE INDEX IF NOT EXISTS idx_product_reviews_likes_review_id ON product_reviews_likes(review_id);
