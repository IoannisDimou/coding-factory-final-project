import {useForm} from "react-hook-form";
import {useNavigate, Navigate} from "react-router";
import {useAuth} from "@/hooks/useAuth.js";
import {Label} from "@/components/ui/label.jsx";
import {Input} from "@/components/ui/input.jsx";
import {Button} from "@/components/ui/button.jsx";
import {toast} from "sonner";

export default function TwoFactorPage() {
    const navigate = useNavigate();
    const {
        verifyTwoFactor,
        isTwoFactorPending,
        twoFactorMessage,
        isAuthenticated,
    } = useAuth();

    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm();


    if (!isTwoFactorPending) {
        if (isAuthenticated) {
            return <Navigate to="/" replace/>;
        }
        return <Navigate to="/login" replace/>;
    }

    const onSubmit = async ({code}) => {
        try {
            await verifyTwoFactor(code);
            toast.success("Login completed");
            navigate("/");
        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Verification failed"
            );
        }
    };

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4"
            autoComplete="off"
        >
            <h2 className="text-xl font-semibold mb-2">Enter verification
                code</h2>

            {twoFactorMessage && (
                <p className="text-sm text-ws-gray mb-2">{twoFactorMessage}</p>)}

            <div>
                <Label htmlFor="code" className="mt-5 mb-1">Code</Label>
                <Input
                    id="code"
                    type="text"
                    maxLength={6}
                    {...register("code", {required: "Code is required"})}
                />
                {errors.code && (
                    <div className="text-ws-dark text-sm">
                        {errors.code.message}
                    </div>
                )}
            </div>

            <Button disabled={isSubmitting} className="w-full">
                {isSubmitting ? "Verifying..." : "Verify"}
            </Button>
        </form>
    );
}