import { useEffect, useState } from "react";
import { Link } from "react-router";
import getProducts, { searchProducts } from "@/services/api.products.js";
import { Button } from "@/components/ui/button.jsx";
import { useSearchParams } from "react-router";
import { formatPrice } from "@/utils/formatPrice.js";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const priceRanges = [{ label: "NVIDIA" }, { label: "AMD" }];

function getImageUrl(image) {
  if (!image) return null;
  if (image.startsWith("http")) return image;

  if (image.startsWith("/")) {
    return `${API_BASE_URL}${image}`;
  }
  return `${API_BASE_URL}/${image}`;
}

const HomePage = () => {
  useEffect(() => {
    document.title = "Arctic Builds";
  }, []);

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [brands, setBrands] = useState([]);
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const [searchParams] = useSearchParams();
  const query = searchParams.get("q") ?? "";

  const toggleBrand = (label) => {
    setBrands((prev) =>
      prev.includes(label) ? prev.filter((l) => l !== label) : [...prev, label],
    );
  };

  useEffect(() => {
    let cancelled = false;

    async function fetchProducts() {
      try {
        setLoading(true);
        setError(null);

        const filters = {};
        const q = query.trim();

        if (q) {
          filters.name = q;
        }

        if (brands.length === 1) {
          filters.brand = brands[0];
        }

        const min = minPrice === "" ? null : Number(minPrice);
        const max = maxPrice === "" ? null : Number(maxPrice);

        if (Number.isFinite(min)) filters.minPrice = min;
        if (Number.isFinite(max)) filters.maxPrice = max;

        if (
          filters.minPrice != null &&
          filters.maxPrice != null &&
          filters.minPrice > filters.maxPrice
        ) {
          const swap = filters.minPrice;
          filters.minPrice = filters.maxPrice;
          filters.maxPrice = swap;
        }

        let data;

        if (Object.keys(filters).length > 0) {
          data = await searchProducts(filters);
        } else {
          data = await getProducts();
        }

        if (brands.length > 1) {
          const set = new Set(brands.map((b) => b.toUpperCase()));
          data = (Array.isArray(data) ? data : []).filter((p) =>
            set.has(String(p?.brand ?? "").toUpperCase()),
          );
        }

        if (!cancelled) {
          setProducts(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to fetch products",
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    fetchProducts();

    return () => {
      cancelled = true;
    };
  }, [query, brands, minPrice, maxPrice]);

  return (
    <main
      className="mt-4 md:mt-6 lg:mt-8 flex flex-col md:flex-row gap-8 md:gap-10 lg:gap-16 pb-16 border-0"
      aria-label="Filters and product listing"
    >
      <aside
        className="w-full md:w-72 lg:w-80 shrink-0"
        aria-label="Product filters"
      >
        <h2 className="text-2xl font-semibold leading-tight mb-6">Filters</h2>

        <section aria-labelledby="price-heading" className="mb-8">
          <h3 id="price-heading" className="text-base font-semibold mb-3">
            Price range
          </h3>

          <div className="flex items-center gap-2 mb-4">
            <label className="w-1/2">
              <span className="sr-only">Minimum price</span>
              <input
                type="number"
                placeholder="From €"
                className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"
                inputMode="decimal"
                min="0"
                step="0.01"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
              />
            </label>
            <span className="text-ws-gray text-sm">–</span>
            <label className="w-1/2">
              <span className="sr-only">Maximum price</span>
              <input
                type="number"
                placeholder="To €"
                className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"
                inputMode="decimal"
                min="0"
                step="0.01"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
              />
            </label>
          </div>
        </section>
        <section aria-labelledby="brand-heading" className="mb-8">
          <h3 id="brand-heading" className="text-base font-semibold mb-3">
            Brands
          </h3>

          <div className="space-y-2 text-sm">
            {priceRanges.map((range) => (
              <label
                key={range.label}
                className="flex items-center gap-2 cursor-pointer"
              >
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded border border-border
                                       "
                  checked={brands.includes(range.label)}
                  onChange={() => toggleBrand(range.label)}
                />
                <span className="flex-1">{range.label}</span>
                <span className="text-xs text-ws-gray">{range.count}</span>
              </label>
            ))}
          </div>
        </section>
      </aside>

      <section className="flex-1" aria-label="Products">
        <header className="mb-6 mt-1 md:mt-[6px]">
          <h1 className="text-3xl font-bold">
            Find the
            <span className="inline-block mx-1 font-bold bg-gradient-to-r from-ws-ice to-cyan-300 bg-clip-text text-transparent">
              Cooolest
            </span>
            GPUs at the BEST prices
          </h1>
        </header>

        {loading && <p className="text-sm text-ws-gray">Loading products...</p>}

        {error && !loading && (
          <p className="text-sm text-destructive">{error}</p>
        )}

        {!loading && !error && products.length === 0 && (
          <p className="text-sm text-ws-gray">No products found.</p>
        )}

        {!loading && !error && products.length > 0 && (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {products.map((product) => (
              <article
                key={product.id}
                className="flex flex-col rounded-lg border border-border bg-card hover:shadow-md transition-shadow"
              >
                <div className="relative aspect-[4/3] w-full bg-secondary rounded-t-lg">
                  {product.image ? (
                    <img
                      src={getImageUrl(product.image)}
                      alt={product.name}
                      className="absolute inset-0  w-full h-full object-cover"
                    />
                  ) : (
                    <span className="text-xs text-ws-gray">No image</span>
                  )}
                </div>

                <div className="flex flex-col gap-1 px-4 py-3 flex-1">
                  <Link
                    to={`/products/${product.id}`}
                    className="text-sm font-semibold text-ws-dark hover:text-ws-gray line-clamp-2"
                  >
                    {product.name}
                  </Link>

                  {product.brand && (
                    <p className="text-xs text-ws-gray">{product.brand}</p>
                  )}

                  <p className="text-xs text-ws-gray line-clamp-2">
                    {product.description}
                  </p>

                  <div className="mt-auto flex items-center justify-between pt-3">
                    <span className="text-base font-semibold">
                      {typeof product.price === "number"
                        ? `${formatPrice(product.price)} €`
                        : product.price}
                    </span>
                    <Button asChild size="sm" className="text-xs px-3 py-1">
                      <Link to={`/products/${product.id}`}>View details</Link>
                    </Button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
};

export default HomePage;
