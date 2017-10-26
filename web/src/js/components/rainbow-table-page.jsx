import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";
import Button from "muicss/lib/react/button";

import RainbowTableListTable from "./rainbow-table-list-table";
import ErrorElement from "./error-element";
import DefaultRainbowTablePage from "./default-rainbow-table-page";


export default class RainbowTablePage extends DefaultRainbowTablePage {
    renderWithRainbowTableService() {
        return (
            <Panel>
                <ErrorElement error={this.state.error}/>
                <h2>Rainbow Tables</h2>

                <div className="mui-divider"/>
                <RainbowTableListTable rainbowTableService={this.state.rainbowTableService} refreshRateSeconds={5}/>

                <a href="/generate-rainbow-table">
                    <Button className="button color-change" variant="fab">+</Button>
                </a>
            </Panel>
        );
    }
}

RainbowTablePage.propTypes = {
    httpService: PropTypes.func.isRequired,
    error: PropTypes.string
};
