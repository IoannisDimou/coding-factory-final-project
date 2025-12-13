import { useEffect, useMemo, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router";
import { Button } from "@/components/ui/button.jsx";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table.jsx";
import { Pencil, Trash2 } from "lucide-react";
import {
  createProduct,
  getProduct,
  updateProduct,
} from "@/services/api.products.js";
import getCategories from "@/services/api.categories.js";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function getImageUrl(image) {
  if (!image) return "";
  if (String(image).startsWith("http")) return image;
  if (String(image).startsWith("/")) return `${API_BASE_URL}${image}`;
  return `${API_BASE_URL}/${image}`;
}

const emptyForm = {
  name: "",
  description: "",
  price: "",
  stock: "",
  isActive: true,
  sku: "",
  brand: "",
  categoryId: "",
  imageUrl: "",
  imageFile: null,
};

export default function ProductForm() {
  const { productId } = useParams();
  const isEditing = Boolean(productId);

  const navigate = useNavigate();

  const [form, setForm] = useState(emptyForm);
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const specs = useMemo(() => {
    const s = form?.specs ?? form?.productSpecs ?? form?.productSpecsList ?? [];
    return Array.isArray(s) ? s : [];
  }, [form]);

  const fileInputRef = useRef(null);

  const goBack = () => {
    if (returnTo) navigate(returnTo, { replace: true });
    else navigate(-1);
  };

  useEffect(() => {
    let cancelled = false;

    (async () => {
      try {
        const data = await getCategories();
        if (!cancelled) setCategories(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error(e);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      if (!isEditing) {
        setForm(emptyForm);
        return;
      }

      try {
        setLoading(true);
        setError(null);

        const data = await getProduct(productId);

        if (cancelled) return;

        setForm((prev) => ({
          ...prev,
          id: data?.id,
          name: data?.name ?? "",
          description: data?.description ?? "",
          price: data?.price ?? "",
          stock: data?.stock ?? "",
          isActive: data?.isActive ?? true,
          sku: data?.sku ?? "",
          brand: data?.brand ?? "",
          categoryId: data?.category?.id ? String(data.category.id) : "",
          imageUrl: data?.image ? getImageUrl(data.image) : "",
          imageFile: null,

          image: data?.image ?? "",
          specs: Array.isArray(data?.specs) ? data.specs : undefined,
          productSpecs: Array.isArray(data?.productSpecs)
            ? data.productSpecs
            : undefined,
        }));
      } catch (e) {
        if (!cancelled)
          setError(e instanceof Error ? e.message : "Failed to load product");
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [isEditing, productId]);

  useEffect(() => {
    return () => {
      if (form.imageUrl?.startsWith("blob:"))
        URL.revokeObjectURL(form.imageUrl);
    };
  }, [form.imageUrl]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleChooseFile = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setForm((prev) => {
      if (prev.imageUrl?.startsWith("blob:"))
        URL.revokeObjectURL(prev.imageUrl);

      return {
        ...prev,
        imageFile: file,
        imageUrl: URL.createObjectURL(file),
      };
    });
  };

  const location = useLocation();
  const returnTo = location.state?.returnTo;

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError(null);

      const payload = {
        ...(isEditing ? { id: Number(productId) } : {}),
        name: form.name,
        description: form.description,
        price: form.price === "" ? null : Number(form.price),
        stock: form.stock === "" ? null : Number(form.stock),
        isActive: !!form.isActive,
        sku: form.sku,
        brand: form.brand,
        categoryId: form.categoryId === "" ? null : Number(form.categoryId),
        oldImageUrl: isEditing ? form.image || "" : "",
      };

      const formData = new FormData();
      formData.append(
        "product",
        new Blob([JSON.stringify(payload)], { type: "application/json" }),
      );

      if (form.imageFile instanceof File) {
        formData.append("image", form.imageFile);
      }

      if (isEditing) await updateProduct(productId, formData);
      else await createProduct(formData);

      navigate(returnTo ?? "/admin?panel=Products", { replace: true });
    } catch (e) {
      console.error(e);
      setError(e instanceof Error ? e.message : "Failed to save product");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="max-w-3xl mx-auto mt-10 p-6 border rounded-xl bg-card">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold">
          {isEditing ? "Edit Product" : "Create Product"}
        </h1>

        <Button variant="outline" onClick={goBack}>
          Back
        </Button>
      </div>

      {error && <p className="text-sm text-destructive mb-4">{error}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-medium mb-1">Name</label>
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2"
            required
          />
        </div>

        <div>
          <label className="block font-medium mb-1">Description</label>
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2"
            rows={3}
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block font-medium mb-1">Price</label>
            <input
              type="number"
              name="price"
              value={form.price}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              min="0"
              step="0.01"
              required
            />
          </div>
          <div className="flex-1">
            <label className="block font-medium mb-1">Stock</label>
            <input
              type="number"
              name="stock"
              value={form.stock}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              min="0"
              step="1"
              required
            />
          </div>
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block font-medium mb-1">SKU</label>
            <input
              name="sku"
              value={form.sku}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>
          <div className="flex-1">
            <label className="block font-medium mb-1">Brand</label>
            <input
              name="brand"
              value={form.brand}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
            />
          </div>
        </div>

        <div>
          <label className="block font-medium mb-1">Category</label>
          <select
            name="categoryId"
            value={form.categoryId}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2"
            required
          >
            <option value="">Select category</option>
            {categories.map((c) => (
              <option key={c.id} value={String(c.id)}>
                {c.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block font-medium mb-1">Upload Image</label>

          <div className="flex items-center gap-3">
            <Button type="button" variant="outline" onClick={handleChooseFile}>
              Choose Image
            </Button>

            <input
              type="file"
              ref={fileInputRef}
              className="hidden"
              accept="image/*"
              onChange={handleFileChange}
            />
          </div>

          {form.imageUrl && (
            <img
              src={form.imageUrl}
              alt="Preview"
              className="mt-3 h-32 w-32 object-cover rounded border"
            />
          )}
        </div>

        <label className="flex gap-2 items-center">
          <input
            type="checkbox"
            name="isActive"
            checked={!!form.isActive}
            onChange={handleChange}
          />
          Active
        </label>

        {specs.length > 0 && (
          <div className="pt-4">
            <h2 className="font-semibold mb-2">Specifications (read-only)</h2>
            <Table>
              <TableHeader className="bg-ws-white">
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Spec</TableHead>
                  <TableHead>Value</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {specs.map((s) => (
                  <TableRow key={s.id}>
                    <TableCell>{s.id}</TableCell>
                    <TableCell>{s.name}</TableCell>
                    <TableCell>{s.value}</TableCell>
                    <TableCell className="flex gap-2 justify-end">
                      <Button size="icon" variant="outline" disabled>
                        <Pencil />
                      </Button>
                      <Button size="icon" variant="destructive" disabled>
                        <Trash2 />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}

        <Button type="submit" disabled={loading}>
          {loading
            ? "Saving..."
            : isEditing
              ? "Update Product"
              : "Save Product"}
        </Button>
      </form>
    </main>
  );
}
