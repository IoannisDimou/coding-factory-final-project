import {BrowserRouter, Routes, Route} from "react-router";
import HomePage from "@/components/pages/HomePage.jsx";
import Layout from "@/components/layout/Layout.jsx"

function App() {

    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route element={<Layout/>}>
                        <Route index element={<HomePage/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    )
}

export default App
