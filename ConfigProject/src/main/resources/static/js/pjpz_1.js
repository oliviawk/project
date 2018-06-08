$(document)
    .ready(
        function() {
            // 发送模板显示
            $
                .ajax({
                    type : "POST",
                    url : "/pjpz/sen_list",
                    async : false,
                    headers : {
                        "Content-Type" : "application/json; charset=utf-8"
                    },
                    success : function(r) {
                        var s = "<tr><td width='50px;'>id</td><td width='120px;'>模板名称</td><td width='80px;'>数据类型</td><td width='200px;'>微信模板</td><td width='50px;'>是否发送微信</td><td width='200px;'>短信模板</td><td width='50px;'>是否发送短信</td><td width='100px;'>操作</td></tr>"
                        var m = "";
                        for (var i = 0; i < r.length; i++) {
                            var sendT = r[i];
                            s += "<tr><td>"
                                + sendT.id
                                + "</td><td>"
                                + sendT.name
                                + "</td><td>"
                                + sendT.type
                                + "</td><td>"
                                + sendT.wechartContentTemplate
                                + "</td><td>"
                                + sendT.wechartSendEnable
                                + "</td><td>"
                                + sendT.smsContentTemplate
                                + "</td><td>"
                                + sendT.smsSendEnable
                                + "</td><td><button class='btn btn-primary' onclick='temptodelet("
                                + sendT.id
                                + ")'>删除</button>&nbsp;<button type='button' class='btn btn-primary' data-toggle='modal' data-target='#sm_m' onclick='upd("
                                + sendT.id
                                + ")'>修改</button></td></tr>"
                            m += "<option value='" + sendT.id
                                + "'>" + sendT.name
                                + "</option>";
                        }
                        // console.log(sendT.wechartContentTemplate);
                        $("#table_send").html(s);
                        $("#selectTemp").html(m);
                        changeContent();
                    },
                    error : function(err) {
                        alert(err);
                        console.log(err.message)
                    }
                });

            // 用户表显示
            $.ajax({
                type : "POST",
                async : false,
                url : "/pjpz/user_list",
                headers : {
                    "Content-Type" : "application/json; charset=utf-8"
                },
                success : function(r) {
                    var m = "";
                    for (var i = 0; i < r.length; i++) {
                        var userT = r[i];
                        m += "<option value='" + userT.id + "'>"
                            + userT.name + "</option>";
                    }
                    $("#selectUser").html(m)
                    $("#usergroup").html(m);
                    $("#usergroup option[value='1']").remove();   //删除Select中索引值为0的Option(第一个)
					$("#user_group").html(m);
                },
                error : function(err) {
                    alert(err);
                    console.log(err.message)
                }
            });
            searchUser();

            // 决策配置表显示
            /*
            $.ajax({
                    type : "POST",
                    url : "/pjpz/pz_list",
                    async : false,
                    headers : {
                        "Content-Type" : "application/json; charset=utf-8"
                    },
                    success : function(r) {
                        var s = "<tr><td>业务名/IP</td><td>环节名</td><td>资料名</td><td>操作</td></tr>"
                        for (var i = 0; i < r.length; i++) {
                            var pzList = r[i];
                            s += "<tr><td height='20px'>"
                                + pzList.service_type
                                + "</td><td>"
                                + pzList.module
                                + "</td><td>"
                                + pzList.sub_name
                                + "</td>"
                                + "<td><button type='button' class='btn btn-info' onclick='lookAlertStrategy(+"
                                + pzList.id
                                + ")'>查看</button>&nbsp;&nbsp;" +
                                // "<button type='button'
                                // class='btn
                                // btn-info'>修改</button>&nbsp;&nbsp;"
                                // +
                                "<button type='button' class='btn btn-info' onclick='pztodelet("
                                + pzList.id + ")'>删除</button>"
                                + "</td></tr>"
                        }
                        // console.log(s);
                        $("#dataTable").html(s);
                    },
                    error : function(err) {
                        alert(err);
                        console.log(err.message)
                    }
                });
            */

            // 决策配置查询选择框
            $
                .ajax({
                    type : "POST",
                    url : "/pjpz/basic_ifo",
                    datatype : "json",
                    data : JSON.stringify({
                        "pid" : "0"
                    }),
                    headers : {
                        "Content-Type" : "application/json; charset=utf-8"
                    },
                    success : function(r) {
                        var s = "<option selected='selected' value='-1'>全部</option>";
                        $("#basic_ifo1").html(s);
                        $("#basic_ifo2").html(s);
                        $("#basic_ifo3").html(s);
                        $("#pzAddSelect1").html(s);
                        $("#pzAddSelect2").html(s);
                        $("#pzAddSelect3").html(s);
                        $("#pzAddSelect4").html(s);
                        for (var i = 0; i < r.length; i++) {
                            var dio = r[i];
                            s += "<option value='" + dio[0] + "'>"
                                + dio[1] + "</option>";
                            // console.log(dio.name)
                        }
                        // console.log(s)
                        $("#basic_ifo").html(s);
                        $("#pzAddSelect1").html(s);
                    },
                    error : function(err) {
                        alert(err);
                        console.log(err.message)
                    }
                });

            var write_id = "wechart_content";

            $("#wechart_content").click(function() {
                write_id = "wechart_content";
            })

            $("#sns_content").click(function() {
                write_id = "sns_content";
            })
            $("#dl_document").find("a").click(function() {
                var old_str = $("#" + write_id).val();
                var str = $(this).html();
                $("#" + write_id).val(old_str + str);
            })

        });

function checkClick(id) {
    var input_id = id.substring(0, id.length - 2);
    if ($("#" + id).is(':checked')) {
        $("#" + input_id).val(1);
    } else {
        $("#" + input_id).val(0);
    }
}

function usertodelet(id) {
    $
        .ajax({
            type : "POST",
            url : "/pjpz/usertodelet",
            datatype : "json",
            data : JSON.stringify({
                "id" : id
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                var s = "<tr><td>组名</td><td>用户名</td><td>微信号</td><td>手机号</td><td>操作</td></tr>";
                for (var i = 0; i < r.length; i++) {
                    var userT = r[i];
                    s += "<tr><td>"
                        + userT[8]
                        + "</td><td>"
                        + userT[1]
                        + "</td><td>"
                        + userT[4]
                        + "</td><td>"
                        + userT[5]
                        + "</td><td><button type='button' class='btn btn-info' onclick='updUser("
                        + userT[0]
                        + ")'>修改</button>&nbsp;&nbsp;<button class='btn btn-primary' onclick='usertodelet("
                        + userT[0] + ")'>删除</button></td></tr>"
                }
                $("#userTable").html(s);
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
}
function temptodelet(id) {
    $
        .ajax({
            type : "POST",
            url : "/pjpz/temptodelet",
            datatype : "json",
            data : JSON.stringify({
                "id" : id
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                var s = "<tr><td width='50px;'>id</td><td width='120px;'>模板名称</td><td width='80px;'>数据类型</td><td width='200px;'>微信模板</td><td width='50px;'>是否发送微信</td><td width='200px;'>短信模板</td><td width='50px;'>是否发送短信</td><td width='100px;'>操作</td></tr>"
                for (var i = 0; i < r.length; i++) {
                    var sendT = r[i];
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + sendT.wechartSendEnable
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + sendT.smsSendEnable
                        + "</td><td><button class='btn btn-primary' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button>&nbsp;<button type='button' class='btn btn-primary' data-toggle='modal' data-target='#sm_m' onclick='upd("
                        + sendT.id + ")'>修改</button></td></tr>"
                }
                $("#table_send").html(s);
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
}
function pztodelet(id) {
    $.ajax({
        type : "POST",
        url : "/pjpz/pztodelet",
        datatype : "json",
        async : false,
        data : JSON.stringify({
            "id" : id
        }),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {
            console.log(r)
        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });
    alertStrategy_Search();

}

function upd(id) {
    // alert(id);
    $.ajax({
        type : "POST",
        url : "/pjpz/sen_d",
        datatype : "json",
        data : JSON.stringify({
            "id" : id
        }),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {
            $("#type_upd").find("option[value='" + r.type + "']").attr(
                "selected", "true");
            $("#weixin_upd")
                .find("option[value='" + r.wechartSendEnable + "']").attr(
                "selected", "true");
            $("#sms_upd").find("option[value='" + r.smsSendEnable + "']").attr(
                "selected", "true");
            $("#name_upd").val(r.name);
            $("#id_v").val(r.id);
            $("#wechart_content_upd").val(r.wechartContentTemplate);
            $("#sns_content_upd").val(r.smsContentTemplate);
        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });
}

function searchTemp() {
    var tempName = $("#tempName").val();
    var tempType = $("#tempType").val();
    var tempWechart = $("#tempWechart").val();
    var tempSms = $("#tempSms").val();
    $
        .ajax({
            type : "POST",
            url : "/pjpz/search",
            datatype : "json",
            data : JSON.stringify({
                "tempName" : tempName,
                "tempType" : tempType,
                "tempWechart" : tempWechart,
                "tempSms" : tempSms
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(list) {
                console.log(list)
                var s = "<tr><td width='50px;'>id</td><td width='120px;'>模板名称</td><td width='80px;'>数据类型</td><td width='200px;'>微信模板</td><td width='50px;'>是否发送微信</td><td width='200px;'>短信模板</td><td width='50px;'>是否发送短信</td><td width='100px;'>操作</td></tr>"
                for (var i = 0; i < list.length; i++) {
                    var sendT = list[i];
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + sendT.wechartSendEnable
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + sendT.smsSendEnable
                        + "</td><td><button class='btn btn-primary' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button>&nbsp;<button type='button' class='btn btn-primary' data-toggle='modal' data-target='#sm_m' onclick='upd("
                        + sendT.id + ")'>修改</button></td></tr>"
                }
                $("#table_send").html(s);

            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
}
function searchUser() {
    var user_name = $("#user_name").val();
    var user_group = $("#user_group").val();
    var user_phone = $("#user_phone").val();
    var user_wechart = $("#user_wechart").val();
    $("#userTable").html("");
    $
        .ajax({
            type : "POST",
            url : "/pjpz/users_pname",
            datatype : "json",
            async : false,
            data : JSON.stringify({
                "user_name" : user_name,
                "user_group" : user_group,
                "user_phone" : user_phone,
                "user_wechart" : user_wechart
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                var s = "<tr><td>组名</td><td>用户名</td><td>微信号</td><td>手机号</td><td>操作</td></tr>";
                for (var i = 0; i < r.length; i++) {
                    var userT = r[i];
                    s += "<tr><td>"
                        + userT[8]
                        + "</td><td>"
                        + userT[1]
                        + "</td><td>"
                        + userT[4]
                        + "</td><td>"
                        + userT[5]
                        + "</td><td><button type='button' class='btn btn-info' onclick='updUser("
                        + userT[0]
                        + ")'>修改</button>&nbsp;&nbsp;<button class='btn btn-primary' onclick='usertodelet("
                        + userT[0] + ")'>删除</button></td></tr>"
                }

                $("#userTable").html(s);
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });

}

function addTemple() {
    $('#tempAdd').modal('hide');
    var name = $("#name").val();
    var type = $("#type").val();
    var wechart_content = $("#wechart_content").val();
    var wechart_send = $("#wechart_send").val();
    var sns_content = $("#sns_content").val();
    var sms_send = $("#sms_send").val();
    console.log({
        "name" : name,
        "type" : type,
        "wechart_content" : wechart_content,
        "wechart_send" : wechart_send,
        "sns_content" : sns_content,
        "sms_send" : sms_send
    })
    $
        .ajax({
            type : "POST",
            url : "/pjpz/toadd",
            datatype : "json",
            async : false,
            data : JSON.stringify({
                "name" : name,
                "type" : type,
                "wechart_content" : wechart_content,
                "wechart_send" : wechart_send,
                "sns_content" : sns_content,
                "sms_send" : sms_send
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(list) {
                var s = "<tr><td>id</td><td>name</td><td>type</td><td>wechart_content</td><td>wechart_send</td><td>sms_content</td><td>sms_send</td><td>操作</td></tr>"
                for (var i = 0; i < list.length; i++) {
                    var sendT = list[i];
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + sendT.wechartSendEnable
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + sendT.smsSendEnable
                        + "</td><td><button class='btn btn-primary' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button>&nbsp;<button type='button' class='btn btn-primary' data-toggle='modal' data-target='#sm_m' onclick='upd("
                        + sendT.id + ")'>修改</button></td></tr>"
                }
                $("#table_send").html(s);

            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });

//    window.location.reload();
        searchUser();
}
function addUser() {
    $('#ueserAdd').modal('hide');
    var username = $("#username").val();
    var usergroup = $("#usergroup").val();
    var userphone = $("#userphone").val();
    var useremail = $("#useremail").val();
    var usergzh = $("#usergzh").val();
    var usersm = $("#usersm").val();
    $
        .ajax({
            type : "POST",
            url : "/pjpz/usertoadd",
            datatype : "json",
            async : false,
            data : JSON.stringify({
                "username" : username,
                "usergroup" : usergroup,
                "userphone" : userphone,
                "useremail" : useremail,
                "usergzh" : usergzh,
                "usersm" : usersm
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                var s = "<tr><td>组名</td><td>用户名</td><td>微信号</td><td>手机号</td><td>操作</td></tr>";
                for (var i = 0; i < r.length; i++) {
                    var userT = r[i];
                    s += "<tr><td>"
                        + userT[8]
                        + "</td><td>"
                        + userT[1]
                        + "</td><td>"
                        + userT[4]
                        + "</td><td>"
                        + userT[5]
                        + "</td><td><button type='button' class='btn btn-info' onclick='updUser("
                        + userT[0]
                        + ")'>修改</button>&nbsp;&nbsp;<button class='btn btn-primary' onclick='usertodelet("
                        + userT[0] + ")'>删除</button></td></tr>"
                }
                $("#userTable").html(s);

            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
    window.location.reload();
}

/* 保存告警配置 */
function pzsave() {
    $('#big_m').modal('hide')

    var selectValue1 = $("#pzAddSelect1").val();
    if (selectValue1 == -1) {
        alert("请选择一种数据!!")
        return;
    } else if (selectValue1 == 1) {
        // alert("保存的是基础资源");
        obj = document.getElementsByName("baseSource");
        check_val = [];
        for (k in obj) {
            if (obj[k].checked)
                check_val.push(obj[k].value);
        }
        var ip = $("#ip").val();
        var dataInfoParams = {
            "ip" : ip,
            "value" : check_val
        }
        // 发布模板参数
        var weChart;
        var sms;
        if (document.getElementById("inlineCheckbox1").checked) {
            weChart = 1;
        } else {
            weChart = 0;
        }
        if (document.getElementById("inlineCheckbox2").checked) {
            sms = 1;
        } else {
            sms = 0;
        }
        var userId = $("#selectUser").val();
        var weChartContent = $("#tempWc").val();
        var smsContent = $("#tempSm").val();

        // console.log({"pznameid":pznameid,"alertLevel":alertLevel,"pzAddtimeyz":pzAddtimeyz,"userId":userId,"weChartContent":weChartContent,"weChart":weChart,"smsContent":smsContent,"sms":sms})
        var strategyParams = {
            "userId" : userId,
            "weChartContent" : weChartContent,
            "weChart" : weChart,
            "smsContent" : smsContent,
            "sms" : sms
        }

        var params = {
            "datainfo" : dataInfoParams,
            "strategy" : strategyParams
        }

        $.ajax({
            type : "POST",
            url : "/pjpz/addBaseSourceConfiger",
            datatype : "json",
            async : false,
            data : JSON.stringify(params),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(list) {
                alert(list)
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });

    } else {
        // alert("保存的是业务数据");
        var selectValue = $("#pzAddSelect4").val();
        if (selectValue == -1) {
            selectValue = $("#pzAddSelect3").val();
        }
        if (selectValue == -1) {
            alert("请选择一种数据!!")
            return;
        }
        // 发布模板参数
        var weChart;
        var sms;
        if (document.getElementById("inlineCheckbox1").checked) {
            weChart = 1;
        } else {
            weChart = 0;
        }
        if (document.getElementById("inlineCheckbox2").checked) {
            sms = 1;
        } else {
            sms = 0;
        }
        var userId = $("#selectUser").val();
        var weChartContent = $("#tempWc").val();
        var smsContent = $("#tempSm").val();

        // console.log({"pznameid":pznameid,"alertLevel":alertLevel,"pzAddtimeyz":pzAddtimeyz,"userId":userId,"weChartContent":weChartContent,"weChart":weChart,"smsContent":smsContent,"sms":sms})
        var strategyParams = {
            "userId" : userId,
            "weChartContent" : weChartContent,
            "weChart" : weChart,
            "smsContent" : smsContent,
            "sms" : sms
        }
        // 数据类型
        var dataInfoParams = [];
        $("#table_cjf").find("tr").each(
            function(m) {
                var inputHidden = $(this).find("input[name='moduleSpan']");
                dataInfoParams.push({
                    "id" : inputHidden[0].value,
                    "timeoutValue" : $("#pzAddtimeyz" + m).val(),
                    "shouldtimeValue" : $("#pzShouleTimeyz" + m).val(),
                    "regular" : $(
                        ":radio[name='regular_" + m + "']:checked")
                        .val(),
                    "monitorTimes" : $("#dataTimeCron_" + m).val(),
                    "fileSizeDefine":$("#fileSizeDefine_" + m).val(),
                    "fileNameDefine":$("#fileNameDefine_" + m).val()
                });
            })
        var params = {
            "datainfo" : dataInfoParams,
            "strategy" : strategyParams
        }

        console.log(params)
        // console.log("提交代码被注释")
        // 发送ajax ，保存操作
        $.ajax({
            type : "POST",
            url : "/pjpz/addstrategy",
            datatype : "json",
            async : false,
            data : JSON.stringify(params),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(list) {
                alert(list)
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
    }

    alertStrategy_Search();

}

function changeContent() {
    var id = $("#selectTemp").val();
    $.ajax({
        type : "POST",
        url : "/pjpz/changeContent",
        datatype : "json",
        data : JSON.stringify({
            "id" : id
        }),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {

            // 把模板表中的信息显示出来
            $("#tempWc").html(r.wechartContentTemplate);
            $("#tempSm").html(r.smsContentTemplate);
            if (r.wechartSendEnable == 1) {
                document.getElementById('inlineCheckbox1').checked = true;
            } else {
                document.getElementById('inlineCheckbox1').checked = false;
            }
            if (r.smsSendEnable == 1) {
                document.getElementById('inlineCheckbox2').checked = true;
            } else {
                document.getElementById('inlineCheckbox2').checked = false;
            }
            // 查看最终发送选择是否选中

        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });

}

/*-------------------->fukl.2018.03.14<-------------------------*/

function onchangeSelect(m) {
    var id = $(m).context.id;
    var value = $("#" + id).val();

    if (id == "basic_ifo" && value == 1) {
        $("#zlName").hide();
    }
    if (id == "basic_ifo" && value == 3) {
        $("#zlName").show();
    }

    var data = {
        "pid" : value
    }
    var putId = '';
    if (id == 'basic_ifo') {
        putId = "basic_ifo1";
    } else if (id == 'basic_ifo1') {
        putId = "basic_ifo2";
    } else if (id == 'basic_ifo2') {
        putId = "basic_ifo3";
    }
    // 决策配置查询选择框
    $.ajax({
        type : "POST",
        url : "/pjpz/basic_ifo",
        datatype : "json",
        data : JSON.stringify(data),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {
            // console.log(1)
            var s = "<option selected='selected' value='-1'>全部</option>";

            // 选择上级，下级子选择框都跟着变动成默认
            if (id == "basic_ifo") {
                $("#basic_ifo1").html(s);
                $("#basic_ifo2").html(s);
                $("#basic_ifo3").html(s);
            } else if (id == "basic_ifo1") {
                $("#basic_ifo2").html(s);
                $("#basic_ifo3").html(s);
            }

            for (var i = 0; i < r.length; i++) {
                var dio = r[i];
                if (putId == "basic_ifo3") {
                    s += "<option value='" + dio[0] + "'>" + dio[5]
                        + "</option>";

                } else {
                    s += "<option value='" + dio[0] + "'>" + dio[1]
                        + "</option>";
                }
            }
            $("#" + putId).html(s);
        },
        error : function(err) {
            console.log(err.message)
        }
    });
}

function onchangeSelect2(m) {
    var id = $(m).context.id;
    var value = $("#" + id).val();
    console.log($(m))

    if (id == "pzAddSelect1" && value == -1) {
        document.getElementById("baseSourceIp").style.display = 'none';
    }

    if (id == "pzAddSelect1" && value == 1) {
        $("#selectTypeHidden").val("1");
        $("#hjName3").hide();
        $("#pzAddSelect3").hide();
        $("#hjName2").hide();
        $("#pzAddSelect2").hide();
    }
    if (id == "pzAddSelect1" && value == 3) {
        $("#selectTypeHidden").val("3");
        $("#hjName3").show();
        $("#pzAddSelect3").show();
        $("#hjName2").show();
        $("#pzAddSelect2").show();
    }

    var data = {
        "pid" : value
    }
    var putId = '';
    if (id == 'pzAddSelect1') {
        putId = "pzAddSelect2";
    } else if (id == 'pzAddSelect2') {
        putId = "pzAddSelect3";
    } else if (id == 'pzAddSelect3') {
        putId = "pzAddSelect4";
    }
    // 决策配置查询选择框
    $
        .ajax({
            type : "POST",
            url : "/pjpz/basic_ifo",
            datatype : "json",
            data : JSON.stringify(data),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                var s = "<option selected='selected' value='-1'>全部</option>";
                var m = "";
                // 选择上级，下级子选择框都跟着变动成默认
                if (id == "pzAddSelect1") {
                    $("#pzAddSelect2").html(s);
                    $("#pzAddSelect3").html(s);
                    $("#pzAddSelect4").html(s);
                } else if (id == "pzAddSelect2") {
                    $("#pzAddSelect3").html(s);
                    $("#pzAddSelect4").html(s);
                }
                var selectType = $("#selectTypeHidden").val();
                $("#table_cjf").html("");
                $("#tip_cjf").html("");
                if ($("#pzAddSelect1").val() == 3) {
                    $("#tip_cjf").html(
                        " (有规律的填写与时次的差值，无规律的填写多个时次不同的差值，中间用英文逗号分开) ");
                }
                for (var i = 0; i < r.length; i++) {
                    var dio = r[i];
                    console.log(dio)
                    if (putId == "pzAddSelect4") {
                        // document.getElementById("baseSourceIp").style="visibility:hidden";
                        m = "<tr>" + "<td height='105px'>";
                        var spanHtml = dio[2];
                        if (spanHtml == "DS") {
                            spanHtml = dio[5];
                        }
                        var ips = dio[11].split("\.");
                        m += "   <span >"
                            + spanHtml+"<br/>"+ips[2]+"."+ips[3]
                            + "</span>"
                            + "   <input type='hidden' name='moduleSpan' value='"
                            + dio[0]
                            + "' />"
                            + "</td>"
                            + "<td><div id='tr_div"
                            + i
                            + "'>"
                            + "                       <div class='row'>"
                            + "                                    <div class='form-control'>时次表达式</div>"
                            + "                                    <div class='form-group'>"
                            + "                                        <input type='text' class='form-control'  placeholder='' id='dataTimeCron_"
                            + i
                            + "'/>"
                            + "                                    </div>"
                            + "                                    <div class='form-control'>间隔规律</div>"
                            + "                                    <div class='radio'>"
                            + "                                        <label>"
                            + "                                            <input type='radio' name='regular_"
                            + i
                            + "' value='1' /> 有规律"
                            + "                                        </label>"
                            + "                                    </div>"
                            + "                                    <div class='radio'>"
                            + "                                        <label>"
                            + "                                            <input type='radio' name='regular_"
                            + i
                            + "' value='2' /> 无规律"
                            + "                                        </label>"
                            + "                                    </div>"
                            + "                               </div>"
                            + "                           <div class='row'>"

                            + "                                    <div class='form-control'>文件名</div>"
                            + "                                    <div class='form-group'>"
                            + "                                        <input type='text' class='form-control'  placeholder='' id='fileNameDefine_" + i + "'/>"
                            + "                                    </div>"
                            + "                                    <div class='form-control'>文件大小</div>"
                            + "                                    <div class='form-group'>"
                            + "                                        <input type='text' class='form-control'  placeholder='' id='fileSizeDefine_" + i + "'/>"
                            + "                                    </div>"
//                            + "                                    <div >"
                            + "                                        <span>byte</span>"
//                            + "                                    </div>"
                            + "                       </div>"
                            + "                       <div class='row'>"
                            + "                                    <div class='form-control'>应到时间</div>"
                            + "                                    <div class='form-group'>"
                            + "                                        <input type='text' class='form-control'  placeholder='' id='pzShouleTimeyz"
                            + i
                            + "'/>"
                            + "                                    </div>"
//                            + "                                    <div >"
                            + "                                        <span>分</span>"
//                            + "                                    </div>"
                            + "                                    <div class='form-control'>超时阈值</div>"
                            + "                                    <div class='form-group'>"
                            + "                                        <input type='text' class='form-control'  placeholder='' id='pzAddtimeyz"
                            + i
                            + "'/>"
                            + "                                    </div>"
//                            + "                                    <div>"
                            + "                                        <span>分</span>"
//                            + "                                    </div>"
                            // + "                                    <div class='form-control' style='background-color: #2aabd2;float: left;width: 100px;margin-left: 36px;'>报警类型</div>"
                            // + "                                    <div class='checkbox' style='float: left;left: 20px;top:-2px;margin-bottom: 0px;' >"
                            // + "                                        <label>"
                            // + "                                            <input type='checkbox' id='before_alert_"+i+"' name='alertType_" + i + "' /> 提前到达报警"
                            // + "                                        </label>"
                            // + "                                    </div>"
                            // + "                                    <div class='checkbox' style='float: left;left: 32px;top:12px;margin-bottom: 0px;' >"
                            // + "                                        <label>"
                            // + "                                            <input type='checkbox' id='after_alert_"+i+"' name='alertType_" + i + "' /> 延迟到达报警"
                            // + "                                        </label>"
                            // + "                                    </div>"
                            + "                               </div>"

                            + "          </div></td></tr>";
                        $("#table_cjf").append(m);
                        $("#pzAddtimeyz" + i + "").val(dio[4]);
                        $("#pzShouleTimeyz" + i + "").val(dio[6]);
                        $("#dataTimeCron_" + i).val(dio[8]);
                        $(":radio[name='regular_" + i + "'][value='"
                            + dio[7] + "']").prop("checked",
                            "checked");

                        $("#fileSizeDefine_" + i + "").val(dio[9]);
                        $("#fileNameDefine_" + i + "").val(dio[10]);


                    } else if (putId == "pzAddSelect2" && selectType == 1) {
                        // console.log(dio)
                        // m="<tr><td width='100px' height='30px'></td>" +
                        // "<td> " +
                        // "<div style=\"\"> " +
                        // " <label class=\"checkbox-inline\">" +
                        // " <input type=\"checkbox\"
                        // name=\"isSendCheckbox\"/> " + dio[1]
                        // " </label>" +
                        // "</div>" +
                        // "</td></tr>";
                        //
                        // console.log(i+","+ r.length)
                        // if(i == r.length-1){
                        // m+="<tr><td width='100px' height='30px'></td>" +
                        // "<td> " +
                        // "<span > 连续告警次数:</span>" +
                        // "<input type='text' id='allDatasourceAlertNumber'
                        // value='5'/> " +
                        // "</td></tr>";
                        // }
                        document.getElementById("baseSourceIp").style.display = 'block';
                        m = "<tr><td width='100px' height='30px'></td>"
                            + "<td>"
                            + "<div style=\"\">"
                            + "	<label class=\"checkbox-inline\">"
                            + "        <input type=\"checkbox\" name=\"baseSource\" value=\"disk\"/> 磁盘"
                            + "    </label>"
                            + "</div>"
                            + "</td>"
                            + "<td>"
                            + "<div style=\"\">"
                            + "	<label class=\"checkbox-inline\">"
                            + "        <input type=\"checkbox\" name=\"baseSource\" value=\"cpu\"/> cpu"
                            + "    </label>"
                            + "</div>"
                            + "</td>"
                            + "<td>"
                            + "<div style=\"\">"
                            + "	<label class=\"checkbox-inline\">"
                            + "        <input type=\"checkbox\" name=\"baseSource\" value=\"mem\"/> 内存"
                            + "    </label>"
                            + "</div>"
                            + "</td>"
                            + "<td>"
                            + "<div style=\"\">"
                            + "	<label class=\"checkbox-inline\">"
                            + "        <input type=\"checkbox\" name=\"baseSource\" value=\"net\"/> 网络"
                            + "    </label>" + "</div>" + "</td>"
                            + "</tr>";

                        $("#table_cjf").append(m);
                        $("#pzAddtimeyz" + i + "").val(dio[4]);
                        break;
                    } else {
                        document.getElementById("baseSourceIp").style.display = 'none';
                        s += "<option value='" + dio[0] + "'>" + dio[1]
                            + "</option>";
                        $("#pzAddtimeyz" + i + "").val(dio[4]);
                    }
                }
                $("#" + putId).html(s);

            },
            error : function(err) {
                console.log(err.message)
            }
        });
}
alertStrategy_Search();
function alertStrategy_Search() {
    var select4 = $("#basic_ifo3").val();
    var select3 = $("#basic_ifo2").val();
    var select2 = $("#basic_ifo1").val();
    var select1 = $("#basic_ifo").val();
    var requestData = {
        "select4" : select4,
        "select3" : select3,
        "select2" : select2,
        "select1" : select1
    }
    $.ajax({
        type : "POST",
        url : "/pjpz/pzSearch",
        datatype : "json",
        async : false,
        data : JSON.stringify(requestData),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {
            var t = {};
            $.each(r, function(i, o) {
                if (typeof t[o.parent_id] === 'undefined')
                    t[o.parent_id] = [];
                t[o.parent_id].push(o);
            }); // 数据预处理 by Edward

            var s = '';
            s = "<tr><td>业务名/IP</td><td>资料名</td><td>环节配置</td><td>操作</td></tr>";

            $.each(t, function(k, o) {
                var modules = '';
                $.each(o, function(i, o2) {
                    modules += o2.module + '<br>';
                });

                s += "<tr><td height='20px'>"
                    + o[0].service_type
                    + "</td><td>"
                    + o[0].sub_name
                    + "</td><td>"
                    + modules
                    + "</td>"
                    + "<td><button type='button' class='btn btn-info' onclick='lookAlertStrategy("
                    + k + ")'>查看</button>&nbsp;&nbsp;" +
                        // "<button type='button' class='btn
                        // btn-info'>修改</button>&nbsp;&nbsp;" +
                    "<button type='button' class='btn btn-info' disabled='disabled' onclick='pztodelet("
                    + k + ")'>删除</button>" + "</td></tr>";
            });

            $("#dataTable").html(s);
        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });
}

function lookAlertStrategy(strid) {
    $('#alertStrategy_look').modal('show');
    $.ajax({
            type : "POST",
            url : "/pjpz/look_strategy",
            datatype : "json",
            data : JSON.stringify({
                "strid" : strid
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(res) {
                // Mod by Edward
                if (res.result == "ok") {
                    var configObj = res.data.config;
                    var alertObj = res.data.alert;


                    var html = '<tr><td colspan="3"><span>TODO: 资料名</span> - <span>'+configObj[0][1]+'</span></td></tr>';
                    $.each(configObj, function(i, v) {
                        html += '<tr><td rowspan="4">'+v[2]+'</td><td>时次表达式</td><td>'+v[8]+'</td></tr>';
                        html += '<tr><td>间隔规律</td><td>'+v[3]+'</td></tr>';
                        html += '<tr><td>应到时间</td><td>'+v[6]+'</td></tr>';
                        html += '<tr><td>超时阈值</td><td>'+v[4]+'</td></tr>';
                    });

                    // 告警内容，目前只取[0]
                    html += '<tr><td rowspan="3">告警策略</td><td>发送用户：<span>TODO: 用户组名</span></td><td>发布模板：<span>TODO: 模板名称</span></td></tr>';
                    html += '<tr><td colspan="2">微信模板：<textarea class="form-control" rows="3" readonly="readonly">'+alertObj[0][6]+'</textarea></td></tr>';
                    html += '<tr><td colspan="2">短信模板：<textarea class="form-control" rows="3" readonly="readonly">'+alertObj[0][7]+'</textarea></td></tr>';

                    $("#strategyDescTable").html(html);
                }
            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });

}
function addalertStrategy() {
    $("#ip").val("");
    $("[name='baseSource']").removeAttr("checked");
}
