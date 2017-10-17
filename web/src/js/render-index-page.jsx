import $ from "jquery";
import React from "react";
import ReactDOM from "react-dom";

import Container from "muicss/lib/react/container";
import Panel from "muicss/lib/react/panel";
import ScAppBar from "./components/app-bar";
import SideDrawer from "./components/side-drawer";
import Footer from "./components/footer";

import {setupSideDrawerTransition} from "./side-drawer-transition";
import {MENU_CATEGORIES, APP_NAME} from "./constants";


$(() => {
    ReactDOM.render(
        <div id="react-root">
            <SideDrawer pageName={APP_NAME} menuCategories={MENU_CATEGORIES}/>
            <ScAppBar appName={APP_NAME} httpService={$}/>
            <div id="content-wrapper">
                <div className="mui--appbar-height"></div>
                <Container className="main-container">
                    <Panel>
                        <h2>Welcome to Hashbash</h2>
                        <div className="mui-divider"></div>
                        <br/>
                        <p>
                            This is a web-based rainbow table generator and searcher. It is deployed on a raspberry pi cluster
                            running kubernetes. Visit my github to view the salt-stack configuration, build tools, and custom
                            docker images I've built to deploy this and other projects on this platform.
                        </p>
                        <p>
                            You can see this particular project <a href="https://github.com/norwoodj/hashbash">here</a>.
                        </p>
                        <p>
                            You might also visit my other project <a href="https://stupidchess.johnmalcolmnorwood.com">Stupid Chess</a>.
                        </p>
                    </Panel>
                </Container>
            </div>
            <Footer/>
        </div>,
        document.getElementById("content-root")
    );

    setupSideDrawerTransition();
});
