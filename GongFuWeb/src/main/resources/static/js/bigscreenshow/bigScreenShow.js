//声明 雷达、精细化预报、常规预报、实况  雷达基站数据
var radar_dataSource,sevpscon_dataSource, sevpsnwfd_dataSource, shikuang_dataSource = [];
$(function () {
    console.info("加载雷达数据")
    $.ajax({
        type: "GET",
        url: "../js/bigscreenshow/radarData.json",
        dataType: "json",
        async: false,
        success: function (d) {
            radar_dataSource = d.slice(0);
        },
        error: function (err) {
            radar_dataSource = [];
        }
    });
    console.info("加载精细化预报/常规预报")
    $.ajax({
        type: "GET",
        url: "../js/bigscreenshow/预报雷达基站数据.json",
        dataType: "json",
        async: false,
        success: function (d) {
            sevpscon_dataSource = d.slice(0);
        },
        error: function (err) {
            sevpscon_dataSource = [];
        }
    });
    $.ajax({
        type: "GET",
        url: "../js/bigscreenshow/预报雷达基站数据.json",
        dataType: "json",
        async: false,
        success: function (d) {
            sevpsnwfd_dataSource = d.slice(0);
        },
        error: function (err) {
            sevpsnwfd_dataSource = [];
        }
    });
    console.info("加载实况")
    $.ajax({
        type: "GET",
        url: "../js/bigscreenshow/实况雷达基站数据.json",
        dataType: "json",
        async: false,
        success: function (d) {
            shikuang_dataSource = d.slice(0);
        },
        error: function (err) {
            shikuang_dataSource = [];
        }
    });

});


function data_jishuju() {
    var data = [];
    // data.push("办公业务,8,0");
    // data.push("预警业务,44,0");
    // data.push("数据资源池,20,0");
    // data.push("核心加工系统,66,0");

    $.ajax({
        type: "GET",
        url: "/show/getoutherdata?url=http://10.30.17.171:8786/basesource/bigscreen",
        dataType: "json",
        async: false,
        success: function (d) {
            console.info(d)
            for (var key in d) {
                data.push(key+","+d[key]);
            }
        },
        error: function (err) {
            console.log("基础资源数据加载失败");
        }
    });

    var svg1 = d3.select("#svg1")
        .attr("width", 1500)
        .attr("height", 300)
        .selectAll("g")
        .data(data)
        .enter()
        .append("g")
        .attr("transform", function (d, i) {
            return "translate(" + (i * 310 + 30) + ",20)";
        });


    svg1.append("image")
        .attr("xlink:href", function (d, i) {
            return "../img/bigscreenshow/watch.png";
        })

    svg1.append("image")
        .attr("xlink:href", function (d, i) {
            return "../img/bigscreenshow/zhen.png";
        })
        .attr("x", function (d, i) {
            return 49
        })
        .attr("y", function (d, i) {
            return 92
        })
        .attr("transform", function (d, i) {
            var BG = d.split(",");
            var BG_arg = BG[1] / (parseInt(BG[1]) + parseInt(BG[2]));
            return "rotate(" + (127 + (244 * BG_arg)) + " 100,100)";
        })
    svg1.append("text")
        .text(function (d, i) {
            var BG = d.split(",");
            return BG[0];
        })
        .attr("x", function (d, i) {
            return 20
        })
        .attr("y", function (d, i) {
            return 175
        })
        .attr("class", "img-text1");

    svg1.append("text")
        .text(function (d, i) {
            var BG = d.split(",");
            return BG[1];
        })
        .attr("x", function (d, i) {
            return 130
        })
        .attr("y", function (d, i) {
            return 175
        })
        .attr("class", "img-text2");
    svg1.append("text")
        .text("/")
        .attr("x", function (d, i) {
            return 150
        })
        .attr("y", function (d, i) {
            return 175
        })
        .attr("class", "img-text1");
    svg1.append("text")
        .text(function (d, i) {
            var BG = d.split(",");
            return BG[2];
        })
        .attr("x", function (d, i) {
            return 160
        })
        .attr("y", function (d, i) {
            return 177
        })
        .attr("class", "img-text3");


}

data_jishuju();


/**
 * 折线图---数据量监控
 */
function lineCharScript(e, dataType) {

    var params_1 = {
        "unit":"M",     //数据单位
        "timeGranularity":-10,  //间隔时间  负数代表往参数时间往前
        "t": 10 ,       //查询多少条数据
        "scale":"1",    //小数点保留几位
        "dataType":dataType, //1 代表数来源  2 代表数据服务
        "dateStr": tool.getDateByUnixTime(new Date().getTime()).Format("yyyy-MM-dd HH:mm"),
        "dateFormat":"yyyy-MM-dd HH:mm",
        "callBackDateFormat":"yyyy-MM-dd HH:mm",
    };

    var lineData_1 = [];
    $.ajax({
        type: "POST",
        url: "../show/getfilesize",
        data: JSON.stringify(params_1),
        dataType: "json",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        async: false,
        success: function (d) {
            lineData_1 = d;
        },
        error: function (err) {
            lineData_1 = [];
        }
    });

    var config1 = new lineChartConfig();
    config1.width = $(e).width();
    config1.height = 280;
    config1.text_x = -13;
    config1.text_y = 18;
    config1.ticks = 10;
    if (dataType == "1" ){
        config1.desc_y = "数据来源";
        config1.lineColor = {
            "气象中心": "#178BCA",
            "气候中心": "#2df03c",
            "探测中心": "#f06256",
            "公服中心": "#f0c059",
            "信息中心": "#7188f0",
            "其他部门": "#8961f0"
        };
    }else{
        config1.desc_y = "数据服务";
        config1.lineColor = {
            "产品加工": "#178BCA",
            "智慧云": "#2df03c",
            "专业服务": "#f06256",
            "影视": "#f0c059",
            "决策服务": "#7188f0",
            "子公司": "#8961f0"
        };
    }


    var obj1 = new lineChartMain(e, lineData_1, config1);;

    var n = 1;
    setInterval(function () {
        params_1.dateStr = tool.getDateByUnixTime(new Date().getTime()).Format("yyyy-MM-dd HH:mm");
        params_1.t = 2;
        $.ajax({
            type: "POST",
            url: "../show/getfilesize",
            data: JSON.stringify(params_1),
            dataType: "json",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            async: false,
            success: function (d) {
                if (d == null || d.length < 1){
                    console.info("返回参数为空")
                    return ;
                }
                if (lineData_1 != null && lineData_1.length > 0
                    && d[0].time == lineData_1[lineData_1.length-1].time){
                    lineData_1[lineData_1.length-1] = d[0];
                }else{
                    lineData_1.shift();
                    lineData_1.push(d[0]);
                }

                console.info(lineData_1)
                obj1.update(lineData_1);
            }
        });

        n++;
    }, 60 * 1000)

    // var element = e;
    // var config1 = new lineChartConfig();
    // config1.width = $(element).width();
    // config1.height = 280;
    // config1.text_x = 13;
    // config1.text_y = 18;
    // config1.desc_y = dataType == "1" ? "数据来源":"数据服务";
    // config1.lineColor = {
    //     "气象中心": "#178BCA",
    //     "气候中心": "#2df03c",
    //     "探测中心": "#f06256",
    //     "公服中心": "#f0c059",
    //     "信息中心": "#7188f0",
    //     "其他部门": "#8961f0"
    // };
    // var obj1 = null;
    //
    // //生成一天的数据
    // var dData = [], lineNowDate = new Date();
    // var tempDate = new Date();
    // var date_0 = tempDate.setMinutes(0, 0, 0);
    // var date_1 = tempDate.setMinutes(15, 0, 0);
    // var date_2 = tempDate.setMinutes(30, 0, 0);
    // var date_3 = tempDate.setMinutes(45, 0, 0);
    //
    // var unixTime = lineNowDate.getTime();
    // if (unixTime < date_1) {
    //     unixTime = date_0;
    // } else if (unixTime < date_2) {
    //     unixTime = date_1;
    // } else if (unixTime < date_3) {
    //     unixTime = date_2;
    // } else {
    //     unixTime = date_3;
    // }
    // for (var i = 7; i >= 0; i--) {
    //     var time = unixTime - (i * 15 * 60 * 1000);
    //     var strTime = tool.getDateByUnixTime(time).Format("yyyy-MM-dd HH:mm");
    //     dData.push({
    //         "气象中心": Math.floor(Math.random() * 15 + 1),
    //         "气候中心": Math.floor(Math.random() * 15 + 1),
    //         "探测中心": Math.floor(Math.random() * 15 + 1),
    //         "公服中心": Math.floor(Math.random() * 15 + 1),
    //         "信息中心": Math.floor(Math.random() * 15 + 1),
    //         "其他部门": Math.floor(Math.random() * 15 + 1),
    //         "time": strTime
    //     });
    // }
    // obj1 = new lineChartMain(element, dData, config1);
    //
    // var n = 1;
    // setInterval(function () {
    //     var time = unixTime + (n * 15 * 60 * 1000);
    //     var strTime = tool.getDateByUnixTime(time).Format("yyyy-MM-dd HH:mm");
    //     dData.push({
    //         "气象中心": Math.floor(Math.random() * 15 + 1),
    //         "气候中心": Math.floor(Math.random() * 15 + 1),
    //         "探测中心": Math.floor(Math.random() * 15 + 1),
    //         "公服中心": Math.floor(Math.random() * 15 + 1),
    //         "信息中心": Math.floor(Math.random() * 15 + 1),
    //         "其他部门": Math.floor(Math.random() * 15 + 1),
    //         "time": strTime
    //     });
    //     dData.shift();
    //     obj1.update(dData);
    //     n++;
    // }, 30 * 1000)
}


