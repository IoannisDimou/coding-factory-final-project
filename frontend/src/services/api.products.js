const API_PRODUCTS_URL = import.meta.env.VITE_API_PRODUCTS_URL;

export default async function getProducts() {
    const res = await fetch(`${API_PRODUCTS_URL}/products`);
    if (!res.ok) {
        throw new Error("Failed to fetch products");
    }
    const body = await res.json();

    if (Array.isArray(body.data)) {
        return body.data;
    }

    return [];
}

export async function getProduct(id) {
    const res = await fetch(`${API_PRODUCTS_URL}/products/${id}`);
    if (!res.ok) throw new Error("Failed to fetch product");
    return res.json();
}

