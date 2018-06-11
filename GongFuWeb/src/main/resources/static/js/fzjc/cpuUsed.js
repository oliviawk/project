var intervalCpu;

function displayCpuUsed(url, id, reloadFrequency, params) {

    var category = ['used', 'free'];

    var legendSize = 10, legendColor = {
        'used': 'rgba(19, 159, 222, 1)',
        'free': 'rgba(231, 126, 0, 1)'
    };

    function generate(baseData, id) {

        var data = baseData.controlsData;
        var margin = {
            top: 20,
            right: 15,
            bottom: 25,
            left: 50
        }, width = 830 - margin.left - margin.right, height = 200 - margin.top
            - margin.bottom;

        var parseDate = d3.time.format("%Y-%m-%d %H:%M").parse;
        var format = d3.time.format("%H:%M");
        var formatPercent = d3.format("%");

        var x = d3.time.scale().range([0, width]);

        var y = d3.scale.linear().range([height, 0]);

        var xAxis = d3.svg.axis().scale(x).ticks(d3.time.minutes, 30).tickFormat(
            d3.time.format("%H:%M"))
        // .tickSize(-height)
        // .tickPadding([6])
            .orient("bottom");

        var yAxis = d3.svg.axis().scale(y).tickSize(-width).tickValues(
            [0.2, 0.4, 0.6, 0.8, 0.9, 1.0]).tickFormat(formatPercent)
            .orient("left");

        var newData = (function () {
            var temp = {}, seriesArr = [];

            category.forEach(function (name) {
                temp[name] = {
                    category: name,
                    values: []
                };
                seriesArr.push(temp[name]);
            });

            data.forEach(function (d) {
                category.map(function (name) {
                    temp[name].values.push({
                        'category': name,
                        'time': parseDate(d['time']),
                        'num': d[name]
                    });
                });
            });

            return seriesArr;
        })();

        x.domain(d3.extent(data, function (d) {
            return parseDate(d['time']);
        }));

        var area = d3.svg.area().x(function (d) {
            return x(d['time']);
        }).y0(height).y1(function (d) {
            return y(d['num'] / 100);
        }).interpolate("cardinal");

        var svg = d3.select(id).append("svg").attr("id", "svg-cpuUsed1").attr(
            "width", width + margin.right + margin.left).attr("height",
            height + margin.top + margin.bottom).append("g").attr(
            "transform",
            "translate(" + margin.left + "," + margin.top + ")");

        svg.append("g").attr("class", "x axis").attr("id", "cpuUsed1-x-axis")
            .attr("fill", "#C0C0C0").attr("transform",
            "translate(0," + height + ")").call(xAxis).selectAll(
            ".tick text").text(function (d) {
            var str = format(d);
            if (str == "00:00") {
                str = d3.time.format("%m-%d")(d);
            }
            return str;
        });

        svg.append("g").attr("class", "y axis").attr("fill", "#C0C0C0").call(
            yAxis);

        var onG = svg
            .append("g")
            .on(
                "mousemove",
                function (d) {
                    d3.selectAll(".tagShape").remove(); // 删除前一次移动生成的点
                    // var xy = d3.mouse(this); // 获取鼠标移动相对当前元素的xy轴坐标
                    // var interval = width / data.length; // 24小时以间隔10分钟分为144份
                    // var xNumber = Math.round(xy[0] / interval); // 获取有多少个10分钟，组成当前时间
                    //
                    // var startTime = new Date(data[0].time);
                    // var times = startTime.getTime();
                    //
                    // var hTime = parseInt(xNumber / 6);
                    // var mTime = xNumber % 6;
                    // startTime.setTime(times + 1000 * 60 * 60 * hTime
                    // 		+ 1000 * 60 * 10 * mTime);
                    //
                    // var used, free;
                    // for (var ii = 0; ii < newData[0].values.length; ii++) {
                    // 	if (startTime + "" == newData[0].values[ii].time
                    // 			+ "") { // 对比数据获取当前时间对应的y轴值，以便确定生成标记的位置
                    // 		used = newData[0].values[ii].num;
                    // 		free = newData[1].values[ii].num;
                    //
                    // 		break;
                    // 	}
                    // }
                    //
                    // if ((xy[0] - 50) <= 50) {
                    // 	xy[0] = 100;
                    // }
                    // if ((xy[0] - 50) > (width - 150)) {
                    // 	xy[0] = width - 100;
                    // }
                    // if (xy[1] >= height / 2) {
                    // 	xy[1] = xy[1] - 50;
                    // } else {
                    // 	xy[1] = xy[1] + 70;
                    // }
                    // // var display = $("#tooltip-p").css("display");
                    // // if( display == 'none' ){
                    // $("#tooltip-p").css({
                    // 	"display" : "",
                    // 	"left" : "" + (xy[0] - 50) + "px",
                    // 	"top" : "" + xy[1] + "px"
                    // });
                    // $("#tooltip-p").find("span")
                    // 		.html(format(startTime));
                    // d3.selectAll(".tagI").remove(); // 删除前一次移动生成的点
                    // $("#tooltip-p").find("div").empty();
                    // if (used == undefined)
                    // 	used = 0;
                    // if (free == undefined)
                    // 	free = 0;
                    // $("#tooltip-p")
                    // 		.find("div")
                    // 		.append(
                    // 				"</br><i class='table_legend' style='background-color:rgba(19, 159, 222, 1);'></i><span> used："
                    // 						+ used
                    // 						+ "%</span>"
                    // 						+ "</br><i class='table_legend' style='background-color:rgba(231, 126, 0, 1);'></i><span> free："
                    // 						+ free + "%</span>");
                }).on("mouseout", function (d) {
                d3.selectAll(".tagShape").remove(); // 删除前一次移动生成的点
                // $("#tooltip-p").css("display", "none");
            });

        var path = onG.selectAll(".gPath").data(newData).enter().append("g")
            .attr("class", "gPath")

        path.append("path").attr("d", function (d) {
            return area(d['values']);
        }).attr("class", function (d) {
            if (d['category'] === 'used')
                return 'areaU';
            else
                return 'areaM';
        });

        svg.append("line").attr("x1", 0).attr("x2", width).attr("y1", y(0.9))
            .attr("y2", y(0.9)).attr("stroke", "red");

        // 表格
        var theadText = ["", "", "min", "max", "avg", "current"];

        var tableData = baseData.tableData;

        var table1 = d3.select("#cpuUsed-table").append("table").style({
            "width": "100%",
            "text-align": "center"
        }).attr("class", "table1");

        var thead = table1.append("thead");

        var tbody = table1.append("tbody");

        var theadRow = thead.append("tr");

        theadRow.selectAll(".theadTd").data(theadText).enter().append("td")
            .attr("class", "theadTd").style({
            "color": "rgb(0, 160, 233)",
            "padding": 0 + "px",
            "margin": 0 + "px"
        }).html(function (d) {
            return d;
        });

        var tableBodyRows = tbody.selectAll("tr").data(category).enter()
            .append("tr");

        tableBodyRows.append("i").attr("class", "table_legend").style(
            "background-color", function (d) {
                return legendColor[d];
            }).style("color", function (d) {
            return legendColor[d];
        })

        tableBodyRows.selectAll("td").data(
            function (d, i) {
                return [category[i], tableData[i].min + "%",
                    tableData[i].max + "%", tableData[i].avg + "%",
                    tableData[i].current + "%"];
            }).enter().append("td").style({
            "padding": 0 + "px",
            "margin": 0 + "px"
        }).html(function (d) {
            return d;
        });

        this.getOpt = function () {
            var axisOpt = new Object();
            axisOpt['x'] = x;
            axisOpt['y'] = y;
            axisOpt['xAxis'] = xAxis;
            axisOpt['width'] = width;

            return axisOpt;
        }

        this.getSvg = function () {
            var svgD = new Object();
            svgD['svg'] = svg;
            svgD['onG'] = onG;
            svgD['area'] = area;
            svgD['path'] = path;

            return svgD;
        }
    }

    // redraw function
    function redraw(data, x, y, xAxis, svg, area, onG) {
        // format of time data
        var parseDate = d3.time.format("%H:%M").parse;
        var formatPercent = d3.format("%");

        var newData = (function () {
            var temp = {}, seriesArr = [];

            category.forEach(function (name) {
                temp[name] = {
                    category: name,
                    values: []
                };
                seriesArr.push(temp[name]);
            });

            data.forEach(function (d) {
                category.map(function (name) {
                    temp[name].values.push({
                        'category': name,
                        'time': parseDate(d['time']),
                        'num': d[name]
                    });
                });
            });

            return seriesArr;
        })();

        x.domain(d3.extent(data, function (d) {
            return parseDate(d['time']);
        }));

        xAxis.ticks(d3.time.hours, 6);

        svg.select("#cpuUsed1-x-axis").transition().duration(200).ease(
            "sin-in-out").call(xAxis);

        d3.selectAll('.gPath').remove();

        var path = onG.selectAll(".gPath").data(newData).enter().append("g")
            .attr("class", "gPath");

        path.append("path").attr("d", function (d) {
            return area(d['values']);
        }).attr("class", function (d) {
            if (d['category'] === 'used')
                return 'areaU';
            else if (d['category'] === 'free')
                return 'areaD';
            else
                return 'areaM';
        });

    }

    $.ajax({
        type: 'POST',
        url: url,
        data: params,
        dataType: "json",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (data) {
            $("#cpuUsed").empty();
            $("#cpuUsed-table").empty();
            if (data["titleTime"] != undefined
                && data["titleTime"] != null) {
                document.getElementById('cpu').innerHTML = "cpu使用率 ("
                    + data["titleTime"] + ")";
            } else {
                document.getElementById('cpu').innerHTML = "cpu使用率 ";
            }
            if ("fail" == data["result"]) {
                console.log("获取内存使用率数据失败,失败原因：" + data["message"]);
            } else {
                var sca = new generate(data["resultData"], id);
                clearInterval(intervalMem);
                intervalMem = setInterval(
                    function () {
                        if (data["titleTime"] != undefined
                            && data["titleTime"] != null) {
                            document.getElementById('cpu').innerHTML = "cpu使用率 ("
                                + data["titleTime"] + ")";
                        } else {
                            document.getElementById('cpu').innerHTML = "cpu使用率 ";
                        }
                        $.ajax({
                            type: 'POST',
                            url: url,
                            data: params,
                            dataType: "json",
                            headers: {
                                "Content-Type": "application/json; charset=utf-8"
                            },
                            success: function (data2) {
                                if ("fail" == data2["result"]) {
                                    console.log("获取内存使用率数据失败,失败原因："
                                        + data2["message"]);
                                } else {
                                    redraw(data2["resultData"], sca
                                        .getOpt()['x'], sca
                                        .getOpt()['y'], sca
                                        .getOpt()['xAxis'], sca
                                        .getSvg()['svg'], sca
                                        .getSvg()['area'], sca
                                        .getSvg()['onG']);
                                }
                            },
                            error: function (e2) {
                                console.error(e2);
                            }
                        })
                    }, reloadFrequency);
            }
        },
        error: function (e) {
            console.error(e);
        }

    });
    // $.post(url,params,function(data) {
    //
    // });
}
