import { useEffect } from "react";
import { useNavigate } from "react-router";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Label } from "@/components/ui/label.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { toast } from "sonner";
import { requestPasswordReset } from "@/services/api.passwordReset.js";
import { useAuth } from "@/hooks/useAuth.js";

const schema = z.object({
  email: z.email("Email is invalid"),
});

export default function ForgotPasswordPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/");
    }
  }, [isAuthenticated, navigate]);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
  });

  const onSubmit = async ({ email }) => {
    try {
      await requestPasswordReset(email);
      toast.success("If this email exists, a reset link has been sent.");
      navigate("/login");
    } catch (err) {
      toast.error(
        err instanceof Error ? err.message : "Failed to request password reset",
      );
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4"
      autoComplete="off"
    >
      <h2 className="text-xl font-semibold mb-2">Forgot your password?</h2>
      <p className="text-sm text-ws-gray mb-3">
        Enter your email and well send you a link to reset your password.
      </p>

      <div>
        <Label htmlFor="email" className="mb-1">
          Email
        </Label>
        <Input id="email" type="email" {...register("email")} />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email.message}</p>
        )}
      </div>

      <Button disabled={isSubmitting} className="w-full">
        {isSubmitting ? "Sending..." : "Send reset link"}
      </Button>
    </form>
  );
}
