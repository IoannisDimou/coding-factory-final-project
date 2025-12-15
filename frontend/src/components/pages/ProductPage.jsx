import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getProduct } from "@/services/api.products.js";
import { Button } from "@/components/ui/button.jsx";
import { ArrowLeftIcon } from "@radix-ui/react-icons";
import { useCart } from "@/hooks/useCart.js";
import { toast } from "sonner";
import { formatPrice } from "@/utils/formatPrice.js";

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
  const { productId } = useParams();
  const navigate = useNavigate();
  const { addItem } = useCart();

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
            err instanceof Error ? err.message : "Failed to fetch product",
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
        <p className="text-sm text-destructive">
          {error || "Product not found"}
        </p>
        <Button onClick={() => navigate("/")}>Back to home</Button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto mt-12 p-6 md:p-8 border rounded-xl bg-card space-y-6">
      <Button
        variant="outline"
        size="sm"
        className="mb-2"
        onClick={() => navigate(-1)}
      >
        <ArrowLeftIcon className="w-5 h-5 mr-2 -ml-1" /> Back
      </Button>

      <div className="grid gap-4 md:grid-cols-3">
        <div className="md:col-span-2 space-y-3">
          <div className="relative aspect-[4/3] w-full overflow-hidden flex items-center justify-center bg-card p-3 rounded-lg">
            {product.image ? (
              <img
                src={getImageUrl(product.image)}
                alt={product.name}
                className="w-full h-full object-contain"
              />
            ) : (
              <span className="text-xs text-ws-gray">No image</span>
            )}
          </div>
          <section className="rounded-lg border border-border bg-background p-4">
            <h2 className="text-base font-semibold mb-3">Details</h2>

            <div className="grid gap-3 sm:grid-cols-2 text-sm">
              <div className="flex justify-between gap-3">
                <span className="text-ws-gray">Brand</span>
                <span className="font-medium">{product.brand || "-"}</span>
              </div>

              <div className="flex justify-between gap-3">
                <span className="text-ws-gray">SKU</span>
                <span className="font-medium">{product.sku || "-"}</span>
              </div>

              <div className="flex justify-between gap-3">
                <span className="text-ws-gray">Stock</span>
                <span className="font-medium">
                  {typeof product.stock === "number" ? product.stock : "-"}
                </span>
              </div>

              <div className="flex justify-between gap-3">
                <span className="text-ws-gray">Status</span>
                <span className="font-medium">
                  {product.isActive ? "Active" : "Inactive"}
                </span>
              </div>

              <div className="flex justify-between gap-3 sm:col-span-2">
                <span className="text-ws-gray">Category</span>
                <span className="font-medium">
                  {product.category?.name || "-"}
                </span>
              </div>
            </div>
          </section>

          <section className="rounded-lg border border-border bg-background p-4">
            <h2 className="text-base font-semibold mb-3">Description</h2>
            <p className="text-sm text-ws-gray leading-relaxed">
              {product.description || "-"}
            </p>
          </section>
        </div>

        <aside className="space-y-3">
          <div className="rounded-lg border border-border bg-background p-4">
            <div className="text-sm text-ws-gray">Price</div>
            <div className="text-2xl font-semibold mt-1">
              {typeof product.price === "number"
                ? `${formatPrice(product.price)} €`
                : product.price}
            </div>

            <Button
              className="w-full mt-4"
              onClick={() => {
                addItem({
                  id: product.id,
                  name: product.name,
                  price: product.price,
                  image: getImageUrl(product.image),
                  brand: product.brand,
                });
                toast.success("Item added to cart");
              }}
            >
              Add to cart
            </Button>
          </div>
        </aside>
      </div>
    </div>
  );
}
