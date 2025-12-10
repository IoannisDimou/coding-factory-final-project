import {useEffect} from "react"

const categories = [
    {name: "CPUs"},
    {name: "GPUs"}
]

const priceRanges = [
    {label: "NVIDIA"},
    {label: "AMD"}
]

const HomePage = () => {
    useEffect(() => {
        document.title = "Arctic Builds"
    }, [])

    return (
        <main
            className="mt-4 md:mt-6 lg:mt-8 flex flex-col md:flex-row gap-8 md:gap-10 lg:gap-16 pb-16 border-0"
            aria-label="Filters and product listing">
            <aside className="w-full md:w-72 lg:w-80 shrink-0"
                   aria-label="Product filters">
                <h2 className="text-2xl font-semibold leading-tight mb-6">Filters</h2>

                <section aria-labelledby="category-heading" className="mb-8">
                    <h3 id="category-heading"
                        className="text-base font-semibold mb-3">
                        Categories
                    </h3>
                    <ul className="space-y-1 text-sm">
                        {categories.map((cat) => (
                            <li
                                key={cat.name}
                                className="flex items-baseline justify-between gap-2 text-ws-dark hover:text-ws-gray cursor-pointer"
                            >
                                <span className="truncate">{cat.name}</span>
                                <span
                                    className="text-xs text-ws-gray">{cat.count}</span>
                            </li>
                        ))}
                    </ul>
                </section>

                <section aria-labelledby="price-heading" className="mb-8">
                    <h3 id="price-heading"
                        className="text-base font-semibold mb-3">
                        Price range
                    </h3>

                    <div className="flex items-center gap-2 mb-4">
                        <label className="w-1/2">
                            <span className="sr-only">Minimum price</span>
                            <input type="number" placeholder="From €"
                                   className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"/>
                        </label>
                        <span className="text-ws-gray text-sm">–</span>
                        <label className="w-1/2">
                            <span className="sr-only">Maximum price</span>
                            <input type="number" placeholder="To €"
                                   className="w-full rounded-full border border-border px-3 py-1 text-sm outline-none focus:border-ws-dark"/>
                        </label>
                    </div>
                </section>
                <section aria-labelledby="price-heading" className="mb-8">
                    <h3 id="price-heading"
                        className="text-base font-semibold mb-3">
                        Brands
                    </h3>

                    <div className="space-y-2 text-sm">
                        {priceRanges.map((range) => (
                            <label key={range.label}
                                   className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox"
                                       className="h-4 w-4 rounded border border-border"/>
                                <span className="flex-1">{range.label}</span>
                                <span
                                    className="text-xs text-ws-gray">{range.count}</span>
                            </label>
                        ))}
                    </div>
                </section>
            </aside>


            <section className="flex-1" aria-label="Products">
                <header className="mb-6 mt-1 md:mt-[6px]">
                    <h1 className="text-2xl font-semibold">PC Upgrades</h1>
                    <p className="text-sm text-ws-gray mt-1"></p>
                </header>
            </section>
        </main>
    )
}

export default HomePage


