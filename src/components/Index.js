import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const Index = () => {
    const { t } = useTranslation();

    return (
        <div className={"wrapper"}>
            <div className={"content"}>
                <div className={"container mt-2"}>
                    <div className={"row align-items-center"}>
                        <div className={"col-md-4 text-center"}>
                            <img
                                src={"/files/img/logo192.webp"}
                                srcSet={"/files/img/logo192.webp 480w, /files/img/logo512.webp 1080w"}
                                sizes={"50vw"}
                                alt={"Profile logo"}
                                width={"192"}
                                height={"192"}
                                className={"img-fluid img-profile rounded-circle"}
                            />
                        </div>
                        <div className={"about col-md-8"}>
                            <h1 className={"display-5"}>{t("app_name")}</h1>
                            <p className={"lead"}>{t("index.what_is.description")}</p>
                            <p className={"text-muted"}>{t("index.version.title")}: 1.1.0</p>
                        </div>
                    </div>
                </div>

                <div className={"container bg-light mt-3"}>
                    <div className={"row mt-2"}>
                        <p className={"fw-bold h5 to-center w-100 text-center"}>
                            {t("index.supported_languages.title")}
                        </p>
                        <div className={"d-flex justify-content-center align-items-center"}>
                            <ul>
                                <li>{t("index.supported_languages.language_1")}</li>
                                <li>{t("index.supported_languages.language_2")}</li>
                            </ul>
                        </div>
                    </div>

                    <div className={"row mt-2"}>
                        <p className={"fw-bold h5 to-center w-100 text-center"}>
                            {t("index.what_you_need.title")}
                        </p>
                        <div className={"col-md-12"}>
                            <div className={"d-flex flex-column justify-content-center align-items-center"}>
                                <ul>
                                    <li>
                                        {t("index.what_you_need.1.description_1/2")}
                                        &nbsp;
                                        <a href={"https://github.com/pintargasper/CardGenerator/releases/latest/download/Cards.xlsx"}
                                           target={"_blank"}
                                           rel={"noreferrer"}
                                           className={"link"}>{t("index.what_you_need.1.description_1_link")}</a> -> {t("index.what_you_need.1.description_2/2")}
                                        &nbsp;
                                        <a href={"https://github.com/pintargasper/CardGenerator/releases/latest"}
                                           target={"_blank"}
                                           rel={"noreferrer"}
                                           className={"link"}>{t("index.what_you_need.1.description_2_link")}</a>
                                    </li>
                                    <li>{t("index.what_you_need.2.description_1/2")}
                                        &nbsp;
                                        <a href={"https://github.com/pintargasper/CardGenerator/releases/latest/download/images.zip"}
                                           target={"_blank"}
                                           rel={"noreferrer"}
                                           className={"link"}>{t("index.what_you_need.2.description_1_link")}</a> -> {t("index.what_you_need.2.description_2/2")}
                                        &nbsp;
                                        <a href={"https://github.com/pintargasper/CardGenerator/releases/latest"}
                                           target={"_blank"}
                                           rel={"noreferrer"}
                                           className={"link"}>{t("index.what_you_need.1.description_2_link")}</a>
                                    </li>
                                    <li>{t("index.what_you_need.3.description_1/2")} -> {t("index.what_you_need.3.description_2/2")}
                                        &nbsp;
                                        <Link to={"./create"} className={"link"}>{t("index.what_you_need.3.description_2_link")}</Link>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <div className={"row mt-2"}>
                        <p className={"fw-bold h5 to-center w-100 text-center"}>
                            {t("index.supported_formats.title")}
                        </p>
                        <div className={"col-md-12"}>
                            <div className={"d-flex flex-column justify-content-center align-items-center"}>
                                <ul>
                                    <li>
                                        A4 -> 3 x
                                        3 {t("index.supported_formats.description_1/3")}: <b>{t("index.supported_formats.description_2/3")}:</b> 240px; <b>{t("index.supported_formats.description_3/3")}:</b> 332px
                                    </li>
                                    <li>
                                        13x18 -> 2 x
                                        2 {t("index.supported_formats.description_1/3")}: <b>{t("index.supported_formats.description_2/3")}:</b> 240px; <b>{t("index.supported_formats.description_3/3")}:</b> 332px
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <div className={"row mt-2"}>
                        <p className={"fw-bold h5 to-center w-100 text-center"}>
                            {t("index.currently_additional_components.title")}
                        </p>

                        <div className={"component-details text-center"}>
                            <div style={{ display: "inline-block", textAlign: "left" }}>
                                <span className={"h6"}>
                                    {t("index.currently_additional_components.components.loading_bar.title")}
                                </span>
                                <p>{t("index.currently_additional_components.components.loading_bar.description")}</p>
                                <p><b>{t("index.currently_additional_components.components.loading_bar.use.title")}:</b>
                                </p>
                                <code className={"text-black"}>
                                    &lt;
                                    <span>LoadingBar</span> <br/>
                                    <span style={{ marginLeft: "20px" }}>title</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.title")}"&#125;<br/>
                                    <span style={{ marginLeft: "20px" }}>progress</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.progress")}"&#125;<br/>
                                    <span style={{ marginLeft: "20px" }}>textColor</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.text_color")}"&#125;<br/>
                                    <span style={{ marginLeft: "20px" }}>loadingBarColor</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.loading_bar_color")}"&#125;<br/>
                                    <span style={{ marginLeft: "20px" }}>fontFamily</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.font_family")}"&#125;<br/>
                                    <span style={{ marginLeft: "20px" }}>fontSize</span>=&#123;"{t("index.currently_additional_components.components.loading_bar.use.parts.font_size")}"&#125;<br/>
                                    /&gt;
                                </code>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default Index;