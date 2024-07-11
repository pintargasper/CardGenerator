import jsPDF from "jspdf";

export const ToPdf = (images) => {
    const pdf = new jsPDF();

    images.forEach((imageData, index) => {
        if (index > 0) {
            pdf.addPage();
        }
        pdf.addImage(imageData, "jpeg", 0, 0, pdf.internal.pageSize.getWidth(), pdf.internal.pageSize.getHeight());
    });
    pdf.save("images.pdf");
};