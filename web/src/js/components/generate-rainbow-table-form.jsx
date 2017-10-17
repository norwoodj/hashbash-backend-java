import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";
import RainbowTableService from "../services/rainbow-table-service";
import ErrorElement from "../components/error-element";
import {getErrorMessage} from "../util";


export default class GenerateRainbowTableForm extends React.Component {
    constructor() {
        super();
        this.state = {
            hashFunctions: [],
            rainbowTableService: null,
            error: null
        };
    }

    componentDidMount() {
        this.setState(
            {rainbowTableService: new RainbowTableService(this.props.httpService, this.props.errorHandler)},
            () => this.state.rainbowTableService.getSupportedHashFunctions().then(hashFunctions => this.setState({hashFunctions: hashFunctions}))
        );
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
                <h2>Generate Rainbow Table</h2>
                <form action="/api/rainbow-table">
                    <div className="mui-textfield">
                        <input type="text" name="name" placeholder="hashbash" required/>
                        <label>Name</label>
                    </div>
                    <div className="mui-select">
                        <select id="hash-function-select" name="hashFunction">
                            {this.state.hashFunctions.map(f => <option key={f}>{f}</option>)}
                        </select>
                        <label>Hash Function</label>
                    </div>
                    <div className="mui-textfield">
                        <input type="number" name="numChains" min="100" max="100000000" placeholder="100000" required/>
                        <label>Number of Chains</label>
                    </div>
                    <div className="mui-textfield">
                        <input type="number" name="chainLength" min="100" max="100000000" placeholder="10000" required/>
                        <label>Chain Length</label>
                    </div>
                    <div className="mui-textfield">
                        <input type="text" name="charset" placeholder="abcdefghijklmnopqrstuvwxyz" required/>
                        <label>Character Set</label>
                    </div>
                    <div className="mui-textfield">
                        <input type="number" name="passwordLength" min="4" max="16" placeholder="8" required/>
                        <label>Password Length</label>
                    </div>
                    <button type="submit" className="mui-btn mui-btn--raised color-change">Submit</button>
                </form>
            </Panel>
        );
    }
}

GenerateRainbowTableForm.propTypes = {
    httpService: PropTypes.func.isRequired
};
