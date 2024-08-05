import { Navbar, Nav, NavDropdown, Dropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const CustomNavbar = () => {
    const { t, i18n } = useTranslation();

    const changeLanguage = (language) => {
        i18n.changeLanguage(language);
    };

    return (
        <Navbar bg="light" variant="light" expand="lg" sticky="top">
            <Navbar.Brand as={Link} to="/" style={{ marginLeft: "20px" }}>{t("app_name")}</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="mr-auto">
                    <Nav.Link as={Link} to="/generator">{t("navigation.links.generator")}</Nav.Link>
                    <Nav.Link as={Link} to="/create">{t("navigation.links.create_card")}</Nav.Link>
                </Nav>
                <Nav style={{ marginLeft: "auto", marginRight: "20px" }}>
                    <Dropdown>
                        <style>
                            {`.dropdown-toggle::before {
                                display: none !important;
                            }`}
                        </style>
                        <NavDropdown title={`${t("navigation.language.language")} (${i18n.language})`} drop={"start"}>
                            <NavDropdown.Item onClick={() => { changeLanguage("sl");}}>{`${t("navigation.language.slovene")} (sl)`}</NavDropdown.Item>
                            <NavDropdown.Item onClick={() => { changeLanguage("en");}}>{`${t("navigation.language.english")} (en)`}</NavDropdown.Item>
                        </NavDropdown>
                    </Dropdown>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    );
};

export default CustomNavbar;