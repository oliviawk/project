function lineChartConfig(){
    return {
        width:720,
        height:350,
        margin:{top:45,right:30,bottom:25,left:50},
        lineColor:{"发布管理平台":"green","监控系统":"#178BCA"},    //图例的 文字和颜色
        lineType:"linear",    //折线的类型  cardinal:圆弧型  linear：线型
        lineWidth:2,
        text_x:12,          //X轴文字字体大小
        text_y:12,          //Y轴文字字体大小
        desc_y:"",          //Y轴 描述文本
        points_r:4,         //离散点 半径
        points_stroke_color:"white",    //离散点 的外圆颜色
        points_stroke_width:1.5,    //离散点 的外圆宽度
        ticks : 15,
        duration:1500       //修改数据时，动画效果时间
    }
}


function lineChartMain(elementId , data , config ){

    if(data == null || data.length < 1){
        return console.error("数据库连接：数据错误")
    }
    if(config == null) {
        config = lineChartConfig();
    }

    // var parseDate = d3.time.format("%H:%M").parse;
    var parseDate = d3.time.format("%Y-%m-%d %H:%M").parse;
    // c、onsole.log(format("2017-07-04 10:10"))

    //数据转换
    function dataSource(objData){
        var temp = {}, seriesArr = [];

        for(var key in config.lineColor){
            temp[key] = {category: key, values:[]};
            seriesArr.push(temp[key]);
        }

        objData.forEach(function (d) {
            for(var key in config.lineColor){
                temp[key].values.push({'category': key, 'time': parseDate(d['time']), 'num': d[key]});
            }
        });

        return seriesArr;
    }

    //转换数据
    var newData = dataSource(data);
    var width = config.width - config.margin.left - config.margin.right;
    var height = config.height  - config.margin.top - config.margin.bottom;

    //设置y轴最大值  取数据内的最大值
    var maxNum = d3.max(newData, function(c) {
        return d3.max(c.values, function(v) {
            return v['num'];
        });
    });

    var x = d3.time.scale()
        .range([0, width])
        .domain(d3.extent(data, function(d) { return parseDate(d['time']); }) );	//domain指的是设置定义域

    var y = d3.scale.linear()
        .range([height, 0])
        .domain([0,maxNum*1.1]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .ticks(d3.time.minute, config.ticks)
        .tickFormat(d3.time.format("%H:%M"))
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .ticks(6)
        .orient("left");

    // 定义线条
    var line = d3.svg.line()
        .interpolate(config.lineType)												//定义线条的类型
        .x(function(d) { return x(d['time']); })
        .y(function(d) { return y(d['num']); });

    d3.select(elementId+" svg").remove();      //删除标签内的内容

    //创建新画布
    var svg = d3.select(elementId).append("svg")
        .attr("width", config.width)
        .attr("height", config.height)
        .append("g")
        .attr("transform", "translate(" + config.margin.left + "," + (config.margin.top) + ")");

    //建立x轴
    svg.append("g")
        .attr("class", "axis axis_x")
        .attr("fill", "#fff")
        .attr("transform", "translate(0," + height + ")")
        .attr("font-size",config.text_x)
        .call(xAxis);
        // .selectAll(".tick text").attr("fill","#fff");

    //建立y轴
    svg.append("g")
        .attr("class", "axis axis_y")
        .attr("font-size",config.text_y)
        .attr("fill", "#fff")
        .call(yAxis)
        // .selectAll(".tick text").attr("fill","#fff")
        .append("text")
        .attr("x", -20)
        .attr("y", y(y.ticks().pop()) - 40)
        .attr("dy", "0.32em")
        .attr("fill", "#fff")
        .attr("font-weight", "bold")
        .attr("text-anchor", "start")
        .text(config.desc_y);

    //画线
    var path = svg.selectAll(".pathg")
        .data(newData)
        .enter().append("g")
        .attr("class", "pathg");

    path.append("path")
        .attr("d", function(d){
            return line(d.values)
        })             //使用数据
        .attr("stroke-width",config.lineWidth)
        .attr("fill","none")
        .attr('stroke', function(d){
            return  config.lineColor[d.category];
        });

    //生成离散点
    var points = svg.selectAll(".pointsg")
        .data(newData)
        .enter().append("g")
        .attr("class", "pointsg");

    points.selectAll(".points")
        .data(function(d){
            return d.values;
        })
        .enter().append("circle")
        .attr("fill", function(d){
            return  config.lineColor[d.category];
        })
        .attr("cx", function (d) { return x(d['time']); })				//点的圆心x坐标
        .attr("cy", function (d) { return y(d['num']); })					//点的圆心y坐标
        .attr("r", config.points_r)                                           //点的圆半径
        // .attr("stroke",config.points_stroke_color)
        // .attr("stroke-width",config.points_stroke_width)
        .on("mouseover", function (d) {                     //鼠标悬浮事件
            d3.select(this).transition().duration(100)
                .attr("r",config.points_r+2);
            $(this).tooltip({
                'container': 'body',
                'placement': 'top',
                'title': '数据量：'+d['num'],
                'trigger': 'hover'
            }).tooltip('show');
        })
        .on("mouseout",  function (d) {
            d3.select(this).transition().duration(100)
                .attr("r",config.points_r);
            $(this).tooltip('destroy');
        });

    //图例
    var colorKeys = [];
    for(var key in config.lineColor){
        colorKeys.push(key);
    }
    var legend = svg.append("g")
        .attr('transform', 'translate(160 ,' + (config.margin.bottom ) + ')');

    var legend_g = legend.selectAll('g')
        .data(colorKeys)
        .enter()
        .append('g')
        .attr('transform', function(d, i) {
            if(i < 3){
                return 'translate(' + (i * 30 ) + ' , 0)';
            }else {
                return 'translate(' + ((i-3) * 30 ) + ' , -30)';
            }


        });

    legend_g.append('line')
        .attr('stroke-width', '3')
        .attr('x1', function(d,i){
            if(i>2){
                return (i-3)*100+30;
            }
            return i*100+30;
        })
        .attr('y1', -8)
        .attr('x2', function(d,i){
            if(i>2){
                return (i-3)*100+50;
            }
            return i*100+50;
        })
        .attr('y2', -8)
        .attr('stroke', function(d) {
            return config.lineColor[d];
        });

    legend_g.append('text')
        .attr('x', function(d,i){
            if(i>2){
                return (i-3)*100+50;
            }
            return i*100+50;
        })
        .attr('y', -3)
        .attr("fill", "#fff")
        .text(function(d){
            return d;
        });


    //修改数据
    function LineDataUpdate(){
        this.update = function(value){

            var realTimeData = dataSource(value);

            //设置y轴最大值  取数据内的最大值
            var maxNum_new = d3.max(realTimeData, function(c) {
                return d3.max(c.values, function(v) {
                    return v['num'];
                });
            });

            //重新定义 X轴、Y轴
            x.domain(d3.extent(value, function(d) {return parseDate(d['time']); }) );
            xAxis.scale(x);
            y.domain([0,maxNum_new+(maxNum_new/6)]);
            yAxis.scale(y);
            //重新绘制 X轴、Y轴
            svg.select(".axis_x").transition().duration(config.duration).call(xAxis).attr("fill", "#fff");
            svg.select(".axis_y").transition().duration(config.duration).call(yAxis).attr("fill", "#fff");

            //修改线条
            svg.selectAll(".pathg").data(realTimeData)
                .select("path").transition().duration(config.duration)
                .attr("d", function(d){
                    return line(d.values)
                })
            //修改离散点
            svg.selectAll(".pointsg").data(realTimeData)
                .selectAll("circle")
                .data(function(d){
                    return d.values;
                })
                .transition().duration(config.duration)
                .attr("cx", function (d) { return x(d['time']); })
                .attr("cy", function (d) { return y(d['num']); })
        }
    }
    return new LineDataUpdate();
}
