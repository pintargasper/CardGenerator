import React from "react";
import {LiveError, LivePreview, LiveProvider } from "react-live";
import LoadingBar from "../LoadingBar";

const Card = React.memo(({ card, template, findImage }) => {
    const liveCode = `
        ${template}
    `;

    const scope = { card, findImage, LoadingBar };

    return (
        <LiveProvider code={liveCode} scope={scope}>
            <LiveError />
            <LivePreview />
        </LiveProvider>
    );
});

export default Card;