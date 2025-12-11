const USERS_URL = import.meta.env.VITE_API_USERS_URL;
const VERIFY_EMAIL_URL = import.meta.env.VITE_API_VERIFY_EMAIL_URL;

export async function registerUser({
                                       firstname,
                                       lastname,
                                       email,
                                       phoneNumber,
                                       password,
                                   }) {
    const payload = {
        firstname,
        lastname,
        email,
        phoneNumber: phoneNumber || null,
        password,
        role: "USER",
        isActive: true,
    };

    const res = await fetch(USERS_URL, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(payload),
    });

    if (!res.ok) {
        let message = "Registration failed";

        try {
            const text = await res.text();
            if (text) {
                try {
                    const data = JSON.parse(text);


                    if (data && typeof data === "object" && !Array.isArray(data)) {
                        const firstError = Object.values(data)[0];
                        if (typeof firstError === "string") {
                            message = firstError;
                        }
                    }


                    if (typeof data?.description === "string" && data.description) {
                        message = data.description;
                    } else if (typeof data?.code === "string" && data.code) {
                        message = data.code;
                    }
                } catch {
                    message = text;
                }
            }
        } catch (err) {
            console.error(err);
        }

        throw new Error(message);
    }

    return res.json();
}

export async function verifyEmail(token) {
    const res = await fetch(VERIFY_EMAIL_URL, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({token}),
    });

    if (!res.ok) {
        let message = "Email verification failed";

        try {
            const text = await res.text();
            if (text) {
                try {
                    const data = JSON.parse(text);
                    if (typeof data?.description === "string" && data.description) {
                        message = data.description;
                    } else if (typeof data?.code === "string" && data.code) {
                        message = data.code;
                    }
                } catch {
                    message = text;
                }
            }
        } catch (err) {
            console.error(err);
        }

        throw new Error(message);
    }

    return;
}

