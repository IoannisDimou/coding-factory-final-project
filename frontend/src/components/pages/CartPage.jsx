import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useCart } from "@/hooks/useCart.js";
import { useAuth } from "@/hooks/useAuth.js";
import { Button } from "@/components/ui/button.jsx";
import { Input } from "@/components/ui/input.jsx";
import { Label } from "@/components/ui/label.jsx";
import { toast } from "sonner";
import {
  createOrder,
  createPayment,
  confirmPayment,
} from "@/services/api.orders.js";
import { formatPrice } from "@/utils/formatPrice.js";

const TAX_RATE = 0.24;

export default function CartPage() {
  const navigate = useNavigate();
  const { items, updateQuantity, removeItem, clearCart } = useCart();

  const { isAuthenticated, accessToken, user } = useAuth();

  const [shipping, setShipping] = useState({
    street: "",
    city: "",
    zipcode: "",
    country: "",
  });

  const [cardNumber, setCardNumber] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    document.title = "Cart";
  }, []);

  if (items.length === 0) {
    return (
      <div className="max-w-3xl mx-auto mt-12 p-8">
        <h1 className="text-2xl font-semibold mb-4">Cart</h1>
        <p className="text-sm text-ws-gray mb-4">Your cart is empty.</p>
        <Button onClick={() => navigate("/")}> Browse products </Button>
      </div>
    );
  }

  const subtotal = items.reduce(
    (sum, item) => sum + Number(item.price) * Number(item.quantity),
    0,
  );

  const taxTotal = subtotal * TAX_RATE;
  const grandTotal = subtotal + taxTotal;

  const handleCheckout = async (a) => {
    a.preventDefault();

    if (!isAuthenticated) {
      navigate("/login", { state: { from: "/cart" } });
      return;
    }

    if (!user?.uuid) {
      toast.error("Please sign in to place an order.");
      return;
    }

    if (!cardNumber.trim()) {
      toast.error("Enter a card number (dummy card number: 6666000000000000)");
      return;
    }

    try {
      setSubmitting(true);

      const orderPayload = {
        userUuid: user.uuid,
        shippingAddress: shipping,
        items: items.map((item) => ({
          productId: item.id,
          quantity: item.quantity,
        })),
      };
      const order = await createOrder(orderPayload, accessToken);

      const payment = await createPayment(
        {
          orderId: order.id,
          method: "CREDIT_CARD",
          cardNumber,
        },
        accessToken,
      );
      await confirmPayment(payment.paymentToken, accessToken);

      toast.success(
        `Order placed. We've emailed you a confirmation with a code to track your order.`,
      );

      clearCart();
      navigate("/");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Failed to place order");
    } finally {
      setSubmitting(false);
    }
  };

  const onShippingChange = (s) => (c) =>
    setShipping((previous) => ({
      ...previous,
      [s]: c.target.value,
    }));

  return (
    <form
      onSubmit={handleCheckout}
      className="max-w-4xl mx-auto mt-12 p-6 md:p-8 border rounded-xl bg-card space-y-6"
    >
      <h1 className="text-2xl font-semibold mb-4">Cart</h1>

      <div className="space-y-3">
        {items.map((item) => (
          <div
            key={item.id}
            className="flex items-center justify-between gap-4  pb-3"
          >
            <div className="flex items-center gap-3">
              {item.image && (
                <img
                  src={item.image}
                  alt={item.name}
                  className="w-16 h-16 rounded-md object-cover"
                />
              )}
              <div>
                <p className="text-sm font-medium">{item.name}</p>
                <p className="text-xs text-ws-gray">
                  {formatPrice(item.price)} €
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Input
                type="number"
                min={1}
                value={item.quantity}
                onChange={(e) => updateQuantity(item.id, e.target.value)}
                className="w-16"
              />
              <span className="text-sm font-semibold">
                {formatPrice(Number(item.price) * item.quantity)} €
              </span>

              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => removeItem(item.id)}
              >
                Remove
              </Button>
            </div>
          </div>
        ))}
      </div>

      <div className="pt-4 space-y-2">
        <div className="flex justify-between text-sm text-ws-gray">
          <span>Subtotal</span>
          <span>{formatPrice(subtotal)} €</span>
        </div>

        <div className="flex justify-between text-sm text-ws-gray">
          <span>Tax ({Math.round(TAX_RATE * 100)}%)</span>
          <span>{formatPrice(taxTotal)} €</span>
        </div>

        <div className="flex justify-between items-center">
          <span className="text-base font-semibold">Total</span>
          <span className="text-base font-semibold">
            {formatPrice(grandTotal)} €
          </span>
        </div>

        <div className="flex justify-end pt-5">
          <Button
            type="button"
            variant="destructive"
            size="sm"
            onClick={clearCart}
          >
            Clear cart
          </Button>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-2 mt-4">
        <div className="space-y-3">
          <h2 className="text-lg font-semibold">Shipping address</h2>

          <div>
            <Label htmlFor="street" className="mb-1">
              Street
            </Label>
            <Input
              id="street"
              value={shipping.street}
              onChange={onShippingChange("street")}
            />
          </div>

          <div>
            <Label htmlFor="city" className="mb-1">
              City
            </Label>
            <Input
              id="city"
              value={shipping.city}
              onChange={onShippingChange("city")}
            />
          </div>

          <div>
            <Label htmlFor="zipcode" className="mb-1">
              Zipcode
            </Label>
            <Input
              id="zipcode"
              value={shipping.zipcode}
              onChange={onShippingChange("zipcode")}
            />
          </div>

          <div>
            <Label htmlFor="country" className="mb-1">
              Country
            </Label>
            <Input
              id="country"
              value={shipping.country}
              onChange={onShippingChange("country")}
            />
          </div>
        </div>

        <div className="space-y-3">
          <h2 className="text-lg font-semibold">Payment</h2>
          <div>
            <Label htmlFor="cardNumber" className="mb-1">
              Card number
            </Label>
            <Input
              id="cardNumber"
              value={cardNumber}
              onChange={(e) => setCardNumber(e.target.value)}
              placeholder="6666 0000 0000 0000"
            />
            <p className="text-xs text-ws-gray mt-1">This is a demo payment.</p>
          </div>
        </div>
      </div>

      <Button
        type="submit"
        disabled={submitting}
        className="w-full md:w-auto mt-4"
      >
        {submitting ? "Placing order..." : "Place order"}
      </Button>
    </form>
  );
}
