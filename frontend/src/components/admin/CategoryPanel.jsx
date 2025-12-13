import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button.jsx";
import { Pencil, Check, X, Plus } from "lucide-react";
import getCategories, {
  createCategory,
  updateCategory,
} from "@/services/api.categories.js";

const CategoryPanel = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [editingCategoryId, setEditingCategoryId] = useState(null);
  const [editValues, setEditValues] = useState({});
  const [addingNew, setAddingNew] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function fetchCategories() {
      try {
        setLoading(true);
        setError(null);
        const data = await getCategories();
        if (!cancelled) {
          setCategories(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to fetch categories",
          );
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchCategories();

    return () => {
      cancelled = true;
    };
  }, []);

  const handleEdit = (category) => {
    setEditingCategoryId(category.id);
    setEditValues({
      name: category.name ?? "",
      isActive: !!category.isActive,
    });
  };

  const handleAddNew = () => {
    setAddingNew(true);
    setEditValues({
      name: "",
      isActive: true,
    });
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setEditValues((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleCancel = () => {
    setEditingCategoryId(null);
    setAddingNew(false);
    setEditValues({});
  };

  const handleSave = async (category) => {
    try {
      if (addingNew) {
        const newCategory = await createCategory(editValues);
        setCategories((prev) => [...prev, newCategory]);
        setAddingNew(false);
      } else {
        const payload = {
          id: category.id,
          name: editValues.name,
          isActive: !!editValues.isActive,
        };
        const updated = await updateCategory(category.id, payload);
        setCategories((prev) =>
          prev.map((c) => (c.id === category.id ? updated : c)),
        );
        setEditingCategoryId(null);
      }
      setEditValues({});
    } catch (err) {
      alert(err.message || "Failed to save category");
    }
  };

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl">Categories</h1>
        <Button
          variant="outline"
          onClick={handleAddNew}
          disabled={addingNew || editingCategoryId}
        >
          <Plus className="w-4 h-4 mr-1" /> Add Category
        </Button>
      </div>

      {loading && <p className="text-sm text-ws-gray">Loading categories...</p>}
      {error && !loading && <p className="text-sm text-destructive">{error}</p>}

      <Table>
        <TableHeader className="bg-ws-white">
          <TableRow>
            <TableHead className="w-[70px]">ID</TableHead>
            <TableHead>Name</TableHead>
            <TableHead>Status</TableHead>
            <TableHead className="text-right w-[180px]">Actions</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {addingNew && (
            <TableRow>
              <TableCell>—</TableCell>
              <TableCell>
                <input
                  type="text"
                  name="name"
                  value={editValues.name}
                  onChange={handleChange}
                  className="border px-1 rounded py-1 w-full"
                  placeholder="Category Name"
                />
              </TableCell>
              <TableCell>
                <label className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    name="isActive"
                    checked={!!editValues.isActive}
                    onChange={handleChange}
                  />
                  Active
                </label>
              </TableCell>
              <TableCell className="text-right space-x-2">
                <Button variant="outline" onClick={() => handleSave(null)}>
                  <Check className="w-4 h-4" />
                </Button>
                <Button variant="destructive" onClick={handleCancel}>
                  <X className="w-4 h-4" />
                </Button>
              </TableCell>
            </TableRow>
          )}

          {}
          {categories.map((category) => (
            <TableRow key={category.id}>
              <TableCell>{category.id}</TableCell>

              <TableCell>
                {editingCategoryId === category.id ? (
                  <input
                    type="text"
                    name="name"
                    value={editValues.name}
                    onChange={handleChange}
                    className="border px-1 rounded py-1 w-full"
                  />
                ) : (
                  category.name
                )}
              </TableCell>

              <TableCell>
                {editingCategoryId === category.id ? (
                  <label className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      name="isActive"
                      checked={!!editValues.isActive}
                      onChange={handleChange}
                    />
                    Active
                  </label>
                ) : category.isActive ? (
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
                {editingCategoryId === category.id ? (
                  <>
                    <Button
                      variant="outline"
                      onClick={() => handleSave(category)}
                    >
                      <Check className="w-4 h-4" />
                    </Button>
                    <Button variant="destructive" onClick={handleCancel}>
                      <X className="w-4 h-4" />
                    </Button>
                  </>
                ) : (
                  <Button
                    variant="outline"
                    onClick={() => handleEdit(category)}
                  >
                    <Pencil className="w-4 h-4" />
                  </Button>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default CategoryPanel;
