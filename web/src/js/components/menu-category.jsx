import React from "react";
import PropTypes from "prop-types";


export default class MenuCategory extends React.Component {
    render() {
        return (
              <ul>
                <li>
                    <strong>{this.props.name}</strong>
                    <ul>{this.props.menuOptions.map((option) =>
                        <li key={option.text}><a href={option.link}>{option.text}</a></li>
                    )}</ul>
                </li>
            </ul>
        );
    }
}

MenuCategory.propTypes = {
    menuOptions: PropTypes.array.isRequired,
    name: PropTypes.string.isRequired
};
