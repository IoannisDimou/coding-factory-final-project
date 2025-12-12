const LOGIN_URL = import.meta.env.VITE_LOGIN_URL;
const VERIFY_2FA_URL = import.meta.env.VITE_VERIFY_2FA_URL;

export async function login({ email, password }) {
  const res = await fetch(LOGIN_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) {
    let message = "Login failed";
    try {
      const data = await res.json();
      if (typeof data?.description === "string" && data.description) {
        message = data.description;
      } else if (typeof data?.code === "string") {
        message = data.code;
      }
    } catch (error) {
      console.error(error);
    }
    throw new Error(message);
  }

  return res.json();
}

export async function verifyTwoFactor({ twoFactorToken, code }) {
  const res = await fetch(VERIFY_2FA_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ twoFactorToken, code }),
  });

  if (!res.ok) {
    let message = "Verification failed";
    try {
      const data = await res.json();
      if (typeof data?.description === "string" && data.description) {
        message = data.description;
      } else if (typeof data?.code === "string") {
        message = data.code;
      }
    } catch (error) {
      console.error(error);
    }
    throw new Error(message);
  }

  return res.json();
}
