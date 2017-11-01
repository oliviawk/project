function ganttChart_config(){
    return {
        width:1800,
        height:500,
        margin:{top:40,right:20,bottom:5,left:100},
        textY_y:20,                 //Y轴文字 y坐标
        textX_y:-5,                 //X轴文字 y坐标
        stroke_dasharray:"3,5",     //虚线线段长度
        isExtend: false,             //是否启用反向延长线
        extend_num: -60,            //反向延长线长度
        taskNames:["船舶","突发事件","预警信号","台风","重点天气提示","LAPS3KM","空气质量","江河水库水位","炎热程度","风流场","台风专题","OCF3H","天气公报"],
        taskStatus:{"OK" : "#41DB00",  "Major":"#DB4907"},    //状态样式码
        show_taking:true,           //是否显示耗时
        show_startTime:false,       //是否显示开始时间
        show_endTime:false,         //是否显示结束时间
        x_orient:"top",             //x 轴位置
        y_orient:"left",            //y 轴位置
        time_granularity:"m",        //时间粒度  y:年 、 M:月 、 d:日、 h:时、 m:分、 s:秒
        time_interval:30,
        time_offset:10,
        isGradient:true,            //是否开启阈值
        jsonToDate_Format:"%Y-%m-%d %H:%M:%S.%L%Z",
    }
}

function ganttChart(elementId , jsonData , config ) {
    if (config == null) {
        config = ganttChart_config();
    }
    var taskNames = config.taskNames;           // 业务名
    var taskStatus = config.taskStatus;         //状态编码
    /*----------时间粒度转换--------*/
    var granularity , granularity_offset = null;
    var timeGranularity = function () {
        var d = config.time_granularity;
        switch(d)
        {
            // case "y":
            //     granularity = d3.time.year;
            //     break;
            // case "M":
            //     granularity = d3.time.month;
            //     break;
            // case "d":
            //     granularity = d3.time.hour;
            //     granularity = d3.time.day;
            //     break;
            case "h":
                granularity = d3.time.hour;
                granularity_offset = d3.time.minute;
                break;
            case "m":
                granularity = d3.time.minute;
                granularity_offset = d3.time.second;
                break;
            // case "s":
            //     granularity = d3.time.second;
            //     break;
            default:
                granularity = d3.time.hour;
                granularity_offset = d3.time.minute;
        }
        return null;
    }();
    var timeDranularityWidth = 0;     //时间粒度对应的 width 属性

    var timeDomainStart = granularity_offset.offset(new Date(),-config.time_offset);
    var timeDomainEnd = granularity_offset.offset(new Date(),+config.time_offset);


    var tickFormat = "%H:%M";
    var format = d3.time.format(config.jsonToDate_Format).parse;
    var format_text = d3.time.format(tickFormat);
    var width = config.width - config.margin.left - config.margin.right;
    var height = config.height  - config.margin.top - config.margin.bottom;
    var margin = config.margin;

    var x = d3.time.scale();
    var y = d3.scale.ordinal();
    var xAxis = d3.svg.axis();
    var yAxis = d3.svg.axis();

    d3.select(elementId+" svg").remove();      //删除标签内的内容

    //创建新画布
    var svg = d3.select(elementId).append("svg")
        .attr("width", config.width)
        .attr("height", config.height);

    var main_g = svg.append("g")
        .attr("class", "main_g")
        .attr("width", width )
        .attr("height", height )
        .attr("transform", "translate(" + margin.left + ", " + margin.top + ")");

    //绘制X、Y轴
    main_g.append("g")
        .attr("class", "x axis")
        .attr("fill","#fff");

    main_g.append("g")
        .attr("class", "y axis")
        .attr("fill","#fff");


    //转换数据
    var data = data_transform(jsonData);
    var sd = granularity.offset(data[0].endDate , +0);
    var dd = granularity.offset(data[0].endDate ,+config.time_interval);

    gantChart_main(data);
    //绘制主方法
    function gantChart_main(data){
        initTimeDomain();
        initAxis();

        if(timeDranularityWidth == 0){
            timeDranularityWidth = x(dd) - x(sd);
        }
        console.log(timeDranularityWidth)

        var one_minute_tdw = timeDranularityWidth / (30 * 60) ; //1秒 ， 对应的长度
        /*--------时效条形图标签---------*/
        var rect_g = main_g.selectAll(".chart")
            .data(data,function(d) {
                return d.startDate + d.taskName + d.endDate;
            });

        rect_g.enter()
            .append("g")
            .attr("class","chart")
            .attr("transform", function(d) {
                return "translate(" + (x(d.startDate)) + "," + (y(d.taskName)) + ")";
            });

        /*------------绘制图形和文字------------------*/
        var rects = rect_g.selectAll("rect")
            .data(function (d) {
                var aa = [];
                aa.push(d)
                aa.push(d)
                return aa;
            });
        //判断是否启动阈值
        if(config.isGradient){

            rects.enter().append("rect")
                .attr("y", 7)
                .attr("height", y.range()[1] * 0.6)
                .attr("width", function(d,i) {
                    if(i == 0){     //实际的长度
                        var wid = (x(d.endDate) - x(d.startDate));
                        return wid < one_minute_tdw ? one_minute_tdw:wid;
                    }else{          //阈值的长度
                        var wid = x(d.endDate) - x(d.startDate);
                        wid = wid > one_minute_tdw*20 ? one_minute_tdw*20 : wid;
                        return wid < one_minute_tdw ? one_minute_tdw:wid;
                    }
                })
                .attr("fill",function (d,i) {
                    if(i == 0){
                        return "#FFB612";
                    }else{
                        return taskStatus[d.status];
                    }

                });
        }else{
            rects.enter().append("rect")
                .attr("y", 7)
                .attr("height", y.range()[1] * 0.6)
                .attr("width", function(d) {
                    //指定最小的长度
                    var wid = (x(d.endDate) - x(d.startDate));
                    return wid < one_minute_tdw ? one_minute_tdw:wid;
                })
                .attr("fill",function (d) {
                    return taskStatus[d.status];
                });
        }
        //显示耗时
        if(config.show_taking){

            rects.enter().append("text")
                .attr("y",y.range()[1] * 0.6)
                .attr("x",function(d) {
                    return (x(d.endDate) - x(d.startDate)) / 2;
                })
                .attr("text-anchor","middle")
                .attr("fill","#fff")
                .text(function (d,i) {
                    if(i == 0){
                        var lTime = (d.endDate - d.startDate)/1000;
                        return lTime < 60 ? (Math.ceil((lTime)) + " s") : (Math.ceil((lTime/60)) + " m");
                    }
                })
        }
        //显示前后时间
        if(config.show_startTime){

            rects.enter().append("text")
                .attr("y",y.range()[1] * 0.6)
                .attr("text-anchor","end")
                .attr("fill","#fff")
                .text(function (d,i) {
                    if(i == 0){
                        return format_text(d.startDate)
                    }
                })
        }
        if(config.show_endTime){
            rects.enter().append("text")
                .attr("x",function(d) {
                    return (x(d.endDate) - x(d.startDate));
                })
                .attr("y",y.range()[1] * 0.6)
                .attr("text-anchor","start")
                .attr("fill","#fff")
                .text(function (d,i) {
                    if(i == 0){
                        return format_text(d.endDate);
                    }
                })
        }

        rect_g.transition()
            .attr("transform", function(d) {
                return "translate(" + (x(d.startDate)) + "," + (y(d.taskName)) + ")";
            });

        rects.exit().remove();

        rect_g.exit().remove();
        //绘制X、Y轴
        main_g.select(".x")
            .transition()
            .call(customXAxis);

        main_g.select(".y")
            .transition().call(customYAxis);

    }

    //修改数据方法
    function updateMain(){
        this.update  = function (upData){
            data = data_transform(upData);
            gantChart_main(data);

        }
    }


    /*---------- X 轴样式修改 ----------------*/
    function customXAxis(g) {
        g.call(xAxis);
        // g.select(".domain").remove();
        g.selectAll(".tick line").attr("stroke", "#fff").attr("stroke-dasharray", config.stroke_dasharray);
        g.selectAll(".tick text").attr("y", config.textX_y);
    }
    /*---------- Y 轴样式修改 ----------------*/
    function customYAxis(g) {
        g.call(yAxis);
        g.selectAll(".tick text").attr("y",config.textY_y)
        g.selectAll(".tick line").attr("opacity","0.5").attr("x1",function (d) {
            if(config.isExtend){
                return config.extend_num;
            }
            return 0;
        });
    }

    /*=-------------数据转换---------*/
    function data_transform(dataOld) {
        var result = [];
        dataOld.forEach(function (d) {
            result.push({
                "startDate":format(d.startDate),
                "endDate":format(d.endDate),
                "taskName":d.taskName,
                "status":d.status
            });
            // d.startDate = format(d.startDate);
            // d.endDate = format(d.endDate);

            if($.inArray(d.taskName, taskNames) == -1){
                taskNames.push(d.taskName);
            }
        })
        return result;
    }
    /*---------- 初始化 X、Y 轴 ----------------*/
    function initAxis() {
        x = d3.time.scale()
            .domain([ timeDomainStart, timeDomainEnd ])
            .range([ 0, width ]).clamp(true);

        y = d3.scale.ordinal().domain(taskNames.concat(""))
            .rangePoints([ 0, height]);
        xAxis = d3.svg.axis().scale(x).orient("top")
            .ticks(granularity, config.time_interval)
            .tickFormat(d3.time.format(tickFormat))
            .tickSubdivide(true)
            .tickSize(-height)
            .tickPadding(8);

        yAxis = d3.svg.axis().scale(y).orient("left").tickSize(-width);

    };

    function initTimeDomain() {
        if (data === undefined || data.length < 1) {
            timeDomainStart = granularity_offset.offset(new Date(),-config.time_offset);
            timeDomainEnd = granularity_offset.offset(new Date(),+config.time_offset);
            return;
        }
        data.sort(function(a, b) {
            return a.endDate - b.endDate;
        });
        timeDomainEnd = granularity_offset.offset(data[data.length - 1].endDate ,+20);
        data.sort(function(a, b) {
            return a.startDate - b.startDate;
        });
        timeDomainStart = granularity_offset.offset(data[0].startDate ,-config.time_offset);
    };


    return new updateMain();

}