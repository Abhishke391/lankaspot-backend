-- Additive migration. Existing advertisement, payment, tier, approval and ledger
-- rows are retained; legacy content columns remain available during cutover.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE advertisements
    ADD COLUMN IF NOT EXISTS advertisement_ref UUID,
    ADD COLUMN IF NOT EXISTS current_tier_id BIGINT,
    ADD COLUMN IF NOT EXISTS queue_number BIGINT,
    ADD COLUMN IF NOT EXISTS sort_order INTEGER,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE advertisements SET advertisement_ref = gen_random_uuid()
WHERE advertisement_ref IS NULL;
UPDATE advertisements SET queue_number = id WHERE queue_number IS NULL;
UPDATE advertisements SET sort_order = 0 WHERE sort_order IS NULL;
UPDATE advertisements SET created_at = COALESCE(published_at, CURRENT_TIMESTAMP)
WHERE created_at IS NULL;
UPDATE advertisements SET updated_at = created_at WHERE updated_at IS NULL;

ALTER TABLE advertisements
    ALTER COLUMN advertisement_ref SET NOT NULL,
    ALTER COLUMN queue_number SET NOT NULL,
    ALTER COLUMN sort_order SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_advertisements_advertisement_ref
    ON advertisements(advertisement_ref);
ALTER TABLE advertisements
    ADD CONSTRAINT fk_advertisement_current_tier
    FOREIGN KEY (current_tier_id) REFERENCES advertisement_tiers(id);

CREATE TABLE IF NOT EXISTS ad_policies (
    id BIGSERIAL PRIMARY KEY,
    price NUMERIC(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'LKR',
    discount_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    tier_id BIGINT NOT NULL REFERENCES advertisement_tiers(id),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    advertisement_id BIGINT REFERENCES advertisements(id),
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ad_policy_advertisement ON ad_policies(advertisement_id);
CREATE INDEX IF NOT EXISTS idx_ad_policy_status ON ad_policies(status);

CREATE TABLE IF NOT EXISTS ad_quotes (
    id BIGSERIAL PRIMARY KEY,
    advertisement_ref UUID NOT NULL,
    version INTEGER NOT NULL,
    advertiser_id BIGINT,
    category_id BIGINT,
    category_name VARCHAR(100),
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL,
    description TEXT,
    price NUMERIC(15,2),
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    area VARCHAR(100),
    address TEXT,
    media_references TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_ad_quote_version UNIQUE (advertisement_ref, version)
);
CREATE INDEX IF NOT EXISTS idx_ad_quote_ad_ref ON ad_quotes(advertisement_ref);

CREATE TABLE IF NOT EXISTS entity_mappings (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL UNIQUE REFERENCES ad_quotes(id),
    policy_id BIGINT NOT NULL REFERENCES ad_policies(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_entity_mapping_policy ON entity_mappings(policy_id);

ALTER TABLE advertisement_approvals
    ALTER COLUMN advertisement_id DROP NOT NULL,
    ADD COLUMN IF NOT EXISTS policy_id BIGINT,
    ADD COLUMN IF NOT EXISTS decision_at TIMESTAMP;
ALTER TABLE advertisement_approvals
    ADD CONSTRAINT fk_advertisement_approval_policy
    FOREIGN KEY (policy_id) REFERENCES ad_policies(id);

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS policy_id BIGINT;
ALTER TABLE payments
    ADD CONSTRAINT fk_payment_policy
    FOREIGN KEY (policy_id) REFERENCES ad_policies(id);

ALTER TABLE ledger_transactions
    ADD COLUMN IF NOT EXISTS policy_id BIGINT;
ALTER TABLE ledger_transactions
    ADD CONSTRAINT fk_ledger_transaction_policy
    FOREIGN KEY (policy_id) REFERENCES ad_policies(id);

CREATE TABLE IF NOT EXISTS ad_histories (
    id BIGSERIAL PRIMARY KEY,
    advertisement_ref UUID NOT NULL,
    old_quote_id BIGINT NOT NULL REFERENCES ad_quotes(id),
    new_quote_id BIGINT NOT NULL REFERENCES ad_quotes(id),
    action_type VARCHAR(30) NOT NULL,
    actor_id BIGINT REFERENCES users(id),
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ad_history_ad_ref ON ad_histories(advertisement_ref);

CREATE TABLE IF NOT EXISTS policy_histories (
    id BIGSERIAL PRIMARY KEY,
    predecessor_policy_id BIGINT NOT NULL REFERENCES ad_policies(id),
    successor_policy_id BIGINT NOT NULL REFERENCES ad_policies(id),
    action_type VARCHAR(30) NOT NULL,
    actor_id BIGINT REFERENCES users(id),
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
