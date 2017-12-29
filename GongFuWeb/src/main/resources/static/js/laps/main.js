/**
 * Created by Edward on 2017/12/15.
 */

$(function(){
    // main entry
    //console.log("test!");

    // TODO: IP暂时无过滤
    // TODO: 合并查询，减少等待时间
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

    //getLapsData('T639', '采集', '');
    //sleep(100);

    getLapsData('LSX', '采集', '');
    sleep(200);

    getLapsData('L1S', '采集', '');
    sleep(100);

    getLapsData('GR2', '采集', '');
    //sleep(100);


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
                    // 没有数据
                    alert('出错啦！服务器没有返回！@@');
                }

            } else {
                // 查询失败
                alert("失败！" + d.message);
            }

        },
        error: function (err) {
            alert(err);
        }
    });

}

