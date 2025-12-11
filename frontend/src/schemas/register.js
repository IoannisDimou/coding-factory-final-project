import {z} from "zod";

export const registerSchema = z
    .object({
        firstname: z.string().min(1, {message: "First name is required"}),
        lastname: z.string().min(1, {message: "Last name is required"}),
        email: z.email({message: "Email is invalid"}),
        phoneNumber: z.string().optional().refine((val) => !val || /^[0-9]{10}$/.test(val), {message: "Phone number must be 10 digits"}),
        password: z.string().regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$!%&*]).{8,}$/, {message: "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character"}),
        confirmPassword: z.string().min(1, {message: "Confirm your password"}),

    })
    .refine((data) => data.password === data.confirmPassword, {
        path: ["confirmPassword"],
        message: "Passwords do not match"
    })