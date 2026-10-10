ALTER TABLE ad_quotes ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE ad_policies ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE ad_histories ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE policy_histories ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE entity_mappings ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'USER';

CREATE TABLE IF NOT EXISTS subscription_plans (
 id BIGSERIAL PRIMARY KEY, code VARCHAR(50) NOT NULL UNIQUE, name VARCHAR(100) NOT NULL,
 price NUMERIC(15,2) NOT NULL, currency VARCHAR(3) NOT NULL DEFAULT 'LKR',
 billing_period VARCHAR(20) NOT NULL, advertisement_allowance INTEGER NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', validity_days INTEGER NOT NULL DEFAULT 30,
 reset_policy VARCHAR(20) NOT NULL DEFAULT 'BILLING_PERIOD', allocation_mode VARCHAR(20) NOT NULL DEFAULT 'SHARED',
 description TEXT, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS user_subscriptions (
 id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES users(id),
 plan_id BIGINT NOT NULL REFERENCES subscription_plans(id), status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
 next_renewal_at TIMESTAMP NOT NULL, period_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 period_end TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 purchased_price NUMERIC(15,2) NOT NULL DEFAULT 0, purchased_currency VARCHAR(3) NOT NULL DEFAULT 'LKR',
 purchased_allowance INTEGER NOT NULL DEFAULT 0, auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
 approval_required BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 cancelled_at TIMESTAMP, cancellation_reason TEXT
);
CREATE INDEX IF NOT EXISTS idx_subscription_due ON user_subscriptions(status,next_renewal_at);
CREATE TABLE IF NOT EXISTS subscription_allowances (
 id BIGSERIAL PRIMARY KEY, subscription_id BIGINT NOT NULL UNIQUE REFERENCES user_subscriptions(id),
 total INTEGER NOT NULL, reserved INTEGER NOT NULL DEFAULT 0, consumed INTEGER NOT NULL DEFAULT 0, version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS subscription_plan_tier_allowances (
 id BIGSERIAL PRIMARY KEY, plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
 tier_id BIGINT NOT NULL REFERENCES advertisement_tiers(id), allowance INTEGER NOT NULL,
 allowance_period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
 CONSTRAINT uq_plan_tier_allowance UNIQUE(plan_id,tier_id)
);
CREATE TABLE IF NOT EXISTS allowance_reservations (
 id BIGSERIAL PRIMARY KEY, subscription_id BIGINT NOT NULL REFERENCES user_subscriptions(id),
 idempotency_key VARCHAR(150) NOT NULL, quantity INTEGER NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT uq_allowance_idempotency UNIQUE(subscription_id,idempotency_key)
);
CREATE TABLE IF NOT EXISTS subscription_renewals (
 id BIGSERIAL PRIMARY KEY, subscription_id BIGINT NOT NULL REFERENCES user_subscriptions(id),
 period_start TIMESTAMP NOT NULL, period_end TIMESTAMP NOT NULL, status VARCHAR(20) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 source VARCHAR(20) NOT NULL DEFAULT 'SCHEDULER',
 CONSTRAINT uq_subscription_renewal_period UNIQUE(subscription_id,period_start)
);
CREATE TABLE IF NOT EXISTS subscription_allowance_usage (
 id BIGSERIAL PRIMARY KEY, subscription_id BIGINT NOT NULL REFERENCES user_subscriptions(id),
 tier_id BIGINT NOT NULL REFERENCES advertisement_tiers(id), ad_policy_id BIGINT REFERENCES ad_policies(id),
 period_start TIMESTAMP NOT NULL, period_end TIMESTAMP NOT NULL, quantity INTEGER NOT NULL DEFAULT 1,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 status VARCHAR(20) NOT NULL DEFAULT 'RESERVED', idempotency_key VARCHAR(150) NOT NULL,
 CONSTRAINT uq_allowance_usage_key UNIQUE(subscription_id,idempotency_key)
);
