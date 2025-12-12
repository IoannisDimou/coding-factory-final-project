const API_PRODUCTS_URL = import.meta.env.VITE_API_PRODUCTS_URL;

export default async function getProducts() {
  const res = await fetch(`${API_PRODUCTS_URL}/products`);
  if (!res.ok) {
    throw new Error("Failed to fetch products");
  }
  const body = await res.json();

  if (Array.isArray(body.data)) {
    return body.data;
  }

  return [];
}

export async function getProduct(id) {
  const res = await fetch(`${API_PRODUCTS_URL}/products/${id}`);
  if (!res.ok) throw new Error("Failed to fetch product");
  return res.json();
}

export async function searchProducts(filters) {
  const payload = {
    page: 0,
    pageSize: 10,
    sortBy: "id",
    sortDirection: "ASC",
    ...filters,
  };

  const res = await fetch(`${API_PRODUCTS_URL}/products/search`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) throw new Error("Failed to search products");

  const body = await res.json();

  return Array.isArray(body.data) ? body.data : [];
}
