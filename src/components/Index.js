const Index = () => {
    return(
        <div className={"container"}>
            <section id={"about"} className={"bg-light"}>
                <h1 className={"text-center"}>Card generator</h1>
                <h3>Kaj je Card generator?</h3>
                <p>Card generator omogoča izdelavo poljubne karte in tudi generacijo le-te v jpg ali png format.<br/>
                    Prav tako pa tudi omogoča prenos slik v pdf načinu.</p>
            </section>
            <section id={"components"} className={"bg-light"}>
                <h3>Trenutne dodatne komponente</h3>
                <h6>LoadingBar komponenta</h6>
                <p>LoadingBar komponenta omogoča prikaz vrstice za nalaganje.</p>
                <p><b>Uporaba komponente</b></p>

            </section>
        </div>
    );
}

export default Index;