
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

function toTitleCase(input) {
    return input.split("_")
        .map(str => str.charAt(0).toUpperCase() + str.substring(1).toLowerCase())
        .join(" ");
}

let HashFunction  = {
    MD5: "MD5",
    SHA1: "SHA1",
    SHA256: "SHA256",
    SHA384: "SHA384",
    SHA512: "SHA512",

    all: () => (
        [
            HashFunction.MD5,
            HashFunction.SHA1,
            HashFunction.SHA256,
            HashFunction.SHA384,
            HashFunction.SHA512,
        ]
    )
};

let MD5_REGEX = /^[0-9abcdef]{32}$/;
let SHA1_REGEX = /^[0-9abcdef]{39}$/;
let SHA256_REGEX = /^[0-9abcdef]{64}$/;
let SHA384_REGEX = /^[0-9abcdef]{96}$/;
let SHA512_REGEX = /^[0-9abcdef]{128}$/;


let REGEX_MAP = new Map([
    [HashFunction.MD5, MD5_REGEX],
    [HashFunction.SHA1, SHA1_REGEX],
    [HashFunction.SHA256, SHA256_REGEX],
    [HashFunction.SHA384, SHA384_REGEX],
    [HashFunction.SHA512, SHA512_REGEX],
]);

function is_hash(hashFunction, supposedHash) {
    console.log(`IS HASH ${supposedHash}`);
    console.log(REGEX_MAP.get(hashFunction).test(supposedHash));
    return REGEX_MAP.get(hashFunction).test(supposedHash);
}

export {getErrorMessage, toTitleCase, HashFunction, is_hash};
