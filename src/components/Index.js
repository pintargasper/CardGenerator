import {Link} from "react-router-dom";

const Index = () => {
    return (
        <div className="container">
            <section id="about" className="bg-light section">
                <h1 className="text-center">Card Generator</h1>
                <div className="section-content">
                    <h3>Kaj je Card Generator?</h3>
                    <p>
                        Card Generator omogoča izdelavo poljubne karte in tudi generacijo le-te v jpg ali png format.
                        Prav tako omogoča prenos slik v pdf načinu.
                    </p>
                </div>
            </section>

            <section id="what-you-need" className="bg-light section">
                <div className="section-content">
                    <h3>Kaj potrebuješ</h3>
                    <ul>
                        <li>Excel datoteko <a
                            href={"https://github.com/pintargasper/CardGenerator/releases/download/pre-release/Cards.xlsx"}
                            target={"_blank"} rel="noreferrer">Cards.xlsx (Vzorčna datoteka)</a> -> pridobljeno iz strani <a
                            href={"https://github.com/pintargasper/CardGenerator/releases"}
                            target={"_blank"} rel="noreferrer">Github</a>
                        </li>
                        <li>Mapo slik <a
                            href={"https://github.com/pintargasper/CardGenerator/releases/download/pre-release/images.zip"}
                            target={"_blank"} rel="noreferrer">images (Vzorčna mapa)</a> -> pridobljeno iz
                            strani <a href={"https://github.com/pintargasper/CardGenerator/releases"}
                                target={"_blank"} rel="noreferrer">Github</a></li>
                        <li>Datoteka (Predloga) -> pridobljeno iz <Link to={"./create"}>predloge</Link></li>
                    </ul>
                </div>
            </section>

            <section id="formats" className="bg-light section">
                <div className="section-content">
                    <h3>Podprti formati</h3>
                    <ul>
                        <li>
                        A4 -> 3 x 3 slik na eno stran. Velikost karte: <b>Širina:</b> 240px; <b>Višina:</b> 332px
                        </li>
                        <li>
                            13x18 -> 2 x 2 slik na eno stran. Velikost karte: <b>Širina:</b> 240px; <b>Višina:</b> 332px
                        </li>
                    </ul>
                </div>
            </section>

            <section id="components" className="bg-light section">
                <div className="section-content">
                    <h3>Trenutno dodatne komponente</h3>
                    <div className="component-details">
                        <h6>LoadingBar komponenta</h6>
                        <p>
                            LoadingBar komponenta omogoča prikaz vrstice za nalaganje.
                        </p>
                        <p>
                            <b>Uporaba komponente:</b>
                        </p>
                        <ul>
                            <li>title=&#123;<b>&#34;Naslov&#34;</b>&#125;</li>
                            <li>progress=&#123;<b>Število</b>&#125;</li>
                            <li>textColor=&#123;<b>&#34;Barva besedila&#34;</b>&#125;</li>
                            <li>loadingBarColor=&#123;<b>&#34;Barva vrstice&#34;</b>&#125;</li>
                            <li>fontFamily=&#123;<b>&#34;Pisava&#34;</b>&#125;</li>
                            <li>fontSize=&#123;<b>Velikost besedila</b>&#125;</li>
                        </ul>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Index;