import {BrowserRouter, Routes, Route} from "react-router";
import HomePage from "@/components/pages/HomePage.jsx";
import Layout from "@/components/layout/Layout.jsx"
import LoginPage from "@/components/pages/LoginPage.jsx";
import {AuthProvider} from "@/context/AuthProvider.jsx";
import {Toaster} from "sonner";
import TwoFactorPage from "@/components/pages/TwoFactorPage.jsx";
import SignupPage from "@/components/pages/SignupPage.jsx";
import VerifyEmailPage from "@/components/pages/VerifyEmailPage.jsx";

//import ProtectedRoute from "@/components/ProtectedRoute.jsx";

function App() {

    return (
        <>
            <AuthProvider>
                <BrowserRouter>
                    <Routes>
                        <Route element={<Layout/>}>
                            <Route index element={<HomePage/>}/>
                            <Route path="/login" element={<LoginPage/>}/>
                            <Route path="/2fa" element={<TwoFactorPage/>}/>
                            <Route path="/signup" element={<SignupPage/>}/>
                            <Route path="/verify-email"
                                   element={<VerifyEmailPage/>}/>
                        </Route>
                    </Routes>
                </BrowserRouter>
                <Toaster richColors/>
            </AuthProvider>
        </>
    )
}

export default App
