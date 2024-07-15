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
                            href={"https://github.com/pintargasper/CardGenerator/releases/download/release-v1.0.2/Cards.xlsx"}
                            target={"_blank"} rel="noreferrer">Cards.xlsx (Vzorčna datoteka)</a> -> pridobljeno iz strani <a
                            href={"https://github.com/pintargasper/CardGenerator/releases"}
                            target={"_blank"} rel="noreferrer">GitHub</a>
                        </li>
                        <li>Mapo slik <a
                            href={"https://github.com/pintargasper/CardGenerator/releases/download/release-v1.0.2/images.zip"}
                            target={"_blank"} rel="noreferrer">images (Vzorčna mapa)</a> -> pridobljeno iz
                            strani <a href={"https://github.com/pintargasper/CardGenerator/releases"}
                                target={"_blank"} rel="noreferrer">GitHub</a></li>
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
                        <code>
                            &lt;<span style={{color: "#5e974b"}}>LoadingBar</span> <br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>title</span>=&#123;<span style={{color: "#990000"}}>"Naslov"</span>&#125; <br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>progress</span>=&#123;<span style={{color: "#990000"}}>Število</span>&#125; <br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>textColor</span>=&#123;<span style={{color: "#990000"}}>"Barva besedila"</span>&#125; <br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>loadingBarColor</span>=&#123;<span style={{color: "#990000"}}>"Barva vrstice"</span>&#125; <br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>fontFamily</span>=&#123;<span style={{color: "#990000"}}>"Pisava"</span>&#125;<br/>
                            <span style={{color: "#321c14", marginLeft: "20px"}}>fontSize</span>=&#123;<span style={{color: "#990000"}}>Velikost besedila</span>&#125;<br/>
                        /&gt;
                        </code>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Index;