INSERT INTO categories (is_active, name, created_at, updated_at)
VALUES
(TRUE, 'GPUs', NOW(), NOW()),
(TRUE, 'CPUs', NOW(), NOW());

INSERT INTO products
(
  category_id,
  name,
  description,
  price,
  stock,
  is_active,
  sku,
  brand,
  image,
  created_at, 
  updated_at
)
VALUES
(
  1,
  'RTX 4060 Ti',
  'MSI GeForce RTX 4060 Ti 8GB GDDR6 Ventus 3X OC',
  799.99,
  10,
  TRUE,
  'NV-RTX-4060-TI',
  'NVIDIA',
  '/images/4060ti.jpeg',
  NOW(), 
  NOW()
),
(
  1,
  'RTX 5060',
  'Gigabyte GeForce RTX 5060 8GB GDDR7 Windforce OC',
  299.99,
  25,
  TRUE,
  'NV-RTX-5060',
  'NVIDIA',
  '/images/5060.jpeg',
  NOW(), 
  NOW()
),
(1, 
 'RTX 5070',
 'Asus GeForce RTX 5070 12GB GDDR7 Prime OC',
 579.99,
 8,
 TRUE,
 'NV-RTX-5070',
 'NVIDIA',
 '/images/5070.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RTX 4060',
 'MSI GeForce RTX 4060 8GB GDDR6 VENTUS 2X WHITE OC',
 399.99,
 20,
 TRUE,
 'NV-RTX-4060',
 'NVIDIA',
 '/images/4060.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RTX 4080',
 'Gigabyte GeForce RTX 4080 16GB GDDR6X Aero OC',
 2399.99,
 3,
 TRUE,
 'NV-RTX-4080',
 'NVIDIA',
 '/images/4080.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RTX 5090',
 'PNY GeForce RTX 5090 32GB GDDR7 Overclocked Triple Fan',
 4999.99,
 2,
 TRUE,
 'NV-RTX-5090',
 'NVIDIA',
 '/images/5090.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 9070',
 'PowerColor Radeon RX 9070 16GB GDDR6 Red Devil OC',
 799.99,
 12,
 TRUE,
 'AMD-RX-9070',
 'AMD',
 '/images/9070.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 9060',
 'Gigabyte Radeon RX 9060 XT 16GB GDDR6 GAMING OC',
 349.99,
 25,
 TRUE,
 'AMD-RX-9060',
 'AMD',
 '/images/9060.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 7600',
 'Gigabyte Radeon RX 7600 8GB GDDR6 GAMING OC',
 199.99,
 20,
 TRUE,
 'AMD-RX-7600',
 'AMD',
 '/images/7600.jpeg',
 NOW(), 
 NOW()
),
(
  1,
  'RX 9070 XT',
  'Sapphire Radeon RX 9070 XT 16GB GDDR6 Pure',
  699.99,
  10,
  TRUE,
  'AMD-RX-9070-XT',
  'AMD',
  '/images/9070xt.jpeg',
  NOW(), 
  NOW()
),
(
  1,
  'RX 9060 XT',
  'Sapphire Radeon RX 9060 XT 16GB GDDR6 Pure',
  399.99,
  25,
  TRUE,
  'AMD-RX-9060-XT',
  'AMD',
  '/images/9060xt.jpeg',
  NOW(), 
  NOW()
),
(1, 
 'RX 7900 XT',
 'ASRock Radeon RX 7900 XT 20GB GDDR6 Phantom Gaming OC',
 999.99,
 10,
 TRUE,
 'AMD-RX-7900-XT',
 'AMD',
 '/images/7900xt.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 7800 XT',
 'ASRock Radeon RX 7800 XT 16GB GDDR6 Steel Legend OC',
 989.99,
 10,
 TRUE,
 'AMD-RX-7800-XT',
 'AMD',
 '/images/7800xt.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 560',
 'Biostar Radeon RX 560 4GB GDDR5',
 119.99,
 4,
 TRUE,
 'AMD-RX-560',
 'AMD',
 '/images/560.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 6800 XT',
 'PowerColor Radeon RX 6800 XT 16GB GDDR6 Red Dragon',
 999.99,
 12,
 TRUE,
 'AMD-RX-6800-XT',
 'AMD',
 '/images/6800xt.jpeg',
 NOW(), 
 NOW()
),
(1, 
 'RX 7700 XT',
 'PowerColor Radeon RX 7700 XT 12GB GDDR6 Hellhound',
 699.99,
 10,
 TRUE,
 'AMD-RX-7700-XT',
 'AMD',
 '/images/7700xt.jpeg',
 NOW(), 
 NOW()
),
(
  2,
  'Ryzen 7 5800X',
  'AMD Ryzen 7 5800X 3.8GHz',
  149.99,
  10,
  TRUE,
  'AMD-R7-5800X',
  'AMD',
  '/images/r75800x.jpeg',
  NOW(), 
  NOW()
),
(
  2,
  'Ryzen 7 7800X3D',
  'AMD Ryzen 7 7800X3D 4.2GHz',
  289.99,
  5,
  TRUE,
  'AMD-R7-7800X3D',
  'AMD',
  '/images/r77800x3d.jpeg',
  NOW(), 
  NOW()
),
(2, 
 'Ryzen 3 4100',
 'AMD Ryzen 3 4100 3.8GHz',
 39.99,
 8,
 TRUE,
 'AMD-R3-4100',
 'AMD',
 '/images/r34100.jpeg',
 NOW(), 
 NOW()
),
(2, 
 'Ryzen 5 7600X',
 'AMD Ryzen 5 7600X 4.7GHz',
 169.99,
 20,
 TRUE,
 'AMD-R5-7600X',
 'AMD',
 '/images/r57600x.jpeg',
 NOW(), 
 NOW()
),
(2, 
 'Ryzen 5 5600G',
 'AMD Ryzen 5 5600G 3.9GHz',
 179.99,
 30,
 TRUE,
 'AMD-R5-5600G',
 'AMD',
 '/images/r55600g.jpeg',
 NOW(), 
 NOW()
),
(2, 
 'i7-14700K',
 'Intel Core i7-14700K 2.5GHz',
 349.99,
 10,
 TRUE,
 'INTEL-I7-14700K',
 'INTEL',
 '/images/i714700K.jpeg',
 NOW(), 
 NOW()
),
(2, 
 'i5-14400F',
 'Intel Core i5-14400F 1.8GHz',
 179.99,
 10,
 TRUE,
 'INTEL-I5-14400F',
 'INTEL',
 '/images/i514400F.jpeg',
 NOW(), 
 NOW()
);

INSERT INTO product_specs
(
  product_id,
  name,
  value,
  created_at, 
  updated_at
)
VALUES
(
  (SELECT id FROM products WHERE sku = 'NV-RTX-4060-TI'),
  'VRAM',
  '8GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'NV-RTX-5060'),
  'VRAM',
  '8GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'NV-RTX-5070'),
  'VRAM',
  '12GB',
  NOW(), 
  NOW()
),

(
  (SELECT id FROM products WHERE sku = 'NV-RTX-4060'),
  'VRAM',
  '8GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'NV-RTX-4080'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'NV-RTX-5090'),
  'VRAM',
  '32GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-9070'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-9060'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-7600'),
  'VRAM',
  '8GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-9070-XT'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-9060-XT'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-7900-XT'),
  'VRAM',
  '20GB',
  NOW(), 
  NOW()
),

(
  (SELECT id FROM products WHERE sku = 'AMD-RX-7800-XT'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-560'),
  'VRAM',
  '4GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-6800-XT'),
  'VRAM',
  '16GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-RX-7700-XT'),
  'VRAM',
  '12GB',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R7-5800X'),
  'Cors',
  '8',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R7-5800X'),
  'Threads',
  '16',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R7-7800X3D'),
  'Cors',
  '8',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R7-7800X3D'),
  'Threads',
  '16',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R3-4100'),
  'Cors',
  '4',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R3-4100'),
  'Threads',
  '8',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R5-7600X'),
  'Cors',
  '6',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R5-7600X'),
  'Threads',
  '12',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R5-5600G'),
  'Cors',
  '6',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'AMD-R5-5600G'),
  'Threads',
  '12',
  NOW(), 
  NOW()
),(
  (SELECT id FROM products WHERE sku = 'INTEL-I7-14700K'),
  'Cors',
  '20',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'INTEL-I7-14700K'),
  'Threads',
  '28',
  NOW(), 
  NOW()
),(
  (SELECT id FROM products WHERE sku = 'INTEL-I5-14400F'),
  'Cors',
  '10',
  NOW(), 
  NOW()
),
(
  (SELECT id FROM products WHERE sku = 'INTEL-I5-14400F'),
  'Threads',
  '16',
  NOW(), 
  NOW()
);
