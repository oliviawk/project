var intervalNet;
var popoverObj;
function displayNetUsed(url, id, reloadFrequency,params) {
          var category = ['upload', 'down'];

          var legendSize = 10,
          	legendColor = {'upload': 'rgba(0, 160, 233, 0.6)', 'down': '#41DB00'};
          
          //generation function
          function generate(data, id) {
        	var controlsData = data.controlsData;
        	var tableData = data.tableData;
            var margin = {top: 20, right: 18, bottom: 25, left: 60},
                width = 383 - margin.left - margin.right,
                height = 200 - margin.top - margin.bottom;

            var parseDate = d3.time.format("%Y-%m-%d %H:%M").parse;
            var format = d3.time.format("%H:%M");
//            var formatPercent = d3.format("kb");

            var x = d3.time.scale()
                .range([0, width]);

            var y = d3.scale.linear()
                .range([height, 0]);

            
            var xAxis = d3.svg.axis()
                .scale(x)
                .ticks(d3.time.hour, 6)
                .tickFormat(d3.time.format("%H:%M"))
                .tickPadding([6])
                .orient("bottom");
            
            var yAxis = d3.svg.axis()
                .scale(y)
                .tickSize(-width)
                .ticks(6)
                .tickFormat(function(v){return v+"MB";})
                .orient("left");

            var newData = (function() {
                var temp = {}, seriesArr = [];

                category.forEach(function (name) {
                    temp[name] = {category: name, values:[]};
                    seriesArr.push(temp[name]);
                  });

                  controlsData.forEach(function (d) {
                    category.map(function (name) {
                      temp[name].values.push({'category': name, 'time': parseDate(d['time']), 'num': d[name]});
                    });
                  });

                return seriesArr;
            })();

            x.domain( d3.extent(controlsData, function(d) { return parseDate(d['time']); }) );	//domain指的是设置定义域，
            																		//extent返回的是最小的ddata的time到最大的ddata的time这样一个跨度
            y.domain([
                      d3.min(newData, function(c) { return d3.min(c.values, function(v) { return v['num']; }); }),
                      d3.max(newData, function(c) { return d3.max(c.values, function(v) { return v['num']; }); })
                    ]);
            
            //定义线条
            var line = d3.svg.line()
                .interpolate("monotone")												//定义线条的样式
                .x(function(d) { return x(d['time']) + 5; })
                .y(function(d) { return y(d['num']); });

            var svg = d3.select(id).append("svg")
                .attr("id", "svg-used2")
                .attr("width", width + margin.right + margin.left)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            svg.append("g")
                .attr("class", "x axis")
                .attr("id", "used1-x-axis")
                .attr("fill", "#C0C0C0")
                .attr("transform", "translate(" + 5 + "," + height + ")")
                .call(xAxis)
                .selectAll(".tick text")
	            .text(function (d) {
	                var str = format(d);
	                if(str == "00:00"){
                        str = d3.time.format("%m-%d")(d);
	                }
	                return str;
	            });

            svg.append("g")
                .attr("class", "y axis")
                .attr("fill", "#C0C0C0")
                .call(yAxis);

            var path = svg.selectAll("countPath2")
            	.data(newData)
            	.enter().append("g")
                .attr("class", "countPath2");

            path.append("path")						//重要
                .attr("d", function(d){
                	return line(d.values)
                })             //使用数据
                .attr("class", 'countRemainPath')
                .attr("stroke-width", 2)          //路径粗细
                .attr('stroke', function(d){
                	return legendColor[d.category]
                });

            
            //图表下的三种类型图标加文字
//            var legend = svg.selectAll('.countLegend')
//                .data(category)
//                .enter()
//                .append('g')
//                .attr('class', 'countLegend')
//                .attr('transform', function(d, i) {
//                  return 'translate(' + (i * 10 * legendSize) + ',' + (height + margin.bottom - legendSize * 1.2) + ')';
//                });
//
//            legend.append('rect')
//                .attr('width', legendSize)
//                .attr('height', legendSize)
//                .style('fill', function(d) {
//                  return legendColor[d];
//                });
//                
//
//            legend.append('text')
//                .data(category)
//                .attr('x', legendSize * 1.2)
//                .attr('y', legendSize / 1.1)
//                .attr("fill", "#fff")
//                .text(function(d){
//                	return d;
//                });

            function xTransLen(t) {
              return x(parseDate(t)) + 5;
            }

          //表格
            var theadText = ["", "", "min", "max", "avg", "current"];
            
            var table1=d3.select("#netUsed-table")
    	 		.append("table")
    	 		.style({"width":"100%", "text-align":"center"})
    	 		.attr("class","table1");

    	    var thead = table1.append("thead");

    	    var tbody = table1.append("tbody");

    	    var theadRow = thead.append("tr");

    	    theadRow.selectAll(".theadTd")
    	    	.data(theadText)
    	    	.enter()
    	        .append("td")
    	        .attr("class", "theadTd")
    	        .style({"color":"rgb(0, 160, 233)","padding":0+"px","margin":0+"px"})
    	        .html(function(d) {
    	            return d;
    	        });
    	    
    	    var tableBodyRows = tbody.selectAll("tr")
    	        .data(category)
    	        .enter()
    	        .append("tr");

    	    tableBodyRows.append("i")
    	    	.attr("class", "table_legend")
    	    	.style("background-color", function(d){
    	    		return legendColor[d];
    	    	})
    	    	.style("color", function(d){
    	    		return legendColor[d];
    	    	})
    	    
    	    tableBodyRows.selectAll("td")
    	        .data(function(d,i) {
    	        	return [category[i], tableData[i].min, tableData[i].max, tableData[i].avg, tableData[i].current];
    	        })
    	        .enter()
    	        .append("td")
    	        .style({"padding":0+"px","margin":0+"px"})
    	        .html(function(d) {
    	            return d;
    	        });
            
            
            this.getOpt = function() {
              var axisOpt = new Object();
              axisOpt['x'] = x;
              axisOpt['y'] = y;
              axisOpt['xAxis'] = xAxis;

              return axisOpt;
            }

            this.getSvg = function() {
              var svgD = new Object();
              svgD['svg'] = svg;
              svgD['points'] = points;
              svgD['line']  =line;
              svgD['legendColor'] = legendColor;

              return svgD;
            }
          }

          //redraw function
          function redraw(data, x, y, xAxis, svg, line, points) {
        	
        	var controlsData = data.controlsData;
          	var tableData = data.tableData;

          	var parseDate = d3.time.format("%H:%M").parse;
//            var formatPercent = d3.format("kb");
            
            var newData = (function() {
                var temp = {}, seriesArr = [];

                category.forEach(function (name) {
                    temp[name] = {category: name, values:[]};
                    seriesArr.push(temp[name]);
                  });

                  controlsData.forEach(function (d) {
                    category.map(function (name) {
                      temp[name].values.push({'category': name, 'time': parseDate(d['time']), 'num': d[name]});
                    });
                  });

                return seriesArr;
            })();

            x.domain( d3.extent(controlsData, function(d) { return parseDate(d['time']); }) );

            xAxis.ticks(d3.time.hours, 6);

            y.domain([
                      d3.min(newData, function(c) { return d3.max(c.values, function(v) { return v['num']; }); }),
                      d3.max(newData, function(c) { return d3.max(c.values, function(v) { return v['num']; }); })
                    ]);
            
            line.x(function(d) { return x(d['time']) + 5; });
            
            if(undefined!=popoverObj){
            	popoverObj.popover('destroy');
            }
            
            svg.select("#used1-x-axis")
                .transition()
                .duration(100)
                .ease("sin-in-out")
                .call(xAxis);

            d3.selectAll('.countPath1').remove();

            var path = svg.selectAll("countPath1")
        	.data(newData)
        	.enter().append("g")
            .attr("class", "countPath1");

	        path.append("path")						//重要
	            .attr("d", function(d){
	            	return line(d.values)
	            })             //使用数据
	            .attr("class", 'countRemainPath')
	            .attr("stroke-width", 3)          //路径粗细
	            .attr('stroke', function(d){
	            	return legendColor[d.category]
            });

            function xTransLen(t) {
              return x(parseDate(t)) + 5;
            }
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
            $("#netUsed").empty();
            $("#netUsed-table").empty();

            if(data["titleTime"]!=undefined){

                document.getElementById('net').innerHTML="网络流量 ("+data["titleTime"]+")";
            }else{
                document.getElementById('net').innerHTML="网络流量 ";
            }
            if( "fail" == data["result"] ){
                console.log("获取网络流量数据失败,失败原因：" + data["message"]);
            }else{
                var sca = new generate(data["resultData"], id);
                clearInterval(intervalMem);
                intervalMem = setInterval(function() {
                    $.ajax({
                        type: 'POST',
                        url: url,
                        data: params,
                        dataType: "json",
                        headers: {
                            "Content-Type": "application/json; charset=utf-8"
                        },
                        success: function (data2) {
                            if(data["titleTime"]!=undefined){
                                document.getElementById('net').innerHTML="网络流量 ("+data["titleTime"]+")";
                            }else{
                                document.getElementById('net').innerHTML="网络流量 ";
                            }
                            if( "fail" == data2["result"] ){
                                console.log("获取网络流量数据失败,失败原因：" + data2["message"]);
                            }else{
                                redraw(data2["resultData"], sca.getOpt()['x'], sca.getOpt()['y'], sca.getOpt()['xAxis'], sca.getSvg()['svg'], sca.getSvg()['line'], sca.getSvg()['points']);
                            }
                        },
                        error: function (e2) {
                            console.error(e2)
                        }
                    });
                }, reloadFrequency);
            }
        },
        error: function (e) {
            console.error(e);
        }

    });
}
