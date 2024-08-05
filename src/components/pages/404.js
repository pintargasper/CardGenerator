import {Link} from "react-router-dom";
import React from "react";
import {useTranslation} from "react-i18next";

const Page404 = () => {
    const { t} = useTranslation();

    return (
        <div className="d-flex align-items-center justify-content-center mt-5">
            <div className="text-center">
                <h1 className="display-1 font-weight-bold text-danger">404</h1>
                <h2 className="my-4">{t("pages.404.short_description")} :(</h2>
                <p className="lead">
                    {t("pages.404.long_description")}.
                </p>
                <Link to="/">
                    <input
                        type="button"
                        value={t("pages.404.button")}
                        className="btn btn-light w-100 mt-2"
                        style={{height: "auto", fontSize: "large"}}
                    />
                </Link>
            </div>
        </div>
    );
};

export default Page404;