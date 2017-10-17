
function getErrorMessage(errorResponse) {
    if (errorResponse.status >= 500) {
        return "Unknown Server Error occurred. This means something's down, or John is a bad programmer, sorry";
    } else if (errorResponse.status == 404) {
        return "The resource that was requested does not exist!";
    } else if (errorResponse.status >= 400 && errorResponse.responseJSON && errorResponse.responseJSON.message) {
        return errorResponse.responseJSON.message;
    } else {
        console.log(errorResponse);
        return "Unknown error occurred, check the console for details";
    }
}

export {getErrorMessage};