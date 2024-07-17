import React, { useState, useRef } from "react";
import * as XLSX from "xlsx";
import { ToImage } from "./generate/ToImage";
import { ToPdf } from "./generate/ToPdf";
import Popup from "./popup/Popup";
import Card from "./card/Card";
import {useTranslation} from "react-i18next";

const Generator = () => {
    const { t} = useTranslation();

    const [excelFile, setExcelFile] = useState(null);
    const [imageFiles, setImageFiles] = useState([]);
    const [templateFile, setTemplateFile] = useState(null);
    const [templateFileName, setTemplateFileName] = useState("");
    const [cards, setCards] = useState([]);
    const [selectedFormat, setSelectedFormat] = useState("a4");
    const [selectedType, setSelectedType] = useState("pdf");
    const [nameOfColumn, setNameOfColumn] = useState("");
    const [showPopup, setShowPopup] = useState(false);
    const [uploading, setUploading] = useState(false);

    const isMobileDevice = /Mobi|Android/i.test(navigator.userAgent);
    const excelFileInputRef = useRef(null);
    const imagesFileInputRef = useRef(null);
    const templateFileInputRef = useRef(null);

    const handleExcelChange = (event) => {
        const file = event.target.files[0];
        setExcelFile(file !== undefined ? file : null);
    };

    const handleImagesChange = (event) => {
        const files = event.target.files;
        const selectedImages = Array.from(files).filter((file) => file.type.startsWith("image/"));
        setImageFiles(selectedImages);
    };

    const handleTemplateChange = (event) => {
        const file = event.target.files[0];

        if (file !== undefined) {
            const reader = new FileReader();
            reader.onload = (event) => {
                setTemplateFile(event.target.result);
            };
            reader.readAsText(file);
            setTemplateFileName(file);
        } else {
            setTemplateFile(null);
            setTemplateFileName("");
        }
    };

    const handleUpload = () => {
        if (!excelFile || imageFiles.length === 0 || uploading) {
            return;
        }
        setUploading(true);
        const reader = new FileReader();

        reader.onload = (event) => {
            const data = new Uint8Array(event.target.result);
            const workbook = XLSX.read(data, { type: "array" });
            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];
            const jsonData = XLSX.utils.sheet_to_json(sheet);

            let cards = [];
            jsonData.forEach(card => {
                const numberOfCopies = card[nameOfColumn] !== undefined ? card[nameOfColumn] : 1;
                for (let i = 0; i < numberOfCopies; i++) {
                    cards.push(card);
                }
            });
            setCards(cards);
        };
        reader.readAsArrayBuffer(excelFile);

        const imageFilesArray = imageFiles
            .filter((file) => file.name.match(/\.(jpg|jpeg|png|gif)$/))
            .map((file) => {
                return new Promise((resolve) => {
                    const reader = new FileReader();
                    reader.onload = (event) => {
                        resolve({ name: file.name, url: event.target.result });
                    };
                    reader.readAsDataURL(file);
                });
            });

        Promise.all(imageFilesArray).then((images) => {
            setImageFiles(images);
        });
    };

    const clearUpload = () => {
        setExcelFile(null);
        setImageFiles([]);
        setTemplateFile(null);
        setTemplateFileName("");
        setCards([]);
        setSelectedFormat("a4");
        setSelectedType("pdf");
        setNameOfColumn("");
        setShowPopup(false);
        setUploading(false);

        if (excelFileInputRef.current) {
            excelFileInputRef.current.value = null;
        }
        if (imagesFileInputRef.current) {
            imagesFileInputRef.current.value = null;
        }
        if (templateFileInputRef.current) {
            templateFileInputRef.current.value = null;
        }
    };

    const findImage = (imageName) => {
        const image = imageFiles.find((image) => image.name === imageName);
        return image ? image.url : "";
    };

    const generate = async () => {
        setShowPopup(true);
        const numberOfCards = selectedFormat === "a4" ? 3 : 2;

        await new Promise((resolve) => setTimeout(resolve, 1000));
        const images = await ToImage(selectedType, numberOfCards, numberOfCards);
        if (selectedType === "pdf" && images !== null) {
            ToPdf(images);
        }
        setShowPopup(false);
    };

    return (
        <div className="container-fluid">
            <div className="row">
                <div className="mt-4 col-md-4">
                    <div className="form-group border rounded p-3">
                        <h5>{t("generator.upload.excel.title")}</h5>
                        <label
                            htmlFor="excel-upload"
                            className={"btn btn-light"}
                            style={{
                                display: "block",
                                fontSize: "large",
                                padding: "10px",
                                textAlign: "center",
                                cursor: "pointer",
                                borderRadius: "5px",
                                marginBottom: "10px"
                            }}
                        >
                            {excelFile === null ? t("generator.view.buttons.upload.excel.before") : t("generator.view.buttons.upload.excel.after") + ": " + excelFile.name}
                        </label>
                        <input
                            id={"excel-upload"}
                            type="file"
                            ref={excelFileInputRef}
                            className="form-control-file w-100"
                            onChange={handleExcelChange}
                            accept=".xlsx, .xls"
                            style={{height: "auto", fontSize: "large", display: "none"}}
                            disabled={uploading}
                        />
                    </div>
                    <div className="form-group border rounded p-3 mt-3">
                        <h5>{t("generator.upload.images.title")}</h5>
                        <h6>{t("generator.upload.images.description_1/4")}
                            <b> ({t("generator.upload.images.description_2/4")}) </b>
                            {t("generator.upload.images.description_3/4")}
                            <b> ({t("generator.upload.images.description_4/4")}) </b>
                        </h6>
                        <label
                            htmlFor="images-upload"
                            className={"btn btn-light"}
                            style={{
                                display: "block",
                                fontSize: "large",
                                padding: "10px",
                                textAlign: "center",
                                cursor: "pointer",
                                borderRadius: "5px",
                                marginBottom: "10px"
                            }}
                        >
                            {imageFiles.length === 0 ? t("generator.view.buttons.upload.images.before") : t("generator.view.buttons.upload.images.after") + ": " + imageFiles.length}
                        </label>
                        <input
                            id={"images-upload"}
                            type="file"
                            ref={imagesFileInputRef}
                            {...(!isMobileDevice && {directory: "", webkitdirectory: ""})}
                            className="form-control-file w-100"
                            onChange={handleImagesChange}
                            multiple
                            style={{height: "auto", fontSize: "large", display: "none"}}
                            disabled={uploading}
                        />
                    </div>
                    <div className="form-group border rounded p-3 mt-3">
                        <h5>{t("generator.view.title")}</h5>
                        <h6>{t("generator.view.description_1/3")}</h6>
                        <input
                            type="text"
                            value={nameOfColumn}
                            className="w-100"
                            placeholder={t("generator.view.description_2/3") + ": 1x"}
                            style={{height: "auto", fontSize: "large"}}
                            onChange={(event) => {
                                setNameOfColumn(event.target.value)
                            }}
                            disabled={uploading}
                        />
                        <h6 className={"mt-2"}>{t("generator.view.description_3/3")}</h6>
                        <label
                            htmlFor="template-upload"
                            className={"btn btn-light"}
                            style={{
                                display: "block",
                                fontSize: "large",
                                padding: "10px",
                                textAlign: "center",
                                cursor: "pointer",
                                borderRadius: "5px",
                                marginBottom: "10px"
                            }}
                        >
                            {templateFileName === "" ? t("generator.view.buttons.upload.template.before") : t("generator.view.buttons.upload.template.after") + ": " + templateFileName.name}
                        </label>
                        <input
                            id={"template-upload"}
                            type="file"
                            ref={templateFileInputRef}
                            className="form-control-file w-100"
                            onChange={handleTemplateChange}
                            accept=".js"
                            style={{height: "auto", fontSize: "large", display: "none"}}
                            disabled={uploading}
                        />
                        <input
                            type="button"
                            value={t("generator.view.buttons.upload.all.title")}
                            className="btn btn-light w-100 mt-2"
                            style={{height: "auto", fontSize: "large"}}
                            onClick={handleUpload}
                            disabled={!excelFile || imageFiles.length === 0 || cards.length !== 0 || !templateFile}
                        />
                        <input
                            type="button"
                            value={t("generator.view.buttons.reset")}
                            className="btn btn-light w-100 mt-2"
                            style={{height: "auto", fontSize: "large"}}
                            onClick={clearUpload}
                            disabled={!excelFile || imageFiles.length === 0 || cards.length === 0}
                        />
                    </div>
                    <div className="form-group border rounded p-3 mt-3">
                        <h5>{t("generator.download.title")}</h5>
                        <h6>{t("generator.download.description_1/3")}: {cards.length}</h6>
                        <h6>{t("generator.download.description_2/3")}</h6>
                        <select className="form-select w-100"
                                style={{height: "auto", fontSize: "large"}}
                                value={selectedFormat}
                                onChange={(event) => setSelectedFormat(event.target.value)}>
                            <option value="a4">A4</option>
                            <option value="13x18">13x18</option>
                        </select>
                        <h6 className={"mt-1"}>{t("generator.download.description_3/3")}</h6>
                        <select
                            className="form-select w-100"
                            style={{height: "auto", fontSize: "large"}}
                            value={selectedType}
                            onChange={(event) => setSelectedType(event.target.value)}>
                            <option value="pdf">Pdf</option>
                            <option value="jpg">Jpg</option>
                            <option value="jpeg">Jpeg</option>
                            <option value="png">Png</option>
                        </select>
                        <input
                            type="button"
                            value={t("generator.download.buttons.generate")}
                            className="btn btn-light w-100 mt-2"
                            style={{height: "auto", fontSize: "large"}}
                            onClick={generate}
                            disabled={!excelFile || imageFiles.length === 0 || !uploading}
                        />
                    </div>
                </div>
                <div className="mt-4 col-md-8 light p-3">
                    <h5>{t("generator.cards")}</h5>
                    <div style={{height: "80vh", overflowY: "auto"}}>
                        <div id={"cards-container"} className="d-flex flex-wrap justify-content-center border">
                            {cards.map((card, index) => (
                                <div key={index} className="card-container">
                                    <Card card={card} template={templateFile} findImage={findImage}/>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            <Popup show={showPopup} type={selectedType}/>
        </div>
    );
};

export default Generator;