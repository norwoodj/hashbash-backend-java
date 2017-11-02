
let MENU_CATEGORIES = [
    {
        category: "Pages",
        options: [
            {text: "Home", link: "/"},
            {text: "Generate New Table", link: "/generate-rainbow-table"},
            {text: "See Generated Tables", link: "/rainbow-tables"},
        ]
    }
];

let APP_NAME = "Hashbash";

let JobStatus = {
    QUEUED: "QUEUED",
    STARTED: "STARTED",
    COMPLETED: "COMPLETED",
    FAILED: "FAILED"
};

let SearchResult = {
    QUEUED: "QUEUED",
    STARTED: "STARTED",
    FOUND: "FOUND",
    NOT_FOUND: "NOT_FOUND"
};

export {
    MENU_CATEGORIES,
    APP_NAME,
    JobStatus,
    SearchResult
};
