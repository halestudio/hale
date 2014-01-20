/**
 *  Draw a set of cells using Snap.svg to a SVG element already existing in the DOM.
 * @param elementId the ID of the SVG element into which to draw.
 * @param thisCell the Cell to be drawn.
 * @param options options for controlling the drawing behaviour (optional)
 */
function drawMapping(elementId, thisCell, options) {
    // don't do anything if the input data is insufficient.
    if (elementId == null || thisCell == null) {
        return;
    }

    // initialize Snap.svg.
    var s = Snap(elementId);

    // set default values for parameters
    var myWidth = 800;
    var fNameBoxHeight = 24;
    var whitespaceRows = 2;
    var padding = 5;
    var charLength = 6;

    // overwrite defaults if they were set in the options object.
    if (options != null) {
        if (options.width != null) { myWidth = options.width };
        if (options.lineHeight != null) { fNameBoxHeight = options.lineHeight };
        if (options.padding != null) { padding = options.padding };
        if (options.charLength != null) { charLength = options.charLength };
    }

    var sourceHeight = calculateHeight(thisCell, whitespaceRows, "source") + 1.5;
    var targetHeight = calculateHeight(thisCell, whitespaceRows, "target") + 1.5;
    var myHeight = sourceHeight > targetHeight ? sourceHeight * fNameBoxHeight : targetHeight * fNameBoxHeight;
    var addHeight = addParamHeight(thisCell.functionParameters.length, myHeight, fNameBoxHeight);
    document.getElementById(elementId.substr(1)).style.height = (myHeight + addHeight) + "px";
    var paddingYShift = Math.sqrt(0.5 * padding * padding);
    var paddingXShift = Math.sqrt(0.5 * paddingYShift * paddingYShift);
    var shadowFilter = s.filter(Snap.filter.shadow(2, 2, 3, "#999999"));

    // draw function name & parameter elements
    var nameLength = thisCell.functionName.length * 1.333; // add additional space for parameters and long function names
    if (nameLength * charLength > myWidth / 3) { // deal with too long function names that would take more than 1/3 of screen width
        thisCell.functionName = thisCell.functionName.substr(0, myWidth / 3) + "...";
        nameLength = thisCell.functionName.length * 1.333;
    }
    var paramCount = thisCell.functionParameters.length;

    for (var j = 0; j < thisCell.functionParameters.length; j++) { // deal with parameter k/v pairs that are too long
        var thisParam = thisCell.functionParameters[j];
        var thisParamlength = thisParam.paramLabel.length + thisParam.paramValue.length;
        var refNameLength = nameLength * 1.333;
        if (thisParamlength > refNameLength) {
            // truncate parameter key and value
            if (thisParam.paramLabel.length > refNameLength / 2 - 1) {
                thisParam.paramLabel = thisParam.paramLabel.substr(0, refNameLength / 2 - 1) + "...";
            }
            if (thisParam.paramValue.length > refNameLength / 2 + 1) {
                thisParam.paramValue =  "..." + thisParam.paramValue.substr(refNameLength / 2 - 1);
            }
        }
    }

    var startX = (myWidth / 2) - ((nameLength + 4) * charLength/2);
    var startY = (myHeight / 2) + fNameBoxHeight / 2;
    var functionNameCoordinates = [
        startX, startY,
        startX + ((nameLength + 4) * charLength), startY,
        startX + ((nameLength + 4) * charLength) + fNameBoxHeight/2, startY - fNameBoxHeight/2,
        startX + ((nameLength + 4) * charLength), startY - fNameBoxHeight,
        startX, startY - fNameBoxHeight,
        startX - fNameBoxHeight/2, startY - fNameBoxHeight/2
    ];

    var functionNameShape = s.polygon(functionNameCoordinates).attr({
        stroke: "#000000",
        fill: "#a8d0f4",
        filter: shadowFilter
    });
    var functionNameText = s.text(startX + 2, myHeight / 2 + 5,thisCell.functionName);
    var helpCircle = s.circle(startX + ((nameLength + 3) * charLength), myHeight / 2, (fNameBoxHeight / 2) - 4).attr({
        "stroke": "#000000",
        "fill": "#f4d887",
        "font-size": "14px",
        "class": "infoButton"
    });
    var helpQMark = s.text(startX + ((nameLength + 3) * charLength) - 5, myHeight / 2 + 5, "?").attr({
        "font-weight": "bold",
        "font-size": "14px"
    });
    var qmarkGroup = s.g(helpCircle, helpQMark);
    qmarkGroup.hover(
        function() { // f_in handler
            helpCircle.attr({
                "fill": "#fcf0c3",
                "r": (fNameBoxHeight / 2) - 3
            });
        },
        function() { // f_out handler
            helpCircle.attr({
                "fill": "#f4d887",
                "r": (fNameBoxHeight / 2) - 4
            });
        }
    ) ;
    var paramKVShape = s.rect(startX, startY, ((nameLength + 4) * charLength), (1 + paramCount * 0.75) * fNameBoxHeight).attr({
        stroke: "#000000",
        fill: "#a8d0f4",
        filter: shadowFilter
    });
    s.text(startX + 4, startY + 18, "Parameters").attr({
        "font-size": "12px",
        "font-style": "italic"
    });
    for (var j = 0; j < thisCell.functionParameters.length; j++) {
        s.text(startX + 4, startY + fNameBoxHeight + 12 + (fNameBoxHeight - 6) * j, thisCell.functionParameters[j].paramLabel).attr({
            "font-size": "12px"
        });
    }
    for (var j = 0; j < thisCell.functionParameters.length; j++) {
        s.text(
                startX + ((nameLength + 4) * charLength) - 4,
                startY + fNameBoxHeight + 12 + (fNameBoxHeight - 6) * j,
                thisCell.functionParameters[j].paramValue).attr({
                "font-size": "12px",
                "text-anchor": "end"
            });
    }

    // draw source elements.................................................................................
    drawSourcesTargets(
        s, "source", thisCell, whitespaceRows, myWidth, myHeight, fNameBoxHeight, charLength,
        paddingXShift, paddingYShift, functionNameCoordinates, shadowFilter, elementId.substr(1));

    // draw target elements.................................................................................
    drawSourcesTargets(
        s, "target", thisCell, whitespaceRows, myWidth, myHeight, fNameBoxHeight, charLength,
        paddingXShift, paddingYShift, functionNameCoordinates, shadowFilter, elementId.substr(1));
}

/**
 * tests whether the defined height is sufficient for the number of parameters, and if not, return the number of
 * additional pixels needed.
 * @param paramCount the number of parameters the current function has.
 * @param myHeight the height in pixels already determined.
 */
function addParamHeight(paramCount, myHeight, lineHeight) {
    var requiredSpace = paramCount * lineHeight * 0.75 + lineHeight;
    if (requiredSpace + lineHeight/2 > myHeight / 2) {
        return Math.abs((myHeight / 2) - (requiredSpace + lineHeight));
    }
    else {
        return 0;
    }
}

/**
 * Returns the number of rows needed to draw a cell.
 * @param cell the Cell for which to calculate the height.
 * @param whitespaceRows the LNumber of rows to use as whitespace in between paths.
 */
function calculateHeight(cell, whitespaceRows, side) {
    var elements = cell.targets;
    if (side === "source") {
        elements = cell.sources;
    }
    var totalTargetRows = (elements.length - 1) * whitespaceRows;
    for (var j = 0; j < elements.length; j++) {
        totalTargetRows += elements[j].propertyPath.length;
    }
    return totalTargetRows;
}

function getLengthOfLongestElement(thisList) {
    var longestPathElementLength = 0;
    for (var k = 0; k < thisList.length; k++) {
        if (thisList[k].length > longestPathElementLength) {
            longestPathElementLength = thisList[k].length ;
        }
    }
    return longestPathElementLength;
}

function getInitialShape(startX, startY, lineHeight, longestPathElementLength, charLength) {
    return [ // generic shape that is later modified.
        startX, startY,
        startX, startY - lineHeight,
        startX - ((longestPathElementLength + 4) * charLength), startY - lineHeight,
        startX - ((longestPathElementLength + 4) * charLength), startY - lineHeight,
        startX - ((longestPathElementLength + 4) * charLength) - lineHeight/2, startY - lineHeight/2,
        startX - ((longestPathElementLength + 4) * charLength), startY
    ];
}

function getPathPolyCoords(thisPath, side, coordinates, k, lineHeight, paddingXShift, paddingYShift, myWidth) {
    // default is a target side geometry

    var targetNameCoordinates = [];
    for (var l = 0, len = coordinates.length; l < len; l++) {
        targetNameCoordinates[l] = coordinates[l];
    }
    // adjust x values
    targetNameCoordinates[0] += (thisPath.length - k) * 5;
    targetNameCoordinates[2] += (thisPath.length - k) * 5;
    targetNameCoordinates[4] -= 5 + (thisPath.length - k - 1) * paddingXShift;
    targetNameCoordinates[6] -= 5 + (thisPath.length - k - 1) * paddingXShift;
    targetNameCoordinates[8] -= (thisPath.length - k) * 5;
    targetNameCoordinates[10] -= 5 + (thisPath.length - k - 1) * paddingXShift;

    // adjust y values
    targetNameCoordinates[1] += (thisPath.length - k - 1) * paddingYShift;
    targetNameCoordinates[3] -= (thisPath.length - k - 1) * lineHeight;
    targetNameCoordinates[5] -= (thisPath.length - k - 1) * lineHeight;
    targetNameCoordinates[7] -= (thisPath.length - k - 1 ) * paddingYShift;
    targetNameCoordinates[11] += (thisPath.length - k - 1 ) * paddingYShift;

    if (side === "source") {
        for (var i = 0; i < targetNameCoordinates.length; i += 2) {
            targetNameCoordinates[i] = myWidth - targetNameCoordinates[i];
        }
    }
    return targetNameCoordinates;
}

function drawSourcesTargets(s, side, cell, whitespaceRows, myWidth, myHeight, lineHeight, charLength,
                            paddingXShift, paddingYShift, functionNameCoordinates, shadowFilter, elementId) {
    // calculate number of rows needed
    var totalRows = calculateHeight(cell, whitespaceRows, side);

    var nextRowToFill = 1;
    var elements = cell.targets;
    var mirrorFactor = 1;
    if (side === "source") {
        elements = cell.sources;
        mirrorFactor = -1;
    }
    for (var j = 0; j < elements.length; j++) {
        var thisPath = elements[j].propertyPath;
        var longestPathElementLength = getLengthOfLongestElement(thisPath);

        if (longestPathElementLength < 2) {
            longestPathElementLength = 2;
        }

        // determine truncation length
        if (longestPathElementLength * charLength > myWidth / 3) {
            longestPathElementLength = myWidth / (4 * charLength);
        }

        var startX = myWidth - 5 * (thisPath.length + 1);
        nextRowToFill += (thisPath.length - 1);
        var startY = (myHeight / 2) + (nextRowToFill - (totalRows / 2.0)) * lineHeight;
        nextRowToFill +=  whitespaceRows + 1;
        var basicCoordinates = getInitialShape(startX, startY, lineHeight, longestPathElementLength, charLength);

        for (var k = 0; k < thisPath.length; k++) {
            var targetNameCoordinates = getPathPolyCoords(
                thisPath, side, basicCoordinates, k, lineHeight, paddingXShift, paddingYShift, myWidth);

            var targetNameShape = s.polygon(targetNameCoordinates).attr({
                stroke: "#000000",
                fill: "#e9c551",
                "fill-opacity": 0.5
            });
            if (k === 0) {
                targetNameShape.attr({filter: shadowFilter});
            }
            var truncatedText = thisPath[k];
            if (truncatedText.length > longestPathElementLength) {
                truncatedText = thisPath[k].substr(0, longestPathElementLength * 0.75) + "...";
            }
            var targetNameText = s.text(targetNameCoordinates[0] - 30 * mirrorFactor, targetNameCoordinates[3] + (lineHeight - 7), truncatedText).attr({
                "font-size": "14px"
            });
            if (side === "target") {
                targetNameText.attr({"text-anchor": "end"});
            };

            var infoIconID = elementId + "_iCircle_" + side + "_" + j + "_" + k;

/*            var infoIcon = s.text(targetNameCoordinates[0] - 12 * mirrorFactor, targetNameCoordinates[3] + (lineHeight / 2) + 4, "i").attr({
                "class": "infoCircle",
                "font-family": "WetfIconfont",
                "id": infoIconID
            });*/

            var matrix = new Snap.Matrix().translate(
                targetNameCoordinates[0] + 9 - 14 * mirrorFactor,
                targetNameCoordinates[3] + (lineHeight / 2) + 9).scale(-0.015,-0.015)

            var infoIcon = s.path("M3 600q0 162 80 299.5t217.5 217.5t299.5 80t299.5 -80t217.5 -217.5t80 -299.5t-80 -300t-217.5 -218t-299.5 -80t-299.5 80t-217.5 218t-80 300zM400 300h400v100h-100v300h-300v-100h100v-200h-100v-100zM500 800h200v100h-200v-100z").attr({
                "transform": matrix,
                "class": "infoCircle",
                "id": infoIconID
            });

            // add listeners to info buttons and adjust state according to data availability.
            (function() {
                var selectID = "#" + infoIconID;
                var propertyDisplayID = elementId + "_properties";
                if (elements[j].propertyDescriptions != null) {
                    var thisPropertyDescription = elements[j].propertyDescriptions[k];
                    Snap.select(selectID).click(
                        function () {
                            displayAttributeProperties(propertyDisplayID, thisPropertyDescription);
                        }
                    );
                }
                else {
                    infoIcon.attr({
                        "class": "infoCircleInactive"
                    });
                }
            })();
        }
        // draw connecting lines
        drawConnectorLine(s, functionNameCoordinates, basicCoordinates, side, myWidth);
    }
}

function displayAttributeProperties(elemID, propertyDescription) {
    // add all attributes for a given propertyDescription to the Div below the SVG area
    if (propertyDescription != null) {
        var propertyDiv = document.getElementById(elemID);

        // remove any children that are present.
        while (propertyDiv.firstChild) {
            propertyDiv.removeChild(propertyDiv.firstChild);
        }

        // create a DIV for the "close" button
        var closeDiv = document.createElement("div");
        closeDiv.appendChild(document.createTextNode("close"));
        closeDiv.className = "closeButton";
        closeDiv.onclick = function() {
            while (propertyDiv.firstChild) {
                propertyDiv.removeChild(propertyDiv.firstChild);
            }
        }
        propertyDiv.appendChild(closeDiv);

        // create table for object properties
        var prpTable = document.createElement("table");
        prpTable.className = "propertyTable";
        propertyDiv.appendChild(prpTable);
        for (var aProperty in propertyDescription) {
            var prpRow = prpTable.insertRow();
            var prpKeyCell = prpRow.insertCell(0);
            prpKeyCell.className = "keyCell";
            var prpValueCell = prpRow.insertCell(1);
            prpValueCell.className = "valueCell";
            prpKeyCell.appendChild(document.createTextNode(aProperty));
            prpValueCell.appendChild(document.createTextNode(propertyDescription[aProperty]));
        }
    }
}

function drawConnectorLine(s, functionNameCoordinates, basicCoordinates, side, myWidth) {
    var deltaX = Math.abs(functionNameCoordinates[4] - basicCoordinates[8]) / 2.5;
    var pathD = [
        functionNameCoordinates[4] , functionNameCoordinates[5],
        functionNameCoordinates[4] + deltaX , functionNameCoordinates[5],
        basicCoordinates[8] - deltaX, basicCoordinates[9],
        basicCoordinates[8], basicCoordinates[9]
    ];
    if (side === "source") {
        for (var i = 0; i < pathD.length; i += 2) {
            pathD[i] = myWidth - pathD[i];
        }
    }
    s.path("M " + pathD[0] + " " + pathD[1] + " C " + pathD[2] + " " + pathD[3] + " " + pathD[4] + " " + pathD[5] + " " + pathD[6] + " " + pathD[7]).attr({
        "fill": "none",
        "stroke": "#000000",
        "stroke-width": 3
    });
}
