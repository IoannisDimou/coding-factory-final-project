const API_PRODUCTS_URL = import.meta.env.VITE_API_PRODUCTS_URL;

function getAuthHeaders() {
  const token = localStorage.getItem("access_token");
  if (!token) return {};
  return { Authorization: `Bearer ${token}` };
}

export async function getProduct(id) {
  const res = await fetch(`${API_PRODUCTS_URL}/products/${id}`);
  if (!res.ok) throw new Error("Failed to fetch product");
  return res.json();
}

export async function searchProducts(filters = {}) {
  const {
    page = 0,
    pageSize = 12,
    sortBy = "id",
    sortDirection = "ASC",
    ...rest
  } = filters;

  const payload = { page, pageSize, sortBy, sortDirection, ...rest };

  const res = await fetch(`${API_PRODUCTS_URL}/products/search`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) throw new Error("Failed to search products");

  return res.json();
}

export async function createProduct(formData) {
  const res = await fetch(`${API_PRODUCTS_URL}/products`, {
    method: "POST",
    headers: {
      ...getAuthHeaders(),
    },
    body: formData,
  });

  const text = await res.text();
  if (!res.ok) {
    console.error("Create product failed:", text);
    throw new Error("Failed to create product");
  }
  return text ? JSON.parse(text) : null;
}

export async function updateProduct(id, formData) {
  const res = await fetch(`${API_PRODUCTS_URL}/products/${id}`, {
    method: "PUT",
    headers: {
      ...getAuthHeaders(),
    },
    body: formData,
  });

  const text = await res.text();
  if (!res.ok) {
    console.error("Update product failed:", text);
    throw new Error("Failed to update product");
  }
  return text ? JSON.parse(text) : null;
}

export async function getProductsPage({ page = 0, size = 12 } = {}) {
  const res = await fetch(
    `${API_PRODUCTS_URL}/products?page=${page}&size=${size}`,
  );
  if (!res.ok) throw new Error("Failed to fetch products");

  return res.json();
}
