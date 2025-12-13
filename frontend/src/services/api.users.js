const API_USERS_URL = import.meta.env.VITE_API_USERS_URL;

function getAuthHeaders() {
  const token = localStorage.getItem("access_token");
  if (!token) return {};

  return {
    Authorization: `Bearer ${token}`,
  };
}

export default async function getUsers() {
  const res = await fetch(`${API_USERS_URL}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
  });

  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Unauthorized (missing or invalid token)");
    }
    throw new Error("Failed to fetch users");
  }

  const body = await res.json();
  return Array.isArray(body.data) ? body.data : [];
}

export async function getUser(id) {
  const res = await fetch(`${API_USERS_URL}/${id}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
  });

  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Unauthorized (missing or invalid token)");
    }
    throw new Error("Failed to fetch user");
  }

  return res.json();
}

export async function updateUser(id, data) {
  const url = `${API_USERS_URL}/${id}`;

  const res = await fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
    body: JSON.stringify(data),
  });

  const text = await res.text();

  if (!res.ok) {
    throw new Error("Failed to update user");
  }

  return JSON.parse(text);
}

export async function createUser(data) {
  const res = await fetch(`${API_USERS_URL}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
    body: JSON.stringify(data),
  });

  if (!res.ok) {
    if (res.status === 401)
      throw new Error("Unauthorized (missing or invalid token)");
    throw new Error("Failed to create user");
  }

  return res.json();
}
