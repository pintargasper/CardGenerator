import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import HttpApi from "i18next-http-backend";

const languageDetectorOptions = {
    order: ["querystring", "cookie", "localStorage", "navigator", "htmlTag", "path", "subdomain"],
    lookupQuerystring: "lng",
    caches: ["localStorage", "cookie"],
};

i18n
    .use(HttpApi)
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        detection: languageDetectorOptions,
        fallbackLng: "en",
        debug: true,
        interpolation: {
            escapeValue: false,
        },
        backend: {
            loadPath: "/locales/{{lng}}/{{ns}}.json",
        }
    }).then(result => null);

export default i18n;