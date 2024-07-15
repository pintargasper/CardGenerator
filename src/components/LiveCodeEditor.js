import React, {useRef, useState} from "react";
import {LiveEditor, LiveError, LivePreview, LiveProvider} from "react-live";
import LoadingBar from "./LoadingBar";

const LiveCodeEditor = () => {
    const [imageFiles, setImageFiles] = useState([]);
    const [userCode, setUserCode] = useState("");
    const imagesFileInputRef = useRef(null);

    const isMobileDevice = /Mobi|Android/i.test(navigator.userAgent);

    const findImage = (imageName) => {
        const image = imageFiles.find((image) => image.name === imageName);
        return image ? URL.createObjectURL(image) : "";
    };

    const handleImagesChange = (event) => {
        const files = event.target.files;
        const selectedImages = Array.from(files).filter((file) =>
            file.type.startsWith("image/")
        );
        setImageFiles(selectedImages);
    };

    const handleCodeChange = (newCode) => {
        setUserCode(newCode);
    };

    const handleEmptyTemplate = () => {
        const defaultCode = `
    (() => {
       const message = "Hello world!";
       return (
            <>
                <p>{message}</p>
            </>    
       );
})()`;
        setUserCode(defaultCode);
    }

    const handleDownload = () => {
        let newCode = userCode.replace(/\bconst\s+card\s*=\s*{[^}]*};?/g, "");

        const blob = new Blob([newCode], { type: "text/javascript" });
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement("a");
        link.style.display = "none";
        link.href = url;
        link.download = "template.js";

        document.body.appendChild(link);
        link.click();

        window.URL.revokeObjectURL(url);
        document.body.removeChild(link);
    };

    const defaultCode = `
(() => {
    const card = {
        Ime: "Daniel",
        Slika: "daniel.jpg",
        Ozadje: "ozadje_1.jpg",
        Opis: "Beltšacár",
        Tip: "Navadna",            
        Zvestoba: 95,
        Modrost: 95,
        Bojevitost: 35,
        Vplivnost: 90
    };

    const cardProperties = {
        dimensions: { width: 240, minWidth: 240, height: 332, minHeight: 332, headerHeight: 30, imageHeight: 162 },
        textColor: "#FFFFFF",
        loadingBarColor: "rgba(37, 150, 190, 0.7)",
        textTitleSize: "20px",
        textSize: "17px",
        fontFamily: "Bahnschrift Condensed",
        cardShadow: 138
    };

    const cardTypeImages = {
        Navadna: "normalna.jpg",
        Posebna: "posebna.jpg",
        Urok: "urok.jpg",
        Sestavljena: "sestavljena.jpg",
        Sestavljena_Urok: "sestavljena.jpg"
    };

    return (
        <div style={{
            width: \`\${cardProperties.dimensions.width}px\`,
            height: \`\${cardProperties.dimensions.height}px\`,
            border: "2px solid black",
            backgroundImage: \`url('\${findImage(card.Ozadje)}')\`
        }}>
            <div>
                <div style={{
                    display: "flex",
                    justifyContent: "space-between",
                    backgroundColor: "rgba(0, 0, 0, 0.5)",
                    height: \`\${cardProperties.dimensions.headerHeight}px\`
                }}>
                    <div style={{color: cardProperties.textColor, fontSize: cardProperties.textTitleSize, fontFamily: cardProperties.fontFamily}}>
                        {card.Ime}
                    </div>
                    <img src={findImage(cardTypeImages[card.Tip.replace("/", "_")])} alt={card.Tip}
                        className="img-fluid border p-0" style={{height: \`\${cardProperties.dimensions.headerHeight}px\`, 
                        width: \`\${cardProperties.dimensions.headerHeight}px\`}}/>
                </div>
                <div style={{height: \`\${cardProperties.dimensions.imageHeight}px\`}}>
                    <img src={findImage(card.Slika)} alt={card.Slika} className="mx-auto d-block p-1"
                        style={{height: \`\${cardProperties.dimensions.imageHeight}px\`, width: \`\${cardProperties.dimensions.imageHeight}px\`, 
                        backgroundColor: "rgba(37, 150, 190, 0.7)"}}/>
                </div>
                <div style={{
                    flexGrow: 1,
                    height: \`\${cardProperties.cardShadow}px\`,
                    backgroundColor: "rgba(0, 0, 0, 0.5)",
                    padding: "5px"
                }}>
                    {card.Tip === "Urok" || card.Tip === "Sestavljena/Urok" ?
                        <div style={{color: cardProperties.textColor, fontSize: cardProperties.textSize, fontFamily: cardProperties.fontFamily}}>
                            {card.Opis}
                        </div>
                        :
                        <>
                            <div style={{color: cardProperties.textColor, fontSize: cardProperties.textSize, textAlign: "right"}}>
                                Skupaj : {card.Zvestoba + card.Modrost + card.Bojevitost + card.Vplivnost}
                            </div>
                            <LoadingBar
                                title={"Zvestoba"}
                                progress={card.Zvestoba}
                                textColor={cardProperties.textColor}
                                loadingBarColor={cardProperties.loadingBarColor}
                                fontFamily={cardProperties.fontFamily}
                                fontSize={cardProperties.textSize}
                            />
                            <LoadingBar
                                title={"Modrost"}
                                progress={card.Modrost}
                                textColor={cardProperties.textColor}
                                loadingBarColor={cardProperties.loadingBarColor}
                                fontFamily={cardProperties.fontFamily}
                                fontSize={cardProperties.textSize}
                            />
                            <LoadingBar
                                title={"Bojevitost"}
                                progress={card.Bojevitost}
                                textColor={cardProperties.textColor}
                                loadingBarColor={cardProperties.loadingBarColor}
                                fontFamily={cardProperties.fontFamily}
                                fontSize={cardProperties.textSize}
                            />
                            <LoadingBar
                                title={"Vplivnost"}
                                progress={card.Vplivnost}
                                textColor={cardProperties.textColor}
                                loadingBarColor={cardProperties.loadingBarColor}
                                fontFamily={cardProperties.fontFamily}
                                fontSize={cardProperties.textSize}
                            />
                        </>
                    }
                </div>
            </div>
        </div>
    );
})()
`;

    const formatCode = (code) => {
        const trimmedCode = code.trim();
        const leadingSpaces = trimmedCode.match(/^\s*/)[0].length;
        return trimmedCode.replace(new RegExp(`^\\s{${leadingSpaces}}`, "gm"), "");
    };

    return (
        <div className="container-fluid">
            <div className="row">
                <LiveProvider code={userCode || defaultCode} scope={{findImage, imageFiles, LoadingBar}}
                              language={"jsx"} enableTypeScript={false}>
                    <div className="col-lg-6" style={{maxHeight: "90vh", overflowY: "auto", overflowX: "auto"}}>
                        <LiveEditor code={formatCode(userCode || defaultCode)} language={"jsx"}
                                    onChange={handleCodeChange}/>
                    </div>
                    <div className="col-lg-3 d-flex justify-content-center">
                        <div className="d-flex mt-2 flex-column align-items-center">
                            <h3 className="mb-2">Predogled</h3>
                            <div className="text-center">
                                <LivePreview/>
                                <LiveError/>
                            </div>
                        </div>
                    </div>
                    <div className="col-lg-3 justify-content-center">
                        <div className="form-group border rounded p-3 mt-3 w-100">
                            <h5>Referenčne slike</h5>
                            <input
                                type="file"
                                ref={imagesFileInputRef}
                                {...(!isMobileDevice && {directory: "", webkitdirectory: ""})}
                                className="form-control-file w-100"
                                onChange={handleImagesChange}
                                multiple
                                style={{height: "auto", fontSize: "large"}}
                            />
                        </div>
                        <div className="form-group border rounded p-3 mt-3 w-100">
                            <h5>Predloga</h5>
                            <input
                                type="button"
                                value={"Prazna predloga"}
                                className="btn btn-light w-100 mt-2"
                                style={{height: "auto", fontSize: "large"}}
                                onClick={handleEmptyTemplate}
                            />
                        </div>
                        <div className="form-group border rounded p-3 mt-3 w-100">
                            <h5>Prenos</h5>
                            <input
                                type="button"
                                value="Prenesi predlogo"
                                className="btn btn-light w-100 mt-2"
                                style={{height: "auto", fontSize: "large"}}
                                onClick={handleDownload}
                            />
                        </div>
                    </div>
                </LiveProvider>
            </div>
        </div>
    );
};

export default LiveCodeEditor;