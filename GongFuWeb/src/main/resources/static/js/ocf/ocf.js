$(function () {

    // 弹出框点击事件
    $('#pubModal').on('show.bs.modal', function (e) {
        var $button = $(e.relatedTarget);
        //var a = $button.attr('id').split(/_(?=[\u4e00-\u9fa5])/);
        //var a = $button.attr('id').split(/_(?![A-Za-z])/);
        //console.log(a);
        var arr = $button.attr('id').split("_分发_"); // [0]为type, [1]为module
        console.log(arr);

        $('#subTypeHidden').val(arr[0]);
        $('#moduleHidden').val("分发");
        arr[1] = arr[1].replace(new RegExp("-", "gm"), ".");
        $('#ipHidden').val(arr[1]);
        //var pageSize = $('pageSizeHidden').val();
        var pageSize = 10;  // 默认分页数10
        $('#pageSizeHidden').val(pageSize);
        $("#pageSizeNumber").html('展示数量：' + pageSize + ' <span class="caret"></span>');

        getHistory(arr[0], "分发", pageSize, arr[1]);


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
        getHistory(subType, module, pageSize, ip);


    });


    var func = function () {
        getDataAggQuery();
    };

    // 设置定时刷新
    var delay = 60000;  // 30s刷新一次
    var timerId = setInterval(func, delay);

    func();

    $('#baseSourceModal').on('shown.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var ip = button.data('ip');
        var modal = $(this);
        modal.find('#baseSourceModalHeader').text("基础资源实时运行情况(" + ip + ")");

        var params = {
            "host": ip,
            "minute": 120
        };

        displayCpuUsed("../basicresource/getCpuData", "#cpuUsed", 1000 * 60 * 10, JSON.stringify(params));
        displayMemoryUsed("../basicresource/getMemoryData", "#memoryUsed", 1000 * 60 * 10, JSON.stringify(params));
        displayNetUsed("../basicresource/getNetData", "#netUsed", 1000 * 60 * 10, JSON.stringify(params));
        directorUsage("../basicresource/getDirectoryUsedData", "#directoryUsed", 1000 * 60 * 10, JSON.stringify(params));

    });

    setInterval(getBase, 60 * 1000);

    function getBase() {
        var url = "../basicresource/getBaseEventData";
        var params = {
            "listIp": [
                "10.30.16.220",
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
});





function getDataAggQuery() {

    $.ajax({
        type: "POST",
        url: "../OCF/rgfAggQuery",
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
                var H1 = 0;
                var H3 = 0;
                var H12 = 0;
                $.each(recv, function (key, dmap) {
                    key = key.replace(/\./g, "-");
                    console.log(key)
                    console.log(dmap)
                    console.log("------")

                    if(dmap.aging_status != '正常'){
                        // 界面报警
                        //list-red
                        $("#" + key).attr("class", "list-red");
                        $("#" + key).attr("title", dmap.fields.data_time);

                        //判断异常数据是否为OCF*H系列，如果是则做标记
                        if( key.indexOf("DEBACKUP") == -1 && (key.indexOf("CH_MERGE_") > -1 || key.indexOf("AGLB_MERGE_") > -1 || key.indexOf("AGLB_") > -1 || key.indexOf("CH_") > -1) ){
                            if(key.indexOf("1H") > -1){
                                H1 = H1 + 1;
                            }else if(key.indexOf("3H") > -1){
                                H3 = H3 + 1;
                            }else if(key.indexOf("12H") > -1){
                                H12 = H12 + 1;
                            }
                        }
                        return ;
                    }

                    $("#" + key).attr("class", "list-green");
                    $("#" + key).attr("title", dmap.fields.data_time);

                });
                if(H1 != 0){
                    $("#OCF1H_分发_10-30-16-220").attr("class","list-red");
                }else {
                    $("#OCF1H_分发_10-30-16-220").attr("class","list-green");
                }
                if(H3 != 0){
                    $("#OCF3H_分发_10-30-16-220").attr("class","list-red");
                }else {
                    $("#OCF3H_分发_10-30-16-220").attr("class","list-green");
                }
                if(H12 != 0){
                    $("#OCF12H_分发_10-30-16-220").attr("class","list-red");
                }else {
                    $("#OCF12H_分发_10-30-16-220").attr("class","list-green");
                }

            } else {
                // 查询失败
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






function getHistory(type, module, size, ip) {
    var r = Math.ceil(Math.random() * 100);

    var types = type;
    var arrType = new Array();
    if (type == "OCF1H"){
        arrType.push("CH_1H","AGLB_1H","AGLB_MERGE_1H","CH_MERGE_1H");
        types = arrType.toString();
    }else if(type == "OCF3H"){
        arrType.push("CH_MERGE_3H","AGLB_MERGE_3H","AGLB_3H","CH_3H");
        types = arrType.toString();
    }else if (type == "OCF12H"){
        arrType.push("CH_MERGE_12H","AGLB_MERGE_12H","AGLB_12H","CH_12H");
        types = arrType.toString();
    }
    console.info(types);
    console.info(arrType);
    var req = {
        "types": ["OCF"],
        "subType": types,
        "module": module,
        "size": size,
        "strIp": ip,
        "rand": r
    };
    console.log(r + " request:  " + req.subType);

    $.ajax({
        type: "POST",
        url: "../OCF/getHistory",
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
            console.log(d);
            if (d.result == 'success') {
                if (d.resultData.length > 0) {
                    // 有数据
                    setHistoryTable(d.resultData);
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



function setHistoryTable(data){
        // 有数据
        var recv = data;
        console.log(recv)
        var subType = recv[0].type;
        var module = recv[0].fields.module;
        //var data = {}, data_1 = {};

        // 表头
        var historyHead = "<tr>";
        historyHead += "<th style='width: 60px;'>编号</th>";
        historyHead += "<th>文件名</th>";
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
            tds += "<td>" + v.fields.file_name + "</td>";
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
                tds += "<td>业务数据未采集到</td>";
            } else {
                tds += "<td>" + (/分发/.test(module) ? v.aging_status : v.fields.event_info) + "</td>";
            }

            trs += "<tr class='" + (/异常|迟到|超时/.test(v.aging_status) ? "danger" : "info") + "'>" + tds + "</tr>";
        });

        $("#history_tbody").html(trs);

        // 判断数据是否准备好
        //console.log(r + " response:  " + data.type);
}