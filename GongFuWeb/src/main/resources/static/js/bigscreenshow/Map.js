var width = $("#mapDiv").width();
var height = $("#mapDiv").height();
;
var arr_province = [
    {"provinceName": "新疆", "cityName": "乌鲁木齐", "coordinate": "87.706202,43.777994"},
    {"provinceName": "西藏", "cityName": "拉萨", "coordinate": "91.124057,29.644356"},
    {"provinceName": "青海", "cityName": "西宁", "coordinate": "101.816641,36.594248"},
    {"provinceName": "甘肃", "cityName": "兰州", "coordinate": "103.836787,36.0678"},
    {"provinceName": "内蒙古", "cityName": "呼和浩特", "coordinate": "111.751622,40.851102"},
    {"provinceName": "黑龙江", "cityName": "哈尔滨", "coordinate": "126.629124,45.848467"},
    {"provinceName": "吉林", "cityName": "长春", "coordinate": "125.378108,43.806327"},
    {"provinceName": "辽宁", "cityName": "沈阳", "coordinate": "123.464789,41.801607"},
    {"provinceName": "河北", "cityName": "石家庄", "coordinate": "114.560498,38.064037"},
    {"provinceName": "北京", "cityName": "北京", "coordinate": "116.473817,39.901897"},
    {"provinceName": "天津", "cityName": "天津", "coordinate": "117.283298,38.988981"},
    {"provinceName": "宁夏", "cityName": "银川", "coordinate": "106.246922,38.478234"},
    {"provinceName": "陕西", "cityName": "西安", "coordinate": "109.05251,34.368893"},
    {"provinceName": "山西", "cityName": "太原", "coordinate": "112.65838,37.890112"},
    {"provinceName": "河南", "cityName": "郑州", "coordinate": "113.688629,34.734228"},
    {"provinceName": "山东", "cityName": "济南", "coordinate": "117.22091,36.655106"},
    {"provinceName": "江苏", "cityName": "南京", "coordinate": "118.83768,32.033969"},
    {"provinceName": "浙江", "cityName": "杭州", "coordinate": "120.180683,30.261844"},
    {"provinceName": "上海", "cityName": "上海", "coordinate": "121.505289,31.23117"},
    {"provinceName": "安徽", "cityName": "合肥", "coordinate": "117.329102,31.782864"},
    {"provinceName": "湖北", "cityName": "武汉", "coordinate": "114.319922,30.627309"},
    {"provinceName": "湖南", "cityName": "长沙", "coordinate": "112.98734,28.131122"},
    {"provinceName": "重庆", "cityName": "重庆", "coordinate": "106.65867,29.492224"},
    {"provinceName": "四川", "cityName": "成都", "coordinate": "104.156638,30.453309"},
    {"provinceName": "云南", "cityName": "昆明", "coordinate": "102.905621,24.753078"},
    {"provinceName": "贵州", "cityName": "贵阳", "coordinate": "106.65867,26.487738"},
    {"provinceName": "广西", "cityName": "南宁", "coordinate": "108.42481,22.720773"},
    {"provinceName": "广东", "cityName": "广州", "coordinate": "113.326468,23.111509"},
    {"provinceName": "江西", "cityName": "南昌", "coordinate": "115.957282,28.634324"},
    {"provinceName": "福建", "cityName": "福州", "coordinate": "119.415973,26.07186"},
    {"provinceName": "海南", "cityName": "海口", "coordinate": "110.290914,19.944521"},
    {"provinceName": "台湾", "cityName": "台北", "coordinate": "121.623649,24.93651"},
    {"provinceName": "香港", "cityName": "香港", "coordinate": "113.602428,21.949283"},
    {"provinceName": "澳门", "cityName": "澳门", "coordinate": "113.602428,21.949283"}
];

var svg = d3.select("#mapDiv").append("svg")
    .attr("width", width)
    .attr("height", height);

// //图片代替
// svg.append("g")
//     .attr("transform", "translate(40,10)")
//     .append("image")
//     .attr("xlink:href","../img/bigscreenshow/223.png")
//     .attr("height","840")
//     .attr("width","1230");

var translateStr = "translate(40,140)";

var svg_main = svg.append("g")
    .attr("transform", translateStr);

var nanHai_map = svg.append("g")
    .attr("transform", "translate(" + (width - 280) + "," + (height - 220) + ")")
    .append("image")
    .attr("xlink:href", "../img/bigscreenshow/南海诸岛.png")
    .attr("height", "208")
    .attr("width", "279");


var grad = svg.append("g")
    .attr("transform", "translate(80,80)")
    .attr("id", "tuli");


// var projection = d3.geoMercator()
var projection = d3.geo.mercator()
    .center([107, 32])
    .scale(1000)
    .translate([width / 2, height / 2]);

// var path = d3.geoPath()
var path = d3.geo.path()
    .projection(projection);

// var color = d3.scale.category20();
// var arr = ["西藏","甘肃","四川","海南","台湾","广东","湖南","福建","浙江","江苏","山东","湖北","河南","河北","辽宁","吉林"];
d3.json("../js/bigscreenshow/china.geojson", function (error, root) {

    if (error) {
        return console.error(error);
    }

    svg_main.selectAll("path")
        .data(root.features)
        .enter()
        .append("path")
        .attr("stroke", "#c3c3c3")
        .attr("stroke-width", 1)
        .attr("fill", function (d, i) {  //填充颜色
            return "#006DD4";
        })
        .attr("d", path)
        .attr("opacity", 0.5)
    // .on("click",function (d,i) {
    //     console.info(d.properties.name)
    //     // $(this).attr("fill","red");
    //     //  $("#exampleS").val(d.properties.name);
    // });


    var grad_g = grad.selectAll("g")
        .attr("id", "tuli")
        .data(function (d) {
            var a = [{"name": "雷达分布", "num": "1"}, {"name": "精细化预报", "num": "2"}, {"name": "常规预报", "num": "3"}
                , {"name": "实况", "num": "4"}];
            return a;
        }).enter().append("g");


    grad_g.append("circle")
        .attr("class", "no_click_circle")
        .attr("id", function (d, i) {
            return "grad_circle_" + i;
        })
        .attr("fill", "#2757D7")
        .attr("cx", function (d, i) {
            return i * 100;
        })				//点的圆心x坐标
        .attr("cy", function (d) {
            return 10;
        })					//点的圆心y坐标
        .attr("stroke", "#ffffff")
        .on("click", function (d) {                     //鼠标悬浮事件
            var circle = $(this);
            if (circle.attr("class") == "click_circle") {
                circle.attr("class", "no_click_circle");
            } else if (circle.attr("class") == "no_click_circle") {
                d3.select("#tuli").selectAll("circle").attr("class", "no_click_circle");
                circle.attr("class", "click_circle");
            }
            showLeiDa(d.num);
        });

    grad_g.append("text")
        .attr("x", function (d, i) {
            return (i * 100) + 15;
        })				//点的圆心x坐标
        .attr("y", function (d) {
            return 15;
        })
        .attr("fill", "#ffffff")
        .text(function (d) {
            return d.name;
        });

});

var timeclear = false; //启动及关闭按钮
var arre = [];
var selectNum = -1;

function showLeiDa(num) {
    arre = [];
    console.info(1)
    console.info(arre)
    svg.select("#leida").remove();
    if (selectNum != -1 && num == selectNum) {
        timeclear = true;
        selectNum = -1;
    } else {
        //获取最新数据
        var circleData = transform_Data(num);
        console.info(circleData)
        if (circleData == null || circleData.length < 1) {
            return;
        }
        //画点
        var leida_circle = svg.append("g").attr("id", "leida")
            .attr("transform", translateStr);

        var leida_circle_t = leida_circle.selectAll(".points")
            .data(circleData.slice(0))
            .enter().append("circle")
            .attr("fill", function (d) {
                if (d.state == "OK") {
                    // return "#03FF07";
                    return "#98fd00";
                } else {
                    arre.push(d);   //发现错误数据，放到集合中
                    return "#ff0200";
                }

            })
            .attr("r", "6")           //点的圆半径
            .on("mouseout", function (d) {
                d3.select(this).transition().duration(100)
                    .attr("r", 6);
                $(this).tooltip('destroy');
            });
        if (num == "1") {
            leida_circle_t
                .attr("cx", function (d) {
                    var prok = projection([d.longitude, d.latitude]);
                    if (d.name == "西沙") {
                        return prok[0] + 382;
                    }
                    return prok[0];
                })				//点的圆心x坐标
                .attr("cy", function (d) {
                    var prok = projection([d.longitude, d.latitude]);
                    if (d.name == "西沙") {
                        return prok[1] - 150;
                    }
                    return prok[1];
                })					//点的圆心y坐标
                .on("mouseover", function (d) {                     //鼠标悬浮事件

                    var titleHtml = "<h4>站名：" + d.name + ",站号:" + d.nameZ + "</h4>";
                    if (d.state != "OK") {
                        titleHtml += "<br/><h3>错误信息：" + d.state + "</h3>";
                    }

                    $(this).tooltip({
                        container: 'body',
                        placement: 'auto top',
                        title: titleHtml,
                        trigger: 'hover',
                        html: true
                    }).tooltip('show');

                    d3.select(this).transition().duration(100)
                        .attr("r", 10);

                });
        } else {
            leida_circle_t
                .attr("cx", function (d) {
                    var prok = projection([d.longitude, d.latitude]);
                    return prok[0];
                })				//点的圆心x坐标
                .attr("cy", function (d) {
                    var prok = projection([d.longitude, d.latitude]);
                    return prok[1];
                })					//点的圆心y坐标
                .on("mouseover", function (d) {                     //鼠标悬浮事件
                    d3.select(this).transition().duration(100)
                        .attr("r", 10);

                    var titleHtml = "<h3>省份：" + d.province + "</h3>";
                    titleHtml += "<h4>共有：" + d.station.length + " 国家站</h4>";
                    if (d.state != "OK") {
                        titleHtml += "<dl class='dl-horizontal'>";
                        d.station.forEach(function (value) {
                            if (value.state == "OK") {
                                return true;
                            }
                            titleHtml += "<dt>站号:" + value.number + "</dt>";
                            titleHtml += "<dd>错误信息:" + value.state + "</dd>"
                        })
                        titleHtml += "</dl>";
                    }

                    $(this).tooltip({
                        container: 'body',
                        placement: 'auto right',
                        title: titleHtml,
                        trigger: 'hover',
                        html: true
                    }).tooltip('show');

                });

        }

        //table框展示  开始
        $("#tbody_map").html("");
        var tbodyHtml = "";
        var tbodyArry = [];
        arre.forEach(function (a) {
            //这个if 放在前面，因为forEach无法退出循环
            if (tbodyArry.length > 3) {
                return false;
            }
            if (num == 1) {

                tbodyArry.push("<tr> " +
                    "<td>" + a.name + "(" + a.province + ")" + "</td>" +
                    "<td>" + a.nameZ + "</td>" +
                    "<td>" + a.state + "</td>" +
                    "</tr>");

            } else {
                var station_arr = a.station;
                station_arr.forEach(function (dd) {
                    if (dd.state != "OK") {
                        tbodyArry.push("<tr>" +
                            "<td>" + dd.name + "(" + a.province + ")" + "</td>" +
                            "<td>" + dd.number + "</td>" +
                            "<td>" + dd.state + "</td>" +
                            "</tr>");
                    }
                });

            }
        });

        if (tbodyArry.length > 3) {
            tbodyArry[3] = "";
            tbodyArry[2] = "<tr><td colspan='3'>......</td></tr>";
        }
        tbodyArry.forEach(function (value) {
            tbodyHtml += value;
        });

        $("#tbody_map").html(tbodyHtml == "" ? "<tr><td colspan='3'>无异常数据</td></tr>" : tbodyHtml);
        //table框展示  结束
        console.info(arre)
        //涟漪效果启动
        if (selectNum == -1) {
            timeclear = false;
            selectNum = num;
            timeIns();
        }
        selectNum = num;

    }
}

function timeIns() {
    // console.info("执行定时任务："+timeclear+" "+selectNum)
    if (timeclear) {
        return;
    }
    ;

    particle(arre);
    setTimeout(timeIns, 1000); //time是指本身,延时递归调用自己,100为间隔调用时间,单位毫秒
}

function transform_Data(num) {
    var resultData = [];
    var tempData = [];
    //判断是哪类数据
    if (num == "1") {
        tempData = radar_dataSource.slice(0);
        var newData = [];
        $.ajax({
            type: "GET",
            url: "/show/getoutherdata?url=http://10.14.83.52:9000/monitor/present/radarbase",
            dataType: "json",
            async: false,
            success: function (d) {
                newData = d.RadarBase.LatedStations;
            },
            error: function (err) {
                newData = [];
            }
        });

        tempData.forEach(function (dd, i) {
            newData.forEach(function (tt, j) {
                if (dd.nameZ == tt.StationID) {
                    dd.state = "延迟 " + tt.DelayTime + " 秒";
                }
            })
        });
    } else if (num == "2") {
        tempData = sevpscon_dataSource.slice(0);
        var newData = [];
        $.ajax({
            type: "GET",
            url: "/show/getoutherdata?url=http://10.14.83.52:9000/monitor/present/sevpscon",
            dataType: "json",
            async: false,
            success: function (d) {
                newData = d.SevpNmcScon.ErrorStations;
            },
            error: function (err) {
                newData = [];
            }
        });

        tempData.forEach(function (dd, i) {
            newData.forEach(function (tt, j) {
                if (dd.nameZ == tt.StationID) {
                    dd.state = tt.ErrorType + ": " + tt.Detail;
                }
            })
        });
    } else if (num == "3") {
        tempData = sevpsnwfd_dataSource.slice(0);
        var newData = [];
        $.ajax({
            type: "GET",
            url: "/show/getoutherdata?url=http://10.14.83.52:9000/monitor/present/sevpsnwfd",
            dataType: "json",
            async: false,
            success: function (d) {
                newData = d.SevpNmcSnwfd.ErrorStations;
            },
            error: function (err) {
                newData = [];
            }
        });

        tempData.forEach(function (dd, i) {
            if (dd.state != "OK"){
                console.info(222)
            }
            newData.forEach(function (tt, j) {
                if (dd.nameZ == tt.StationID) {
                    dd.state = tt.ErrorType + ": " + tt.Detail;
                }
            })
        });
    } else if (num == "4") {
        tempData = shikuang_dataSource.slice(0);
        var newData = [];
        $.ajax({
            type: "GET",
            url: "/show/getoutherdata?url=http://10.14.83.52:9000/monitor/present/surfchnhorn",
            dataType: "json",
            async: false,
            success: function (d) {
                newData = d.SurfChinaHourlyNational.AbsentStations;
            },
            error: function (err) {
                newData = [];
            }
        });

        tempData.forEach(function (dd, i) {
            newData.forEach(function (tt, j) {
                if (dd.nameZ == tt) {
                    dd.state = "缺报";
                }
            })
        });
    }
    if (num != "1") {
        arr_province.forEach(function (p) {
            var coordinates = p.coordinate.split(",");
            var stations = [];
            var state = "OK";
            tempData.forEach(function (dd) {
                if (p.provinceName == dd.province) {
                    if (dd.state != "OK") {
                        state = "Error";
                    }
                    stations.push({
                        "name": dd.name,
                        "number": dd.nameZ,
                        "state": dd.state
                    });
                }
            });
            resultData.push({
                "province": p.provinceName,
                "longitude": coordinates[0],
                "latitude": coordinates[1],
                "station": stations,
                "state": state
            });
        });
        return resultData;
    }

    //排序，将错误的放到最后
    tempData.sort(function (a, b) {
        if (a.state == "OK" && b.state != "OK") {
            return -1;
        } else if (a.state == b.state) {
            return 0;
        } else {
            return 1;
        }
    });
    return tempData;
}


/**
 * 涟漪效果
 */
function particle(arre) {

    for (var k = 0; k < arre.length; k++) {
        var prok = projection([arre[k].longitude, arre[k].latitude]);

        d3.select("#leida").insert("circle")
            .attr("class", "e_circle")
            .attr("cx", function () {
                if (arre[k].name == "西沙") {
                    return prok[0] + 382;
                }
                return prok[0];
            })
            .attr("cy", function () {
                if (arre[k].name == "西沙") {
                    return prok[1] - 150;
                }
                return prok[1];
            })
            .attr("r", 1e-6)
            // .style("stroke", d3.hsl((i = (i + 2) % 360), 1, .5)) //变换颜色
            .style("stroke", "#ff0200")     //固定颜色
            .style("stroke-opacity", 1)
            .style("stroke-width", 5)
            .transition()
            .duration(2000)
            .ease(Math.sqrt)
            .attr("r", 35)
            .style("stroke-opacity", 1e-6)
            .remove();
    }
    // d3.event.preventDefault();
}

//
// function bolt(){
//     if ($("g#leidian").length < 1){
//         var leidian = svg.append("g")
//             .attr("id","leidian")
//             .attr("transform", translateStr);
//
//         var peking = [116.3, 39.9];
//         var proPeking = projection(peking);
//         leidian.append("image")
//             .attr("xlink:href","../static/img/0.png")
//             .attr("height","70")
//             .attr("width","70")
//             .attr("x",proPeking[0]-35)
//             .attr("y",proPeking[1]-35);
//
//         var peking2 = [113.23, 31.43];
//         var proPeking2 = projection(peking2);
//         leidian.append("image")
//             .attr("xlink:href","../static/img/0.png")
//             .attr("height","70")
//             .attr("width","70")
//             .attr("x",proPeking2[0]-35)
//             .attr("y",proPeking2[1]-35);
//
//     }else{
//         svg.select("#leidian").remove();
//     }
//
// }
//
// function areaPath() {
//     if ($("g#yushuifenbu").length < 1){
//         var yushui =  svg.append("g").attr("id","yushuifenbu")
//             .attr("transform", "translate(500,400)");
//
//         var path1 ="m257.62176,91.348c0,0 -0.04164,-0.04333 -0.89866,0.97846c-4.733,5.64305 -8.12414,10.41802 -10.78386,13.69847c-2.14434,2.64479 -5.35164,4.45118 -6.29059,7.8277c-0.52083,1.87296 -0.89866,2.93539 -0.89866,3.91385c0,0 0.43698,0.806 0.89866,2.93539c0.61941,2.85686 0,9.78462 0,16.63385c0,6.84923 0,15.65539 0,22.50463c0,5.87077 -0.08681,11.75809 0.89866,17.61232c0.8261,4.90745 1.52273,9.86548 3.59462,17.61232c1.58906,5.94154 3.72815,12.66702 6.29059,20.5477c2.8452,8.75032 4.10517,14.43048 6.29059,20.5477c2.37174,6.63875 5.46883,14.64165 8.0879,21.52616c3.70392,9.73617 8.0879,19.56924 12.58117,28.3754c4.49328,8.80616 8.15908,13.63875 14.37848,22.50463c2.72739,3.88796 6.88202,9.16529 11.68252,13.69847c3.39446,3.20544 7.81065,5.73243 14.37848,7.8277c5.24577,1.67349 11.61583,2.44377 16.17579,2.93539c6.25997,0.6749 10.78386,0 14.37848,0c2.69597,0 4.56169,0.37445 5.39193,0c1.17415,-0.52954 1.4534,-2.0314 1.79731,-2.93539c0.48635,-1.27841 1.38438,-2.00927 1.79731,-3.91385c0.46168,-2.12937 2.4895,-2.96157 2.69597,-3.91385c0.46168,-2.12937 1.79731,-1.95692 1.79731,-3.91385c0,0 0.89866,-1.95692 0.89866,-1.95692c0,-0.97846 1.60846,-4.96905 2.69597,-7.8277c2.30696,-6.06409 4.70153,-10.69231 6.29059,-16.63385c1.03594,-3.87342 1.41044,-7.95783 2.69597,-11.74154c0.69718,-2.052 0.89866,-4.89231 0.89866,-7.8277c0,-5.87077 -0.07257,-10.74794 -0.89866,-15.65539c-0.98547,-5.85423 -2.17668,-10.94972 -3.59462,-14.67693c-2.00527,-5.27107 -4.74787,-8.6164 -8.98655,-15.65539c-2.87312,-4.77127 -5.07638,-8.99771 -7.18924,-12.72001c-2.36226,-4.16164 -4.27338,-7.95686 -6.29059,-11.74154c-1.62632,-3.05132 -1.79731,-6.84923 -2.69597,-9.78462c-0.89866,-2.93539 -0.89866,-3.91385 -0.89866,-6.84923c0,-2.93539 -0.57363,-7.14806 1.79731,-11.74154c2.20137,-4.26495 6.04481,-8.115 9.88521,-11.74154c3.39446,-3.20546 8.20652,-5.63135 11.68252,-7.8277c2.80244,-1.77075 5.28455,-3.18191 10.78386,-5.87077c4.4147,-2.15855 10.06391,-5.50369 17.07445,-9.78462c7.39438,-4.51531 14.58414,-10.49853 18.87176,-14.67693c3.78791,-3.69142 6.29059,-6.84923 8.0879,-8.80616c1.79731,-1.95692 1.79731,-2.93539 1.79731,-2.93539c0,-0.97846 0,-1.95692 0,-3.91385c0,-1.95692 0.43186,-4.93021 0,-7.8277c-0.4552,-3.05422 -1.99521,-5.67007 -4.49328,-8.80616c-2.944,-3.69592 -5.72624,-7.22604 -8.98655,-9.78462c-3.09299,-2.42729 -6.29059,-4.89231 -9.88521,-6.84923c-5.39193,-2.93539 -9.71057,-5.33106 -13.47983,-6.84923c-3.4751,-1.39969 -6.29059,-1.95692 -8.98655,-2.93539c-2.69597,-0.97846 -6.18145,-1.46129 -8.98655,-1.95692c-3.5482,-0.62693 -5.39193,-0.97846 -8.0879,-0.97846c-2.69597,0 -5.41886,0.47821 -8.98655,0c-3.67748,-0.49292 -11.68252,-0.97846 -17.9731,-0.97846c-6.29059,0 -11.68252,0 -16.17579,0c-4.49328,0 -8.98655,0 -12.58117,0c-3.59462,0 -7.22291,-0.59776 -11.68252,0c-3.67748,0.49292 -8.15241,1.65482 -12.58117,2.93539c-2.74659,0.79418 -5.53506,1.57596 -8.0879,2.93539c-2.91067,1.54998 -5.25959,3.10475 -6.29059,3.91385c-1.63016,1.27929 -2.69597,0.97846 -2.69597,2.93539c0,0 -0.26321,0.28658 -0.89866,0.97846c-0.63545,0.69188 0,0.97846 0,1.95692c0,0 -0.55476,1.05294 -0.89866,1.95692c-0.48635,1.27842 -0.62236,1.16046 -1.79731,2.93539c-0.7431,1.12256 -2.06052,1.26505 -2.69597,1.95692c-0.63545,0.69188 0,0.97846 0,1.95692c0,0 0,0 0,0c0,0.97846 0,0.97846 -0.89866,0.97846c0,0 0,0 0,0c0,0.97846 0,0.97846 0,0.97846c0,0 0,0 0,0.97846c0,0 0,0 0,0c0,0 -0.89866,0 -0.89866,0.97846c0,0 0,0 0,0c0,0.97846 0,0.97846 0,0.97846c0,0 0,0 0,0c0,0.97846 0,0.97846 0,0.97846c-0.89866,0 -0.89866,0 -0.89866,0l0,0.97846l0,0";
//         var path2 = "m274.99997,141.99998c0,0 0,0 0,1c0,4 0,10 0,15c0,6 0,11 0,14c0,2 -0.38269,2.07613 0,3c0.5412,1.30656 1.41885,2.41885 3,4c1.58115,1.58115 2,4 3,5c1,1 2.186,1.69255 4,3c2.29454,1.65381 4.61383,3.297 7,6c1.47984,1.67633 3,4 5,5c2,1 3,2 5,3c2,1 3.69344,1.4588 5,2c1.84776,0.76537 4,0 5,0c2,0 3,0 4,0c1,0 1.61731,-1.07613 2,-2c0.5412,-1.30656 0.69344,-2.4588 2,-3c0.92387,-0.38269 1,-1 1,-2c0,-1 1,-2 1,-3c0,-1 0,-2 0,-2c0,-1 0,-2 0,-3c0,-1 0,-1 0,-2c0,0 0,-1 0,-1c0,-1 0,-2 0,-4c0,-3 0.66251,-6.31001 -1,-9c-0.7435,-1.203 -1.4588,-3.69344 -2,-5c-0.38269,-0.92388 -1.1731,-2.85273 -2,-4c-1.30745,-1.814 -2,-4 -3,-6c-1,-2 -2.69255,-4.186 -4,-6c-1.65381,-2.29454 -2.69255,-3.186 -4,-5c-0.8269,-1.14727 -2,-3 -3,-4c-1,-1 -2.29289,-1.29289 -3,-2c-0.70711,-0.70711 -1,-1 -1,-1c0,-1 -1,-1 -1,-1c-1,0 -1,0 -1,0c-1,0 -2,0 -4,0c-1,0 -4,-1 -7,-1c-1,0 -3,0 -4,0c0,0 -1,0 -1,0c-1,1 -1,1 -2,2c0,0 -1,1 -1,2c0,0 -1,1 -1,1c0,1 -0.29289,0.29289 -1,1c-0.70711,0.70711 0,1 0,1c-1,1 -1,1 -1,1c0,1 0,1 0,2c0,0 0,0 0,1c0,0 0,1 0,1c0,1 0,2 0,2c0,1 0,1 0,2c0,0 0,1 0,1c0,0 0,1 0,1c0,0 0,0 0,1c0,0 0,0 0,0c0,1 0,1 0,1c0,0 1,1 1,1c0,0 0,0 0,1c0,0 0,0 0,0c0,0 0,1 0,1c0,0 0,0 0,0c0,1 0,1 0,1l0,-1";
//
//         //125 157 184
//         yushui.selectAll("path")
//             .data( function (d) {
//                 return [path1,path2];
//             })
//             .enter()
//             .append("path")
//             .attr("stroke","#000000")
//             .attr("stroke-width",0)
//             .attr("fill", function(d,i){  //填充颜色
//                 if (i == 0){
//                     return "#42a2ed";
//                 } else {
//                     return "#2e70e1";
//                 }
//             })
//             .attr("d", function (d) {
//                 return d;
//             })
//             .style("opacity",0.8);
//     }else{
//         svg.select("#yushuifenbu").remove();
//     }
// }
