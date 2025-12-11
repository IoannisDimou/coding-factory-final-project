import {Link, useNavigate} from "react-router"
import {
    MagnifyingGlassIcon,
    PersonIcon
} from "@radix-ui/react-icons"

import {ShoppingCart} from "lucide-react"
import {Button} from "@/components/ui/button.jsx"
import logo from "@/assets/ArcticBuildsLogo.png"
import {useAuth} from "@/hooks/useAuth.js";

const links = [
    {path: "/login", label: "Log in"},
    {path: "/signup", label: "Sign up"},
]


const Header = () => {

    const {isAuthenticated, logoutUser} = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logoutUser();
        navigate("/");
    }

    return (
        <header className="bg-background fixed top-0 inset-x-0 z-40">
            <div className="container mx-auto px-4 sm:px-6">

                <div
                    className="flex flex-wrap items-center gap-6 md:gap-8 pt-10 pb-3 md:pt-8 md:pb-4">

                    <div
                        className="flex items-center gap-3 shrink-0 mr-6 md:mr-8">
                        <Link to="/" aria-label="Go to home">
                            <img src={logo} alt="Arctic Builds logo"
                                 className="h-12 w-auto md:h-14 lg:h-16"/>
                        </Link>
                    </div>


                    <form role="search"
                          className="order-3 w-full md:order-2 md:flex-1 md:w-auto mt-1">
                        <div
                            className="hidden md:flex items-center w-full rounded-full bg-secondary px-5 py-2.5">
                            <MagnifyingGlassIcon
                                className="w-4 h-4 text-ws-gray"/>
                            <input
                                type="search"
                                placeholder="What are you looking for?"
                                className="ml-3 flex-1 bg-transparent text-sm text-ws-dark placeholder:text-ws-gray outline-none"/>
                        </div>

                        <div
                            className="flex md:hidden items-center w-full rounded-full bg-secondary px-4 py-2">
                            <MagnifyingGlassIcon
                                className="w-4 h-4 text-ws-gray"/>
                            <input type="search"
                                   placeholder="Search products..."
                                   className="ml-3 flex-1 bg-transparent text-sm text-ws-dark placeholder:text-ws-gray outline-none"/>
                        </div>
                    </form>

                    <nav
                        className="order-2 ml-auto flex items-center gap-4 md:order-3 mt-1"
                        aria-label="Primary navigation">
                        <div className="flex items-center gap-4 md:gap-5">
                            {!isAuthenticated && links.map((item) => (
                                <Button
                                    key={item.path}
                                    asChild
                                    variant="outline"
                                    size="sm"
                                    className="rounded-full px-4 py-1.5 text-xs sm:text-sm"
                                >
                                    <Link
                                        to={item.path}
                                        aria-label={item.label}
                                    >
                                        {item.path === "/login" ? (
                                            <span
                                                className="flex items-center gap-1">
                                                <PersonIcon
                                                    className="w-4 h-4"/>
                                                <span
                                                    className="hidden sm:inline">
                                                  {item.label}
                                                </span>
                                            </span>
                                        ) : (
                                            item.label
                                        )}
                                    </Link>
                                </Button>
                            ))}

                            {isAuthenticated && (
                                <Button
                                    type="button"
                                    variant="outline"
                                    size="sm"
                                    className="rounded-full px-4 py-1.5 text-xs sm:text-sm"
                                    onClick={handleLogout}
                                >
                                    Sign out
                                </Button>
                            )}

                            <Button
                                type="button"
                                variant="outline"
                                size="icon"
                                className="relative rounded-full w-9 h-9 border-2 border-cart hover:bg-hover-cart  hover:opacity-95 ml-2 md:ml-3"
                                aria-label="Open cart"
                            >
                                <ShoppingCart className="w-5 h-5 text-cart"/>
                                <span
                                    className="absolute -top-1 -right-1 min-w-[1.1rem] rounded-full bg-notification px-1 text-[0.65rem] leading-tight font-bold text-white">
                                    0
                                </span>
                            </Button>
                        </div>
                    </nav>
                </div>
            </div>
        </header>
    )
}

export default Header
