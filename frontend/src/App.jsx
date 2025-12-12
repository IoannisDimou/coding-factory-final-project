import { BrowserRouter, Routes, Route } from "react-router";
import HomePage from "@/components/pages/HomePage.jsx";
import Layout from "@/components/layout/Layout.jsx";
import LoginPage from "@/components/pages/LoginPage.jsx";
import { AuthProvider } from "@/context/AuthProvider.jsx";
import { Toaster } from "sonner";
import TwoFactorPage from "@/components/pages/TwoFactorPage.jsx";
import SignupPage from "@/components/pages/SignupPage.jsx";
import VerifyEmailPage from "@/components/pages/VerifyEmailPage.jsx";
import ProductPage from "@/components/pages/ProductPage.jsx";
import ForgotPasswordPage from "@/components/pages/ForgotPasswordPage.jsx";
import ResetPasswordPage from "@/components/pages/ResetPasswordPage.jsx";
import CartPage from "@/components/pages/CartPage.jsx";
import { CartProvider } from "@/context/CartContext.jsx";
import OrderLookupPage from "@/components/pages/OrderLookupPage.jsx";

//import ProtectedRoute from "@/components/ProtectedRoute.jsx";

function App() {
  return (
    <>
      <AuthProvider>
        <CartProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route index element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/2fa" element={<TwoFactorPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/verify-email" element={<VerifyEmailPage />} />
                <Route path="/products/:productId" element={<ProductPage />} />
                <Route
                  path="/forgot-password"
                  element={<ForgotPasswordPage />}
                />
                <Route path="/reset-password" element={<ResetPasswordPage />} />
                <Route path="/cart" element={<CartPage />} />
                <Route path="/orders" element={<OrderLookupPage />} />
              </Route>
            </Routes>
          </BrowserRouter>
          <Toaster richColors />
        </CartProvider>
      </AuthProvider>
    </>
  );
}

export default App;
