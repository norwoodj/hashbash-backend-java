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
            '<div id="progress-rt-' + rainbowTable.id + '" class="progress-bar-inner color-change" style="text-align: center; width: ' + progress + '">' + progress + '</div>',
            '</div>',
            '</td>'
        ].join('\n')
    }

    function addRow(rainbowTable) {
        var rowHtml = [
            '<tr>',
            '<td>' + rainbowTable.id + '</td>',
            '<td>' + rainbowTable.name + '</td>',
            '<td>' + rainbowTable.status + '</td>',
            '<td>' + rainbowTable.hashFunction + '</td>',
            '<td>' + rainbowTable.numChains + '</td>',
            '<td>' + rainbowTable.chainsGenerated + '</td>',
            '<td>' + rainbowTable.chainLength + '</td>',
            '<td>' + rainbowTable.characterSet + '</td>',
            '<td>' + rainbowTable.passwordLength + '</td>',
            getProgressBarCell(rainbowTable),
            '</tr>'
        ].join('\n');

        tableBody.append(rowHtml);
    }

    function updateProgressBar(rainbowTable) {
        $('#progres-rt-' + rainbowTable.id).css('width', getProgress(rainbowTable));
    }

    function pollRainbowTables() {
        $.get('/api/rainbow-table/', function (rainbowTables) {
            for (var i = 0; i < rainbowTables.length; ++i) {
                if (!rainbowTableSet.has(rainbowTables[i].id)) {
                    addRow(rainbowTables[i]);
                    rainbowTableSet.add(rainbowTables[i].id);
                } else if (rainbowTables[i].status != 'COMPLETED') {
                    updateProgressBar(rainbowTables[i]);
                }
            }

            setTimeout(pollRainbowTables, 2000);
        });
    }

    pollRainbowTables();
});