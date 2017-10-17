import React from "react";
import PropTypes from "prop-types";
import Appbar from "muicss/lib/react/appbar";
import Container from "muicss/lib/react/container";


export default class ScAppBar extends React.Component {
    constructor() {
        super();
    }

    render() {
        return (
            <header id="header">
                <Appbar className="mui--appbar-line-height color-change">
                    <Container fluid={true}>
                        <table width="100%"><tbody><tr>
                            <td>
                                <a className="sidedrawer-toggle mui--visible-xs-inline-block js-show-sidedrawer">☰</a>
                                <a className="sidedrawer-toggle mui--hidden-xs js-hide-sidedrawer">☰</a>
                                <a className="link mui--text-title mui--invisible-xs" href="/">{this.props.appName}</a>
                            </td>
                        </tr></tbody></table>
                    </Container>
                </Appbar>
            </header>
        );
    }
}

ScAppBar.propTypes = {
    appName: PropTypes.string.isRequired,
    httpService: PropTypes.func.isRequired
};
