import { z } from "zod";

export const loginSchema = z.object({
  email: z.email({ error: "Email is invalid" }),
  password: z.string().min(1, { error: "Password is invalid" }),
});
