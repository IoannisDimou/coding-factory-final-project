import { Link, useNavigate } from "react-router";
import { MagnifyingGlassIcon, PersonIcon } from "@radix-ui/react-icons";

import { ShoppingCart } from "lucide-react";
import { Button } from "@/components/ui/button.jsx";
import logo from "@/assets/ArcticBuildsLogo.png";
import { useAuth } from "@/hooks/useAuth.js";
import { useCart } from "@/hooks/useCart.js";
import { useEffect, useState } from "react";
import { searchProducts } from "@/services/api.products.js";

const links = [
  { path: "/login", label: "Log in" },
  { path: "/signup", label: "Sign up" },
];

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function getImageUrl(image) {
  if (!image) return null;
  if (image.startsWith("http")) return image;
  if (image.startsWith("/")) return `${API_BASE_URL}${image}`;
  return `${API_BASE_URL}/${image}`;
}

const Header = () => {
  const { isAuthenticated, logoutUser, user } = useAuth();
  const navigate = useNavigate();
  const { totalItems } = useCart();

  const handleLogout = () => {
    logoutUser();
    navigate("/");
  };

  const [searchTerm, setSearchTerm] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [suggestionsOpen, setSuggestionsOpen] = useState(false);
  const [suggestionsLoading, setSuggestionsLoading] = useState(false);

  useEffect(() => {
    const q = searchTerm.trim();

    if (q.length < 2) {
      setSuggestions([]);
      setSuggestionsOpen(false);
      setSuggestionsLoading(false);
      return;
    }

    let cancelled = false;

    const t = setTimeout(async () => {
      try {
        setSuggestionsLoading(true);
        const res = await searchProducts({
          name: q,
          isActive: true,
          page: 0,
          pageSize: 6,
        });
        const items = Array.isArray(res?.data) ? res.data : [];

        if (!cancelled) {
          setSuggestions(items);
          setSuggestionsOpen(true);
        }
      } catch {
        if (!cancelled) {
          setSuggestions([]);
          setSuggestionsOpen(false);
        }
      } finally {
        if (!cancelled) setSuggestionsLoading(false);
      }
    }, 250);

    return () => {
      cancelled = true;
      clearTimeout(t);
    };
  }, [searchTerm]);

  const handleSearch = (e) => {
    e.preventDefault();
    const q = searchTerm.trim();

    setSuggestionsOpen(false);

    if (!q) {
      navigate(`/`);
    } else {
      navigate(`/?q=${encodeURIComponent(q)}`);
    }
  };

  return (
    <header className="bg-background fixed top-0 inset-x-0 z-40">
      <div className="container mx-auto px-4 sm:px-6">
        <div className="flex flex-wrap items-center gap-6 md:gap-8 pt-10 pb-3 md:pt-8 md:pb-4">
          <div className="flex items-center gap-3 shrink-0 mr-6 md:mr-8">
            <Link to="/" aria-label="Go to home">
              <img
                src={logo}
                alt="Arctic Builds logo"
                className="h-12 w-auto md:h-14 lg:h-16"
              />
            </Link>
          </div>

          <form
            role="search"
            className="order-3 w-full md:order-2 md:flex-1 md:w-auto mt-1 relative"
            onSubmit={handleSearch}
          >
            <div className="hidden md:flex items-center w-full rounded-full bg-secondary px-5 py-2.5">
              <Button
                type="submit"
                aria-label="Search products"
                variant="ghost"
                className="shrink-0"
                size="icon-sm"
              >
                <MagnifyingGlassIcon className="w-4 h-4 text-ws-gray" />
              </Button>
              <input
                type="search"
                placeholder="What are you looking for?"
                className="ml-3 flex-1 bg-transparent text-sm text-ws-dark placeholder:text-ws-gray outline-none"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <div className="flex md:hidden items-center w-full rounded-full bg-secondary px-4 py-2">
              <Button
                type="submit"
                variant="ghost"
                size="icon-sm"
                aria-label="Search"
                className="shrink-0"
              >
                <MagnifyingGlassIcon className="w-4 h-4 text-ws-gray" />
              </Button>
              <input
                type="search"
                placeholder="Search products..."
                className="ml-3 flex-1 bg-transparent text-sm text-ws-dark placeholder:text-ws-gray outline-none"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {suggestionsOpen && (
              <div
                className="absolute left-0 right-0 top-full mt-2 rounded-xl border border-border bg-background shadow-md overflow-hidden z-50"
                onMouseDown={(e) => e.preventDefault()}
              >
                {suggestionsLoading ? (
                  <div className="px-4 py-3 text-sm text-ws-gray">
                    {" "}
                    Searching...
                  </div>
                ) : suggestions.length === 0 ? (
                  <div className="px-4 py-3 text-sm text-ws-gray">
                    {" "}
                    No results found
                  </div>
                ) : (
                  <div className="max-h-80 overflow-auto">
                    {suggestions.map((product) => (
                      <Button
                        key={product.id}
                        asChild
                        variant="ghost"
                        size="default"
                        className="w-full justify-start rounded-none px-7 py-6"
                      >
                        <Link
                          to={`/products/${product.id}`}
                          onClick={() => setSuggestionsOpen(false)}
                          className="flex items-center gap-3 w-full min-h-20"
                        >
                          <div className="h-15 w-15 rounded-md bg-secondary overflow-hidden shrink-0 flex items-center justify-center">
                            {product.image ? (
                              <img
                                src={getImageUrl(product.image)}
                                alt={product.name}
                                className="h-full w-full object-contain p-2 bg-card"
                              />
                            ) : (
                              <span className="text-[10px] text-ws-gray">
                                No img
                              </span>
                            )}
                          </div>

                          <div className="min-w-0 flex-1">
                            <div className="truncate font-medium text-ws-dark">
                              {product.name}
                            </div>
                            {product.brand ? (
                              <div className="text-xs text-ws-gray truncate">
                                {product.brand}
                              </div>
                            ) : null}
                          </div>

                          <div className="tex-sm font-semibold whitespace-nowrap">
                            {typeof product.price === "number"
                              ? `${product.price.toFixed(2)} €`
                              : product.price}
                          </div>
                        </Link>
                      </Button>
                    ))}
                  </div>
                )}
              </div>
            )}
          </form>

          <nav
            className="order-2 ml-auto flex items-center gap-4 md:order-3 mt-1"
            aria-label="Primary navigation"
          >
            <div className="flex items-center gap-4 md:gap-5">
              {!isAuthenticated &&
                links.map((item) => (
                  <Button
                    key={item.path}
                    asChild
                    variant="outline"
                    size="sm"
                    className="rounded-full px-4 py-1.5 text-xs sm:text-sm"
                  >
                    <Link to={item.path} aria-label={item.label}>
                      {item.path === "/login" ? (
                        <span className="flex items-center gap-1">
                          <PersonIcon className="w-4 h-4" />
                          <span className="hidden sm:inline">{item.label}</span>
                        </span>
                      ) : (
                        item.label
                      )}
                    </Link>
                  </Button>
                ))}

              {isAuthenticated && (
                <Button
                  asChild
                  variant="outline"
                  size="sm"
                  className="rounded-full px-4 py-1.5 text-xs sm:text-sm"
                >
                  <Link to="/orders">My orders</Link>
                </Button>
              )}

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
                onClick={() => navigate("/cart")}
              >
                <ShoppingCart className="w-5 h-5 text-cart" />
                <span className="absolute -top-1 -right-1 min-w-[1.1rem] rounded-full bg-notification px-1 text-[0.65rem] leading-tight font-bold text-white">
                  {totalItems}
                </span>
              </Button>

              {isAuthenticated && user.role === "ADMIN" && (
                <Button
                  asChild
                  variant="outline"
                  size="sm"
                  className="rounded-full px-4 py-1.5 text-xs text-destructive sm:text-sm ml-2 hover:bg-hover-cart hover:text-destructive"
                >
                  <Link to="/admin">Admin Panel</Link>
                </Button>
              )}
            </div>
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;
