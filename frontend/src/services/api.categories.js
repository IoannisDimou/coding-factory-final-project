const API_CATEGORIES_URL = import.meta.env.VITE_API_CATEGORIES_URL;

function getAuthHeaders() {
  const token = localStorage.getItem("access_token");
  if (!token) return {};
  return {
    Authorization: `Bearer ${token}`,
  };
}

export default async function getCategories(pageSize = 500) {
  const res = await fetch(
    `${API_CATEGORIES_URL}?page=0&pageSize=${pageSize}&size=${pageSize}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        ...getAuthHeaders(),
      },
    },
  );

  if (!res.ok) {
    if (res.status === 401) throw new Error("Unauthorized");
    throw new Error("Failed to fetch categories");
  }

  const body = await res.json();
  return Array.isArray(body.data) ? body.data : [];
}

export async function createCategory(data) {
  const res = await fetch(`${API_CATEGORIES_URL}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
    body: JSON.stringify(data),
  });

  if (!res.ok) {
    if (res.status === 401) throw new Error("Unauthorized");
    throw new Error("Failed to create category");
  }

  return res.json();
}

export async function updateCategory(id, data) {
  const res = await fetch(`${API_CATEGORIES_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
    body: JSON.stringify(data),
  });

  if (!res.ok) {
    if (res.status === 401) throw new Error("Unauthorized");
    throw new Error("Failed to update category");
  }

  return res.json();
}
