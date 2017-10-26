import React from "react";
import PropTypes from "prop-types";
import RainbowTableService from "../services/rainbow-table-service";
import {getErrorMessage} from "../util";


export default class DefaultRainbowTablePage extends React.Component {
    constructor() {
        super();
        this.state = {
            rainbowTableService: null,
            error: null
        };
    }

    componentDidMount() {
        this.handleErrorMessage(this.props.error);
        this.setState(
            {rainbowTableService: new RainbowTableService(this.props.httpService, this.handleError.bind(this))},
            this.retrieveData.bind(this)
        );
    }

    handleError(error) {
        if (!error) {
            this.setState({error: null});
            return;
        }

        this.setState({error: getErrorMessage(error)});
        setTimeout(() => this.handleError(null), 5000);
    }

    handleErrorMessage(errorMessage) {
        if (!errorMessage) return;
        this.setState({error: errorMessage});
        setTimeout(() => this.handleError(null), 5000);
    }

    retrieveData() {
    }

    render() {
        if (this.state.rainbowTableService === null) {
            return null;
        }

        return this.renderWithRainbowTableService();
    }
}


DefaultRainbowTablePage.propTypes = {
    httpService: PropTypes.func.isRequired,
    error: PropTypes.string
};
