import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";

const Index = () => {
    const { t} = useTranslation();

    return (
        <div className="container">
            <section id="about" className="bg-light section">
                <h1 className="text-center">{t("app_name")}</h1>
                <div className="section-content">
                    <p className={"h3"}>{t("index.what_is.title")}</p>
                    <p>{t("index.what_is.description")}</p>
                </div>
            </section>

            <section id="what-you-need" className="bg-light section">
                <div className="section-content">
                    <p className={"h3"}>{t("index.what_you_need.title")}</p>
                    <ul>
                        <li>{t("index.what_you_need.1.description_1/2")}
                            &nbsp;
                            <a href={"https://github.com/pintargasper/CardGenerator/releases/download/v1.0.3/Cards.xlsx"}
                               target={"_blank"} rel="noreferrer"
                               style={{color: "#1a0dab"}}>{t("index.what_you_need.1.description_1_link")}</a> -> {t("index.what_you_need.1.description_2/2")}
                            &nbsp;
                            <a href={"https://github.com/pintargasper/CardGenerator/releases"}
                               target={"_blank"} rel="noreferrer"
                               style={{color: "#1a0dab"}}>{t("index.what_you_need.1.description_2_link")}</a>
                        </li>
                        <li>{t("index.what_you_need.2.description_1/2")}
                            &nbsp;
                            <a href={"https://github.com/pintargasper/CardGenerator/releases/download/v1.0.3/images.zip"}
                               target={"_blank"} rel="noreferrer"
                               style={{color: "#1a0dab"}}>{t("index.what_you_need.2.description_1_link")}</a> -> {t("index.what_you_need.2.description_2/2")}
                            &nbsp;
                            <a href={"https://github.com/pintargasper/CardGenerator/releases"}
                               target={"_blank"} rel="noreferrer"
                               style={{color: "#1a0dab"}}>{t("index.what_you_need.1.description_2_link")}</a></li>
                        <li>{t("index.what_you_need.3.description_1/2")} -> {t("index.what_you_need.3.description_2/2")}
                            &nbsp;
                            <Link to={"./create"} style={{color: "#1a0dab"}}>{t("index.what_you_need.3.description_2_link")}</Link></li>
                    </ul>
                </div>
            </section>

            <section id="formats" className="bg-light section">
                <div className="section-content">
                    <p className={"h3"}>{t("index.supported_formats.title")}</p>
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
            </section>

            <section id="components" className="bg-light section">
                <div className="section-content">
                    <p className={"h3"}>{t("index.currently_additional_components.title")}</p>
                    <div className="component-details">
                        <span
                            className={"h6"}>{t("index.currently_additional_components.components.loading_bar.title")}</span>
                        <p>{t("index.currently_additional_components.components.loading_bar.description")}</p>
                        <p><b>{t("index.currently_additional_components.components.loading_bar.use.title")}:</b></p>
                        <code className={"text-black"}>
                            &lt;
                            <span>LoadingBar</span> <br/>
                            <span style={{marginLeft: "20px"}}>title</span>=&#123;
                            <span>"{t("index.currently_additional_components.components.loading_bar.use.parts.title")}"</span>&#125;
                            <br/>
                            <span style={{marginLeft: "20px"}}>progress</span>=&#123;
                            <span>{t("index.currently_additional_components.components.loading_bar.use.parts.progress")}</span>&#125;
                            <br/>
                            <span style={{marginLeft: "20px"}}>textColor</span>=&#123;
                            <span>"{t("index.currently_additional_components.components.loading_bar.use.parts.text_color")}"</span>&#125;
                            <br/>
                            <span style={{marginLeft: "20px"}}>loadingBarColor</span>=&#123;
                            <span>"{t("index.currently_additional_components.components.loading_bar.use.parts.loading_bar_color")}"</span>&#125;
                            <br/>
                            <span style={{marginLeft: "20px"}}>fontFamily</span>=&#123;
                            <span>"{t("index.currently_additional_components.components.loading_bar.use.parts.font_family")}"</span>&#125;
                            <br/>
                            <span style={{marginLeft: "20px"}}>fontSize</span>=&#123;
                            <span>{t("index.currently_additional_components.components.loading_bar.use.parts.font_size")}</span>&#125;
                            <br/>
                            /&gt;
                        </code>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Index;