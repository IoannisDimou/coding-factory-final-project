import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router";
import {getProduct} from "@/services/api.products.js";
import {Button} from "@/components/ui/button.jsx";
import {ArrowLeftIcon} from "@radix-ui/react-icons"

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function getImageUrl(image) {
    if (!image) return null;
    if (image.startsWith("http")) return image;

    if (image.startsWith("/")) {
        return `${API_BASE_URL}${image}`;
    }
    return `${API_BASE_URL}/${image}`;
}

export default function ProductPage() {
    const {productId} = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;

        async function fetchProduct() {
            try {
                setLoading(true);
                setError(null);
                const data = await getProduct(productId);
                if (!cancelled) {
                    setProduct(data);
                    document.title = data?.name || "Product";
                }
            } catch (err) {
                if (!cancelled) {
                    setError(
                        err instanceof Error ? err.message : "Failed to fetch product"
                    );
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        }

        fetchProduct();

        return () => {
            cancelled = true;
        };
    }, [productId]);

    if (loading) {
        return (
            <div className="max-w-3xl mx-auto mt-12 p-8">
                <p className="text-sm text-ws-gray"> Loading product... </p>
            </div>
        );
    }

    if (error || !product) {
        return (
            <div className="max-w-3xl mx-auto mt-12 p-8 space-y-4">
                <p className="text-sm text-destructive">{error || "Product not found"}</p>
                <Button onClick={() => navigate("/")}>Back to home</Button>
            </div>
        );
    }

    const specs = Array.isArray(product.productSpecs) ? product.productSpecs : [];
    return (
        <div
            className="max-w-4xl mx-auto mt-12 p-6 md:p-8 border rounded-xl bg-card space-y-6">
            <Button
                variant="outline"
                size="sm"
                className="mb-2"
                onClick={() => navigate(-1)}
            >
                <ArrowLeftIcon className="w-5 h-5 mr-2 -ml-1"/> Back
            </Button>

            <div className="flex flex-col md:flex-row gap-6">
                <div
                    className="w-full md:w-64 rounded-lg bg-secondary flex items-center justify-center overflow-hidden shrink-0">
                    {product.image ? (
                        <img src={getImageUrl(product.image)} alt={product.name}
                             className="text-xs text-ws-gray"/>

                    ) : (
                        <span className="text-xs text-ws-gray">No image</span>
                    )
                    }
                </div>

                <div className="flex-1 space-y-3">
                    <h1 className="text-2xl font-semibold">{product.name}</h1>
                    {product.brand && (
                        <p className="text-sm text-ws-gray">Brand: {product.brand}</p>
                    )}
                    <p className="text-sm text-ws-gray">{product.description}</p>

                    <div className="flex items-center gap-4 mt-2">
                        <span className="text-2xl font-semibold">
                            {typeof product.price === "number"
                                ? `${product.price.toFixed(2)} €` : product.price}
                        </span>
                        {typeof product.stock === "number" && (
                            <span className="text-sm text-ws-gray">
                                {product.stock > 0 ? `${product.stock} in stock` : "Out of stock"}
                            </span>
                        )}
                    </div>

                    <div className="mt-4">
                        <Button className="w-full md:w-auto">
                            Add to cart
                        </Button>
                    </div>
                </div>
            </div>

            {specs.length > 0 && (
                <section className="mt-4">
                    <h2 className="text-lg font-semibold mb-2">Specifications</h2>
                    <div
                        className="rounded-lg border border-border bg-background">
                        <dl className="divide-y divide-border">
                            {specs.map((spec) => (
                                <div
                                    key={spec.id}
                                    className="flex items-center justify-between px-4 py-2 text-sm"
                                >
                                    <dt className="text-ws-gray">{spec.name}</dt>
                                    <dd className="font-medium text-ws-dark ml-4">
                                        {spec.value}
                                    </dd>
                                </div>
                            ))}
                        </dl>
                    </div>
                </section>
            )}
        </div>
    );
}






