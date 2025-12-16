INSERT INTO categories (is_active, name, created_at, updated_at)
VALUES
  (TRUE, 'GPUs', NOW(), NOW())
AS new
ON DUPLICATE KEY UPDATE
  is_active = new.is_active,
  updated_at = new.updated_at;
INSERT INTO products
(
  category_id, name, description, price, stock, is_active, sku, brand, image, created_at, updated_at
)
VALUES
  (1,'RTX 4060 Ti','MSI GeForce RTX 4060 Ti 8GB GDDR6 Ventus 3X OC',599.99,10,TRUE,'NV-RTX-4060-TI','NVIDIA','/images/4060ti.jpeg',NOW(),NOW()),
  (1,'RTX 5060','Gigabyte GeForce RTX 5060 8GB GDDR7 Windforce OC',299.99,25,TRUE,'NV-RTX-5060','NVIDIA','/images/5060.jpeg',NOW(),NOW()),
  (1,'RTX 5070','Asus GeForce RTX 5070 12GB GDDR7 Prime OC',579.99,8,TRUE,'NV-RTX-5070','NVIDIA','/images/5070.jpeg',NOW(),NOW()),
  (1,'RTX 4060','MSI GeForce RTX 4060 8GB GDDR6 VENTUS 2X WHITE OC',399.99,20,TRUE,'NV-RTX-4060','NVIDIA','/images/4060.jpeg',NOW(),NOW()),
  (1,'RTX 4080','Gigabyte GeForce RTX 4080 16GB GDDR6X Aero OC',1399.99,3,TRUE,'NV-RTX-4080','NVIDIA','/images/4080.jpeg',NOW(),NOW()),
  (1,'RTX 5090','PNY GeForce RTX 5090 32GB GDDR7 Overclocked Triple Fan',2999.99,2,TRUE,'NV-RTX-5090','NVIDIA','/images/5090.jpeg',NOW(),NOW()),
  (1,'RX 9070','PowerColor Radeon RX 9070 16GB GDDR6 Red Devil OC',499.99,12,TRUE,'AMD-RX-9070','AMD','/images/9070.jpeg',NOW(),NOW()),
  (1,'RX 9060','Gigabyte Radeon RX 9060 XT 16GB GDDR6 GAMING OC',349.99,25,TRUE,'AMD-RX-9060','AMD','/images/9060.jpeg',NOW(),NOW()),
  (1,'RX 7600','Gigabyte Radeon RX 7600 8GB GDDR6 GAMING OC',199.99,20,TRUE,'AMD-RX-7600','AMD','/images/7600.jpeg',NOW(),NOW()),
  (1,'RX 9070 XT','Sapphire Radeon RX 9070 XT 16GB GDDR6 Pure',499.99,10,TRUE,'AMD-RX-9070-XT','AMD','/images/9070xt.jpeg',NOW(),NOW()),
  (1,'RX 9060 XT','Sapphire Radeon RX 9060 XT 16GB GDDR6 Pure',399.99,25,TRUE,'AMD-RX-9060-XT','AMD','/images/9060xt.jpeg',NOW(),NOW()),
  (1,'RX 7900 XT','ASRock Radeon RX 7900 XT 20GB GDDR6 Phantom Gaming OC',999.99,10,TRUE,'AMD-RX-7900-XT','AMD','/images/7900xt.jpeg',NOW(),NOW()),
  (1,'RX 7800 XT','ASRock Radeon RX 7800 XT 16GB GDDR6 Steel Legend OC',989.99,10,TRUE,'AMD-RX-7800-XT','AMD','/images/7800xt.jpeg',NOW(),NOW()),
  (1,'RX 560','Biostar Radeon RX 560 4GB GDDR5',119.99,4,TRUE,'AMD-RX-560','AMD','/images/560.jpeg',NOW(),NOW()),
  (1,'RX 6800 XT','PowerColor Radeon RX 6800 XT 16GB GDDR6 Red Dragon',799.99,12,TRUE,'AMD-RX-6800-XT','AMD','/images/6800xt.jpeg',NOW(),NOW()),
  (1,'RX 7700 XT','PowerColor Radeon RX 7700 XT 12GB GDDR6 Hellhound',699.99,10,TRUE,'AMD-RX-7700-XT','AMD','/images/7700xt.jpeg',NOW(),NOW()),
  (1,'RTX 3050','Gigabyte GeForce RTX 3050 6GB Windforce OC v2',199.99,10,TRUE,'NV-RTX-3050','NVIDIA','/images/3050.jpeg',NOW(),NOW()),
  (1,'RTX 5070 Ti','Zotac GeForce RTX 5070 Ti 16GB AMP Extreme INFINITY',899.99,10,TRUE,'NV-RTX-5070-TI','NVIDIA','/images/5070ti.jpeg',NOW(),NOW()),
  (1,'RTX 3070 Ti','Gigabyte GeForce RTX 3070 Ti rev. 1.0 8GB OC LHR',799.99,10,TRUE,'NV-RTX-3070-TI','NVIDIA','/images/3070ti.jpeg',NOW(),NOW()),
  (1,'RTX 5050','Zotac GeForce RTX 5050 8GB Low Profile',199.99,10,TRUE,'NV-RTX-5050','NVIDIA','/images/5050.jpeg',NOW(),NOW())

AS new
ON DUPLICATE KEY UPDATE
  category_id = new.category_id,
  name        = new.name,
  description = new.description,
  price       = new.price,
  stock       = new.stock,
  is_active   = new.is_active,
  brand       = new.brand,
  image       = new.image,
  updated_at  = new.updated_at;


