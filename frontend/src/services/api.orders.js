const API_PRODUCTS_URL = import.meta.env.VITE_API_PRODUCTS_URL;

const ORDERS_URL = `${API_PRODUCTS_URL}/orders`;
const PAYMENTS_URL = `${API_PRODUCTS_URL}/payments`;

async function processResponse(res, defaultMessage) {
  if (!res.ok) {
    let message = defaultMessage;
    try {
      const data = await res.json();
      if (typeof data?.description === "string" && data.description) {
        message = data.description;
      }
    } catch {
      // ignore }

      throw new Error(message);
    }
  }
  return res.json();
}

export async function createOrder(payload, accessToken) {
  const res = await fetch(ORDERS_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(payload),
  });
  return processResponse(res, "Failed to create order");
}

export async function createPayment(payload, accessToken) {
  const res = await fetch(PAYMENTS_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(payload),
  });
  return processResponse(res, "Failed to create payment");
}

export async function confirmPayment(paymentToken, accessToken) {
  const res = await fetch(`${PAYMENTS_URL}/confirm-payment`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ paymentToken }),
  });
  return processResponse(res, "Failed to confirm payment");
}

export async function getOrderByCode(orderCode, accessToken) {
  const res = await fetch(
    `${ORDERS_URL}/code/${encodeURIComponent(orderCode)}`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    },
  );
  return processResponse(res, "Failed to fetch order");
}
