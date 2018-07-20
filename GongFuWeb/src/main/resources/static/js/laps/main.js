/**
 * Created by Edward on 2017/12/15.
 */

$(function () {
    // main entry
    //console.log("test!");

    // 弹出框点击事件
    $('#pubModal').on('show.bs.modal', function (e) {
        var $button = $(e.relatedTarget);
        //var a = $button.attr('id').split(/_(?=[\u4e00-\u9fa5])/);
        //var a = $button.attr('id').split(/_(?![A-Za-z])/);
        //console.log(a);
        var arr = $button.attr('id').split(/_(?![A-Za-z])/); // [0]为type, [1]为module
        //console.log(arr);

        $('#subTypeHidden').val(arr[0]);
        $('#moduleHidden').val(arr[1]);
        arr[2] = arr[2].replace(new RegExp("-", "gm"), ".");
        $('#ipHidden').val(arr[2]);
        //var pageSize = $('pageSizeHidden').val();
        var pageSize = 10;  // 默认分页数10
        $('#pageSizeHidden').val(pageSize);
        $("#pageSizeNumber").html('展示数量：' + pageSize + ' <span class="caret"></span>');
        getLapsHistory(arr[0], arr[1], pageSize, arr[2]);

        console.log(arr[0],arr[1],arr[2])
    });

    // 历史分页数改变事件
    $('#ddl_pageSize li>a').on('click', function (e) {
        var $item = $(e.target);
        var pageSize = parseInt($item.html());
        //console.log(pageSize);
        $("#pageSizeNumber").html('展示数量：' + pageSize + ' <span class="caret"></span>');
        var subType = $('#subTypeHidden').val();
        var module = $('#moduleHidden').val();
        var ip = $('#ipHidden').val();
        getLapsHistory(subType, module, pageSize, ip);

    });

    var func = function () {
        getLapsDataAggQuery();
    };

    // 设置定时刷新
    var delay = 60000;  // 30s刷新一次
    var timerId = setInterval(func, delay);

    func();
});

/**
 * 聚合获取LAPS数据状态信息
 *
 */
function getLapsDataAggQuery() {

    $.ajax({
        type: "POST",
        url: "../laps/lctAggQuery",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: false,
        beforeSend: function () {
        },
        complete: function () {
        },
        success: function (d) {
            console.log(d)
            if (d.result == 'success') {
                var recv = d.resultData;
                $.each(recv, function (key, dmap) {
                    key = key.replace(/\./g, "-");
                    console.log(key)
                    console.log(dmap)
                    console.log("------")

                    // 如果第1条是未处理判断第2条如果是正常外报警
                    if (dmap.aging_status != '正常') {
                        // 界面报警
                        //list-red
                        $("#" + key).attr("class", "list-red");
                        $("#" + key).attr("title", dmap.fields.data_time);
                        return;
                    }

                    $("#" + key).attr("class", "list-green");
                    $("#" + key).attr("title", dmap.fields.data_time);

                })

            } else {
                // 查询失败
                //alert("失败！" + d.message);
                console.log("%c失败！" + d.message, "color:#c7254e");
                $("div[data-target='#pubModal']").each(function () {
                    $(this).attr("class", "list-red");
                })
            }

        },
        error: function (err) {
//            alert(err);
        }
    });

}

/**
 * 获取LAPS历史数据信息
 * @param type
 * @param module
 * @param size
 * @param ip
 */
function getLapsHistory(type, module, size, ip) {
    var r = Math.ceil(Math.random() * 100);

    var req = {
        "types": ["MQPF"],
        "subType": type,
        "module": module,
        "size": size,
        "strIp": ip,
        "rand": r
    };
    console.log(r + " request:  " + req.subType);

    $.ajax({
        type: "POST",
        url: "../laps/getHistory",
        data: JSON.stringify(req),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function () {
            $("#history_thead").html("");
            $("#history_tbody").html("");
            $("#modalHeader").html(req.module + "环节" + req.subType + "历史数据");
        },
        complete: function () {
        },
        success: function (d) {
            //console.log(d);
            if (d.result == 'success') {
                if (d.resultData.length > 0) {
                    // 有数据
                    var recv = d.resultData;
                    var subType = recv[0].type; // T639
                    var module = recv[0].fields.module; // 采集
                    //var data = {}, data_1 = {};


                    var regex = /LapsTD|LapsRain1Hour|LapsWSWD|LapsTRH/;

                    // 表头
                    var historyHead = "<tr>";
                    historyHead += "<th style='width: 60px;'>编号</th>";
                    historyHead += !regex.test(subType) ? "<th>文件名</th>" : "";
                    historyHead += "<th style='width: 245px;'>资料时次</th>";
                    historyHead += "<th style='width: 245px;'>更新时间</th>";
                    historyHead += "<th style='width: 75px;'>耗时</th>";
                    historyHead += "<th style='width: 60px;'>状态</th>";
                    historyHead += "<th>信息</th>";
                    historyHead += "</tr>";
                    $("#history_thead").html(historyHead);


                    // 表内容
                    var trs = "", tds = "", trStatus = "";
                    $.each(recv, function (i, v) {
                        //console.log(v);
                        // 编号
                        tds = "<td>" + (i + 1) + "</td>";
                        //文件名
                        tds += !regex.test(subType) ? "<td>" + v.fields.file_name + "</td>" : "";
                        // 资料时间
                        tds += "<td>" + v.fields.data_time + "</td>";
                        // 更新时间
                        if (v.fields.end_time != null) {
                            tds += "<td>" + v.fields.end_time + "</td>";
                        }
                        else {
                            tds += "<td>-</td>";
                        }
                        // 耗时
                        if (v.fields.hasOwnProperty("totalTime")) {
                            tds += "<td>" + Math.round(v.fields.totalTime * 10) / 10 + "秒</td>";
                        } else if (v.fields.hasOwnProperty("start_time") && v.fields.hasOwnProperty("end_time")) {
                            var begin = v.fields.start_time;
                            begin = new Date(begin.replace(new RegExp("-", "gm"), "/")).getTime();
                            var end = v.fields.end_time;
                            end = new Date(end.replace(new RegExp("-", "gm"), "/")).getTime();
                            tds += "<td>" + Math.round((end - begin) / 100) / 10 + "秒</td>";
                        } else {
                            tds += "<td>-</td>";
                        }

                        // 状态
                        tds += "<td>" + v.aging_status + "</td>";
                        // 信息
                        //tds += "<td>"+ v.fields.event_info + "</td>";
                        if (v.aging_status == "超时") {
                            tds += "<td>日志未采集到</td>";
                        } else {
                            tds += "<td>" + (/分发/.test(module) ? v.aging_status : v.fields.event_info) + "</td>";
                        }

                        trs += "<tr class='" + (/异常|迟到|超时/.test(v.aging_status) ? "danger" : "info") + "'>" + tds + "</tr>";
                    });

                    $("#history_tbody").html(trs);

                    // 判断数据是否准备好
                    //console.log(r + " response:  " + data.type);


                } else {
                    // 没有数据
                    alert('出错啦！服务器没有返回！@@');
                    $("#history_thead").html("");
                    $("#history_tbody").html("There's nothing I can show you. @_@");
                }

            } else {
                // 查询失败
                //alert("失败！" + d.message);
                console.log("%c失败！" + d.message, "color:#c7254e");
                $("div[id^='" + req.subType + "_" + req.module + "']").attr("class", "list-red");
                $("#history_tbody").html("There's nothing I can show you. @_@");
            }

        },
        error: function (err) {
            alert(err);
        }
    });
}


/**
 * 基础资源监控
 */
$('#baseSourceModal').on('shown.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var ip = button.data('ip');
    var modal = $(this);
    modal.find('#baseSourceModalHeader').text("基础资源实时运行情况(" + ip + ")");

    var params = {
        "host": ip,
        "minute": 120
    };

    displayCpuUsed("../laps/getCpuData", "#cpuUsed", 1000 * 60 * 10, JSON.stringify(params));
    displayMemoryUsed("../laps/getMemoryData", "#memoryUsed", 1000 * 60 * 10, JSON.stringify(params));
    displayNetUsed("../laps/getNetData", "#netUsed", 1000 * 60 * 10, JSON.stringify(params));
    directorUsage("../laps/getDirectoryUsedData", "#directoryUsed", 1000 * 60 * 10, JSON.stringify(params));

});


$(document).ready(function () {
    //一分钟自动刷新一次
    setInterval(getBase, 60 * 1000);

    function getBase() {
        var url = "../laps/getBaseEventData";
        var params = {
            "listIp": [
                "10.30.16.242",
                "10.30.16.224",
                "10.30.16.220",
                "10.0.74.226"
            ],
            "minute": -240
        }
        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(params),
            dataType: "json",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            success: function (data2) {
                if ("fail" == data2["result"]) {
                    console.log("获取基础设施告警信息数据失败,失败原因："
                        + data2["message"]);
                } else {
                    var resultdata = data2["resultData"];
                    for (var i in resultdata) {
                        var ary = resultdata[i];
                        var div_ip = $('div[data-ip="' + i + '"]');
                        if (div_ip.length > 0) {
                            div_ip.each(function () {
                                $(this).find('li').removeClass();
                                for (var j in ary) {
                                    var num = ary[j];
                                    var class_name = num == 0 ? "green" : "red";
                                    $(this).find('li').eq(j).addClass(class_name);
                                }
                            })
                        }
                    }
                }
            },
            error: function (e2) {
                console.error(e2)
            }
        });
    }
})