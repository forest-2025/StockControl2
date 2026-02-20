INSERT INTO m_user
(
   id,
   email_address,
   password,
   family_name,
   first_name,
   employee_number,
   is_admin,
   is_deleted,
   register_date_time,
   update_date_time
)
VALUES
(
   '1',
   'yama@company.co.jp',
   '$2a$10$YgFiI/AIXXj3wm40TquMreQL7PYGqccgV5YTST6bJvM5tXqABfpze',
   'やま',
   'のぼる',
   'E00001',
   '1',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
),
(
'2',
   'umi@company.co.jp',
   '$2a$10$aSuzDDvaISzuIiwQ7G5Hi.YuRVXtInEkI4q2GlY4dr95Qr2MrRW8y',
   'うみ',
   'わたる',
   'E00002',
   '0',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
);
INSERT INTO m_supplier
(
   id,
   name,
   furigana,
   is_deleted,
   register_date_time,
   update_date_time
)
VALUES
(
   '1',
   '株式会社〇〇',
   'かぶしきがいしゃまるまる',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
);
INSERT INTO m_customer
(
   id,
   name,
   furigana,
   is_deleted,
   register_date_time,
   update_date_time
)
VALUES
(
   '1',
   '株式会社△△',
   'かぶしきがいしゃさんかくさんかく',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
);
INSERT INTO m_product
(
   id,
   name,
   number,
   supplier_id,
   image,
   is_deleted,
   register_date_time,
   update_date_time
)
VALUES
(
   '1',
   'キャップ',
   'H25001',
   '1',
   '243526b6-74e8-42b9-9923-e43d1c5fc099.jpg',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
);
INSERT INTO t_stock
(
   id,
   product_id,
   stock_quantity,
   register_date_time,
   update_date_time
)
VALUES
(
   '1',
   '1',
   '0',
   '2025-10-11 00:00:00',
   '2026-02-13 00:00:00'
);