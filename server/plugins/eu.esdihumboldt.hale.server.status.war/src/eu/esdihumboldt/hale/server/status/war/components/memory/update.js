var chart_plot;
$(document).ready(function() {
	total = [[0,0]];
	used = [[0,0]];
	chart_plot = $.jqplot('chart', [total, used], {
		seriesDefaults: {fill: true, showMarker: false},
		series: [
			{label: 'Total'},
			{label: 'Used'}
		],
		highlighter: {sizeAdjust: 7.5},
		axes: {
			xaxis: {min: 0, max: 60, numberTicks: 7, tickOptions: {formatString:'%ds'}}, 
			yaxis: {min: 0, max: 900, numberTicks: 5, tickOptions: {formatString:'%d&nbsp;MB'}}
		}
	});
});