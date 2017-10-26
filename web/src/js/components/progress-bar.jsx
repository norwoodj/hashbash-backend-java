import React from "react";
import PropTypes from "prop-types";


export default class ProgressBar extends React.Component {
    render() {
        let {full, numerator, denominator} = this.props;

        let progress = full ? 100 : numerator / denominator * 100;
        let roundedProgress = Math.floor(progress);
        let progressPercent = `${roundedProgress}%`;

        return (
            <div className="progress-bar-outer">
                <div className="progress-bar-inner color-change" style={{width: progressPercent}}>{progressPercent}</div>
            </div>
        );
    }
}

ProgressBar.propTypes = {
    full: PropTypes.bool,
    numerator: PropTypes.number.isRequired,
    denominator: PropTypes.number.isRequired,
};
