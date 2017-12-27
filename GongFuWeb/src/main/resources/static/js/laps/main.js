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
    //sleep(300);

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
                if (d.resultData.length > 1) {  // TODO: 最新时次未处理的判断条件
                    // 有数据
                    var data = d.resultData;
                    dataRecv.proc[data[0].type] = data;

                    // 设置列表状态
                    var subType = data[0].type;
                    var module = data[0].fields.module;

                    if (module == "加工") {
                        $("#" + subType + "_" + module).attr("class", "list-green");
                        $("#" + subType + "_" + module).attr("title", data[0].fields.data_time);

                    } else if (module == "分发") {
                        // data[0]为外网分发data[1]为内网分发
                        // 分发id编码type_module_ip末位
                        var suff_1 = data[0].fields.ip_addr.split('.')[3];
                        var suff_2 = data[1].fields.ip_addr.split('.')[3];
                        //console.log(suff_1 + "-" + suff_2);
                        var selecter_1 = "#" + subType + "_" + module + "_" + suff_1;
                        var selecter_2 = "#" + subType + "_" + module + "_" + suff_2;
                        var time_1 = data[0].fields.data_time;
                        var time_2 = data[1].fields.data_time;
                        $(selecter_1).attr("class", "list-green");
                        $(selecter_2).attr("class", "list-green");
                        $(selecter_1).attr("title", time_1);
                        $(selecter_2).attr("title", time_2);

                    }


                    // 判断数据是否准备好
                    console.log(r + " response:  " + data[0].type);






                } else {
                    // 没有数据
                    $("#" + req.subType + "_" + req.module).attr("class", "list-red");

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

