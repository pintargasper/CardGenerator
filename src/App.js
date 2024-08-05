import {BrowserRouter, Route, Routes} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css"
import Index from "./components/Index";
import Generator from "./components/Generator";
import LiveCodeEditor from "./components/LiveCodeEditor";
import Navigation from "./components/navigation/Navigation";
import Page404 from "./components/pages/404"

const App = () => {
    return (
        <BrowserRouter>
            <Navigation/>
            <Routes>
                <Route path="/" element={<Index />} />
                <Route path="/generator" element={<Generator />} />
                <Route path="/create" element={<LiveCodeEditor />} />
                <Route path="*" element={<Page404 />} />
            </Routes>
        </BrowserRouter>
    );
};
export default App;