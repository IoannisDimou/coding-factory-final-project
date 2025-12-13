import { useEffect, useState } from "react";
import ProductPanel from "@/components/admin/ProductPanel.jsx";
import UserPanel from "@/components/admin/UserPanel.jsx";
import CategoryPanel from "@/components/admin/CategoryPanel.jsx";
import { useSearchParams } from "react-router";

const panels = [
  { name: "Users" },
  { name: "Products" },
  { name: "Categories" },
];
const AdminPanelPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const panelParam = searchParams.get("panel");

  const [activePanel, setActivePanel] = useState(panelParam ?? "Users");

  useEffect(() => {
    setActivePanel(panelParam ?? "Users");
  }, [panelParam]);

  const selectPanel = (name) => {
    setSearchParams({ panel: name }, { replace: true });
  };

  const renderPanel = () => {
    switch (activePanel) {
      case "Users":
        return <UserPanel />;
      case "Products":
        return <ProductPanel />;
      case "Categories":
        return <CategoryPanel />;
      default:
        return null;
    }
  };
  return (
    <main className="mt-4 md:mt-6 lg:mt-8 flex flex-col md:flex-row gap-8 md:gap-10 lg:gap-16 pb-16 border-0">
      <aside
        className="w-full md:w-72 lg:w-80 shrink-0"
        aria-label="Admin panels"
      >
        <h2 className="text-2xl font-semibold leading-tight mb-6">Panels</h2>

        <ul className="space-y-1 text-sm">
          {panels.map((panel) => {
            const isActive = activePanel === panel.name;

            return (
              <li key={panel.name}>
                <button
                  type="button"
                  onClick={() => selectPanel(panel.name)}
                  className={[
                    "w-full flex items-center justify-between p-3 rounded-md border border-border shadow-sm",
                    "transition-all cursor-pointer",
                    isActive
                      ? "bg-ws-ice text-white shadow-md"
                      : "bg-card text-ws-dark hover:bg-secondary hover:text-ws-dark",
                  ].join(" ")}
                >
                  <span className="truncate">{panel.name}</span>
                </button>
              </li>
            );
          })}
        </ul>
      </aside>

      <section className="flex-1" aria-label="Admin content">
        {renderPanel()}
      </section>
    </main>
  );
};

export default AdminPanelPage;
