import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Label } from "@/components/ui/label.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { toast } from "sonner";
import { confirmPasswordReset } from "@/services/api.passwordReset.js";
import { useAuth } from "@/hooks/useAuth.js";

const schema = z
  .object({
    password: z.string().min(8, "Password must be at least 8 characters"),
    confirmPassword: z.string().min(1, "Confirm your password"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    path: ["confirmPassword"],
    message: "Passwords do not match",
  });

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
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

  useEffect(() => {
    if (!token) {
      toast.error("Invalid or missing reset token.");
      navigate("/login");
    }
  }, [token, navigate]);

  const onSubmit = async ({ password }) => {
    try {
      if (!token) return;
      await confirmPasswordReset(token, password);
      toast.success("Password reset successfully. You can now log in.");
      navigate("/login");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Password reset failed");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4"
      autoComplete="off"
    >
      <h2 className="text-xl font-semibold mb-2">Reset password</h2>

      <div>
        <Label htmlFor="password" className="mb-1">
          New password
        </Label>
        <Input id="password" type="password" {...register("password")} />
        {errors.password && (
          <p className="text-sm text-destructive">{errors.password.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="confirmPassword" className="mb-1">
          Confirm password
        </Label>
        <Input
          id="confirmPassword"
          type="password"
          {...register("confirmPassword")}
        />
        {errors.confirmPassword && (
          <p className="text-sm text-destructive">
            {errors.confirmPassword.message}
          </p>
        )}
      </div>

      <Button disabled={isSubmitting} className="w-full">
        {isSubmitting ? "Resetting..." : "Reset password"}
      </Button>
    </form>
  );
}
