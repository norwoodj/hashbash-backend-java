import $ from "jquery";
import React from "react";
import ReactDOM from "react-dom";

import Container from "muicss/lib/react/container";
import Panel from "muicss/lib/react/panel";
import AppBar from "./components/app-bar";
import SideDrawer from "./components/side-drawer";
import Footer from "./components/footer";

import {setupSideDrawerTransition} from "./side-drawer-transition";
import {MENU_CATEGORIES, APP_NAME} from "./constants";


$(() => {
    ReactDOM.render(
        <div id="react-root">
            <SideDrawer pageName={APP_NAME} menuCategories={MENU_CATEGORIES}/>
            <AppBar appName={APP_NAME}/>
            <div id="content-wrapper">
                <div className="mui--appbar-height"/>
                <Container className="main-container">
                    <Panel>
                        <h2>Welcome to Hashbash</h2>
                        <div className="mui-divider"/>
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
                        <h3>Implementation</h3>
                        <p>
                            A rainbow table is a data structure that supports the space and time efficient reversal of cryptographic
                            hash functions. For details on how this works visit <a href="https://en.wikipedia.org/wiki/Rainbow_table">this article</a>
                        </p>
                        <p>
                            This implementation uses java, spring-batch, and mysql to generate and store the rainbow table. You can then
                            use the API or web interface to search existing rainbow tables. This is certainly not the most efficient implementation
                            of rainbow tables possible or available. This was simply a fun way to implement the algorithm in a user-friendly way.
                        </p>
                        <p>
                            What you can do from here:
                        </p>
                        <ul>
                            <li><a href="/generate-rainbow-table">Generate a new Rainbow Table</a></li>
                            <li><a href="/rainbow-tables">View Existing Rainbow Tables</a></li>
                        </ul>
                    </Panel>
                </Container>
            </div>
            <div className="footer-height mui--hidden-md mui--hidden-lg mui--hidden-xl"/>
            <Footer/>
        </div>,
        document.getElementById("content-root")
    );

    setupSideDrawerTransition();
});
