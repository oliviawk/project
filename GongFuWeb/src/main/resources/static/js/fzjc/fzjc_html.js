/**
 * 初始化告警列表
 */
function initAlertDiv(){
        var alertBean = {
            "types":["alert"],
            "size":6
        }

        $.ajax({
            type: 'POST',
            url: '../fzjc/findalert',
            data: JSON.stringify(alertBean),
            dataType: "json",
            async: false,
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            success: function (json) {
                if(json.result != "success"){
                    console.log(json.message)
                    return ;
                }
                var data = json.resultData;
                $.each(data,function(){
                    var obj = this;
                    var strDiv = "<div id='"+obj._id+"' name='alertDiv'>";

                    strDiv += "<h4>"+obj.ip+":"+obj.time+"</h4>";
                    strDiv += "<h5>环节:"+obj.module+"&nbsp;&nbsp;"+obj.data_name+"</h5>";
                    strDiv += "<p>时次:"+obj.data_time+"</p>";
                    var title = obj.title.split("，");
                    if(title.length == 2){
                        strDiv += "<p>"+title[1]+"</p>";
                    }else{
                        strDiv += "<p>"+obj.title+"</p>";
                    }


                    strDiv += "</div>";

                    $("#alertDiv").append(strDiv);
                });

            },
            error:function (e) {
                console.error(e);
            }
        })
}

/**
 * 新增告警信息
 */
function alert_div_update(id,obj_json){
    var obj = jQuery.parseJSON(obj_json);
    console.log("下面是kafka推送的消息")
    console.log(obj)
    var isExist = false;

    $("div[name='alertDiv']").each(function (i,element) {
        if(obj.documentId == $(element).attr('id')){
            isExist = true;
        }
    })
    if(isExist){
        var divHtml = "";
        divHtml += "<h4>"+obj.ip+":"+obj.time+"</h4>";
        divHtml += "<h5>环节:"+obj.module+"&nbsp;&nbsp;"+obj.data_name+"</h5>";
        divHtml += "<p>时次:"+obj.data_time+"</p>";
        var title = obj.title.split("，");
        if(title.length == 2){
            divHtml += "<p>"+title[1]+"</p>";
        }else{
            divHtml += "<p>"+obj.title+"</p>";
        }

        $("#"+obj.documentId).html(divHtml);
    }else{
        var strDiv = "<div id='"+obj.documentId+"' name='alertDiv'>";

        strDiv += "<h4>"+obj.ip+":"+obj.time+"</h4>";
        strDiv += "<h5>环节:"+obj.module+"&nbsp;&nbsp;"+obj.data_name+"</h5>";
        strDiv += "<p>时次:"+obj.data_time+"</p>";
        var title = obj.title.split("，");
        if(title.length == 2){
            strDiv += "<p>"+title[1]+"</p>";
        }else{
            strDiv += "<p>"+obj.title+"</p>";
        }

        strDiv += "</div>";

        $(id).prepend(strDiv);

        $(id).children().each(function (i,element) {
            if(i > 5){
                element.remove();
            }
        })
    }

}


/**
 * 流程图各环节状态
 */
function lct_status_regular(findType) {
    var esQeuryBean_web = {
        // "indices":["log_20170920"],
        "types":["FZJC"],
        "findType":findType
    }

    $.ajax({
        type: 'POST',
        url: '../fzjc/findDataNew',
        data: JSON.stringify(esQeuryBean_web),
        dataType: "json",
        // async: false,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (json) {
            var list1  = new Array();
            if(findType == "regular"){
                list1.push("collect_radarlatlon")
                list1.push("distribute_radarlatlon")
                list1.push("collect_satellite")
                list1.push("machining_ReadFY2NC")
                list1.push("distribute_satellite")
                list1.push("machining_hot")
                list1.push("distribute_hot")
            }else if(findType == "no_regular"){
                list1.push("machining_LatLonQREFEnd")
                list1.push("collect_LAPS3KM")
                list1.push("collect_cityforcast")
                list1.push("collect_typhoon")
                list1.push("collect_warning")
                list1.push("collect_emergency")
                list1.push("collect_boats")
                list1.push("collect_trafficjam")
            }else if(findType == "T639"){
                list1.push("machining_T639")
                list1.push("distribute_T639")
            }


            if(json.result != "success" || json.resultData.length < 1){
                for (var i = 0; i < list1.length ; i++){
                    var strId = list1[i];
                    if(strId.indexOf("distribute_") > -1){
                        $("#"+strId).attr({
                            "class":"list-red-f"
                        });
                        $("#"+strId +" i").each(function (i) {
                            if( i == 1){
                                $(this).attr("class","sn-r bd");
                            }else{
                                $(this).attr("class","sn-r");
                            }
                        })
                    }else{
                        $("#"+strId).attr({
                            "class":"list-red"
                        });
                    }
                }
                return ;
            }
            var data = json.resultData;
            $.each(data,function(key,values){
                var liStatus = "list-red";
                var liI = "sn-r";

                var agingStatus_isOK = false ;
                var eventStatus_isOK = false ;
                console.log(key);
                console.log(values)
                console.log("-----")


                var strKeys = key.split("_");
                var strType = strKeys[0];
                var strModule = strKeys[1];
                if(strType == "雷达"){
                    strType = "radarlatlon";
                }else if(strType == "云图"){
                    strType = "satellite";
                }else if(strType == "炎热指数"){
                    strType = "hot";
                }else if(strType == "风流场"){
                    strType = "T639";
                }else if(strType == "城市预报"){
                    strType = "cityforcast";
                }else if(strType == "台风"){
                    strType = "typhoon";
                }else if(strType == "预警信号"){
                    strType = "warning";
                }else if(strType == "突发事件"){
                    strType = "emergency";
                }else if(strType == "船舶"){
                    strType = "boats";
                }else if(strType == "交通拥堵"){
                    strType = "trafficjam";
                }
                if(strModule == "采集"){
                    strModule = "collect";
                }else if(strModule == "加工"){
                    strModule = "machining";
                }else if(strModule == "分发"){
                    strModule = "distribute";
                }
                var strIdHtml = strModule+"_"+strType;
                if(values.hasOwnProperty("aging_status")){
                    if(values.aging_status == "未处理" || values.aging_status == "正常"){
                        agingStatus_isOK = true;
                    }else{
                        agingStatus_isOK = false;
                    }
                }else {
                    agingStatus_isOK = true;
                }
                if(values.fields.hasOwnProperty("event_status") ){
                    if(values.fields.event_status == "0" || values.fields.event_status.toUpperCase() == "OK") {
                        eventStatus_isOK = true;
                    }else{
                        eventStatus_isOK = false;
                    }
                }else{
                    eventStatus_isOK = true;
                }

                if(agingStatus_isOK && eventStatus_isOK){
                    liStatus = "list-green";
                    liI = "sn-g";
                }

                for (var i = 0; i < list1.length ; i++){
                    var strId = list1[i];
                    if(strIdHtml != strId){
                        continue;
                    }
                    console.log(strId)
                    if(strId.indexOf("distribute_") > -1){
                        $("#"+strId).attr({
                            "class":liStatus+"-f"
                        });
                        $("#"+strId +" i").each(function (i) {
                            if( i == 1){
                                $(this).attr("class", liI +" bd");
                            }else{
                                $(this).attr("class", liI );
                            }
                        })
                    }else{
                        $("#"+strId).attr({
                            "class":liStatus
                        });
                    }
                }
            });
            data = null;
        },
        error:function (e) {
            console.error(e);
        }
    });
}
/**
 * 流程图各环节状态
 */
function lct_statusNew(moduleName,ip,subType) {
    var esQeuryBean_web = {
        // "indices":["log_20170920"],
        "types":["FZJC"],
        "subType":subType,
        "module":moduleName,
        "strIp":ip
    }

    var moduleE = "";
    if(moduleName == "采集"){
        moduleE = "collect";
    }else if(moduleName == "加工"){
        moduleE = "machining";
    }else if(moduleName == "分发"){
        moduleE = "distribute";
    }

    $.ajax({
        type: 'POST',
        url: '../fzjc/findData_DI',
        data: JSON.stringify(esQeuryBean_web),
        dataType: "json",
        async: false,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (json) {
            console.log("---");
            console.log(esQeuryBean_web);
            console.log(json);
            console.log("---");
            if(subType == "FY-2E/G云图数据" || subType == "云图"){
                subType = "satellite";
            }else if(subType == "雷达数据" || subType == "雷达"){
                subType = "radarlatlon";
            }else if(subType == "风流场"){
                subType = "T639";
            }else if(subType == "炎热指数"){
                subType = "hot";
            }else if(subType == "城市预报"){
                subType = "cityforcast";
            }else if(subType == "台风"){
                subType = "typhoon";
            }else if(subType == "预警信号"){
                subType = "warning";
            }else if(subType == "突发事件"){
                subType = "emergency";
            }else if(subType == "船舶"){
                subType = "boats";
            }else if(subType == "交通拥堵"){
                subType = "trafficjam";
            }

            if(json.result != "success"){
                var upId = moduleE+"_"+subType;
                if(moduleName == "分发"){
                    $("#"+upId).attr({
                        "class":"list-red-f"
                    });
                    $("#"+upId +" i").each(function (i) {
                        if( i == 1){
                            $(this).attr("class","sn-r bd");
                        }else{
                            $(this).attr("class","sn-r");
                        }
                    })
                }else{
                    $("#"+upId).attr({
                        "class":"list-red"
                    });
                }
                return;
            }
            var data = json.resultData;

            if(data.length < 1){
                var upId = moduleE+"_"+subType;
                if(moduleName == "分发"){
                    $("#"+upId).attr({
                        "class":"list-red-f"
                    });
                    $("#"+upId +" i").each(function (i) {
                        if( i == 1){
                            $(this).attr("class","sn-r bd");
                        }else{
                            $(this).attr("class","sn-r");
                        }
                    })
                }else{
                    $("#"+upId).attr({
                        "class":"list-red"
                    });
                }
                console.error("查询到的数据为空")
                return;
            }
            $.each(data,function(i,values){
                var liStatus = "list-red";
                var liI = "sn-r";

                var agingStatus_isOK = false ;
                var eventStatus_isOK = false ;
                $(values).each(function(){
                    if(this.hasOwnProperty("aging_status")){
                        if(this.aging_status == "未处理" || this.aging_status == "正常"){
                            agingStatus_isOK = true;
                        }else{
                            agingStatus_isOK = false;
                        }
                    }else {
                        agingStatus_isOK = true;
                    }
                    if(this.fields.hasOwnProperty("event_status") ){
                        if(this.fields.event_status == "0" || this.fields.event_status.toUpperCase() == "OK") {
                            eventStatus_isOK = true;
                        }else{
                            eventStatus_isOK = false;
                        }
                    }else{
                        eventStatus_isOK = true;
                    }

                    if(agingStatus_isOK && eventStatus_isOK){
                        liStatus = "list-green";
                        liI = "sn-g";
                    }
                    var upId = moduleE+"_"+subType;
                    if(moduleName == "分发"){
                        $("#"+upId).attr({
                            "class":liStatus+"-f",
                            "title":this.fields.data_time
                        });
                        $("#"+upId +" i").each(function (i) {
                            if( i == 1){
                                $(this).attr("class",liI + " bd");
                            }else{
                                $(this).attr("class",liI);
                            }
                        })
                    }else{
                        $("#"+upId).attr({
                            "class":liStatus,
                            "title":this.fields.data_time
                        });
                    }

                });
            });
            data = null;
        },
        error:function (e) {
            console.error(e);
        }
    });
}

/**
 * 历史数据弹出框
 */
$('#pubModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var subType = button.data('subtype');
    var module = button.data('module');
    var modal = $(this);
    modal.find('#modalHeader').text(module+' 环节历史数据');
    var size = $("#sizeHidden").val();
    $("#moduleHidden").val(module);
    $("#subTypeHidden").val(subType);

    /* Mod by Edward 2017/11/15
     * 雷达的加工过程隐藏文件名和耗时列
     * 隐藏外网采集文件名和耗时列
     */
    var regex = /LatLonQREFEnd|ReadFY2NC|LAPS3KM|城市预报|台风|预警信号|突发事件|船舶|交通拥堵/;

    //先确认头信息
    var historyHead  = "<tr>";
        historyHead += "<th style='width: 60px;'>编号</th>";
        if(!regex.test(subType)){
            historyHead += "<th >文件名</th>";
        }

        historyHead += "<th style='width: 245px;'>资料时次</th>";
        historyHead += "<th style='width: 245px;'>更新时间</th>";

        if(subType == "风流场" || subType == "T639"){
            //$("#sizeNumberButton").attr("disabled","true");
            $("#sizeNumberButton").addClass('disabled');
        }else{
            //$("#sizeNumberButton").removeAttr("disabled");
            $("#sizeNumberButton").removeClass('disabled');
        }

        if(!regex.test(subType)){
            historyHead += "<th style='width: 75px;'>耗时</th>";
        }

        historyHead += "<th style='width: 60px;'>状态</th>";
        historyHead += "<th>错误信息</th>";
        historyHead += "</tr>";

    $("#history_thead").html(historyHead);
    //拼接数据
    initHistory(subType,module,size);

})

/**
 * 修改显示条数
 * @param size
 */
function changeSize(size){
    $("#sizeHidden").val(size);
    var module = $("#moduleHidden").val();
    var subType = $("#subTypeHidden").val();
    initHistory(subType,module,size);

    $("#sizeNumber").html("展示数量："+size+" <span class='caret'></span>");
}

/**
 * 加载历史数据
 * @param subType
 * @param module
 * @param size
 */
function initHistory(subType,module,size) {
    var esQeuryBean_web = {
        // "indices":["log_20170920"],
        "types":["FZJC"],
        "subType":subType,
        "module":module,
        "size":size
    }

    $("#history_tbody").html("");
    $.ajax({
        type: 'POST',
        url: '../fzjc/findData_DI_history',
        data: JSON.stringify(esQeuryBean_web),
        dataType: "json",
        async: false,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (resultJson) {
            // console.log(resultJson);
            if(resultJson.result != "success"){
                return "null";
            }
            var json = resultJson.resultData;
            var trs = "";
            $.each(json,function(i,values){
                var trStatus = "danger";
                // var subType = values.type;
                if(values.hasOwnProperty("$ref")){      //错误数据----原因未知
                    console.error("未知错误")
                    return true;
                }
                console.log(values)
                var tds = "<td>"+(i+1)+"</td>";
                var agingStatus_isOK , eventStatus_isOK ;
                $(values).each(function(){

                    agingStatus_isOK = false ;
                    eventStatus_isOK = false ;

                    /* Mod by Edward 2017/11/15
                     * 雷达的加工过程隐藏文件名和耗时列
                     * 隐藏外网采集文件名和耗时列
                     */
                    var regex = /LAPS3KM|城市预报|台风|预警信号|突发事件|船舶|交通拥堵/;

                    if (this.fields.module == "加工" && (this.type == "LatLonQREFEnd" || this.type == "ReadFY2NC")
                        || this.fields.module == "采集" && regex.test(this.type)) { }
                    else {
                    //文件名
                    if(this.fields.hasOwnProperty("file_name") && this.fields.file_name != "-1"){
                        tds += "<td>"+this.fields.file_name+"</td>";
                    }else{
                        tds += "<td> -</td>";
                    }
                    }

                    //资料时次
                    tds += "<td>"+this.fields.data_time+"</td>";
                    /*if(subType == "风流场" || subType == "T639"){
                        tds += "<td>"+this.fields.end_time+"</td>";
                    }*/

                    //更新时间
                    if (this.fields.hasOwnProperty('end_time')) {
                        tds += "<td>"+this.fields.end_time+"</td>";
                    } else {
                        tds += "<td> -</td>";
                    }

                    //耗时
                    if (this.fields.module == "加工" && (this.type == "LatLonQREFEnd" || this.type == "ReadFY2NC")
                        || this.fields.module == "采集" && regex.test(this.type)) { }
                    else {
                    if(this.fields.hasOwnProperty("total_time")){
                        var totalTime = this.fields.total_time;

                        tds += "<td>"+ (Math.round(totalTime*100)/100) +" 秒</td>";
                    }else{
                        tds += "<td> -</td>";
                    }
                    }

                    if(this.hasOwnProperty("aging_status")){
                        if(this.aging_status == "未处理" || this.aging_status == "正常"){
                            agingStatus_isOK = true;
                        }else{
                            agingStatus_isOK = false;
                        }
                    }else{
                        agingStatus_isOK = true;
                    }
                    if(this.fields.hasOwnProperty("event_status") ){
                        if(this.fields.event_status == "0" || this.fields.event_status.toUpperCase() == "OK") {
                            eventStatus_isOK = true;
                        }else{
                            eventStatus_isOK = false;
                        }
                    }else{
                        eventStatus_isOK = true;
                    }

                    if(agingStatus_isOK && eventStatus_isOK){
                        trStatus = "info";
                        tds += "<td>正常</td>";
                        tds += "<td>-</td>";
                    }else if(!agingStatus_isOK  && eventStatus_isOK){
                        if(this.aging_status == "迟到"){
                            trStatus = "danger";
                            tds += "<td>异常</td>";
                            tds += "<td>"+this.fields.event_info+"</td>";
                        }else if(this.aging_status == "超时"){
                            trStatus = "danger";
                            tds += "<td>异常</td>";
                            //tds += "<td>数据未到达</td>";
                            tds += "<td>日志未采集到</td>";
                        }
                    }else if(agingStatus_isOK  && !eventStatus_isOK){
                        trStatus = "danger";
                        tds += "<td>异常</td>";
                        tds += "<td>"+this.fields.event_info+"</td>";
                    }else{
                        trStatus = "danger";
                        tds += "<td>异常</td>";
                        tds += "<td>"+this.fields.event_info+"</td>";
                    }
                });
                trs += "<tr class='"+trStatus+"'>"+tds+"</tr>";
            });
            $("#history_tbody").html(trs);


        },
        error:function (e) {
            console.error(e);
            $("#history_tbody").html("");
        }

    });

}


