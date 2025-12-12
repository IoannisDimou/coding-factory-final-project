import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerSchema } from "@/schemas/register.js";
import { registerUser } from "@/services/api.register.js";
import { toast } from "sonner";
import { useNavigate } from "react-router";
import { Label } from "@/components/ui/label.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { useAuth } from "@/hooks/useAuth.js";
import { useEffect } from "react";

export default function SignUpPage() {
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
  } = useForm({ resolver: zodResolver(registerSchema) });

  const onSubmit = async (data) => {
    try {
      await registerUser({
        firstname: data.firstname,
        lastname: data.lastname,
        email: data.email,
        phoneNumber: data.phoneNumber || null,
        password: data.password,
      });

      toast.success(
        "Registration successful, Check your email for verification",
      );
      navigate("/login");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Registration failed");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="max-w-sm mx-auto mt-12 p-8 border rounded-md space-y-4"
      autoComplete="off"
    >
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <Label htmlFor="firstname" className="mb-1">
            First name
          </Label>
          <Input id="firstname" {...register("firstname")} />
          {errors.firstname && (
            <p className="text-sm text-destructive">
              {errors.firstname.message}
            </p>
          )}
        </div>
        <div>
          <Label htmlFor="lastname" className="mb-1">
            Last name
          </Label>
          <Input id="lastname" {...register("lastname")} />
          {errors.lastname && (
            <p className="text-sm text-destructive">
              {errors.lastname.message}
            </p>
          )}
        </div>
      </div>

      <div>
        <Label htmlFor="email" className="mb-1">
          Email
        </Label>
        <Input id="email" type="email" {...register("email")} />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="phoneNumber" className="mb-1">
          Phone (optional)
        </Label>
        <Input id="phoneNumber" {...register("phoneNumber")} />
        {errors.phoneNumber && (
          <p className="text-sm text-destructive">
            {errors.phoneNumber.message}
          </p>
        )}
      </div>

      <div>
        <Label htmlFor="password" className="mb-1">
          Password
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
        {isSubmitting ? "Creating account..." : "Sign up"}
      </Button>
    </form>
  );
}
