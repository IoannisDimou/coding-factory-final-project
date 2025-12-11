import {useEffect, useState} from "react";
import {useNavigate, useSearchParams} from "react-router";
import {toast} from "sonner";
import {verifyEmail} from "@/services/api.register.js";
import {Button} from "@/components/ui/button.jsx";

export default function VerifyEmailPage() {

    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    const [status, setStatus] = useState("pending");
    const [serverMessage, setServerMessage] = useState("");

    useEffect(() => {
        if (!token) {
            setServerMessage("Invalid verification link.");
            setStatus("error");
            return;
        }

        (async () => {
            try {
                await verifyEmail(token);
                const msg = "Email verified successfully.";
                setServerMessage(msg);
                setStatus("success");
                toast.success(msg);
            } catch (err) {
                const msg = err instanceof Error ? err.message : "Email verification failed";
                setServerMessage(msg);
                setStatus("error");
                toast.error(msg);
            }
        })();
    }, [token]);

    if (status === "pending") {
        return null;
    }

    if (status === "success") {
        return (
            <div
                className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4">
                <h2 className="text-xl text-green-600 font-semibold mb-4 pb-1">Your
                    email has been verified </h2>
                <Button className="w-full" onClick={() => navigate("/login")}>Go
                    to login </Button>
            </div>
        );
    }

    return (
        <div className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4">
            <h2 className="text-xl font-semibold mb-2">Verification failed</h2>
            <p className="text-sm text-destructive mb-4">
                {serverMessage || "The verification link is invalid or expired."}
            </p>
            <Button className="w-full" onClick={() => navigate("/signup")}>Back
                to sign up </Button>
        </div>
    );
}
