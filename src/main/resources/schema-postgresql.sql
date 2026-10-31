CREATE TABLE IF NOT EXISTS outbox_events (
    event_id varchar(36) PRIMARY KEY,
    routing_key varchar(160) NOT NULL,
    event_type varchar(255) NOT NULL,
    payload text NOT NULL,
    occurred_at bigint NOT NULL,
    attempts integer NOT NULL DEFAULT 0,
    next_attempt bigint NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS outbox_due_idx ON outbox_events(next_attempt, occurred_at);
CREATE TABLE IF NOT EXISTS processed_events (
    event_id varchar(255) PRIMARY KEY,
    processed_at timestamptz NOT NULL DEFAULT now()
);
