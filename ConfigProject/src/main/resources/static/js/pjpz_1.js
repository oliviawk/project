$(document)
    .ready(
        function() {
            // 发送模板显示
            $.ajax({
                type : "POST",
                url : "/pjpz/sen_list",
                async : false,
                headers : {
                    "Content-Type" : "application/json; charset=utf-8"
                },
                success : function(r) {
                    var s = "<tr><td class='shade1'>id</td><td class='shade1'>模板名称</td><td class='shade1'>数据类型</td><td width='250px' class='shade1'>微信模板</td><td class='shade1'>是否发微信</td><td width='250px' class='shade1'>短信模板</td><td class='shade1'>是否发短信</td><td class='shade1'>操作</td></tr>"
                    var m = "";
                    for (var i = 0; i < r.length; i++) {
                        var sendT = r[i];
                        var a = null;
                        var b = null;
                        if(sendT.wechartSendEnable == "1"){
                            a = '是';
                        }else {
                            a = '否'
                        }
                        if(sendT.smsSendEnable == "1"){
                            b = '是';
                        }else {
                            b = '否'
                        }
                        s += "<tr><td>"
                            + sendT.id
                            + "</td><td>"
                            + sendT.name
                            + "</td><td>"
                            + sendT.type
                            + "</td><td>"
                            + sendT.wechartContentTemplate
                            + "</td><td>"
                            + a
                            + "</td><td>"
                            + sendT.smsContentTemplate
                            + "</td><td>"
                            + b
                            + "</td><td><button type='button' style='border-radius: 5px;' class='shade' onclick='temptodelet("
                            + sendT.id
                            + ")'>删除</button></br><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_m' onclick='upd("
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
            // 决策配置查询选择框
            $.ajax({
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
            var phonezreo;
            // $.ajax({
            //     type:"POST",
            //     async : false,
            //     url:"/pjpz/SelectUserPhonezero",
            //     headers : {
            //         "Content-Type" : "application/json; charset=utf-8"
            //     },
            //     success  : function(r) {
            //         var m = "";
            //         for (var i = 0; i < r.length; i++) {
            //             if (i==1){
            //                 phonezreo=userT.phone;
            //             }
            //             var userT = r[i];
            //             m += "<option value='" + userT.id + "'>"
            //                 + userT.name + "</option>";
            //         }
            //         $("#selectUserhx").html(m)
            //         $("#selectuserphonehx").val(phonezreo);
            //     },
            //     error : function(err) {
            //         alert(err);
            //         console.log(err.message)
            //     }
            // });
            // 基础资源配置初始化加载 ！！
            $.ajax({
                type:"POST",
                async : false,
                url:"/pjpz/SelectUserPhonezero",
                headers : {
                    "Content-Type" : "application/json; charset=utf-8"
                },
               success:function (r) {
                    var listone=r.listone;
                    var list=listone;
                    var listtwo=r.listtwo;
                    $("#rulesid").val(listtwo[0][0]);
                    $("#alertnumhx").val(listtwo[0][1]);
                    $("#alerttimehx").val(listtwo[0][2]);
                    $("#alertmehx").val(listtwo[0][3]);
                   var s = "<tr style='height: 40px;'><th style='display: none;vertical-align:middle;text-align: center' class='shade1' >id</th><th  class='shade1' style='vertical-align:middle;text-align: center'>用户名</th><th class='shade1' style='vertical-align:middle;text-align: center'>用户电话</th><th class='shade1' style='vertical-align:middle;text-align: center'>是否发送生效</th></th>"
                   var m = "";
                   for (var i = 0; i < listone.length; i++) {
                       debugger
                       s += "<tr style='height: 40px;'><td style='display: none ; vertical-align:middle;text-align: center'>"
                           + list[i][0]
                           + "</td><td style='vertical-align:middle;text-align: center;height: 40px'>"
                           + list[i][1]
                           + "</td><td style='vertical-align:middle;text-align: center;height: 40px'>"
                           + list[i][2]+"</td>"
                           var last=list[i][3]
                          if(last==0||last==null){
                              s=s+"<td style='vertical-align:middle;text-align: center;height: 40px'><input class='zhuantai' type='checkbox' style='margin:0 auto;margin-left: 10px; height: 20px;width: 20px ' ></td></tr>"
                          }
                          else {
                              s=s+"<td style='vertical-align:middle;text-align: center;height: 40px'><input class='zhuantai' checked='checked' type='checkbox' style='margin:0 auto;margin-left: 10px; height: 20px;width: 20px ' ></td></tr>"
                          }
                   }

                   $("#hxuser").html(s);
               }, error : function(err) {
                    alert(err);
                    console.log(err.message)
                }

            });

        }

        );


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
                var s = "<tr><td class='shade1'>组名</td><td class='shade1'>用户名</td><td class='shade1'>微信号</td><td class='shade1'>手机号</td><td class='shade1'>操作</td></tr>";
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
                        + "</td><td><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_d' onclick='updUser("
                        + userT[0]
                        + ")'>修改</button>&nbsp;&nbsp;<button style='border-radius: 5px;' class='shade' onclick='usertodelet("
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
    $.ajax({
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
                    var a = null;
                    var b = null;
                    if(sendT.wechartSendEnable == "1"){
                        a = '是';
                    }else {
                        a = '否'
                    }
                    if(sendT.smsSendEnable == "1"){
                        b = '是';
                    }else {
                        b = '否'
                    }
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + a
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + b
                        + "</td><td><button type='button' style='border-radius: 5px;' class='shade' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button></br><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_m' onclick='upd("
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
    alert(tempWechart)
    $.ajax({
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
                    var s = "<tr><td class='shade1'>id</td><td class='shade1'>模板名称</td><td class='shade1'>数据类型</td><td width='250px' class='shade1'>微信模板</td><td class='shade1'>是否发微信</td><td width='250px' class='shade1'>短信模板</td><td class='shade1'>是否发短信</td><td class='shade1'>操作</td></tr>"
                    for (var i = 0; i < list.length; i++) {
                    var sendT = list[i];
                    var a = null;
                    var b = null;
                    if(sendT.wechartSendEnable == "1"){
                        a = '是';
                    }else {
                        a = '否'
                    }
                    if(sendT.smsSendEnable == "1"){
                        b = '是';
                    }else {
                        b = '否'
                    }
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + a
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + b
                        + "</td><td><button type='button' style='border-radius: 5px;' class='shade' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button></br><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_m' onclick='upd("
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
                var s = "<tr><td class='shade1'>组名</td><td class='shade1'>用户名</td><td class='shade1'>微信号</td><td class='shade1'>手机号</td><td class='shade1'>操作</td></tr>";
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
                        + "</td><td><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_d' onclick='updUser("
                        + userT[0]
                        + ")'>修改</button>&nbsp;&nbsp;<button style='border-radius: 5px;' class='shade' onclick='usertodelet("
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
function clearcontent(){
    //alert(1)
    $("#name").val("");
    $("#type").val("");
    $("#wechart_content").val("");
    $("input[id='wechart_send_c']").prop("checked", false);
    $("#wechart_send").val(0);
    $("#sns_content").val("");
    $("input[id='sms_send_c']").prop("checked", false);
    $("#sms_send").val(0);
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
                    var s = "<tr><td class='shade1'>id</td><td class='shade1'>模板名称</td><td class='shade1'>数据类型</td><td width='250px' class='shade1'>微信模板</td><td class='shade1'>是否发微信</td><td width='250px' class='shade1'>短信模板</td><td class='shade1'>是否发短信</td><td class='shade1'>操作</td></tr>"
                    for (var i = 0; i < list.length; i++) {
                    var sendT = list[i];
                    var a = null;
                    var b = null;
                    if(sendT.wechartSendEnable == "1"){
                        a = '是';
                    }else {
                        a = '否'
                    }
                    if(sendT.smsSendEnable == "1"){
                        b = '是';
                    }else {
                        b = '否'
                    }
                    s += "<tr><td>"
                        + sendT.id
                        + "</td><td>"
                        + sendT.name
                        + "</td><td>"
                        + sendT.type
                        + "</td><td>"
                        + sendT.wechartContentTemplate
                        + "</td><td>"
                        + a
                        + "</td><td>"
                        + sendT.smsContentTemplate
                        + "</td><td>"
                        + b
                        + "</td><td><button type='button' style='border-radius: 5px;' class='shade' onclick='temptodelet("
                        + sendT.id
                        + ")'>删除</button></br><button type='button' style='border-radius: 5px;' class='shade' data-toggle='modal' data-target='#sm_m' onclick='upd("
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
                        + "</td><td><button type='button' class='btn btn-info' data-toggle='modal' data-target='#sm_d' onclick='updUser("
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
function updUser(id) {
    $.ajax({
        type : "POST",
        url : "/pjpz/sen_c",
        datatype : "json",
        data : JSON.stringify({
            "id" : id
        }),
        headers : {
            "Content-Type" : "application/json; charset=utf-8"
        },
        success : function(r) {
            $("#id_vu").val(r.id);
            $("#name_updu").val(r.name);
            $("#wechat_updu").val(r.wechart);
            $("#phone_updu").val(r.phone);
            $("#email_updu").val(r.email);
            if(r.descs != undefined || r.descs != ""){
                $("#descs_updu").val(r.descs);
            }else {
                $("#descs_updu").val(无);
            }

        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });
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
        var selectTemp = $("#selectTemp").val();
        // console.log({"pznameid":pznameid,"alertLevel":alertLevel,"pzAddtimeyz":pzAddtimeyz,"userId":userId,"weChartContent":weChartContent,"weChart":weChart,"smsContent":smsContent,"sms":sms})
        var strategyParams = {
            "userId" : userId,
            "selectTemp" : selectTemp,
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
        var pzAddSelect=$("#pzAddSelect2").find("option:selected").text();
        var userId = $("#selectUser").val();
        var weChartContent = $("#tempWc").val();
        var smsContent = $("#tempSm").val();
        var selectTemp = $("#selectTemp").val();
        // console.log({"pznameid":pznameid,"alertLevel":alertLevel,"pzAddtimeyz":pzAddtimeyz,"userId":userId,"weChartContent":weChartContent,"weChart":weChart,"smsContent":smsContent,"sms":sms})
        var strategyParams = {
            "userId" : userId,
            "selectTemp" : selectTemp,
            "weChartContent" : weChartContent,
            "weChart" : weChart,
            "smsContent" : smsContent,
            "sms" : sms,
            "businesstypes":pzAddSelect
        }
        // 数据类型
        var dataInfoParams = [];
        $("#table_cjf").find("tr").each(
            function(m) {
                var inputHidden = $(this).find("input[name='moduleSpan']");
                var tparams = {
                                  "id" : inputHidden[0].value,
                                  "timeoutValue" : $("#pzAddtimeyz" + m).val()?$("#pzAddtimeyz" + m).val():null,
                                  "shouldtimeValue" : $("#pzShouleTimeyz" + m).val()?$("#pzShouleTimeyz" + m).val():null,
                                  "regular" : $(
                                      ":radio[name='regular_" + m + "']:checked")
                                      .val()?$(":radio[name='regular_" + m + "']:checked").val():0,
                                  "monitorTimes" : $("#dataTimeCron_" + m).val()?$("#dataTimeCron_" + m).val():null,
                                  "fileNameDefine":$("#fileNameDefine_" + m).val()?$("#fileNameDefine_" + m).val():null,
                                  "beforeAlert":$("#before_alert_" + m).prop("checked")?1:0,
                                  "delayAlert":$("#after_alert_" + m).prop("checked")?1:0,
                                  "alertTimeRange":$("#alertTimeRange_" + m).val(),
                                  "maxAlerts":$("#maxAlerts_" + m).val()

                              };

                var sizes = $("#fileSizeDefine_" + m).val()?$("#fileSizeDefine_" + m).val():null;
                if(sizes!=null){
                    var sarr = sizes.split(",");
                    var unit = $("#unit_" + m).val();
                    sizes = sarr[0] * eval(unit);
                    if(sarr.length == 2){
                        sizes += "," + sarr[1] * eval(unit);
                    }
                }
                tparams["fileSizeDefine"] = sizes;
                dataInfoParams.push(tparams);
            })
        var params = {
            "datainfo" : dataInfoParams,
            "strategy" : strategyParams
        }

        console.log(params)
        // 发送ajax ，保存操作
        // console.log(selectTemp);
        // alert("测试，未保存")
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
/*保存基础资源*/
function pzsavehx(){
    console.info("执行保存方法！！pzsavehx")
     $("#pzsavehx").attr("disabled",true);
    var mytable = document.getElementById("hxuser");
    var data = [];
    for(var i=1,rows=mytable.rows.length; i<rows; i++){
        for(var j=0,cells=mytable.rows[i].cells.length; j<cells; j++){
            if(!data[i-1]){
                data[i-1] = new Array();
            }
            if(j==3){
             var tf= mytable.rows[i].cells[j].firstChild.checked;
                console.info("元素："+j+"row："+i+"boolean:"+tf)
             if (tf){
                 data[i-1][j]="1";
             }
             else {
                 data[i-1][j]="0";
             }
        }
             else {
                data[i-1][j] = mytable.rows[i].cells[j].innerHTML;
            }

        }
    }
    var alertnum=$("#alertnumhx").val();
    var alerttime=$("#alerttimehx").val();
    var alertme=$("#alertmehx").val();
    var rulesid=$("#rulesid").val();
    var datatemple={
        "alertnum":alertnum,
        "alerttime":alerttime,
        "alertme":alertme,
        "rulesid":rulesid,
        "tabledata":data
    }

        $.ajax({
            type : "POST",
            url : "/pjpz/SaveBasicreSources",
            datatype : "json",
            async : false,
            data : JSON.stringify(datatemple),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                $("#pzsavehx").attr("disabled",false);
                alert(r)
            },
            error : function(err) {
                $("#pzsavehx").attr("disabled",false);
                alert(err);
                console.log(err.message)
            }
        });
}
/**
 * 发布模板初始化
 */
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

function changeContentlc() {

    var id = $("#selectTemplc").val();
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
            $("#tempWclc").html(r.wechartContentTemplate);
            $("#tempSmlc").html(r.smsContentTemplate);
            if (r.wechartSendEnable == 1) {
                document.getElementById('inlineCheckbox1lc').checked = true;
            } else {
                document.getElementById('inlineCheckbox1lc').checked = false;
            }
            if (r.smsSendEnable == 1) {
                document.getElementById('inlineCheckbox2lc').checked = true;
            } else {
                document.getElementById('inlineCheckbox2lc').checked = false;
            }
            // 查看最终发送选择是否选中

        },
        error : function(err) {
            alert(err);
            console.log(err.message)
        }
    });

}
   /*根据所选用户名查询用户电话*/
 function changeContentUserhx(){
     console.info("执行changeContentUser方法")
    var user= $("#selectUserhx").find("option:selected").text()

     console.info("参数："+user)
     if (user==null|| user==""){

         return ;
     }
     else {
         $.ajax({
             url: "/pjpz/SelectUserPhone",
             data: {"user": user},
             type: "post",
             success: function (r) {
                 if (r!==null){
                     $("#selectuserphonehx").val(r)
                 }
                 else {
                     alert("数据异常！！！")
                 }

             },
             error: function (error) {

             }
         });
     }
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
    // console.log($(m))

    if (id == "pzAddSelect1" && value == -1) {
        document.getElementById("baseSourceIp").style.display = 'none';
    }

    if (id == "pzAddSelect1" && value == 1) {
        $("#selectTypeHidden").val("1");
        $("#hjName3").hide();
        $("#pzAddSelect3").hide();
        $("#hjName2").hide();
        $("#pzAddSelect2").hide();
        $("#baseSourceIp").show();
        $("#tip_cjf").hide();
    }
    if (id == "pzAddSelect1" && value == 3) {
        $("#selectTypeHidden").val("3");
        $("#hjName3").show();
        $("#pzAddSelect3").show();
        $("#hjName2").show();
        $("#pzAddSelect2").show();
        $("#tip_cjf").show();
        $("#baseSourceIp").hide();
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
                // if ($("#pzAddSelect1").val() == 3) {
                //     $("#tip_cjf").style("display","black");
                // }else{
                //     $("#tip_cjf").style("display","none");
                // }
                for (var i = 0; i < r.length; i++) {
                    var dio = r[i];
                    if (putId == "pzAddSelect4") {
                        if (i == 0){
                            initSendTemplate(dio[0]);
                        }
                        console.info(dio)
                        // document.getElementById("baseSourceIp").style="visibility:hidden";
                        var sty="";

                        m = "<tr><td >";
                        var spanHtml = dio[1]+" "+dio[2];
                        if (spanHtml == "DS") {
                            spanHtml = dio[5];
                        }
                        var ips = dio[11].split("\.");

                        m += "  <span class='little-title'>"+spanHtml+"&nbsp;&nbsp;"+ips[2]+"."+ips[3]+"</span>";
                        m += "  <input type='hidden' name='moduleSpan' value='"+dio[0]+"'>";
                        m += "  <div id=\"tr_div"+i+"\" class=\"area\" >";

                        if (dio[3] == 1){
                            m += "      <div class=\"row\">";
                            m += "          <div class=\"form-control form-attribute\" >时次表达式</div>";
                            m += "          <div class='form-group'>";
                            m += "              <input type='text'   class='form-control'  placeholder='' id='dataTimeCron_"+i+"'/>";
                            m += "          </div>";
                            m += "          <div class=\"form-control form-attribute\">间隔规律</div>";
                            m += "          <div class=\"radio\">";
                            m += "              <label><input type='radio' name='regular_"+i+"' value='1' /> 有规律 </label>";
                            m += "          </div>";
                            m += "          <div class=\"radio\">";
                            m += "              <label><input type='radio' name='regular_"+i+"' value='2' /> 无规律 </label>";
                            m += "          </div></div>";

                            m += "      <div class=\"row\">";
                            m += "          <div class='form-control form-attribute'>文件名</div>";
                            m += "          <div class='form-group'>";
                            m += "              <input type='text' class='form-control' style='width:475px;'  placeholder='' id='fileNameDefine_" + i + "'/>";
                            m += "          </div>";
                            m += "       </div>";


                            m += "      <div class='row'>";
                            m += "          <div class='form-control  form-attribute'>报警提示</div>";
                            m += "          <div class='checkbox'>";
                            m += "              <label><input type='checkbox' id='before_alert_"+ i + "' name='alertType_"+i+"'/>正常到达 </label>";
                            m += "          </div>";
                            m += "          <div class='checkbox' style='margin-right: 27px;'>";
                            m += "              <label><input type='checkbox' id='after_alert_"+ i + "' name='alertType_"+i+"'/>延迟到达 </label>";
                            m += "          </div>";
                            m += "          <div class='form-control form-attribute'>文件大小</div>";
                            m += "          <div class='form-group'>";
                            m += "              <input type='text' class='form-control'  placeholder='' id='fileSizeDefine_" + i + "'/>";
                            m += "          </div>";
                            m += "          <div class='form-group'>";
                            m += "              <select id='unit_"+i+ "' class='unit'>";
                            m += "                  <option value='1'>B</option><option value='1024'>KB</option><option value='1024*1024'>MB</option><option value='1024*1024*1024'>GB</option>";
                            m += "              </select>";
                            m += "          </div>";
                            m += "      </div>";

                            m += "      <div class='row'>";
                            m += "          <div class='form-control  form-attribute'>应到时间</div>";
                            m += "          <div class='form-group'>";
                            m += "              <input type='text' class='form-control'  placeholder='' id='pzShouleTimeyz"+ i + "'/>";
                            m += "          </div><span>分</span>";
                            m += "          <div class='form-control form-attribute'>超时阈值</div>";
                            m += "          <div class='form-group'>";
                            m += "              <input type='text' class='form-control'  placeholder='' id='pzAddtimeyz"+ i + "'/>";
                            m += "          </div><span>分</span>";
                            m += "      </div>";


                        }
                        m += "      <div class='row'>";
                        m += "          <div class='form-control  form-attribute'>报警时间段</div>";
                        m += "          <div class='form-group'>";
                        m += "              <input type='text' class='form-control'  placeholder='' id='alertTimeRange_"+ i + "'/>";
                        m += "          </div>";
                        m += "          <div class='form-control form-attribute'>连续报警数</div>";
                        m += "          <div class='form-group'>";
                        m += "              <input type='text' class='form-control'  placeholder='' id='maxAlerts_"+ i + "'/>";
                        m += "          </div>";
                        m += "      </div>";

                        m += "  </div>";

                        $("#table_cjf").append(m);
                        if (dio[8]==null||dio[8]==""){
                            $("#dataTimeCron_" + i).attr("readonly",true);
                            $("#before_alert_" + i).attr("checked",dio[12]==1);
                            $("#after_alert_" + i).prop("checked",false);
                            $("#after_alert_" + i).attr("disabled",true);
                            $("#pzAddtimeyz"+i).attr("readonly",true);
                            $("#pzShouleTimeyz"+i).attr("readonly",true);
                            $(":radio[name='regular_" + i + "'][value='2']").prop("checked",
                                "checked");
                            $(":radio[name='regular_" + i + "']").attr("disabled",true);


                        }
                        else {
                            $("#before_alert_" + i).prop("checked",dio[12]==1);
                            $("#after_alert_" + i).prop("checked",dio[13]==1);
                            $(":radio[name='regular_" + i + "'][value='"
                                + dio[7] + "']").prop("checked",
                                "checked");
                        }
                        $("#pzAddtimeyz" + i + "").val(dio[4]);
                        $("#pzShouleTimeyz" + i + "").val(dio[6]);
                        $("#dataTimeCron_" + i).val(dio[8]);


                        $("#fileSizeDefine_" + i + "").val(dio[9]);
                        $("#fileNameDefine_" + i + "").val(dio[10]);


                        $("#alertTimeRange_" + i + "").val(dio[14]?dio[14]:"00:00:00-23:59:59");
                        $("#maxAlerts_" + i + "").val(dio[15]);


                    } else if (putId == "pzAddSelect2" && selectType == 1) {
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
            s = "<tr><td style='border-left: #e1dfe3 solid 1px' class='shade1'>业务名/IP</td><td style='border-left: #e1dfe3 solid 1px' class='shade1'>资料名</td><td style='border-left: #e1dfe3 solid 1px' class='shade1'>环节配置</td><td style='border-left: #e1dfe3 solid 1px' class='shade1'>操作</td></tr>";

            $.each(t, function(k, o) {
                console.log(o)
                var modules = '';
                var sub_name = '';
                $.each(o, function(i, o2) {
                    modules += o2.module + '<br>';
                    sub_name += o2.sub_name + '<br>';
                });





                s += "<tr><td height='20px'>"
                    + o[0].service_type
                    + "</td><td>"
                    + sub_name
                    + "</td><td>"
                    + modules
                    + "</td>"
                    + "<td><button type='button' style='border-radius: 5px;' class='shade' onclick='lookAlertStrategy(" + k + ")'>查看</button>&nbsp;&nbsp;" +
                        // "<button type='button' class='btn
                        // btn-info'>修改</button>&nbsp;&nbsp;" +
                    "<button type='button' style='border-radius: 5px;' class='shade' onclick='pztodelet(" + k + ")'>删除</button>" + "</td></tr>";
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

function initSendTemplate(data_id){
    if (data_id > 0){
        $.ajax({
            type : "POST",
            url : "/pjpz/findstrategy",
            datatype : "json",
            async : false,
            data : JSON.stringify({
                "data_id" : data_id
            }),
            headers : {
                "Content-Type" : "application/json; charset=utf-8"
            },
            success : function(r) {
                if (r == null || r == "null" || r == ""){
                    $("#tempWc").html(" ");
                    $("#tempSm").html(" ");
                    document.getElementById('inlineCheckbox1').checked = false;
                    document.getElementById('inlineCheckbox2').checked = false;
                }else{
                    $("#selectUser").val(r.send_users);
                    $("#selectTemp").val(r.template_id);

                    $("#tempWc").html(r.wechart_content);
                    $("#tempSm").html(r.sms_content);
                    if (r.wechart_send_enable == 1) {
                        document.getElementById('inlineCheckbox1').checked = true;
                    } else {
                        document.getElementById('inlineCheckbox1').checked = false;
                    }
                    if (r.sms_send_enable == 1) {
                        document.getElementById('inlineCheckbox2').checked = true;
                    } else {
                        document.getElementById('inlineCheckbox2').checked = false;
                    }

                }

            },
            error : function(err) {
                alert(err);
                console.log(err.message)
            }
        });
    }
}

