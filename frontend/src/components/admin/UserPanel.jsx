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
import getUsers, { updateUser } from "@/services/api.users.js";

const VITE_API_USERS_URL = import.meta.env.VITE_API_USERS_URL;

const UserPanel = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingUserId, setEditingUserId] = useState(null);
  const [editValues, setEditValues] = useState({});

  useEffect(() => {
    let cancelled = false;

    async function fetchUsers() {
      try {
        setLoading(true);
        setError(null);
        const data = await getUsers();
        if (!cancelled) {
          setUsers(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to fetch users",
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    fetchUsers();

    return () => {
      cancelled = true;
    };
  }, []);

  const handleEdit = (user) => {
    setEditingUserId(user.id);
    setEditValues({
      uuid: user.uuid,
      firstname: user.firstname ?? "",
      lastname: user.lastname ?? "",
      email: user.email ?? "",
      role: user.role ?? "USER",
      isActive: !!user.isActive,
    });
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditValues((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSave = async (user) => {
    const { uuid, id } = user;

    setUsers((prev) =>
      prev.map((u) => (u.id === id ? { ...u, ...editValues } : u)),
    );

    setEditingUserId(null);
    setEditValues({});

    try {
      const updatedUser = await updateUser(uuid, editValues);

      setUsers((prev) => prev.map((u) => (u.id === id ? updatedUser : u)));
    } catch (err) {
      alert(err.message || "Failed to update user");
    }
  };

  const handleCancel = () => {
    setEditingUserId(null);
    setEditValues({});
  };

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl mb-6">Users</h1>
      </div>

      {loading && <p className="text-sm text-ws-gray">Loading users...</p>}

      {error && !loading && <p className="text-sm text-destructive">{error}</p>}

      {!loading && !error && users.length === 0 && (
        <p className="text-sm text-ws-gray">No users found.</p>
      )}

      {!loading && !error && users.length > 0 && (
        <Table>
          <TableHeader className="bg-ws-white">
            <TableRow>
              <TableHead className="w-[70px]">ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Role</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right w-[180px]">Actions</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>

                <TableCell>
                  {editingUserId === user.id ? (
                    <>
                      <input
                        type="text"
                        name="firstname"
                        value={editValues.firstname ?? ""}
                        onChange={handleChange}
                        className="border px-1 rounded py-1 w-40 mr-1"
                      />
                      <input
                        type="text"
                        name="lastname"
                        value={editValues.lastname ?? ""}
                        onChange={handleChange}
                        className="border px-1 rounded py-1 w-40"
                      />
                    </>
                  ) : (
                    `${user.firstname} ${user.lastname}`
                  )}
                </TableCell>

                <TableCell>
                  {editingUserId === user.id ? (
                    <input
                      type="email"
                      name="email"
                      value={editValues.email ?? ""}
                      onChange={handleChange}
                      className="border py-1 px-1 rounded w-80"
                    />
                  ) : (
                    user.email
                  )}
                </TableCell>

                <TableCell>
                  {editingUserId === user.id ? (
                    <select
                      name="role"
                      value={editValues.role ?? "USER"}
                      onChange={handleChange}
                      className="border px-1 py-1 rounded"
                    >
                      <option value="ADMIN">ADMIN</option>
                      <option value="USER">USER</option>
                    </select>
                  ) : (
                    user.role
                  )}
                </TableCell>

                <TableCell>
                  {editingUserId === user.id ? (
                    <div className="flex gap-2">
                      <label>
                        Active
                        <input
                          type="checkbox"
                          name="isActive"
                          checked={!!editValues.isActive}
                          onChange={handleChange}
                          className="ml-1"
                        />
                      </label>
                    </div>
                  ) : user.isActive ? (
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
                  {editingUserId === user.id ? (
                    <>
                      <Button
                        variant="outline"
                        onClick={() => handleSave(user)}
                      >
                        <Check className="w-4 h-4" />
                      </Button>
                      <Button variant="destructive" onClick={handleCancel}>
                        <X className="w-4 h-4" />
                      </Button>
                    </>
                  ) : (
                    <>
                      <Button
                        variant="outline"
                        onClick={() => handleEdit(user)}
                      >
                        <Pencil className="w-4 h-4" />
                      </Button>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
};

export default UserPanel;
