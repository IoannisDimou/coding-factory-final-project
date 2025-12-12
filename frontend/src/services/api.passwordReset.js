const API_AUTH_URL = import.meta.env.VITE_API_AUTH_URL;

export async function requestPasswordReset(email) {
  const res = await fetch(`${API_AUTH_URL}/password-reset/request`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email }),
  });

  if (!res.ok) {
    let message = "Failed to request password reset";
    try {
      const data = await res.json();
      if (typeof data?.description === "string" && data.description) {
        message = data.description;
      }
    } catch {
      // ignore
    }
    throw new Error(message);
  }

  return res.json();
}

export async function confirmPasswordReset(token, newPassword) {
  const res = await fetch(`${API_AUTH_URL}/password-reset/confirm`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ token, newPassword }),
  });

  if (!res.ok) {
    let message = "Failed to reset password";
    try {
      const data = await res.json();
      if (typeof data?.description === "string" && data.description) {
        message = data.description;
      }
    } catch {
      // ignore
    }
    throw new Error(message);
  }

  return res.json();
}
