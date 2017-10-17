import React from "react";
import PropTypes from "prop-types";


export default class ProgressBar extends React.Component {
    render() {
        let progress = this.props.full
            ? "100%"
            : `${Math.ceil(this.props.numerator / this.props.denominator * 100)}%`;

        return (
            <div className="progress-bar-outer">
                <div className="progress-bar-inner color-change" style={{width: progress}}>{progress}</div>
            </div>
        );
    }
}

ProgressBar.propTypes = {
    full: PropTypes.bool,
    numerator: PropTypes.number.isRequired,
    denominator: PropTypes.number.isRequired
};
