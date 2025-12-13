import { Navigate, Outlet, useLocation } from "react-router";
import { useAuth } from "@/hooks/useAuth.js";

const ProtectedRoute = ({ requiredRole }) => {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRole && user?.role !== requiredRole)
    return <Navigate to="/" replace />;

  return <Outlet />;
};

export default ProtectedRoute;
