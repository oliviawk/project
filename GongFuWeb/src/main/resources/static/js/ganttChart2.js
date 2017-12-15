function ganttChart_config(){
    return {
        width:1900,
        height:900,
        margin:{top:40,right:20,bottom:5,left:150},
        textY_y:20,                 //Y轴文字 y坐标
        textY_x:-10,
        textX_y:-5,                 //X轴文字 y坐标
        stroke_dasharray:"3,5",     //虚线线段长度
        isExtend: false,             //是否启用反向延长线
        extend_num: -60,            //反向延长线长度
        taskNames:[""],
        taskStatus:{"OK" : "#41DB00",  "Error":"#DB4907" , "Warn":"#f8f7ff"},    //状态样式码
        show_taking:true,           //是否显示耗时
        x_orient:"top",             //x 轴位置
        y_orient:"left",            //y 轴位置
        time_granularity:"m",        //时间粒度  y:年 、 M:月 、 d:日、 h:时、 m:分、 s:秒
        time_interval:20,
        time_offset:10,
        isGradient:false,            //是否开启阈值
        jsonToDate_Format:"%Y-%m-%d %H:%M:%S.%L%Z",
        tickFormat_X:"%H:%M",
        tickFormat_circle:"%m-%d %H:%M",
        circle_r:8,
        circle_cy:18
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
            case "h":
                granularity = d3.time.hour;
                granularity_offset = d3.time.minute;
                break;
            case "m":
                granularity = d3.time.minute;
                granularity_offset = d3.time.second;
                break;
            default:
                granularity = d3.time.hour;
                granularity_offset = d3.time.minute;
        }
        return null;
    }();
    var timeDranularityWidth = 0;     //时间粒度对应的 width 属性

    var timeDomainStart = granularity_offset.offset(new Date(),-config.time_offset);
    var timeDomainEnd = granularity_offset.offset(new Date(),+config.time_offset);


    var tickFormat = config.tickFormat_X;
    var format = d3.time.format(config.jsonToDate_Format).parse;
    var format_text = d3.time.format(tickFormat);
    var format_text2 = d3.time.format(config.tickFormat_circle);
    var width = config.width - config.margin.left - config.margin.right;
    var height = config.height  - config.margin.top - config.margin.bottom;
    var margin = config.margin;

    var x = d3.time.scale();
    var y = d3.scale.ordinal();
    var xAxis = d3.svg.axis();
    var yAxis = d3.svg.axis();

    d3.select(elementId+" svg").remove();      //删除标签内的内容
    d3.select(elementId+" div").remove();
    //创建新画布
    var svg = d3.select(elementId).append("svg")
        .attr("width", config.width)
        .attr("height", config.height);

    var tooltip = d3.select(elementId)
        .append("div")
        .attr("id","gantTooltip")
        .attr("class","tooltip_f")
        .style("opacity",0.0);

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
        var rects = rect_g.selectAll("circle")
            .data(function (d) {
                var aa = [];
                aa.push(d)
                return aa;
            });
        rects.enter().append("circle")
            .attr("cx", 0)				//点的圆心x坐标
            .attr("cy", config.circle_cy)					//点的圆心y坐标
            .attr("r", config.circle_r)     //点的圆半径
            .attr("fill",function (d) {
                if(d.status == "0" ||  d.status == "OK" || d.status == "Ok"){
                   return taskStatus["OK"];
                }else if(d.status == "99"){
                    return taskStatus["Warn"];
                }
                return taskStatus["Error"];
            })
            .on("mouseover", function (d) {                     //鼠标悬浮事件
                var lTime = 0;
                if(d.total_time == "-1"){
                    lTime = (d.endDate - d.startDate)/1000;
                }else {
                    lTime = (Math.round(d.total_time*100)/100);
                }
                var txt  =  lTime < 60 ? (lTime + " s") : (Math.ceil((lTime/60)) + " m");
                var tooltipHtml = "";

                tooltipHtml += "<p>耗&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;时："+txt+"</p>" ;

                if(d.hasOwnProperty("data_time")){
                    tooltipHtml += "<p>时&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;次："+d.data_time+"</p>" ;
                }

                if(d.hasOwnProperty("file_name") && d.file_name != "-1"){
                    tooltipHtml += "<p>文件名称："+d.file_name+"</p>";
                }

                if(!(d.status == "0" ||  d.status == "OK" || d.status == "Ok")){
                    tooltipHtml += "<p>错误消息："+d.errorMessage+"</p>";
                }

                d3.select(this).transition().duration(100)
                    .attr("r",config.circle_r+2);

                tooltip.html(tooltipHtml);
                var tooltipWidth = $("#gantTooltip").width();
                var mouseX = d3.event.pageX;
                if(mouseX > config.width/2){
                    mouseX -= tooltipWidth;
                }
                tooltip.style("left", (mouseX) + "px")
                    .style("top", (d3.event.pageY - 50) + "px")
                    .style("z-index","10")
                    .style("opacity",1.0);
            })
            .on("mousemove",function(d){
                /* 鼠标移动时，更改样式 left 和 top 来改变提示框的位置 */
                var tooltipWidth = $("#gantTooltip").width();
                var mouseX = d3.event.pageX;
                if(mouseX > config.width/2){
                    mouseX -= tooltipWidth;
                }
                tooltip.style("left", (mouseX) + "px")
                    .style("top", (d3.event.pageY - 50) + "px");
            })
            .on("mouseout",  function (d) {
                d3.select(this).transition().duration(100)
                    .attr("r",config.circle_r);
                tooltip.style("opacity",0.0)
                    .style("z-index","-1");
            });

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
        // .text(function (d) {
        //     console.log(d)
        //      return format_text(d);
        // });
    }
    /*---------- Y 轴样式修改 ----------------*/
    function customYAxis(g) {
        g.call(yAxis);
        g.selectAll(".tick text").attr("y",config.textY_y).attr("x",config.textY_x)
            .style("stroke",function (d) {
                if( d == "雷达" || d == "云图" || d == "风流场" || d == "炎热程度"){
                    return "#f4e925";
                }
                return "#fff";
            })
            .style("stroke-width" ,".5px");

        g.selectAll(".tick line").attr("opacity","0.7").attr("x1",function (d) {
            if(config.isExtend){
                return config.extend_num;
            }
            return 0;
        });
    }

    /*=-------------数据转换---------*/
    function data_transform(dataOld) {
        var result = [];
        console.log(dataOld)
        dataOld.forEach(function (d) {
            //耗时
            var totalTime = "-1";
            if(d.fields.hasOwnProperty("total_time")){
                totalTime = d.fields.total_time;
            }
            if(!d.fields.hasOwnProperty("start_time")){
                if(d.type == "T639" || d.type == "风流场"){
                    result.push({
                        "startDate":format(d.fields.data_time),
                        "endDate":format(d.fields.data_time),
                        "taskName":d.type,
                        "status":"99",
                        "errorMessage":"未到达",
                        "data_time":d.fields.data_time,
                        "total_time":totalTime,
                        "file_name":d.fields.file_name
                    });
                }else{
                    if(!d.hasOwnProperty("should_time") ){
                        return true;
                    }
                    result.push({
                        "startDate":format(d.should_time),
                        "endDate":format(d.last_time),
                        "taskName":d.type,
                        "status":"99",
                        "errorMessage":"未到达",
                        "data_time":d.fields.data_time,
                        "total_time":totalTime,
                        "file_name":d.fields.file_name
                    });
                }

            }else{
                if(d.type == "T639" || d.type == "风流场"){
                    result.push({
                        "startDate":format(d.fields.data_time),
                        "endDate":format(d.fields.data_time),
                        "taskName":d.type,
                        "status":d.fields.event_status,
                        "errorMessage":d.fields.event_info,
                        "data_time":d.fields.data_time,
                        "total_time":totalTime,
                        "file_name":d.fields.file_name
                    });
                }else{
                    result.push({
                        "startDate":format(d.fields.start_time),
                        "endDate":format(d.fields.end_time),
                        "taskName":d.type,
                        "status":d.fields.event_status,
                        "errorMessage":d.fields.event_info,
                        "data_time":d.fields.data_time,
                        "total_time":totalTime,
                        "file_name":d.fields.file_name
                    });
                }

            }

            // d.startDate = format(d.startDate);
            // d.endDate = format(d.endDate);

            if($.inArray(d.type, taskNames) == -1){
                taskNames.push(d.type);
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
        timeDomainEnd = granularity_offset.offset(data[data.length - 1].endDate ,+config.time_offset);
        data.sort(function(a, b) {
            return a.startDate - b.startDate;
        });
        timeDomainStart = granularity_offset.offset(data[0].startDate ,-config.time_offset);
    };


    return new updateMain();

}