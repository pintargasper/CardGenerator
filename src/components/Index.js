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
                            <li>title={"<b>Naslov</b>"}</li>
                            <li>progress={"<b>Število</b>"}</li>
                            <li>textColor={"<b>Barva besedila</b>"}</li>
                            <li>loadingBarColor={"<b>Barva vrstice</b>"}</li>
                            <li>fontFamily={"<b>Pisava</b>"}</li>
                            <li>fontSize={"<b>Velikost besedila</b>"}</li>
                        </ul>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Index;