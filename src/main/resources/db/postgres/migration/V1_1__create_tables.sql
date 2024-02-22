CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists addresses
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    name        VARCHAR(255) NOT NULL,
    details     TEXT         NOT NULL,
    district    VARCHAR(255) NOT NULL,
    city        VARCHAR(255) NOT NULL,
    country     VARCHAR(255) NOT NULL,
    zipcode     VARCHAR(255),
    description TEXT,
    is_main     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE if not exists customers
(
    id                 UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    title              VARCHAR(255) NOT NULL,
    logo               VARCHAR(255),
    tax_number         VARCHAR(255) NOT NULL,
    tax_administration VARCHAR(255),
    mersis_number      VARCHAR(255),
    phone_number       VARCHAR(255),
    is_dealer          BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT tax_number_unique UNIQUE (tax_number)
);

ALTER TABLE addresses
    ADD COLUMN if not exists customer_id UUID NOT NULL REFERENCES customers (id) ON DELETE CASCADE;

CREATE TABLE if not exists customer_preferences
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    name        VARCHAR(255) NOT NULL,
    value       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    customer_id UUID         NOT NULL REFERENCES customers (id)
);

CREATE TABLE if not exists product_categories
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    name       VARCHAR(255) NOT NULL,
    image      VARCHAR(255)
);

CREATE TABLE if not exists products
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    name        VARCHAR(255) NOT NULL,
    image       VARCHAR(255),
    price       DECIMAL      NOT NULL,
    factor      DECIMAL      NOT NULL,
    unit        VARCHAR(255) CHECK (unit IN ('MM', 'CM', 'M', 'G', 'KG', 'PIECE')),
    category_id UUID         NOT NULL REFERENCES product_categories (id),
    customer_id UUID         NOT NULL REFERENCES customers (id)
);

CREATE TABLE if not exists product_history
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP    NOT NULL,
    name        VARCHAR(255) NOT NULL,
    image       VARCHAR(255),
    price       DECIMAL      NOT NULL,
    factor      DECIMAL      NOT NULL,
    category_id UUID         NOT NULL REFERENCES product_categories (id),
    product_id  UUID         NOT NULL REFERENCES products (id)
);

CREATE TABLE if not exists stocks
(
    id               UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    total_amount     DECIMAL   NOT NULL,
    available_amount DECIMAL   NOT NULL,
    product_id       UUID      NOT NULL REFERENCES products (id) ON DELETE CASCADE UNIQUE,
    customer_id      UUID      NOT NULL REFERENCES customers (id)
);

CREATE TABLE if not exists stock_history
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at   TIMESTAMP NOT NULL,
    action       VARCHAR(255) CHECK (action IN ('INCREASE', 'DECREASE')),
    amount       DECIMAL   NOT NULL,
    total_amount DECIMAL   NOT NULL,
    product_id   UUID      NOT NULL REFERENCES products (id),
    stock_id     UUID      NOT NULL REFERENCES stocks (id)
);

CREATE TABLE if not exists users
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    roles      TEXT[]
);

CREATE TABLE if not exists customer_users
(
    customer_id UUID NOT NULL REFERENCES customers (id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE if not exists contracts
(
    id                 UUID PRIMARY KEY                                                                    DEFAULT uuid_generate_v4(),
    created_at         TIMESTAMP                                                                  NOT NULL,
    updated_at         TIMESTAMP                                                                  NOT NULL DEFAULT NOW(),
    contract_number    VARCHAR(255)                                                               NOT NULL,
    status             VARCHAR(255) CHECK (status IN ('PREPARING', 'ACTIVE', 'CANCELED', 'DONE')) NOT NULL DEFAULT 'PREPARING',
    start_at           TIMESTAMP                                                                  NOT NULL,
    end_at             TIMESTAMP,
    special_area_price DECIMAL,
    seller_customer_id UUID                                                                       NOT NULL REFERENCES customers (id),
    taker_customer_id  UUID                                                                       NOT NULL REFERENCES customers (id),
    user_id            UUID                                                                       NOT NULL REFERENCES users (id),
    address_id         UUID                                                                       NOT NULL REFERENCES addresses (id)
);

CREATE TABLE if not exists transactions
(
    id             UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    receipt_number VARCHAR(255),
    type           VARCHAR(255) CHECK (type IN ('INBOUND', 'OUTBOUND')),
    amount         DECIMAL   NOT NULL,
    description    VARCHAR(255),
    issue_date     TIMESTAMP NOT NULL,
    contract_id    UUID      NOT NULL REFERENCES contracts (id) ON DELETE CASCADE,
    product_id     UUID      NOT NULL REFERENCES products (id)
);

CREATE TABLE if not exists notes
(
    id         UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    text       TEXT      NOT NULL,
    user_id    UUID      NOT NULL REFERENCES users (id)
);

CREATE TABLE if not exists contract_notes
(
    contract_id UUID NOT NULL REFERENCES contracts (id) ON DELETE CASCADE,
    note_id     UUID NOT NULL REFERENCES notes (id) ON DELETE CASCADE
);

CREATE TABLE if not exists transports
(
    id                   UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    description          VARCHAR(255) NOT NULL,
    transport_type       VARCHAR(255) CHECK (transport_type IN ('INBOUND', 'OUTBOUND')),
    start_at             TIMESTAMP    NOT NULL,
    end_at               TIMESTAMP    NOT NULL,
    sender_customer_id   UUID         NOT NULL REFERENCES customers (id),
    receiver_customer_id UUID         NOT NULL REFERENCES customers (id),
    contract_id          UUID         NOT NULL REFERENCES contracts (id)
);

CREATE TABLE if not exists waybills
(
    id                   UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    serial_number        VARCHAR(255) NOT NULL,
    description          VARCHAR(255),
    sender_customer_id   UUID         NOT NULL REFERENCES customers (id),
    receiver_customer_id UUID         NOT NULL REFERENCES customers (id),
    contract_id          UUID         NOT NULL REFERENCES contracts (id),
    receiver_address_id  UUID         NOT NULL REFERENCES addresses (id),
    sender_address_id    UUID         NOT NULL REFERENCES addresses (id)
);

CREATE TABLE if not exists system_settings
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    key        VARCHAR(255) NOT NULL,
    value      VARCHAR(255)
);

CREATE TABLE if not exists allowances
(
    id          UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    modified_by VARCHAR(255) NOT NULL,
    amount      DECIMAL,
    is_paid     BOOLEAN      NOT NULL DEFAULT FALSE,
    description VARCHAR(255),
    start_time  TIMESTAMP    NOT NULL DEFAULT NOW(),
    end_time    TIMESTAMP    NOT NULL DEFAULT NOW(),
    contract_id UUID         NOT NULL REFERENCES contracts (id)
);

CREATE TABLE if not exists discounts
(
    id            UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    modified_by   VARCHAR(255) NOT NULL,
    amount        DECIMAL,
    before_amount DECIMAL,
    after_amount  DECIMAL,
    description   VARCHAR(255),
    allowance_id  UUID         NOT NULL REFERENCES allowances (id)
);

CREATE TABLE if not exists invoices
(
    id           UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    serial       VARCHAR(255) NOT NULL,
    amount       DECIMAL      NOT NULL,
    description  VARCHAR(255),
    allowance_id UUID         NOT NULL REFERENCES allowances (id)
);

-- SQL KEY WORDS: https://www.postgresql.org/docs/current/sql-keywords-appendix.html