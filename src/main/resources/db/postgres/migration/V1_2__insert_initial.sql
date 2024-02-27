INSERT INTO "customers" ("id", "created_at", "updated_at", "title", "logo", "tax_number", "tax_administration",
                         "mersis_number", "phone_number", "is_dealer")
VALUES ('1ad58199-d546-425c-913d-c783b89b64e1', '2023-12-13 20:43:42.309602', '2023-12-13 20:43:42.309602',
        'Karadeniz İskele', 'karadeniziskele.png', '5090583923', 'Çekirge Vergi Dairesi Müd.', '123456789123', '4448541', true);

INSERT INTO "customers" ("id", "created_at", "updated_at", "title", "logo", "tax_number", "tax_administration",
                         "mersis_number", "phone_number", "is_dealer")
VALUES ('7346ff0e-65aa-442c-a07a-3b0b3651f7d4', '2023-12-14 20:12:37.323683', '2023-12-14 20:12:37.323683',
        'Örnek İnşaat', NULL, '456789123852', 'Küçükyalı', '84576893472039', '05321112233', false);

INSERT INTO "addresses" ("id", "created_at", "updated_at", "name", "details", "district", "city", "country", "zipcode",
                         "description", "customer_id", "is_main")
VALUES ('fb6288b1-8c55-421f-b524-8d7371240617', '2023-12-13 20:40:53.667737', '2023-12-13 20:40:53.667737',
        'Karadeniz İskele Ofis', 'Ürünlü Mah. İzmir yolu Cad. Uzay Sok. No:3/1', 'Nilüfer', 'Bursa', 'Türkiye', '16120',
        'Ürünlü, İzmir yolu Uludağ Üniversitesi Kavşağı', '1ad58199-d546-425c-913d-c783b89b64e1', true);

INSERT INTO "addresses" ("id", "created_at", "updated_at", "name", "details", "district", "city", "country", "zipcode",
                         "description", "customer_id", "is_main")
VALUES ('23762715-e9db-4fcd-8663-d9625cbaafd9', '2023-12-14 20:12:37.246244', '2023-12-14 20:12:37.246244',
        'Örnek İnşaat Ofis', 'Ferhatpaşa Mah. Cadde Sokak', 'Ataşehir', 'İstanbul', 'Türkiye', '34100', 'Ferhatpaşa',
        '7346ff0e-65aa-442c-a07a-3b0b3651f7d4', true);

INSERT INTO "addresses" ("id", "created_at", "updated_at", "name", "details", "district", "city", "country", "zipcode",
                         "description", "is_main", "customer_id")
VALUES ('eac90d51-2ef4-4202-8f69-01f303b4259e', '2024-01-21 22:54:25.004518', '2024-01-21 22:54:25.004518',
        'Örnek Şantiye', 'örnek şantiye', 'Maltepe', 'İstanbul', 'Türkiye', '34852', 'burası şantiye', 'f',
        '7346ff0e-65aa-442c-a07a-3b0b3651f7d4');

INSERT INTO "product_categories" ("id", "created_at", "updated_at", "name", "image")
VALUES ('0fac08b9-9ad2-443b-9b53-e0b5e3ae40d6', '2023-12-14 00:52:34.491375', '2023-12-14 00:52:34.491375',
        'Yatay Eleman', NULL);

INSERT INTO "products" ("id", "created_at", "updated_at", "name", "image", "price", "factor", "unit", "category_id",
                        "customer_id")
VALUES ('caeb60ff-e54a-4948-bba9-1c94b0ea4d74', '2023-12-14 00:58:32.572505', '2023-12-14 00:58:32.572505',
        '2 MT DİKEY ELEMAN 4 FLANŞ', NULL, 10.5, 4.1666, 'MM', '0fac08b9-9ad2-443b-9b53-e0b5e3ae40d6',
        '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "products" ("id", "created_at", "updated_at", "name", "image", "price", "factor", "unit", "category_id",
                        "customer_id")
VALUES ('54160792-9ad3-4d22-b401-bf60331883eb', '2023-12-14 00:58:32.572505', '2023-12-14 00:58:32.572505',
        '2 MT YATAY ELEMAN (GALV.)', NULL, 15, 4.1666, 'MM', '0fac08b9-9ad2-443b-9b53-e0b5e3ae40d6',
        '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "stocks" ("id", "created_at", "updated_at", "total_amount", "available_amount", "product_id", "customer_id")
VALUES ('b1b6b6a0-0b9e-4b0e-8b0a-9b0b9b0b9b0b', '2023-12-14 01:01:02.123456', '2023-12-14 01:01:02.123456', '100',
        '100', 'caeb60ff-e54a-4948-bba9-1c94b0ea4d74', '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "stocks" ("id", "created_at", "updated_at", "total_amount", "available_amount", "product_id", "customer_id")
VALUES ('aaef073c-cc9f-4b75-9a06-b8b731377c82', '2023-12-14 01:01:02.123456', '2023-12-14 01:01:02.123456', '50',
        '50', '54160792-9ad3-4d22-b401-bf60331883eb', '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "stock_history" ("id", "created_at", "action", "amount", "total_amount", "product_id", "stock_id")
VALUES ('74959635-09d1-48bb-82bb-67a18a419453', '2023-12-23 01:53:03.437155', 'INCREASE', 100, 100,
        'caeb60ff-e54a-4948-bba9-1c94b0ea4d74', 'b1b6b6a0-0b9e-4b0e-8b0a-9b0b9b0b9b0b');

INSERT INTO "stock_history" ("id", "created_at", "action", "amount", "total_amount", "product_id", "stock_id")
VALUES ('d14cc303-c9a5-436d-b7ba-4f88f281e857', '2023-12-23 01:53:03.437155', 'INCREASE', 50, 50,
        '54160792-9ad3-4d22-b401-bf60331883eb', 'aaef073c-cc9f-4b75-9a06-b8b731377c82');

INSERT INTO "users" ("id", "created_at", "updated_at", "name", "email", "password", "active", "roles")
VALUES ('5f1dbabd-2392-4b46-a416-649b3a640d53', '2024-01-28 16:37:43.276574', '2024-01-28 16:37:43.276574',
        'user', 'info@karadeniziskele.com', '{bcrypt}$2a$10$xJyPegfXXcRNpzewt3WMS.5rAgMCgduvcE7Ns3NO/Uq5gx1wJfYYS', 't',
        '{CUSTOMER}'),
       ('a4c9d089-ddae-4121-b410-d313bdd9a3f7', '2024-01-28 16:37:43.352835', '2024-01-28 16:37:43.352835',
        'admin', 'admin@admin.com', '{bcrypt}$2a$10$m3V6uz/MDuLUFthQJBBYJ.1Vo72uLsyQM7K2gYJbK8lbHaPDV1yym', 't',
        '{CUSTOMER,DEALER,ADMIN}');

INSERT INTO "customer_preferences" ("id", "created_at", "updated_at", "name", "value", "description", "customer_id")
VALUES ('1b10cda6-1f1b-4e28-9c6e-63a4ad2c1bf8', '2024-01-23 22:35:57.766619', '2024-01-23 22:35:57.766619',
        'payment_calculation_type', 'AREA', 'Kiralama için hesaplama tipi', '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "customer_preferences" ("id", "created_at", "updated_at", "name", "value", "description", "customer_id")
VALUES ('ba4bece9-c113-46d3-977c-e6c9761a1c1b', '2024-01-24 21:47:12.91729', '2024-01-24 21:47:12.91729',
        'special_area_price', '30', 'Ön tanımlı m2 fiyatı', '1ad58199-d546-425c-913d-c783b89b64e1');

INSERT INTO "contracts" ("id", "created_at", "updated_at", "contract_number", "status", "start_at", "end_at",
                         "special_area_price", "seller_customer_id", "taker_customer_id", "user_id", "address_id")
VALUES ('e8c8209f-1857-4160-a98d-799a0232b359', '2024-01-21 23:15:28.992173', '2024-01-21 23:15:28.992173', 'KI2801',
        'PREPARING', '2024-01-21 19:38:05.408', NULL, 30, '1ad58199-d546-425c-913d-c783b89b64e1',
        '7346ff0e-65aa-442c-a07a-3b0b3651f7d4', '5f1dbabd-2392-4b46-a416-649b3a640d53', 'eac90d51-2ef4-4202-8f69-01f303b4259e'),
       ('dde21739-1bc0-43ff-9fed-01cf1a2e3740', '2024-01-21 23:18:21.305428', '2024-01-21 23:18:21.305428', 'KI2801',
        'PREPARING', '2024-01-21 19:38:05.408', NULL, 30, '1ad58199-d546-425c-913d-c783b89b64e1',
        '7346ff0e-65aa-442c-a07a-3b0b3651f7d4', '5f1dbabd-2392-4b46-a416-649b3a640d53', 'eac90d51-2ef4-4202-8f69-01f303b4259e'),
       ('6b372803-22ee-48e4-9f0f-ce449a303eaf', '2024-01-21 23:48:19.735429', '2024-01-21 23:48:19.735429', 'KI2801',
        'ACTIVE', '2024-01-21 19:38:05.408', NULL, 30, '1ad58199-d546-425c-913d-c783b89b64e1',
        '7346ff0e-65aa-442c-a07a-3b0b3651f7d4', '5f1dbabd-2392-4b46-a416-649b3a640d53', 'eac90d51-2ef4-4202-8f69-01f303b4259e');

INSERT INTO "transactions" ("id", "created_at", "updated_at", "receipt_number", "type", "amount", "description", "issue_date", "contract_id", "product_id")
VALUES ('f45f182a-278e-4b14-946a-3d8a038d880c', '2024-01-21 23:15:29.046475', '2024-01-21 23:15:29.046475', 'KRA23-135',
        'OUTBOUND', 150, 'örnek', '2024-01-22 19:38:05.408', 'e8c8209f-1857-4160-a98d-799a0232b359',
        'caeb60ff-e54a-4948-bba9-1c94b0ea4d74'),
       ('55748fe3-5ffe-44e7-90b7-2b380d8f14f2', '2024-01-21 23:15:29.051655', '2024-01-21 23:15:29.051655', 'KRA23-136',
        'INBOUND', 100, 'test', '2024-02-10 19:38:05.408', 'e8c8209f-1857-4160-a98d-799a0232b359',
        'caeb60ff-e54a-4948-bba9-1c94b0ea4d74'),
       ('9d18bd0e-f591-4d79-b055-afde1248043d', '2024-01-21 23:18:21.341743', '2024-01-21 23:18:21.341743', 'KRA23-135',
        'OUTBOUND', 150, 'örnek', '2024-01-23 19:38:05.408', 'dde21739-1bc0-43ff-9fed-01cf1a2e3740',
        '54160792-9ad3-4d22-b401-bf60331883eb'),
       ('3eb1a8dd-bb6d-4d8b-9b72-6a5ec5af876b', '2024-01-21 23:18:21.347334', '2024-01-21 23:18:21.347334', 'KRA23-136',
        'INBOUND', 100, 'test', '2024-02-15 19:38:05.408', 'dde21739-1bc0-43ff-9fed-01cf1a2e3740',
        '54160792-9ad3-4d22-b401-bf60331883eb'),
       ('37508b21-0e18-46b4-8e77-1d121bd02601', '2024-01-21 23:48:19.795801', '2024-01-21 23:48:19.795801', 'KRA23-135',
        'OUTBOUND', 150, 'örnek', '2024-01-24 19:38:05.408', '6b372803-22ee-48e4-9f0f-ce449a303eaf',
        'caeb60ff-e54a-4948-bba9-1c94b0ea4d74'),
       ('0457d4a0-a241-4ef3-a771-41c2afe3ca31', '2024-01-21 23:48:19.800917', '2024-01-21 23:48:19.800917', 'KRA23-136',
        'INBOUND', 100, 'test', '2024-02-19 19:38:05.408', '6b372803-22ee-48e4-9f0f-ce449a303eaf',
        'caeb60ff-e54a-4948-bba9-1c94b0ea4d74');

INSERT INTO "notes" ("id", "created_at", "updated_at", "text", "user_id")
VALUES ('87956ed9-6c11-42a5-9736-cb03953f7525', '2024-01-21 23:48:19.805788', '2024-01-21 23:48:19.805788', 'test note',
        '5f1dbabd-2392-4b46-a416-649b3a640d53'),
       ('0b2693ac-0996-450e-908a-ea51912cc621', '2024-01-23 22:23:45.228073', '2024-01-23 22:23:45.228073', 'test2note',
        '5f1dbabd-2392-4b46-a416-649b3a640d53'),
       ('cb444bd0-02d9-4a9c-9a26-ff0d17015f9b', '2024-01-23 22:27:42.531986', '2024-01-23 22:27:42.531986',
        'test2note3', '5f1dbabd-2392-4b46-a416-649b3a640d53'),
       ('e511d78c-4932-49b6-9980-2946dca65053', '2024-01-23 22:27:47.668952', '2024-01-23 22:27:47.668952',
        'test2note4', '5f1dbabd-2392-4b46-a416-649b3a640d53');

INSERT INTO "contract_notes" ("contract_id", "note_id")
VALUES ('6b372803-22ee-48e4-9f0f-ce449a303eaf', '87956ed9-6c11-42a5-9736-cb03953f7525'),
       ('6b372803-22ee-48e4-9f0f-ce449a303eaf', '0b2693ac-0996-450e-908a-ea51912cc621'),
       ('6b372803-22ee-48e4-9f0f-ce449a303eaf', 'cb444bd0-02d9-4a9c-9a26-ff0d17015f9b'),
       ('6b372803-22ee-48e4-9f0f-ce449a303eaf', 'e511d78c-4932-49b6-9980-2946dca65053');

INSERT INTO "customer_users" ("customer_id", "user_id")
VALUES ('1ad58199-d546-425c-913d-c783b89b64e1', '5f1dbabd-2392-4b46-a416-649b3a640d53');