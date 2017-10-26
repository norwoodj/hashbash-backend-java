import React from "react";
import PropTypes from "prop-types";
import ErrorElement from "./error-element";
import {is_hash} from "../util";


export default class SearchRainbowTableForm extends React.Component {
    constructor() {
        super();
        this.submitted = new Set();
        this.state = {
            error: null
        };
    }

    handleSubmit(evt) {
        evt.preventDefault();
        let hash = document.getElementById("rainbow-table-hash").value;

        if (this.submitted.has(hash)) {
            this.setState({error: `Already submitted hash ${hash}!`});
            setTimeout(() => this.setState({error: null}), 3000);
            return;
        }

        let hashFn = this.props.rainbowTable.hashFunction;
        if (!is_hash(hashFn, hash)) {
            this.setState({error: `${hash} is not a valid ${hashFn} hash!`});
            setTimeout(() => this.setState({error: null}), 3000);
            return;
        }

        this.submitted.add(hash);
        this.props.rainbowTableService.submitSearchRequest(
            this.props.rainbowTable.id,
            document.getElementById("rainbow-table-hash").value
        );
    }

    render() {
        return (
            <div>
                <ErrorElement error={this.state.error}/>

                <form onSubmit={this.handleSubmit.bind(this)}>
                    <div className="mui-textfield">
                        <input id="rainbow-table-hash" type="text" name="hash"
                               placeholder={`${this.props.rainbowTable.hashFunction} hash`} required/>
                        <label>Name</label>
                    </div>
                    <button id="rainbow-table-search-submit" type="submit"
                            className="mui-btn mui-btn--raised color-change">
                        Submit
                    </button>
                </form>
            </div>
        );
    }
}

SearchRainbowTableForm.propTypes = {
    rainbowTable: PropTypes.object.isRequired,
    rainbowTableService: PropTypes.object.isRequired
};
