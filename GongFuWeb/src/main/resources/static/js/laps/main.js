/**
 * Created by Edward on 2017/12/15.
 */

$(function(){
    // main entry
    console.log("test!");

    dataIsReady = false;

    // TODO: IP暂时无过滤
    // 加工状态
    getLapsData('LapsTD', '加工', '10.30.16.224');
    sleep(100);

    getLapsData('LapsRain1Hour', '加工', '10.30.16.224');
    sleep(100);

    getLapsData('LapsWSWD', '加工', '10.30.16.224');
    sleep(100);

    getLapsData('LapsTRH', '加工', '10.30.16.224');

    // 分发状态
    /*
    getLapsData('TD', '分发', '');
    getLapsData('PRCPV', '分发', '');
    getLapsData('EU4', '分发', '');
    getLapsData('T', '分发', '');
    getLapsData('RH', '分发', '');
*/


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
                if (d.resultData.length > 1) {  // TODO: 最新时次判断条件
                    // 有数据
                    var data = d.resultData;
                    dataRecv.proc[data[0].type] = data;

                    // 设置列表状态
                    var subType = data[0].type;
                    var module = data[0].fields.module;
                    /*
                    var moduleEng = "";
                    if (module == "加工") {
                        moduleEng = "process";
                    } else if (module == "分发") {
                        moduleEng = "distribute";
                    }
                    */
                    $("#" + subType + "_" + module).attr("class", "list-green");


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

