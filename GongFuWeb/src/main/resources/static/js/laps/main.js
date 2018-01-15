/**
 * Created by Edward on 2017/12/15.
 */

$(function(){
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
        //var pageSize = $('pageSizeHidden').val();
        var pageSize = 10;  // 默认分页数10
        $('#pageSizeHidden').val(pageSize);
        $("#pageSizeNumber").html('展示数量：'+pageSize+' <span class="caret"></span>');
        getLapsHistory(arr[0], arr[1], pageSize,'');

    });

    // 历史分页数改变事件
    $('#ddl_pageSize li>a').on('click', function (e) {
        var $item = $(e.target);
        var pageSize = parseInt($item.html());
        //console.log(pageSize);
        $("#pageSizeNumber").html('展示数量：'+pageSize+' <span class="caret"></span>');
        var subType = $('#subTypeHidden').val();
        var module = $('#moduleHidden').val();
        getLapsHistory(subType, module, pageSize, '');

    });


    var func = function () {
        console.log('get data...');
        $.each(dataTypes, function (i, v) {
            //console.log(i);
            $.each(v, function (i2, v2) {
                //console.log(v2);
                setTimeout(function () {
                    getLapsData(v2, i, '');
                }, i2*160);
            });
        });
    };

    // 设置定时刷新
    var delay = 20000;  // 10s刷新一次
    var timerId = setInterval(func, delay);

    func();

        // TODO: IP暂时无过滤
    // TODO: 合并查询，减少等待时间
    /*
    // 加工状态
    getLapsData('LapsTD', '加工', '10.30.16.224');
    sleep(100);

    getLapsData('LapsRain1Hour', '加工', '10.30.16.224');
    sleep(200);

    getLapsData('LapsWSWD', '加工', '10.30.16.224');
    sleep(100);

    getLapsData('LapsTRH', '加工', '10.30.16.224');
    sleep(100);


    // 分发状态
    // 中转和分发一起取
    getLapsData('LAPS3KMGEO_PRCPV', '分发', '');
    sleep(200);

    getLapsData('LAPS3KMGEO_EU4', '分发', '');
    sleep(100);

    getLapsData('LAPS3KMGEO_TD', '分发', '');
    sleep(200);

    getLapsData('LAPS3KMGEO_T', '分发', '');
    sleep(100);

    getLapsData('LAPS3KMGEO_RH', '分发', '');
    sleep(100);

    getLapsData('LAPS3KM_ME', '分发', '');
    sleep(100);

    // 采集状态
    getLapsData('CIMISS', '采集', '');
    sleep(200);

    getLapsData('T639', '采集', '');
    sleep(100);

    getLapsData('LSX', '采集', '');
    sleep(200);

    getLapsData('L1S', '采集', '');
    sleep(100);

    getLapsData('GR2', '采集', '');
    //sleep(100);
*/

    //$("#Laps_分发").css("margin", "20px");

});


/* global varibles */

var dataRecv = {
    'proc': {
        'LapsTD': { },
        'LapsRain1Hour': { },
        'LapsWSWD': { },
        'LapsTRH': { }
    },
    'dist': {
        // TODO:
    }
};  // identify whether the data is ready.

var dataTypes = {
    '采集':[ 'CIMISS', 'T639', 'LSX', 'L1S', 'GR2' ],
    '加工':[ 'LapsTD', 'LapsRain1Hour', 'LapsWSWD', 'LapsTRH' ],
    '分发':[ 'LAPS3KMGEO_PRCPV', 'LAPS3KMGEO_EU4', 'LAPS3KMGEO_TD', 'LAPS3KMGEO_T', 'LAPS3KMGEO_RH', 'LAPS3KM_ME']
};  // define data types



/**
 * 获取LAPS数据状态信息
 * @param type
 * @param module
 * @param ip
 */
function getLapsData(type, module, ip) {
    var r = Math.ceil(Math.random()*100);

    var req = {
        "types":["LAPS"],
        "subType":type,
        "module":module,
        "strIp":ip,
        "rand":r
    };
    console.log(r + " request:  " + req.subType);


    $.ajax({
        type: "POST",
        url: "../laps/getData",
        data: JSON.stringify(req),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function () { },
        complete: function () { },
        success: function (d) {
            //console.log(d);
            if (d.result == 'success') {
                if (d.resultData.length > 1) {
                    // 有数据
                    var recv = d.resultData;
                    var subType = recv[0].type; // T639
                    var module = recv[0].fields.module; // 采集
                    var data = {}, data_1 = {};

                    if (/采集|加工/.test(module)) {
                        // 状态：未处理，迟到，正常，异常...
                        // TODO: 超时和迟到的区别？
                        if (recv[0].aging_status == '未处理') {
                            data = recv[1];
                        } else {
                            data = recv[0];
                        }

                        // 如果第1条是未处理判断第2条如果是正常外报警
                        if (data.aging_status != '正常') {
                            // 界面报警
                            $("#" + subType + "_" + module).attr("class", "list-red");
                            return;
                        }

                        $("#" + subType + "_" + module).attr("class", "list-green");
                        $("#" + subType + "_" + module).attr("title", data.fields.data_time);

                    } else if (/分发/.test(module)) {
                        // 分发有内外网
                        if (recv[0].aging_status == '未处理' && recv[1].aging_status == '未处理') {
                            data = recv[2];
                            data_1 = recv[3];
                        } else {
                            data = recv[0];
                            data_1 = recv[1];
                        }

                        // data[0]为外网分发data[1]为内网分发
                        // 分发id编码type_module_ip末位
                        var suff_1 = data.fields.ip_addr.split('.')[3];
                        var suff_2 = data_1.fields.ip_addr.split('.')[3];
                        //console.log(suff_1 + "-" + suff_2);
                        var selecter_1 = "#" + subType + "_" + module + "_" + suff_1;
                        var selecter_2 = "#" + subType + "_" + module + "_" + suff_2;

                        if (data.aging_status != '正常' || data_1.aging_status != '正常') {
                            // 界面报警
                            $(selecter_1).attr("class", "list-red");
                            $(selecter_2).attr("class", "list-red");
                            return;
                        }

                        $(selecter_1).attr("class", "list-green");
                        $(selecter_2).attr("class", "list-green");

                        var time_1 = data.fields.data_time;
                        var time_2 = data_1.fields.data_time;
                        $(selecter_1).attr("title", time_1);
                        $(selecter_2).attr("title", time_2);

                    } else {
                        // 有未知类型
                        alert('出错啦！未知情况！@@'+module);
                    }

                    //dataRecv.proc[data[0].type] = data;


                    // 判断数据是否准备好
                    console.log(r + " response:  " + data.type);


                } else {
                    // 没有数据，状态无效
                    //alert('出错啦！服务器没有返回！@@');
                    var subType = d.resultData[0].type; // T639
                    var module = d.resultData[0].fields.module; // 采集
                    $("div[id^='" + subType + "_" + module + "']").attr("class", "list-red");
                }

            } else {
                // 查询失败
                //alert("失败！" + d.message);
                console.log("%c失败！" + d.message, "color:#c7254e");
                $("div[id^='" + req.subType + "_" + req.module + "']").attr("class", "list-red");
            }

        },
        error: function (err) {
            alert(err);
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
    var r = Math.ceil(Math.random()*100);

    var req = {
        "types":["LAPS"],
        "subType":type,
        "module":module,
        "size":size,
        "strIp":ip,
        "rand":r
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
        complete: function () { },
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
                    historyHead += "<th>错误信息</th>";
                    historyHead += "</tr>";
                    $("#history_thead").html(historyHead);


                    // 表内容
                    var trs = "", tds = "", trStatus = "";
                    $.each(recv, function (i, v) {
                        //console.log(v);
                        // 编号
                        tds = "<td>"+(i+1)+"</td>";
                        //文件名
                        tds += !regex.test(subType) ? "<td>"+v.fields.file_name+"</td>" : "";
                        // 资料时间
                        tds += "<td>"+v.fields.data_time+"</td>";
                        // 更新时间
                        tds += "<td>"+v.fields.end_time+"</td>";
                        // 耗时
                        //tds += "<td>"+(Math.round((111)*100)/100)+" 秒</td>";
                        tds += "<td>"+Math.round((v.receive_time-v.occur_time)/1000)+" 秒</td>";
                        // 状态
                        tds += "<td>"+ v.aging_status + "</td>";
                        // 错误信息
                        tds += "<td>"+ v.aging_status + "</td>";


                        if (/异常|迟到/.test(v.fields.aging_status)) {
                            trStatus = "danger";
                        } else {
                            trStatus = "info";
                        }

                        trs += "<tr class='" + trStatus + "'>" + tds + "</tr>";
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



