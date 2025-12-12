import { useEffect, useState } from "react";
import { AuthContext } from "@/context/AuthContext.js";
import { jwtDecode } from "jwt-decode";
import { login, verifyTwoFactor } from "@/services/api.login.js";

function getValidToken() {
  try {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return null;
    }
    const decoded = jwtDecode(token);
    const expire = decoded.exp;

    if (expire && expire * 1000 < Date.now()) {
      localStorage.removeItem("access_token");
      return null;
    }
    return token;
  } catch {
    localStorage.removeItem("access_token");
    return null;
  }
}

export const AuthProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const [twoFactorToken, setTwoFactorToken] = useState(null);
  const [twoFactorDelivery, setTwoFactorDelivery] = useState(null);
  const [twoFactorMessage, setTwoFactorMessage] = useState(null);
  const [isTwoFactorPending, setIsTwoFactorPending] = useState(false);

  useEffect(() => {
    const token = getValidToken();
    setAccessToken(token);

    try {
      const aUser = localStorage.getItem("auth_user");
      setUser(aUser ? JSON.parse(aUser) : null);
    } catch {
      setUser(null);
    }

    setLoading(false);
  }, []);

  const loginUser = async ({ email, password }) => {
    const challenge = await login({ email, password });

    setTwoFactorToken(challenge.twoFactorToken);
    setTwoFactorDelivery(challenge.deliveryMethod);
    setTwoFactorMessage(challenge.message);
    setIsTwoFactorPending(true);

    return challenge;
  };

  const verifyTwoFactorCode = async (code) => {
    if (!twoFactorToken) {
      throw new Error("No pending two-factor challenge");
    }

    const res = await verifyTwoFactor({ twoFactorToken, code });

    localStorage.setItem("access_token", res.token);

    const userData = {
      firstname: res.firstname,
      lastname: res.lastname,
      role: res.role,
      uuid: res.uuid,
    };

    localStorage.setItem("auth_user", JSON.stringify(userData));

    setAccessToken(res.token);
    setUser(userData);

    setIsTwoFactorPending(false);
    setTwoFactorToken(null);
    setTwoFactorDelivery(null);
    setTwoFactorMessage(null);

    return res;
  };

  const logoutUser = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("auth_user");

    setAccessToken(null);
    setUser(null);
    setIsTwoFactorPending(false);
    setTwoFactorToken(null);
    setTwoFactorDelivery(null);
    setTwoFactorMessage(null);
  };

  return (
    <>
      <AuthContext.Provider
        value={{
          isAuthenticated: !!accessToken,
          accessToken,
          user,
          isTwoFactorPending,
          twoFactorToken,
          twoFactorDelivery,
          twoFactorMessage,
          loginUser,
          verifyTwoFactor: verifyTwoFactorCode,
          logoutUser,
          loading,
        }}
      >
        {loading ? null : children}
      </AuthContext.Provider>
    </>
  );
};
