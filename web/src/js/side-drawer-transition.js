import $ from "jquery";
import overlay from "muicss/lib/js/overlay";


function setupSideDrawerTransition() {
    var bodyEl = $("body"),
        sidedrawerEl = $("#sidedrawer");

    function showSidedrawer() {
        // show overlay
        var options = {
            onclose: function () {
                sidedrawerEl
                    .removeClass("active")
                    .appendTo(document.body);
            }
        };

        var overlayEl = $(overlay("on", options));

        // show element
        sidedrawerEl.appendTo(overlayEl);
        setTimeout(function () {
            sidedrawerEl.addClass("active");
        }, 20);
    }


    function hideSidedrawer() {
        bodyEl.toggleClass("hide-sidedrawer");
    }


    $(".js-show-sidedrawer").on("click", showSidedrawer);
    $(".js-hide-sidedrawer").on("click", hideSidedrawer);

    hideSidedrawer();
}

export {setupSideDrawerTransition};
