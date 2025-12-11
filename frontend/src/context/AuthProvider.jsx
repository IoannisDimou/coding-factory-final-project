import {useEffect, useState} from "react";
import {deleteCookie, getCookie, setCookie} from "@/utils/cookies.js";
import {AuthContext} from "@/context/AuthContext.js";
import {login, verifyTwoFactor} from "@/services/api.login.js";

export const AuthProvider = ({children}) => {

    const [accessToken, setAccessToken] = useState(null);
    const [loading, setLoading] = useState(true);

    const [twoFactorToken, setTwoFactorToken] = useState(null);
    const [twoFactorDelivery, setTwoFactorDelivery] = useState(null);
    const [twoFactorMessage, setTwoFactorMessage] = useState(null);
    const [isTwoFactorPending, setIsTwoFactorPending] = useState(false);

    useEffect(() => {
        const token = getCookie("access_token");
        setAccessToken(token ?? null);
        setLoading(false);
    }, []);

    const loginUser = async ({email, password}) => {

        const challenge = await login({email, password});

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

        const res = await verifyTwoFactor({twoFactorToken, code});

        setCookie("access_token", res.token, {
            expires: 1,
            sameSite: "Lax",
            secure: false, // true (HTTPS)
            path: "/",
        });

        setAccessToken(res.token);

        setIsTwoFactorPending(false);
        setTwoFactorToken(null);
        setTwoFactorDelivery(null);
        setTwoFactorMessage(null);

        return res;
    };

    const logoutUser = () => {
        deleteCookie("access_token");
        setAccessToken(null);
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
                    isTwoFactorPending,
                    twoFactorToken,
                    twoFactorDelivery,
                    twoFactorMessage,
                    loginUser,
                    verifyTwoFactor: verifyTwoFactorCode,
                    logoutUser,
                    loading,
                }}>
                {loading ? null : children}
            </AuthContext.Provider>
        </>
    );
};
