import React from 'react';

const LoadingBar = ({ title, progress = 0, textColor, loadingBarColor, fontFamily, fontSize }) => {
    const titleWidth = "80px";

    return (
        <div className="progress-container text-start" style={{ display: "flex", alignItems: "center", height: "25px" }}>
            <span style={{ width: titleWidth, color: textColor }}>{title}</span>
            <div className="progress" style={{ flex: 1, height: "20px", marginLeft: "10px", position: "relative", border: "1px solid #2596BE", borderRadius: "0" }}>
                <div
                    role="progressbar"
                    style={{ width: `${progress}%`, height: "100%", backgroundColor: loadingBarColor, borderRadius: "0" }}
                    aria-valuenow={progress}
                    aria-valuemin="0"
                    aria-valuemax="100"
                ></div>
                <div
                    style={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        transform: "translate(-50%, -45%)",
                        width: "100%",
                        textAlign: "center",
                        fontFamily: fontFamily,
                        fontSize: fontSize,
                    }}
                >
                    <b>{progress}</b>
                </div>
            </div>
        </div>
    );
};

export default LoadingBar;