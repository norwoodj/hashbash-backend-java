jQuery(function ($) {
    $.get('/api/rainbow-table/hash-functions', function (hashFunctions) {
        $('#hash-function-select').html(hashFunctions.map(function (hashFn) {
            return '<option>' + hashFn + '</option>'
        }).join('\n'));
    });

    var rainbowTableGenerateForm = $('form');

    rainbowTableGenerateForm.submit(function () {
        var jsonData = {};
        rainbowTableGenerateForm.serializeArray().forEach(function (kvPair) {
            jsonData[kvPair.name] = kvPair.value;
        });

        $.ajax({
            type: 'POST',
            url: '/api/rainbow-table/',
            contentType: 'application/json',
            data: JSON.stringify(jsonData),
            success: function () {
                window.location.replace('/rainbow-tables.html');
            },
            error: function failure() {
                window.location.replace('/rainbow-tables.html');
            }
        });

        return false;
    });
});
