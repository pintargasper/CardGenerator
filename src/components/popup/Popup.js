import {useTranslation} from "react-i18next";

const Popup = ({ show, type }) => {

    const { t} = useTranslation();

    if (!show) {
        return null;
    }

    return (
        <>
            <div className="modal fade show" style={{ display: "block" }} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div className="modal-dialog" role="document">
                    <div className="modal-content">
                        <div className="modal-body text-center">
                            <h4>{t("popup.title")} {type === "pdf" ? t("popup.pdf") : t("popup.images")} ({type})</h4>
                        </div>
                    </div>
                </div>
            </div>
            <div className="modal-backdrop fade show"></div>
        </>
    );
};

export default Popup;