import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";
import ErrorElement from "../components/error-element";
import DefaultRainbowTablePage from "./default-rainbow-table-page";


export default class GenerateRainbowTableForm extends DefaultRainbowTablePage {
    constructor() {
        super();
        Object.assign(this.state, {hashFunctions: []});
    }

    retrieveData() {
        this.state.rainbowTableService.getSupportedHashFunctions().then(
            hashFunctions => this.setState({hashFunctions: hashFunctions})
        );
    }

    renderWithRainbowTableService() {
        return (
            <Panel>
                <ErrorElement error={this.state.error}/>
                <h2>Generate Rainbow Table</h2>
                <form method="POST" action="/api/rainbow-table">
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
                        <input defaultValue="1000000" type="number" name="numChains" min="1000" max="100000000" required/>
                        <label>Number of Chains</label>
                    </div>
                    <div className="mui-textfield">
                        <input defaultValue="10000" type="number" name="chainLength" min="100" max="100000" required/>
                        <label>Chain Length</label>
                    </div>
                    <div className="mui-textfield">
                        <input defaultValue="abcdefghijklmnopqrstuvwxyz" type="text" name="charset" required/>
                        <label>Character Set</label>
                    </div>
                    <div className="mui-textfield">
                        <input defaultValue="8" type="number" name="passwordLength" min="4" max="16" required/>
                        <label>Password Length</label>
                    </div>
                    <button
                        type="submit"
                        className="mui-btn mui-btn--raised color-change"
                    >
                        Submit
                    </button>
                </form>
            </Panel>
        );
    }
}

GenerateRainbowTableForm.propTypes = {
    httpService: PropTypes.func.isRequired,
    error: PropTypes.string
};
