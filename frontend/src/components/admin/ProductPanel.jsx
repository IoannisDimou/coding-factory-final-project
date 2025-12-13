import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button.jsx";
import { Trash2, Pencil, Plus } from "lucide-react";
import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router";
import getProducts from "@/services/api.products.js";

const ProductPanel = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const returnTo = location.pathname + location.search;

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
  }, []);

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl">Products</h1>
        <Button
          variant="outline"
          onClick={() =>
            navigate("/admin/products/new", { state: { returnTo } })
          }
        >
          Add Product
        </Button>
      </div>
      {loading && <p className="text-sm text-ws-gray">Loading products...</p>}

      {error && !loading && <p className="text-sm text-destructive">{error}</p>}

      {!loading && !error && products.length === 0 && (
        <p className="text-sm text-ws-gray">No products found.</p>
      )}

      {!loading && !error && products.length > 0 && (
        <Table>
          <TableHeader className="bg-ws-white">
            <TableRow>
              <TableHead className="w-[70px]">ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Price</TableHead>
              <TableHead>Stock</TableHead>
              <TableHead>Brand</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right w-[150px]">Actions</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {products.map((product) => (
              <TableRow key={product.id}>
                <TableCell>{product.id}</TableCell>
                <TableCell>{product.name}</TableCell>
                <TableCell>${product.price.toFixed(2)}</TableCell>
                <TableCell>{product.stock}</TableCell>
                <TableCell>{product.brand}</TableCell>
                <TableCell>
                  {product.isActive ? (
                    <span className="text-notification font-semibold">
                      Active
                    </span>
                  ) : (
                    <span className="text-destructive font-semibold">
                      Inactive
                    </span>
                  )}
                </TableCell>
                <TableCell className="text-right space-x-2">
                  <Button
                    variant="outline"
                    onClick={() =>
                      navigate(`/admin/products/${product.id}/edit`, {
                        state: { returnTo },
                      })
                    }
                  >
                    <Pencil className="w-4 h-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
};

export default ProductPanel;
