import {Label} from "@/components/ui/label.jsx";
import {Input} from "@/components/ui/input.jsx";
import {Button} from "@/components/ui/button.jsx";
import {zodResolver} from "@hookform/resolvers/zod";
import {useForm} from "react-hook-form";
import {toast} from "sonner";
import {useNavigate} from "react-router";
import {useAuth} from "@/hooks/useAuth.js";
import {loginSchema} from "@/schemas/login.js";

export default function LoginPage() {
    const navigate = useNavigate();
    const {loginUser} = useAuth();

    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm({
        resolver: zodResolver(loginSchema),
    })

    const onSubmit = async (data) => {
        try {
            const challenge = await loginUser(data);
            toast.success(
                challenge.message ||
                `Verification code sent via ${challenge.deliveryMethod}`
            );

            navigate("/2fa");

        } catch (err) {
            toast.error(
                err instanceof Error ? err.message : "Login failed"
            );
        }
    };

    return (
        <>
            <form
                onSubmit={handleSubmit(onSubmit)}
                className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4"
                autoComplete="off"
            >
                <div>
                    <Label htmlFor="email" className="mb-1">Email</Label>
                    <Input id="email" type="email" {...register("email")}/>
                    {errors.email && (
                        <div className="text-ws-dark text-sm">
                            {errors.email.message}
                        </div>
                    )}
                </div>

                <div>
                    <Label htmlFor="password" className="mb-1">Password</Label>
                    <Input
                        type="password"
                        id="password" {...register("password")}
                    />
                    {errors.password && (
                        <div className="text-ws-dark text-sm">
                            {errors.password.message}
                        </div>
                    )}
                </div>
                <Button disabled={isSubmitting} className="w-full">
                    {isSubmitting ? "Sending code..." : "Login"}
                </Button>
            </form>
        </>
    )
}

