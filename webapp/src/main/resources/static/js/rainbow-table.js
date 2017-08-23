jQuery(function ($) {
    var tableBody = $('tbody');
    var rainbowTableSet = new Set();

    function getProgress(rainbowTable) {
        return rainbowTable.status == 'COMPLETED'
            ? '100%'
            : Math.round(rainbowTable.chainsGenerated / rainbowTable.numChains * 100).toString() + "%";
    }

    function getProgressBarCell(rainbowTable) {
        var progress = getProgress(rainbowTable);

        return [
            '<td>',
            '<div class="progress-bar-outer">',
            '<div id="progress-rt-' + rainbowTable.id + '" class="progress-bar-inner color-change" style="width: ' + progress + ';"></div>',
            '</div>',
            '</td>'
        ].join('\n')
    }

    function addRow(rainbowTable) {
        var rowHtml = [
            '<tr>',
            '<td>' + rainbowTable.id + '</td>',
            '<td>' + rainbowTable.name + '</td>',
            '<td id="status-rt-' + rainbowTable.id + '">' + rainbowTable.status + '</td>',
            '<td>' + rainbowTable.hashFunction + '</td>',
            '<td>' + rainbowTable.numChains + '</td>',
            '<td id="chains-rt-' + rainbowTable.id + '">' + rainbowTable.chainsGenerated + '</td>',
            '<td>' + rainbowTable.chainLength + '</td>',
            '<td>' + rainbowTable.characterSet + '</td>',
            '<td>' + rainbowTable.passwordLength + '</td>',
            getProgressBarCell(rainbowTable),
            '</tr>'
        ].join('\n');

        tableBody.append(rowHtml);
    }

    function updateGenerationProgress(rainbowTable) {
        $('#progress-rt-' + rainbowTable.id).css({'width': getProgress(rainbowTable)});
        $('#chains-rt-' + rainbowTable.id).html(rainbowTable.chainsGenerated);
        $('#status-rt-' + rainbowTable.id).html(rainbowTable.status);
    }

    function pollRainbowTables() {
        $.get('/api/rainbow-table/', function (rainbowTables) {
            rainbowTables.forEach(function (rainbowTable) {
                if (!rainbowTableSet.has(rainbowTable.id)) {
                    addRow(rainbowTable);
                    rainbowTableSet.add(rainbowTable.id);
                } else {
                    updateGenerationProgress(rainbowTable);
                }
            });

            setTimeout(pollRainbowTables, 2000);
        });
    }

    pollRainbowTables();
});