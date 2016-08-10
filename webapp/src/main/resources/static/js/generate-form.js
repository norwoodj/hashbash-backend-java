jQuery(function ($) {
    $.get('/api/rainbow-table/hash-functions', function (hashFunctions) {
        $('#hash-function-select').html(hashFunctions.map(function (hashFn) {
            return '<option>' + hashFn + '</option>'
        }).join('\n'));
    });
});
