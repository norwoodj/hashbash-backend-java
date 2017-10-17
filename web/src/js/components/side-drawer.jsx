import React from "react";
import PropTypes from "prop-types";
import MenuCategory from "./menu-category";


export default class SideDrawer extends React.Component {
    render() {
        return (
            <div id="sidedrawer">
                <div id="sidedrawer-brand" className="mui--appbar-line-height">
                    <span className="mui--text-title">{this.props.pageName}</span>
                </div>
                <div className="mui-divider"></div>
                {this.props.menuCategories.map(menuCategory =>
                    <MenuCategory name={menuCategory.category} key={menuCategory.category} menuOptions={menuCategory.options}/>
                )}
            </div>
        );
    }
}

SideDrawer.propTypes = {
    pageName: PropTypes.string.isRequired,
    menuCategories: PropTypes.array.isRequired
};
