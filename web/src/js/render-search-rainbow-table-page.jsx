import $ from "jquery";
import React from "react";
import ReactDOM from "react-dom";

import Container from "muicss/lib/react/container";
import AppBar from "./components/app-bar";
import SideDrawer from "./components/side-drawer";
import Footer from "./components/footer";
import SearchRainbowTablesPage from "./components/search-rainbow-table-page";

import {setupSideDrawerTransition} from "./side-drawer-transition";
import {MENU_CATEGORIES, APP_NAME} from "./constants";


$(() => {
    let error = document.getElementById("error-text")
        ? document.getElementById("error-text").getAttribute("data-error")
        : null;

    let rainbowTableId = document.getElementById("rainbow-table-id").getAttribute("data-id");
    console.log(rainbowTableId);

    ReactDOM.render(
        <div id="react-root">
            <SideDrawer pageName={APP_NAME} menuCategories={MENU_CATEGORIES}/>
            <AppBar appName={APP_NAME}/>
            <div id="content-wrapper">
                <div className="mui--appbar-height"/>
                <Container className="main-container">
                    <SearchRainbowTablesPage error={error} httpService={$} rainbowTableId={rainbowTableId}/>
                </Container>
            </div>
            <div className="footer-height mui--hidden-md mui--hidden-lg mui--hidden-xl"/>
            <Footer/>
        </div>,
        document.getElementById("content-root")
    );

    setupSideDrawerTransition();
});
