import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router";
import { useAuth } from "@/hooks/useAuth.js";
import { getOrderByCode } from "@/services/api.orders.js";
import { Button } from "@/components/ui/button.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Label } from "@/components/ui/label.jsx";
import { toast } from "sonner";

export default function OrderLookupPage() {
  const { isAuthenticated, accessToken } = useAuth();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const initialCode = searchParams.get("code") ?? "";
  const [orderCode, setOrderCode] = useState(initialCode);
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    document.title = "Your order";
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }
    if (initialCode) {
      fetchOrder(initialCode);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  const fetchOrder = async (code) => {
    try {
      setLoading(true);
      setOrder(null);
      const data = await getOrderByCode(code, accessToken);
      setOrder(data);
    } catch (err) {
      toast.error(
        err instanceof Error ? err.message : "Failed to load order details",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const trimmed = orderCode.trim();
    if (!trimmed) {
      toast.error("Enter an order code");
      return;
    }
    setSearchParams({ code: trimmed });
    fetchOrder(trimmed);
  };

  if (!isAuthenticated) {
    return (
      <div className="max-w-3xl mx-auto mt-12 p-8 space-y-4">
        <h1 className="text-2xl font-semibold">Your orders</h1>
        <p className="text-sm text-ws-gray">
          You need to log in to view your orders.
        </p>
        <Button onClick={() => navigate("/login")}>Go to login</Button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto mt-12 p-8 space-y-6">
      <h1 className="text-2xl font-semibold">Find your order</h1>
      <p className="text-sm text-ws-gray">
        We have sent you an email with your order code. Paste it below to view
        the full details.
      </p>

      <form onSubmit={handleSubmit} className="flex gap-3 items-end">
        <div className="flex-1">
          <Label htmlFor="orderCode" className="mb-1">
            Order code
          </Label>
          <Input
            id="orderCode"
            value={orderCode}
            onChange={(e) => setOrderCode(e.target.value)}
          />
        </div>
        <Button type="submit" disabled={loading}>
          {loading ? "Loading..." : "View order"}
        </Button>
      </form>

      {order && (
        <div className="mt-6 border rounded-xl bg-card p-6 space-y-4">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-lg font-semibold">Order {order.orderCode}</h2>
              <p className="text-sm text-ws-gray">
                Status: {order.status} • Total:{" "}
                {Number(order.totalPrice).toFixed(2)} €
              </p>
            </div>
            <p className="text-xs text-ws-gray">
              Placed: {order.createdAt?.replace("T", " ").replace("Z", "")}
            </p>
          </div>

          {order.shippingAddress && (
            <div>
              <h3 className="text-sm font-semibold mb-1">Shipping address</h3>
              <p className="text-sm">
                {order.shippingAddress.street}
                <br />
                {order.shippingAddress.city} {order.shippingAddress.zipcode}
                <br />
                {order.shippingAddress.country}
              </p>
            </div>
          )}

          {Array.isArray(order.items) && order.items.length > 0 && (
            <div>
              <h3 className="text-sm font-semibold mb-2">Items</h3>
              <div className="space-y-2">
                {order.items.map((item) => (
                  <div
                    key={item.productId}
                    className="flex justify-between text-sm"
                  >
                    <span>
                      {item.productName} × {item.quantity}
                    </span>
                    <span>
                      {Number(item.price * item.quantity).toFixed(2)} €
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
