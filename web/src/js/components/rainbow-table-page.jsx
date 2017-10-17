import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";
import Button from "muicss/lib/react/button";

import RainbowTableList from "./rainbow-table-list";
import ErrorElement from "./error-element";

import RainbowTableService from "../services/rainbow-table-service";
import {getErrorMessage} from "../util";


export default class RainbowTablePage extends React.Component {
    constructor() {
        super();
        this.state = {
            rainbowTableService: null,
            playerUuid: null,
            playerName: "",
            error: null,
            games: []
        };
    }

    componentDidMount() {
        this.setState({rainbowTableService: new RainbowTableService(this.props.httpService, this.handleError.bind(this))});
    }

    handleError(error) {
        this.setState({error: getErrorMessage(error)});
    }

    render() {
        if (this.state.rainbowTableService == null) {
            return null;
        }

        return (
            <Panel>
                <ErrorElement error={this.state.error}/>
                <h2>Rainbow Tables</h2>

                <div className="mui-divider"></div>
                <RainbowTableList rainbowTableService={this.state.rainbowTableService}/>

                <a href="/create-game">
                    <Button className="button" variant="fab">+</Button>
                </a>
            </Panel>
        );
    }
}

RainbowTablePage.propTypes = {
    httpService: PropTypes.func.isRequired,
};
