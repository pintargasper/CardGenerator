import html2canvas from "html2canvas";
import JSZip from "jszip";

export const ToImage = async (selectedFormat, numOfColCard = 2, numOfRowCard = 2) => {
    const cardsContainer = document.getElementById("cards-container");

    const cards = Array.from(cardsContainer.children);
    const cardWidth = cards[0].offsetWidth;
    const cardHeight = cards[0].offsetHeight;

    const cardsPerImage = numOfColCard * numOfRowCard;
    const zip = new JSZip();
    const images = [];

    for (let i = 0; i < cards.length; i += cardsPerImage) {
        const cardGroup = cards.slice(i, i + cardsPerImage);
        const canvas = document.createElement("canvas");
        canvas.width = cardWidth * numOfColCard * 4;
        canvas.height = cardHeight * numOfRowCard * 4;
        const context = canvas.getContext("2d");
        context.scale(4, 4);

        const canvasPromises = cardGroup.map((card, index) => {
            const row = Math.floor(index / numOfColCard);
            const col = index % numOfColCard;
            return html2canvas(card, { scale: 4, useCORS: true, backgroundColor: "#FFFFFF" }).then((cardCanvas) => {
                context.drawImage(cardCanvas, col * cardWidth, row * cardHeight, cardWidth, cardHeight);
            });
        });

        await Promise.all(canvasPromises);

        const imageData = canvas.toDataURL("image/" + (selectedFormat === "jpeg" ? "jpeg" :
                                                                    selectedFormat === "jpg" ? "jpg" :
                                                                    selectedFormat === "pdf" ? "jepg" : "jpg"))
            .replace(/^data:image\/(png|jpg|jpeg);base64,/, "");
        if (selectedFormat === "pdf") {
            images.push(imageData);
        } else {
            zip.file(`image_${Math.floor(i / cardsPerImage) + 1}.${selectedFormat === "jpg" ? "jpg" : selectedFormat === "jpeg" ? "jpeg" : "png"}`, imageData, { base64: true });
        }
    }

    if (selectedFormat === "pdf") {
        return images;
    } else {
        const content = await zip.generateAsync({ type: "blob" });

        const zipFileName = "images.zip";

        const downloadLink = document.createElement("a");
        downloadLink.href = URL.createObjectURL(content);
        downloadLink.download = zipFileName;
        downloadLink.style.display = "none";
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }
};