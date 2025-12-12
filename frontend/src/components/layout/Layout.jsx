import Header from "./Header.jsx";
import Footer from "./Footer.jsx";
import { Outlet } from "react-router";

const Layout = () => {
  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <div className="flex-1">
        <div className="container mx-auto px-4 pt-40 md:pt-28 pb-10">
          <Outlet />
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Layout;
