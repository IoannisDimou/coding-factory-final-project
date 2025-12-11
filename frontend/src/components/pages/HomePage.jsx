import {useEffect, useState} from "react"
import {Link} from "react-router";
import getProducts from "@/services/api.products.js";
import {Button} from "@/components/ui/button.jsx";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const categories = [
    {name: "CPUs"},
    {name: "GPUs"}
]

const priceRanges = [
    {label: "NVIDIA"},
    {label: "AMD"}
]

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
        document.title = "Arctic Builds"
    }, [])

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;


        async function fetchProducts() {
            try {
                setLoading(true);
                setError(null);
                const data = await getProducts();
                if (!cancelled) {
                    setProducts(Array.isArray(data) ? data : []);
                }
            } catch (err) {
                if (!cancelled) {
                    setError(
                        err instanceof Error ? err.message : "Failed to fetch products"
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
    }, []);

    return (
        <main
            className="mt-4 md:mt-6 lg:mt-8 flex flex-col md:flex-row gap-8 md:gap-10 lg:gap-16 pb-16 border-0"
            aria-label="Filters and product listing">
            <aside className="w-full md:w-72 lg:w-80 shrink-0"
                   aria-label="Product filters">
                <h2 className="text-2xl font-semibold leading-tight mb-6">Filters</h2>

                <section aria-labelledby="category-heading" className="mb-8">
                    <h3 id="category-heading"
                        className="text-base font-semibold mb-3">
                        Categories
                    </h3>
                    <ul className="space-y-1 text-sm">
                        {categories.map((cat) => (
                            <li
                                key={cat.name}
                                className="flex items-baseline justify-between gap-2 text-ws-dark hover:text-ws-gray cursor-pointer"
                            >
                                <span className="truncate">{cat.name}</span>
                                <span
                                    className="text-xs text-ws-gray">{cat.count}</span>
                            </li>
                        ))}
                    </ul>
                </section>

                <section aria-labelledby="price-heading" className="mb-8">
                    <h3 id="price-heading"
                        className="text-base font-semibold mb-3">
                        Price range
                    </h3>

                    <div className="flex items-center gap-2 mb-4">
                        <label className="w-1/2">
                            <span className="sr-only">Minimum price</span>
                            <input type="number" placeholder="From €"
                                   className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"/>
                        </label>
                        <span className="text-ws-gray text-sm">–</span>
                        <label className="w-1/2">
                            <span className="sr-only">Maximum price</span>
                            <input type="number" placeholder="To €"
                                   className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"/>
                        </label>
                    </div>
                </section>
                <section aria-labelledby="price-heading" className="mb-8">
                    <h3 id="price-heading"
                        className="text-base font-semibold mb-3">
                        Brands
                    </h3>

                    <div className="space-y-2 text-sm">
                        {priceRanges.map((range) => (
                            <label key={range.label}
                                   className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox"
                                       className="h-4 w-4 rounded border border-border"/>
                                <span className="flex-1">{range.label}</span>
                                <span
                                    className="text-xs text-ws-gray">{range.count}</span>
                            </label>
                        ))}
                    </div>
                </section>
            </aside>


            <section className="flex-1" aria-label="Products">
                <header className="mb-6 mt-1 md:mt-[6px]">
                    <h1 className="text-2xl font-semibold">PC Upgrades</h1>
                </header>

                {loading && (
                    <p className="text-sm text-ws-gray">Loading products...</p>
                )}

                {error && !loading && (
                    <p className="text-sm text-destructive">{error}</p>
                )}

                {!loading && !error && products.length === 0 && (
                    <p className="text-sm text-ws-gray">No products found.</p>
                )}

                {!loading && !error && products.length > 0 && (
                    <div
                        className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                        {products.map((product) => (
                            <article
                                key={product.id}
                                className="flex flex-col rounded-lg border border-border bg-card hover:shadow-md transition-shadow"
                            >
                                <div
                                    className="aspect-[4/3] w-full bg-secondary flex items-center justify-center overflow-hidden rounded-t-lg">
                                    {product.image ? (
                                        <img
                                            src={getImageUrl(product.image)}
                                            alt={product.name}
                                            className="w-full h-full object-cover"
                                        />
                                    ) : (
                                        <span className="text-xs text-ws-gray">No image</span>
                                    )}
                                </div>

                                <div
                                    className="flex flex-col gap-1 px-4 py-3 flex-1">
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

                                    <div
                                        className="flex items-center justify-between px-4 pb-3 pt-1">
                    <span className="text-base font-semibold">
                      {typeof product.price === "number"
                          ? `${product.price.toFixed(2)} €`
                          : product.price}
                    </span>
                                        <Button asChild size="sm"
                                                className="text-xs px-3 py-1">
                                            <Link
                                                to={`/products/${product.id}`}>View
                                                details</Link>
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


export default HomePage


